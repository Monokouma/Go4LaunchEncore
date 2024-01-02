package com.despaircorp.ui.main.restaurants.details

import com.despaircorp.ui.utils.NativeText

data class RestaurantDetailsViewState(
    val name: String,
    val rating: Double?,
    val photoUrl: String?,
    val vicinity: String,
    val websiteUrl: String?,
    val phoneNumber: String?,
    val coworkersInside: List<RestaurantDetailsCoworkerViewStateItems>,
    val isSnackBarVisible: Boolean,
    val snackBarMessage: NativeText?,
    val snackBarColor: Int,
    val fabIcon: Int,
    val likeIcon: Int,
    val onFabClicked: () -> Unit,
    val onLikeClicked: () -> Unit
)