package com.despaircorp.ui.main.restaurants.details

sealed class RestaurantDetailsAction {
    data class Error(val message: Int) : RestaurantDetailsAction()
}