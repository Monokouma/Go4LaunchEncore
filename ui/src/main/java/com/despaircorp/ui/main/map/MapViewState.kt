package com.despaircorp.ui.main.map

import com.google.android.gms.maps.model.LatLng

data class MapViewState(
    val mapViewStateItems: List<MapViewStateItems>,
    val userLocation: LatLng,
    val restaurantsCount: Int
)