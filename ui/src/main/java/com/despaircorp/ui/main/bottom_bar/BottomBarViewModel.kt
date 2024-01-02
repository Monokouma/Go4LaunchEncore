package com.despaircorp.ui.main.bottom_bar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.despaircorp.domain.firebase_auth.DisconnectUserUseCase
import com.despaircorp.domain.firebase_auth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firestore.GetFirestoreUserAsFlowUseCase
import com.despaircorp.domain.location.GetUserLocationEntityAsFlowUseCase
import com.despaircorp.domain.notifications.CreateNotificationChannelUseCase
import com.despaircorp.domain.workers.EnqueueLaunchNotificationWorker
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomBarViewModel @Inject constructor(
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase,
    private val disconnectUserUseCase: DisconnectUserUseCase,
    private val getFirestoreUserAsFlowUseCase: GetFirestoreUserAsFlowUseCase,
    private val enqueueLaunchNotificationWorker: EnqueueLaunchNotificationWorker,
    private val createNotificationChannelUseCase: CreateNotificationChannelUseCase,
    private val getUserLocationEntityAsFlowUseCase: GetUserLocationEntityAsFlowUseCase
) : ViewModel() {
    private val viewActionMutableLiveData: MutableLiveData<Event<BottomBarAction>> =
        MutableLiveData<Event<BottomBarAction>>()
    val viewAction: LiveData<Event<BottomBarAction>> = viewActionMutableLiveData
    
    init {
        viewModelScope.launch {
            createNotificationChannelUseCase.invoke()
            enqueueLaunchNotificationWorker.invoke()
        }
    }
    
    val viewState: LiveData<BottomBarViewState> = liveData {
        combine(
            getFirestoreUserAsFlowUseCase.invoke(getAuthenticatedUserUseCase.invoke().uid),
            getUserLocationEntityAsFlowUseCase.invoke()
        ) { user, location ->
            emit(
                BottomBarViewState(
                    username = user.displayName,
                    emailAddress = user.mailAddress,
                    userImage = user.picture,
                    userLatLn = location.userLatLng
                )
            )
        }.collect()
        
    }
    
    
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