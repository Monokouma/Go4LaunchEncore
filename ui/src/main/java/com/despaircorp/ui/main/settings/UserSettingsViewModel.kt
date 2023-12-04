package com.despaircorp.ui.main.settings

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.despaircorp.domain.firebase_auth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firebase_auth.UpdateAuthMailAddressUseCase
import com.despaircorp.domain.firebase_auth.UpdateAuthPasswordUseCase
import com.despaircorp.domain.firebase_storage.UpdateUserImageThenGetLinkUseCase
import com.despaircorp.domain.firestore.GetFirestoreUserAsFlowUseCase
import com.despaircorp.domain.firestore.UpdateFirestoreMailAddressUseCase
import com.despaircorp.domain.firestore.UpdateUserImageUseCase
import com.despaircorp.domain.firestore.UpdateUsernameUseCase
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
    private val updateNotificationStateUseCase: UpdateNotificationStateUseCase,
    private val updateAuthMailAddressUseCase: UpdateAuthMailAddressUseCase,
    private val updateFirestoreMailAddressUseCase: UpdateFirestoreMailAddressUseCase,
    private val updateUsernameUseCase: UpdateUsernameUseCase,
    private val updateAuthPasswordUseCase: UpdateAuthPasswordUseCase,
    private val updateUserImageThenGetLinkUseCase: UpdateUserImageThenGetLinkUseCase,
    private val updateUserImageUseCase: UpdateUserImageUseCase,
    
    ) : ViewModel() {
    
    private var newUsername: String? = null
    private var newMailAddress: String? = null
    private var newPassword: String? = null
    
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
    
    fun onUsernameTextChanged(inputUsername: String) {
        newUsername = inputUsername
    }
    
    fun onValidateUsernameButtonClicked() {
        onValidateModification(ARG_USERNAME)
    }
    
    fun onValidateMailButtonClicked() {
        onValidateModification(ARG_MAIL)
    }
    
    fun onValidatePasswordButtonClicked() {
        onValidateModification(ARG_PASSWORD)
    }
    
    fun onMailAddressTextChanged(inputMailAddress: String) {
        newMailAddress = inputMailAddress
    }
    
    fun onPasswordTextChanged(inputPassword: String) {
        newPassword = inputPassword
    }
    
    private fun onValidateModification(from: String) {
        viewModelScope.launch {
            when (from) {
                ARG_USERNAME -> {
                    if (newUsername.isNullOrEmpty()) {
                        //Error
                        viewAction.value =
                            Event(UserSettingsAction.Error(R.string.error_username_empty))
                    } else {
                        if (updateUsernameUseCase.invoke(
                                newUsername ?: return@launch,
                                getAuthenticatedUserUseCase.invoke().uid
                            )
                        ) {
                            viewAction.value = Event(UserSettingsAction.ModificationsSuccess)
                        } else {
                            viewAction.value =
                                Event(UserSettingsAction.Error(R.string.error_occurred))
                        }
                    }
                }
                
                ARG_MAIL -> {
                    if (newMailAddress.isNullOrEmpty()) {
                        //Error
                        viewAction.value =
                            Event(UserSettingsAction.Error(R.string.error_mail_empty))
                        
                    } else {
                        if (updateAuthMailAddressUseCase.invoke(
                                newMailAddress ?: return@launch
                            )
                        ) {
                            if (updateFirestoreMailAddressUseCase.invoke(
                                    getAuthenticatedUserUseCase.invoke().uid,
                                    newMailAddress ?: return@launch
                                )
                            ) {
                                viewAction.value = Event(UserSettingsAction.ModificationsSuccess)
                                
                            } else {
                                viewAction.value =
                                    Event(UserSettingsAction.Error(R.string.error_occurred))
                                
                            }
                        } else {
                            viewAction.value =
                                Event(UserSettingsAction.Error(R.string.error_occurred))
                            
                        }
                        
                    }
                }
                
                ARG_PASSWORD -> {
                    if (newPassword.isNullOrEmpty()) {
                        //Error
                        viewAction.value =
                            Event(UserSettingsAction.Error(R.string.error_password_empty))
                        
                    } else {
                        if (updateAuthPasswordUseCase.invoke(newPassword ?: return@launch)) {
                            viewAction.value =
                                Event(UserSettingsAction.ModificationsSuccess)
                        } else {
                            viewAction.value =
                                Event(UserSettingsAction.Error(R.string.error_occurred))
                        }
                    }
                }
            }
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
    
    fun onUserImageChange(data: Uri) {
        viewModelScope.launch {
            viewAction.value = if (updateUserImageUseCase.invoke(
                    getAuthenticatedUserUseCase.invoke().uid,
                    updateUserImageThenGetLinkUseCase.invoke(
                        getAuthenticatedUserUseCase.invoke().uid,
                        data
                    )
                )
            ) {
                Event(UserSettingsAction.ModificationsSuccess)
                
            } else {
                Event(UserSettingsAction.Error(R.string.error_occurred))
                
            }
            
        }
    }
    
    companion object {
        private const val ARG_USERNAME = "ARG_USERNAME"
        private const val ARG_MAIL = "ARG_MAIL"
        private const val ARG_PASSWORD = "ARG_PASSWORD"
    }
}