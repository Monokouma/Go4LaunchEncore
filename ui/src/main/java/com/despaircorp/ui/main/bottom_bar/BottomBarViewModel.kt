package com.despaircorp.ui.main.bottom_bar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.despaircorp.domain.firebase_auth.DisconnectUserUseCase
import com.despaircorp.domain.firebase_auth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firestore.GetFirestoreUserAsFlowUseCase
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomBarViewModel @Inject constructor(
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase,
    private val disconnectUserUseCase: DisconnectUserUseCase,
    private val getFirestoreUserAsFlowUseCase: GetFirestoreUserAsFlowUseCase,
) : ViewModel() {
    
    val viewState: LiveData<BottomBarViewState> = liveData {
        getFirestoreUserAsFlowUseCase.invoke(getAuthenticatedUserUseCase.invoke().uid).collect {
            emit(
                BottomBarViewState(
                    username = it.displayName,
                    emailAddress = it.mailAddress,
                    userImage = it.picture,
                )
            )
        }
    }
    
    private val viewActionMutableLiveData: MutableLiveData<Event<BottomBarAction>> = MutableLiveData<Event<BottomBarAction>>()
    val viewAction: LiveData<Event<BottomBarAction>> = viewActionMutableLiveData

    fun onDisconnectUser() {
        viewModelScope.launch {
            viewActionMutableLiveData.value = if (disconnectUserUseCase.invoke()) {
                Event(BottomBarAction.OnDisconnect)
            } else {
                Event(BottomBarAction.Error(R.string.error_occurred))
            }
        }
    }
}