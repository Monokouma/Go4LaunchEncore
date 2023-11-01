package com.despaircorp.ui.main.chat.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.despaircorp.domain.firestore.GetFirestoreUserAsFlowUseCase
import com.despaircorp.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class ChatDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getFirestoreUserAsFlowUseCase: GetFirestoreUserAsFlowUseCase
) : ViewModel() {
    
    
    private val isMessageFilledMutableStateFlow = MutableStateFlow(false)
    
    val viewState = liveData<ChatDetailsViewState> {
        val receiverUid = savedStateHandle.get<String>(ARG_TARGET_USER_UID)
            ?: return@liveData
        
        combine(
            isMessageFilledMutableStateFlow,
            getFirestoreUserAsFlowUseCase.invoke(receiverUid)
        ) { isMessageFilled, firestoreUserEntity ->
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
                    }
                )
            )
        }.collect()
        
    }
    
    fun onChatTextChanged(message: String) {
        isMessageFilledMutableStateFlow.value = message.isNotEmpty()
    }
    
    companion object {
        private const val ARG_TARGET_USER_UID = "ARG_TARGET_USER_UID"
    }
}