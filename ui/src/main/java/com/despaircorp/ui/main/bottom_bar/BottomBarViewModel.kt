package com.despaircorp.ui.main.bottom_bar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.despaircorp.domain.firebaseAuth.DisconnectUserUseCase
import com.despaircorp.domain.firebaseAuth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firestore.GetFirestoreUserUseCase
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomBarViewModel @Inject constructor(
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase,
    private val getFirestoreUserUseCase: GetFirestoreUserUseCase,
    private val disconnectUserUseCase: DisconnectUserUseCase,
    
    ) : ViewModel() {
    
    val viewState = liveData<BottomBarViewState> {
        emit(
            BottomBarViewState(
                username = getFirestoreUserUseCase.invoke(getAuthenticatedUserUseCase.invoke().uid).displayName,
                emailAddress = getFirestoreUserUseCase.invoke(getAuthenticatedUserUseCase.invoke().uid).mailAddress,
                userImage = getFirestoreUserUseCase.invoke(getAuthenticatedUserUseCase.invoke().uid).picture,
            )
        )
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