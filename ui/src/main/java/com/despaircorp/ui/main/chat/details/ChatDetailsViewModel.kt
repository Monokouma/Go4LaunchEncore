package com.despaircorp.ui.main.chat.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.despaircorp.domain.firebase_real_time.GetAllUserMessagesWithSpecificUserUseCase
import com.despaircorp.domain.firebase_real_time.SendMessageUseCase
import com.despaircorp.domain.firestore.GetFirestoreUserAsFlowUseCase
import com.despaircorp.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getFirestoreUserAsFlowUseCase: GetFirestoreUserAsFlowUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getAllUserMessagesWithSpecificUserUseCase: GetAllUserMessagesWithSpecificUserUseCase
) : ViewModel() {
    private val isMessageFilledMutableStateFlow = MutableStateFlow(false)
    private var message: String? = null
    
    val viewState = liveData<ChatDetailsViewState> {
        val receiverUid = savedStateHandle.get<String>(ARG_TARGET_USER_UID)
            ?: return@liveData
        
        combine(
            isMessageFilledMutableStateFlow,
            getFirestoreUserAsFlowUseCase.invoke(receiverUid),
            getAllUserMessagesWithSpecificUserUseCase.invoke(receiverUid)
        ) { isMessageFilled, firestoreUserEntity, allMessagesEntities ->
            
            emit(
                ChatDetailsViewState(
                    firestoreUserEntity.displayName,
                    firestoreUserEntity.picture,
                    if (firestoreUserEntity.online) {
                        R.drawable.green_circle
                    } else {
                        R.drawable.gray_circle
                    },
                    if (isMessageFilled) {
                        R.drawable.send_colored
                    } else {
                        R.drawable.send_uncolored
                    },
                    chatDetailsViewStateItems = allMessagesEntities.map {
                        ChatDetailsViewStateItems(
                            receiverId = it.receiverUid,
                            senderId = it.senderUid,
                            messageValue = it.value,
                            timestamp = it.timestamp,
                            isOnRight = it.senderUid == firestoreUserEntity.uid
                        )
                    }
                )
            )
        }.collect()
        
    }
    
    fun onChatTextChanged(messageInput: String) {
        isMessageFilledMutableStateFlow.value = messageInput.isNotEmpty()
        message = messageInput
    }
    
    fun onSendButtonClicked() {
        val receiverUid = savedStateHandle.get<String>(ARG_TARGET_USER_UID)
            ?: return
        viewModelScope.launch {
            sendMessageUseCase.invoke(
                receiverUid = receiverUid,
                message = message ?: return@launch
            )
        }
    }
    
    companion object {
        private const val ARG_TARGET_USER_UID = "ARG_TARGET_USER_UID"
    }
}