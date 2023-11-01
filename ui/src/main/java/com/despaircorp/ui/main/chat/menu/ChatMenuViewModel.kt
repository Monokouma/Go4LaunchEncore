package com.despaircorp.ui.main.chat.menu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.despaircorp.domain.firebase_real_time.CreateConversationUseCase
import com.despaircorp.domain.firestore.GetAllCoworkersForChatUseCase
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.Event
import com.despaircorp.ui.utils.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatMenuViewModel @Inject constructor(
    private val getAllCoworkersForChatUseCase: GetAllCoworkersForChatUseCase,
    private val createConversationUseCase: CreateConversationUseCase
) : ViewModel() {
    
    val viewAction = MutableLiveData<Event<ChatMenuAction>>()
    
    val viewState = liveData<ChatMenuViewState> {
        getAllCoworkersForChatUseCase.invoke().collect { coworkerChatEntities ->
            emit(
                ChatMenuViewState(
                    chatUserViewStateItems = coworkerChatEntities.map { coworkerChatEntity ->
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
                )
            )
        }
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