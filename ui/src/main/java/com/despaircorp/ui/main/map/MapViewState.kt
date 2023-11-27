package com.despaircorp.ui.main.map

import com.despaircorp.ui.utils.NativeText
import com.google.android.gms.maps.model.LatLng

data class MapViewState(
    val mapViewStateItems: List<MapViewStateItems>,
    val userLocation: LatLng,
    val restaurantsCountToast: NativeText,
    val canShowUserLocation: Boolean,
)