package com.despaircorp.ui.welcome

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.despaircorp.domain.firebase_auth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firestore.GetFirestoreUserUseCase
import com.despaircorp.ui.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val getFirestoreUserUseCase: GetFirestoreUserUseCase,
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase,
) : ViewModel() {
    
    val viewAction = MutableLiveData<Event<WelcomeViewAction>>()
    
    val viewState = liveData<WelcomeViewState> {
        emit(
            WelcomeViewState(
                username = getFirestoreUserUseCase
                    .invoke(
                        getAuthenticatedUserUseCase.invoke().uid
                    ).displayName
            )
        )
        
        delay(2.seconds)
        
        viewAction.value = Event(WelcomeViewAction.Continue)
    }
}