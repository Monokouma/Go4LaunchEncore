package com.despaircorp.ui.login

sealed class LoginAction {
    object GoToWelcome : LoginAction()
    
    data class Error(val message: Int) : LoginAction()
    
    object ChoseUsername : LoginAction()
}