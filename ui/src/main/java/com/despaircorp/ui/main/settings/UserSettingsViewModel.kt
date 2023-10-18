package com.despaircorp.ui.main.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.despaircorp.domain.firebaseAuth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firestore.GetFirestoreUserAsFlowUseCase
import com.despaircorp.domain.room.IsNotificationsEnabledUseCase
import com.despaircorp.domain.room.model.NotificationsStateEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserSettingsViewModel @Inject constructor(
    private val isNotificationsEnabledUseCase: IsNotificationsEnabledUseCase,
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase,
    private val getFirestoreUserAsFlowUseCase: GetFirestoreUserAsFlowUseCase
) : ViewModel() {
    
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
}