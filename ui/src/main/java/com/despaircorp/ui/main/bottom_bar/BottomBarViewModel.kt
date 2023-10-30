package com.despaircorp.ui.main.bottom_bar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.despaircorp.domain.firebaseAuth.DisconnectUserUseCase
import com.despaircorp.domain.firebaseAuth.GetAuthenticatedUserUseCase
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
    private val getFirestoreUserAsFlowUseCase: GetFirestoreUserAsFlowUseCase
) : ViewModel() {
    
    val viewState = liveData<BottomBarViewState> {
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
    
    val viewAction = MutableLiveData<Event<BottomBarAction>>()
    
    fun onDisconnectUser() {
        viewModelScope.launch {
            viewAction.value = if (disconnectUserUseCase.invoke()) {
                Event(BottomBarAction.OnDisconnect)
            } else {
                Event(BottomBarAction.Error(R.string.error_occurred))
            }
        }
    }
}