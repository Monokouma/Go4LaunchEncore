package com.despaircorp.ui.main.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.despaircorp.ui.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint


@SuppressLint("MissingPermission")
@AndroidEntryPoint
class MapFragment : SupportMapFragment() {
    private val viewModel: MapViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val customIcon = BitmapDescriptorFactory.fromResource(R.drawable.restaurant)
        
        getMapAsync { googleMap ->
            setGoogleMapOption(googleMap)
            
            viewModel.viewState.observe(viewLifecycleOwner) {
                Toast.makeText(
                    requireContext(),
                    StringBuilder().append(getString(R.string.nearby_restaurants_count)).append(" ")
                        .append(it.restaurantsCount),
                    Toast.LENGTH_SHORT
                ).show()
                
                it.mapViewStateItems.forEach { item ->
                    googleMap.addMarker(
                        MarkerOptions()
                            .title(item.name)
                            .icon(customIcon)
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