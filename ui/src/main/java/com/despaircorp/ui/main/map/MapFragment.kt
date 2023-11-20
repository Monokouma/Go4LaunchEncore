package com.despaircorp.ui.main.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
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
    
    private var toastHasBeenShowed = false
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val customIcon = BitmapDescriptorFactory.fromResource(R.drawable.restaurant)
        
        savedInstanceState?.getStringArrayList(ARGS_MARKER_ID)?.let { list ->
            list.forEach { alreadyPresentMarkersForPlaceIds.add(it) }
        }
        
        savedInstanceState?.getBoolean(ARGS_IS_CAMERA_PLACED).let {
            isCameraPlaced = it ?: false
        }
        getLocationPermission()
        getMapAsync { googleMap ->
            setGoogleMapOption(googleMap)
            
            viewModel.viewState.observe(viewLifecycleOwner) {
                if (!toastHasBeenShowed) {
                    it.restaurantsCountToast.showAsToast(requireContext())
                    toastHasBeenShowed = true
                }
                
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
        outState.putBoolean(ARGS_IS_CAMERA_PLACED, isCameraPlaced)
        outState.putStringArrayList(ARGS_MARKER_ID, ArrayList(alreadyPresentMarkersForPlaceIds))
    }
    
    private fun getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS
                ),
                0
            )
            return
        }
    }
    
    private fun setGoogleMapOption(googleMap: GoogleMap) {
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.uiSettings.isZoomControlsEnabled = true
    }
    
    companion object {
        private const val ARGS_MARKER_ID = "ARGS_MARKER_ID"
        private const val ARGS_IS_CAMERA_PLACED = "ARGS_IS_CAMERA_PLACED"
        
    }
}