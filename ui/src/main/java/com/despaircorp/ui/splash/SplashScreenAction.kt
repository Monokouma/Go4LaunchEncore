package com.despaircorp.ui.splash


sealed class SplashScreenAction {
    object GoToWelcome : SplashScreenAction()
    
    object EnableNotifications : SplashScreenAction()
    
    data class Error(val message: Int) : SplashScreenAction()
    
    object ChoseUsername : SplashScreenAction()
    
    object GoToLogin : SplashScreenAction()
}