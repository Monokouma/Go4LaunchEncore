package com.despaircorp.data.firebase_real_time

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.despaircorp.data.utils.TestCoroutineRule
import com.despaircorp.domain.firebase_real_time.model.ChatEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runCurrent
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FirebaseRealTimeDataRepositoryUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val firebaseRealTime: FirebaseDatabase = mockk()
    
    private val repository = FirebaseRealTimeDataRepository(
        coroutineDispatcherProvider = testCoroutineRule.getTestCoroutineDispatcherProvider(),
        firebaseRealTime = firebaseRealTime
    )
    
    companion object {
        private const val DEFAULT_SENDER_UID = "DEFAULT_SENDER_UID"
        private const val DEFAULT_RECEIVER_UID = "DEFAULT_RECEIVER_UID"
        
        private const val RECEIVER_NODE_KEY = "receiver"
        private const val VALUE_NODE_KEY = "value"
        private const val TIMESTAMP_NODE_KEY = "timestamp"
        private const val SENDER_UID_NODE_KEY = "senderUID"
        
        private const val DEFAULT_CHAT_TIMESTAMP = 1_000L
        private const val DEFAULT_CHAT_VALUE = "DEFAULT_CHAT_VALUE"
        private const val DEFAULT_CHAT_SNAPSHOT_KEY = "DEFAULT_CHAT_SNAPSHOT_KEY"
        
    }
    
    @Before
    fun setup() {
    
    }
    
    @Test
    fun `nominal case - get chat entity`() = testCoroutineRule.runTest {
        val dataSnapshot: DataSnapshot = mockk() {
            every { children } returns listOf(
                mockk() {
                    every { key } returns DEFAULT_SENDER_UID
                    every { child(RECEIVER_NODE_KEY).value } returns DEFAULT_RECEIVER_UID
                    every { children } returns listOf(
                        mockk() {
                            every { key } returns DEFAULT_CHAT_SNAPSHOT_KEY
                            every { child(VALUE_NODE_KEY).value } returns DEFAULT_CHAT_VALUE
                            every { child(TIMESTAMP_NODE_KEY).value } returns DEFAULT_CHAT_TIMESTAMP
                            every { child(SENDER_UID_NODE_KEY).value } returns DEFAULT_SENDER_UID
                        }
                    )
                }
            )
        }
        
        val slot = slot<ValueEventListener>()
        
        coEvery {
            firebaseRealTime.getReference("chat").addValueEventListener(capture(slot))
        } returns mockk()
        
        repository.getAllLastChatEntities(DEFAULT_SENDER_UID).test {
            runCurrent()
            slot.captured.onDataChange(dataSnapshot)
            cancel()
            
            val result = awaitItem()
            awaitComplete()
            
            assertThat(result).isEqualTo(
                provideChatsEntity()
            )
            
            coVerify {
                firebaseRealTime.getReference("chat").addValueEventListener(any())
            }
            confirmVerified(firebaseRealTime)
        }
    }
    
    @Test
    fun `nominal case - existing conversation`() = testCoroutineRule.runTest {
        val dataSnapshot: DataSnapshot = mockk() {
            every { exists() } returns true
        }
        val slot = slot<ValueEventListener>()
        
        coEvery {
            firebaseRealTime.getReference("chat")
                .child("${DEFAULT_SENDER_UID}_${DEFAULT_RECEIVER_UID}")
                .addValueEventListener(capture(slot))
        } returns mockk()
        
        repository.createConversation(DEFAULT_RECEIVER_UID, DEFAULT_SENDER_UID).test {
            runCurrent()
            slot.captured.onDataChange(dataSnapshot)
            cancel()
            
            val result = awaitItem()
            awaitComplete()
            
            assertThat(result).isTrue()
            
            coVerify {
                firebaseRealTime.getReference("chat")
                    .child("${DEFAULT_SENDER_UID}_${DEFAULT_RECEIVER_UID}")
                    .addValueEventListener(any())
            }
            confirmVerified(firebaseRealTime)
        }
    }
    
    @Test
    fun `nominal case - create conversation`() = testCoroutineRule.runTest {
        val dataSnapshot: DataSnapshot = mockk() {
            every { exists() } returns false
        }
        
        val slot = slot<ValueEventListener>()
        
        coEvery {
            firebaseRealTime.getReference("chat")
                .child("${DEFAULT_SENDER_UID}_${DEFAULT_RECEIVER_UID}")
                .addValueEventListener(capture(slot))
        } returns mockk()
        
        coEvery {
            firebaseRealTime.getReference("chat")
                .child("${DEFAULT_SENDER_UID}_${DEFAULT_RECEIVER_UID}").setValue(
                    mapOf(
                        "receiver" to DEFAULT_RECEIVER_UID,
                        "sender" to DEFAULT_SENDER_UID
                    )
                )
        } returns mockk()
        
        repository.createConversation(DEFAULT_RECEIVER_UID, DEFAULT_SENDER_UID).test {
            runCurrent()
            slot.captured.onDataChange(dataSnapshot)
            cancel()
            awaitComplete()
            
            coVerify {
                firebaseRealTime.getReference("chat")
                    .child("${DEFAULT_SENDER_UID}_${DEFAULT_RECEIVER_UID}")
                    .addValueEventListener(any())
                
                firebaseRealTime.getReference("chat")
                    .child("${DEFAULT_SENDER_UID}_${DEFAULT_RECEIVER_UID}").setValue(
                        mapOf(
                            "receiver" to DEFAULT_RECEIVER_UID,
                            "sender" to DEFAULT_SENDER_UID
                        )
                    )
            }
            confirmVerified(firebaseRealTime)
        }
    }
    
    @Test
    fun `nominal case - insert message failure`() = testCoroutineRule.runTest {
        coEvery {
            firebaseRealTime.getReference("chat")
                .child("${DEFAULT_SENDER_UID}_${DEFAULT_RECEIVER_UID}")
                .child(DEFAULT_CHAT_SNAPSHOT_KEY)
                .setValue(
                    mapOf(
                        "senderUID" to DEFAULT_SENDER_UID,
                        "timestamp" to DEFAULT_CHAT_TIMESTAMP,
                        "value" to DEFAULT_CHAT_VALUE
                    )
                )
        } returns getDefaultSetUserTaskException()
        
        val result =
            repository.insertMessage(provideChatEntity().copy(senderUid = DEFAULT_CHAT_VALUE))
        
        assertThat(result).isFalse()
        
    }
    
    //Region IN
    private inline fun getDefaultSetUserTask(crossinline mockkBlock: Task<Void>.() -> Unit = {}): Task<Void> =
        mockk {
            every { isComplete } returns true
            every { isSuccessful } returns true
            every { exception } returns null
            every { isCanceled } returns false
            every { result } returns mockk()
            
            mockkBlock(this)
        }
    
    private inline fun getDefaultSetUserTaskException(crossinline mockkBlock: Task<Void>.() -> Unit = {}): Task<Void> =
        mockk {
            every { isComplete } returns true
            every { isSuccessful } returns true
            every { exception } returns Exception()
            every { isCanceled } returns false
            every { result } returns mockk()
            
            mockkBlock(this)
        }
    
    private fun provideChatEntity() = ChatEntity(
        chatId = DEFAULT_CHAT_SNAPSHOT_KEY,
        value = DEFAULT_CHAT_VALUE,
        timestamp = DEFAULT_CHAT_TIMESTAMP,
        senderUid = DEFAULT_SENDER_UID,
        receiverUid = DEFAULT_RECEIVER_UID
    )
    
    
    private fun provideChatsEntity() = List<ChatEntity>(1) {
        ChatEntity(
            chatId = DEFAULT_CHAT_SNAPSHOT_KEY,
            value = DEFAULT_CHAT_VALUE,
            timestamp = DEFAULT_CHAT_TIMESTAMP,
            senderUid = DEFAULT_SENDER_UID,
            receiverUid = DEFAULT_RECEIVER_UID
        )
    }
}