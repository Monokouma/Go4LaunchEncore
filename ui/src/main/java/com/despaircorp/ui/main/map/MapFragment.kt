package com.despaircorp.ui.main.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("MissingPermission")
@AndroidEntryPoint
class MapFragment : SupportMapFragment() {
    private val viewModel: MapViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        getMapAsync { googleMap ->
            setGoogleMapOption(googleMap)
            
            viewModel.viewState.observe(viewLifecycleOwner) {
                
                it.mapViewStateItems.forEach { item ->
                    googleMap.addMarker(
                        MarkerOptions()
                            .title(item.name)
                            .position(
                                LatLng(
                                    item.latitude,
                                    item.longitude,
                                )
                            )
                    )
                }
                
                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        it.userLocation,
                        14f
                    )
                )
            }
        }
    }
    
    private fun setGoogleMapOption(googleMap: GoogleMap) {
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.uiSettings.isZoomControlsEnabled = true
    }
}