package com.despaircorp.ui.main.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.showAsToast
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
    
    private val alreadyPresentMarkersForPlaceIds = mutableSetOf<String>()
    private var isCameraPlaced = false
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val customIcon = BitmapDescriptorFactory.fromResource(R.drawable.restaurant)
        
        savedInstanceState?.getStringArrayList("toto")?.let { list ->
            list.forEach { alreadyPresentMarkersForPlaceIds.add(it) }
        }
        
        getMapAsync { googleMap ->
            setGoogleMapOption(googleMap)
            
            viewModel.viewState.observe(viewLifecycleOwner) {
                it.restaurantsCountToast.showAsToast(requireContext())
                
                it.mapViewStateItems.forEach { item ->
                    if (alreadyPresentMarkersForPlaceIds.add(item.placeId)) {
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
                }
                
                if (!isCameraPlaced) {
                    googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            it.userLocation,
                            14f
                        )
                    )
                    isCameraPlaced = true
                }
                
            }
        }
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        
        outState.putStringArrayList("toto", ArrayList(alreadyPresentMarkersForPlaceIds))
    }
    
    private fun setGoogleMapOption(googleMap: GoogleMap) {
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.uiSettings.isZoomControlsEnabled = true
    }
}