package com.despaircorp.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.despaircorp.domain.firebase_auth.CreateCredentialsUserUseCase
import com.despaircorp.domain.firebase_auth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firebase_auth.GetCurrentFacebookAccessTokenUseCase
import com.despaircorp.domain.firebase_auth.IsUserAlreadyAuthUseCase
import com.despaircorp.domain.firebase_auth.IsUserWithCredentialsSignedInUseCase
import com.despaircorp.domain.firebase_auth.SignInTokenUserUseCase
import com.despaircorp.domain.firebase_auth.model.AuthenticateUserEntity
import com.despaircorp.domain.firestore.GetFirestoreUserUseCase
import com.despaircorp.domain.firestore.InsertUserInFirestoreUseCase
import com.despaircorp.domain.firestore.IsFirestoreUserExistUseCase
import com.despaircorp.domain.room.InitUserPreferencesUseCase
import com.despaircorp.domain.room.IsNotificationsEnabledUseCase
import com.despaircorp.domain.room.IsUserPreferencesTableExistUseCase
import com.despaircorp.domain.room.model.NotificationsStateEnum
import com.despaircorp.domain.room.model.UserPreferencesDomainEntity
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.Event
import com.facebook.AccessToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInTokenUserUseCase: SignInTokenUserUseCase,
    private val isFirestoreUserExistUseCase: IsFirestoreUserExistUseCase,
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase,
    private val getFirestoreUserUseCase: GetFirestoreUserUseCase,
    private val insertUserInFirestoreUseCase: InsertUserInFirestoreUseCase,
    private val isUserAlreadyAuthUseCase: IsUserAlreadyAuthUseCase,
    private val isUserWithCredentialsSignedInUseCase: IsUserWithCredentialsSignedInUseCase,
    private val createCredentialsUserUseCase: CreateCredentialsUserUseCase,
    private val isNotificationsEnabledUseCase: IsNotificationsEnabledUseCase,
    private val initUserPreferencesUseCase: InitUserPreferencesUseCase,
    private val isUserPreferencesTableExistUseCase: IsUserPreferencesTableExistUseCase,
    private val getCurrentFacebookAccessToken: GetCurrentFacebookAccessTokenUseCase,
) : ViewModel() {
    private var userMailAddress: String? = null
    private var userPassword: String? = null
    
    val viewAction = MutableLiveData<Event<LoginAction>>()
    
    fun onMailTextChanged(mailInput: String) {
        userMailAddress = mailInput
    }
    
    fun onPasswordTextChanged(passwordInput: String) {
        userPassword = passwordInput
    }
    
    fun onConnectWithCredentialsClicked() {
        viewModelScope.launch {
            if (userPassword.isNullOrEmpty() || userMailAddress.isNullOrEmpty()) {
                viewAction.value = Event(LoginAction.Error(R.string.error_credentials_empty))
            } else {
                if (isUserWithCredentialsSignedInUseCase.invoke(
                        userMailAddress ?: return@launch,
                        userPassword ?: return@launch
                    )
                ) {
                    //User already signed in
                    if (isFirestoreUserExistUseCase.invoke(getAuthenticatedUserUseCase.invoke().uid)) {
                        if (getFirestoreUserUseCase.invoke(getAuthenticatedUserUseCase.invoke().uid).displayName != "none") {
                            //Had chose a display name can continue
                            if (isUserPreferencesTableExistUseCase.invoke()) { //Table created
                                if (isNotificationsEnabledUseCase.invoke().isNotificationsEnabled == NotificationsStateEnum.NOT_KNOW) {
                                    viewAction.value = Event(LoginAction.EnableNotifications)
                                } else {
                                    viewAction.value = Event(LoginAction.GoToWelcome)
                                }
                            } else {
                                if (initUserPreferencesUseCase.invoke(
                                        UserPreferencesDomainEntity(
                                            NotificationsStateEnum.NOT_KNOW
                                        )
                                    )
                                ) {
                                    viewAction.value = Event(LoginAction.EnableNotifications)
                                } else {
                                    viewAction.value =
                                        Event(LoginAction.Error(R.string.error_occurred))
                                }
                            }
                            
                        } else {
                            viewAction.value = Event(LoginAction.ChoseUsername)
                        }
                    } else {
                        if (insertUserInFirestoreUseCase.invoke(getAuthenticatedUserUseCase.invoke())) {
                            
                            if (getFirestoreUserUseCase.invoke(getAuthenticatedUserUseCase.invoke().uid).displayName != "none") {
                                //Had chose a display name can continue
                                if (isUserPreferencesTableExistUseCase.invoke()) { //Table created
                                    if (isNotificationsEnabledUseCase.invoke().isNotificationsEnabled == NotificationsStateEnum.NOT_KNOW) {
                                        viewAction.value = Event(LoginAction.EnableNotifications)
                                    } else {
                                        viewAction.value = Event(LoginAction.GoToWelcome)
                                    }
                                } else {
                                    if (initUserPreferencesUseCase.invoke(
                                            UserPreferencesDomainEntity(NotificationsStateEnum.NOT_KNOW)
                                        )
                                    ) {
                                        viewAction.value = Event(LoginAction.EnableNotifications)
                                    } else {
                                        viewAction.value =
                                            Event(LoginAction.Error(R.string.error_occurred))
                                    }
                                }
                            } else {
                                viewAction.value = Event(LoginAction.ChoseUsername)
                            }
                        } else {
                            viewAction.value = Event(LoginAction.Error(R.string.error_occurred))
                        }
                    }
                } else {
                    //Have to sign in
                    if (createCredentialsUserUseCase.invoke(
                            userMailAddress ?: return@launch,
                            userPassword ?: return@launch
                        )
                    ) {
                        if (insertUserInFirestoreUseCase.invoke(getAuthenticatedUserUseCase.invoke())) {
                            
                            if (getFirestoreUserUseCase.invoke(getAuthenticatedUserUseCase.invoke().uid).displayName != "none") {
                                //Had chose a display name can continue
                                if (isUserPreferencesTableExistUseCase.invoke()) { //Table created
                                    if (isNotificationsEnabledUseCase.invoke().isNotificationsEnabled == NotificationsStateEnum.NOT_KNOW) {
                                        viewAction.value = Event(LoginAction.EnableNotifications)
                                    } else {
                                        viewAction.value = Event(LoginAction.GoToWelcome)
                                    }
                                } else {
                                    if (initUserPreferencesUseCase.invoke(
                                            UserPreferencesDomainEntity(NotificationsStateEnum.NOT_KNOW)
                                        )
                                    ) {
                                        viewAction.value = Event(LoginAction.EnableNotifications)
                                    } else {
                                        viewAction.value =
                                            Event(LoginAction.Error(R.string.error_occurred))
                                    }
                                }
                            } else {
                                viewAction.value = Event(LoginAction.ChoseUsername)
                            }
                        } else {
                            viewAction.value = Event(LoginAction.Error(R.string.error_occurred))
                        }
                    } else {
                        viewAction.value = Event(LoginAction.Error(R.string.error_occurred))
                    }
                }
            }
        }
    }
    
    fun onFacebookConnection(token: AccessToken) {
        viewModelScope.launch {
            viewAction.value = if (signInTokenUserUseCase.invoke(token)) {
                val authenticatedUser = getAuthenticatedUserUseCase.invoke()
                if (isFirestoreUserExistUseCase.invoke(authenticatedUser.uid)) {
                    //Check display name
                    
                    if (getFirestoreUserUseCase.invoke(authenticatedUser.uid).displayName != "none") {
                        //Had chose a display name can continue
                        if (isUserPreferencesTableExistUseCase.invoke()) { //Table created
                            if (isNotificationsEnabledUseCase.invoke().isNotificationsEnabled == NotificationsStateEnum.NOT_KNOW) {
                                Event(LoginAction.EnableNotifications)
                            } else {
                                Event(LoginAction.GoToWelcome)
                            }
                        } else if (initUserPreferencesUseCase.invoke(
                                UserPreferencesDomainEntity(
                                    NotificationsStateEnum.NOT_KNOW
                                )
                            )
                        ) {
                            Event(LoginAction.EnableNotifications)
                        } else {
                            Event(LoginAction.Error(R.string.error_occurred))
                        }
                    } else {
                        Event(LoginAction.ChoseUsername)
                    }
                } else {
                    if (insertUserInFirestoreUseCase.invoke(
                            AuthenticateUserEntity(
                                picture = "https://graph.facebook.com${authenticatedUser.picture}?type=large&access_token=${getCurrentFacebookAccessToken.invoke()}",
                                displayName = authenticatedUser.displayName,
                                mailAddress = authenticatedUser.mailAddress,
                                uid = authenticatedUser.uid,
                                currentlyEating = authenticatedUser.currentlyEating,
                                eatingPlaceId = authenticatedUser.eatingPlaceId
                            )
                        )
                    ) {
                        if (getFirestoreUserUseCase.invoke(authenticatedUser.uid).displayName != "none") {
                            //Had chose a display name can continue
                            if (isUserPreferencesTableExistUseCase.invoke()) { //Table created
                                if (isNotificationsEnabledUseCase.invoke().isNotificationsEnabled == NotificationsStateEnum.NOT_KNOW) {
                                    Event(LoginAction.EnableNotifications)
                                } else {
                                    Event(LoginAction.GoToWelcome)
                                }
                            } else if (initUserPreferencesUseCase.invoke(
                                    UserPreferencesDomainEntity(
                                        NotificationsStateEnum.NOT_KNOW
                                    )
                                )
                            ) {
                                Event(LoginAction.EnableNotifications)
                            } else {
                                Event(LoginAction.Error(R.string.error_occurred))
                            }
                        } else {
                            Event(LoginAction.ChoseUsername)
                        }
                    } else {
                        Event(LoginAction.Error(R.string.error_occurred))
                    }
                }
            } else {
                Event(LoginAction.Error(R.string.error_occurred))
            }
        }
    }
}