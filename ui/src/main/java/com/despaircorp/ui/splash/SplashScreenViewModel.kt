package com.despaircorp.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.despaircorp.domain.firebase_auth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firebase_auth.IsUserAlreadyAuthUseCase
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
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val isFirestoreUserExistUseCase: IsFirestoreUserExistUseCase,
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase,
    private val getFirestoreUserUseCase: GetFirestoreUserUseCase,
    private val insertUserInFirestoreUseCase: InsertUserInFirestoreUseCase,
    private val isUserAlreadyAuthUseCase: IsUserAlreadyAuthUseCase,
    private val isNotificationsEnabledUseCase: IsNotificationsEnabledUseCase,
    private val initUserPreferencesUseCase: InitUserPreferencesUseCase,
    private val isUserPreferencesTableExistUseCase: IsUserPreferencesTableExistUseCase,
) : ViewModel() {
    
    
    val viewAction = liveData<Event<SplashScreenAction>> {
        if (isUserAlreadyAuthUseCase.invoke()) {
            if (isFirestoreUserExistUseCase.invoke(getAuthenticatedUserUseCase.invoke().uid)) {
                if (getFirestoreUserUseCase.invoke(getAuthenticatedUserUseCase.invoke().uid).displayName != "none") {
                    if (isUserPreferencesTableExistUseCase.invoke()) { //Table created
                        if (isNotificationsEnabledUseCase.invoke().isNotificationsEnabled == NotificationsStateEnum.NOT_KNOW) {
                            emit(Event(SplashScreenAction.EnableNotifications))
                        } else {
                            emit(Event(SplashScreenAction.GoToWelcome))
                        }
                    } else {
                        if (initUserPreferencesUseCase.invoke(
                                UserPreferencesDomainEntity(
                                    NotificationsStateEnum.NOT_KNOW
                                )
                            )
                        ) {
                            emit(Event(SplashScreenAction.EnableNotifications))
                        } else {
                            emit(Event(SplashScreenAction.Error(R.string.error_occurred)))
                        }
                    }
                } else {
                    emit(Event(SplashScreenAction.ChoseUsername))
                }
            } else {
                //Create user
                if (insertUserInFirestoreUseCase.invoke(getAuthenticatedUserUseCase.invoke())) {
                    if (getFirestoreUserUseCase.invoke(getAuthenticatedUserUseCase.invoke().uid).displayName != "none") {
                        if (isUserPreferencesTableExistUseCase.invoke()) { //Table created
                            if (isNotificationsEnabledUseCase.invoke().isNotificationsEnabled == NotificationsStateEnum.NOT_KNOW) {
                                emit(Event(SplashScreenAction.EnableNotifications))
                            } else {
                                emit(Event(SplashScreenAction.GoToWelcome))
                            }
                        } else {
                            if (initUserPreferencesUseCase.invoke(
                                    UserPreferencesDomainEntity(
                                        NotificationsStateEnum.NOT_KNOW
                                    )
                                )
                            ) {
                                emit(Event(SplashScreenAction.EnableNotifications))
                            } else {
                                
                                emit(Event(SplashScreenAction.Error(R.string.error_occurred)))
                            }
                        }
                    } else {
                        emit(Event(SplashScreenAction.ChoseUsername))
                    }
                } else {
                    emit(Event(SplashScreenAction.Error(R.string.error_occurred)))
                }
                
            }
        } else {
            emit(Event(SplashScreenAction.GoToLogin))
        }
    }
    
    
}






