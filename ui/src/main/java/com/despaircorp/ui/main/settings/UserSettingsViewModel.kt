package com.despaircorp.ui.main.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.despaircorp.domain.firebaseAuth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firestore.GetFirestoreUserAsFlowUseCase
import com.despaircorp.domain.room.IsNotificationsEnabledUseCase
import com.despaircorp.domain.room.UpdateNotificationStateUseCase
import com.despaircorp.domain.room.model.NotificationsStateEnum
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSettingsViewModel @Inject constructor(
    private val isNotificationsEnabledUseCase: IsNotificationsEnabledUseCase,
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase,
    private val getFirestoreUserAsFlowUseCase: GetFirestoreUserAsFlowUseCase,
    private val updateNotificationStateUseCase: UpdateNotificationStateUseCase
) : ViewModel() {
    
    val viewAction = MutableLiveData<Event<UserSettingsAction>>()
    
    val viewState = liveData<UserSettingsViewState> {
        getFirestoreUserAsFlowUseCase.invoke(getAuthenticatedUserUseCase.invoke().uid).collect {
            emit(
                UserSettingsViewState(
                    emailAddress = it.mailAddress,
                    displayName = it.displayName,
                    isNotificationsChecked = isNotificationsEnabledUseCase.invoke().isNotificationsEnabled == NotificationsStateEnum.ENABLED,
                    userImageUrl = it.picture
                )
            )
        }
    }
    
    fun onSwitchChanged(isEnabled: Boolean) {
        viewModelScope.launch {
            viewAction.value = if (updateNotificationStateUseCase.invoke(isEnabled)) {
                Event(UserSettingsAction.ModificationsSuccess)
            } else {
                Event(UserSettingsAction.Error(R.string.error_occurred))
            }
        }
    }
}