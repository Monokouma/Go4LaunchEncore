package com.despaircorp.ui.main.bottom_bar

sealed class BottomBarAction {
    object OnDisconnect : BottomBarAction()
    
    data class Error(val message: Int) : BottomBarAction()
    object SuccessWorker : BottomBarAction()
}