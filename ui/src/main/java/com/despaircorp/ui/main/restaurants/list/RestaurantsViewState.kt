package com.despaircorp.ui.main.restaurants.list

data class RestaurantsViewState(
    val restaurants: List<RestaurantsViewStateItems>,
    val isSpinnerVisible: Boolean,
)