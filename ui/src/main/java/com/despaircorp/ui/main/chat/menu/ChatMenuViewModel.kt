package com.despaircorp.ui.main.chat.menu

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.despaircorp.domain.firebase_auth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firebase_real_time.CreateConversationUseCase
import com.despaircorp.domain.firebase_real_time.GetAllUserConversationUseCase
import com.despaircorp.domain.firestore.GetAllCoworkersForChatUseCase
import com.despaircorp.domain.firestore.GetFirestoreUserUseCase
import com.despaircorp.ui.R
import com.despaircorp.ui.main.chat.menu.messages.ChatMenuMessagesViewStateItems
import com.despaircorp.ui.main.chat.menu.online_users.ChatMenuOnlineUserViewStateItems
import com.despaircorp.ui.utils.Event
import com.despaircorp.ui.utils.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ChatMenuViewModel @Inject constructor(
    private val getAllCoworkersForChatUseCase: GetAllCoworkersForChatUseCase,
    private val createConversationUseCase: CreateConversationUseCase,
    private val getAllUserConversationUseCase: GetAllUserConversationUseCase,
    private val getFirestoreUserUseCase: GetFirestoreUserUseCase,
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase,
    private val application: Application
) : ViewModel() {
    
    val viewAction = MutableLiveData<Event<ChatMenuAction>>()
    
    val viewState = liveData<ChatMenuViewState> {
        combine(
            getAllCoworkersForChatUseCase.invoke(),
            getAllUserConversationUseCase.invoke()
        ) { coworkerChatEntities, conversationEntities ->
            val groupedConversation =
                conversationEntities.groupBy { Pair(it.senderUid, it.receiverUid) }
                    .values
                    .map { group ->
                        group.maxByOrNull { it.timestamp } ?: group.first()
                    }
            
            emit(
                ChatMenuViewState(
                    chatMenuOnlineUserViewStateItems = coworkerChatEntities.map { coworkerChatEntity ->
                        ChatMenuOnlineUserViewStateItems(
                            uid = coworkerChatEntity.uid,
                            name = coworkerChatEntity.name,
                            dotDrawable = if (coworkerChatEntity.isOnline) {
                                R.drawable.green_circle
                            } else {
                                R.drawable.gray_circle
                            },
                            userImageAlpha = if (coworkerChatEntity.isOnline) {
                                1.0f
                            } else {
                                0.6f
                            },
                            pictureUrl = coworkerChatEntity.pictureUrl,
                            online = coworkerChatEntity.isOnline
                        )
                    }.sortedWith(compareBy({ !it.online }, { it.uid })),
                    
                    chatMessagesViewStateItems = groupedConversation.map {
                        ChatMenuMessagesViewStateItems(
                            convId = it.chatId,
                            senderId = it.senderUid,
                            receiverId = it.receiverUid,
                            message = StringBuilder()
                                .append(
                                    if (getAuthenticatedUserUseCase.invoke().uid == it.senderUid) {
                                        application.getString(R.string.you)
                                    } else {
                                        getFirestoreUserUseCase.invoke(it.receiverUid).displayName
                                    }
                                )
                                .append(":")
                                .append(" ")
                                .append(it.value)
                                .append("•")
                                .append(" ")
                                .append(
                                    LocalDateTime.ofInstant(
                                        Instant.ofEpochMilli(it.timestamp),
                                        ZoneId.systemDefault()
                                    ).format(
                                        DateTimeFormatter.ofPattern("HH:mm")
                                    )
                                )
                                .toString(),
                            receiverImage = getFirestoreUserUseCase.invoke(it.receiverUid).picture,
                            receiverName = getFirestoreUserUseCase.invoke(it.receiverUid).displayName,
                            timestamp = it.timestamp
                        )
                    }.sortedByDescending { it.timestamp },
                )
            )
        }.collect()
        
    }
    
    fun createConversation(uid: String) {
        viewModelScope.launch {
            createConversationUseCase.invoke(uid).collect {
                viewAction.value = if (it) {
                    Event(ChatMenuAction.Success(uid))
                } else {
                    Event(ChatMenuAction.Error(NativeText.Resource(R.string.error_occurred)))
                }
            }
        }
    }
}