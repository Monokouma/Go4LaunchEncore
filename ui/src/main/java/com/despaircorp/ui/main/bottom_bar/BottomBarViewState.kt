package com.despaircorp.ui.main.bottom_bar

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

data class BottomBarViewState(
    val username: String,
    val emailAddress: String,
    val userImage: String,
    val userLatLn: LatLng,
    val yourLunchSentence: String,
    val autoCompleteSearchAreaLatLngBounds: LatLngBounds
)