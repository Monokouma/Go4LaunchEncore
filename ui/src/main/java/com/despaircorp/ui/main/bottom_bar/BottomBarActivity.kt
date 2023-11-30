package com.despaircorp.ui.main.bottom_bar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.toAndroidXPair
import androidx.core.view.postDelayed
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.despaircorp.ui.BuildConfig
import com.despaircorp.ui.R
import com.despaircorp.ui.databinding.ActivityBottomBarBinding
import com.despaircorp.ui.databinding.HeaderNavigationDrawerBinding
import com.despaircorp.ui.login.LoginActivity
import com.despaircorp.ui.main.coworkers.CoworkersFragment
import com.despaircorp.ui.main.map.MapFragment
import com.despaircorp.ui.main.restaurants.list.RestaurantsFragment
import com.despaircorp.ui.main.settings.UserSettingsActivity
import com.despaircorp.ui.utils.viewBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BottomBarActivity : AppCompatActivity() {
    private val binding by viewBinding { ActivityBottomBarBinding.inflate(it) }
    private val viewModel: BottomBarViewModel by viewModels()
    
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.activity_bottom_bar_Fragment_autocomplete)
                    as AutocompleteSupportFragment
        changeVisibilityWithAnimation(autocompleteFragment.requireView())
        Places.initialize(this, BuildConfig.MAPS_API_KEY);
        
        
        setSupportActionBar(binding.activityBottomBarToolbarRoot)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        
        
        binding.activityBottomBarToolbarRoot.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search -> {
                    changeVisibilityWithAnimation(autocompleteFragment.requireView())
                }
            }
            
            true
        }
        
        
        val headerBinding = HeaderNavigationDrawerBinding.bind(
            binding.activityBottomBarNavigationViewProfile.getHeaderView(0)
        )
        
        val fade = Fade().apply {
            duration = 2000
        }
        window.enterTransition = fade
        
        loadFragment(MapFragment())
        
        binding.activityBottomBarBottomBar.selectedItemId = savedInstanceState
            ?.getInt(KEY_BOTTOM_NAV_BAR_SELECTED_ITEM_ID, R.id.navigation_map)
            ?: R.id.navigation_map
        
        binding.activityBottomBarToolbarRoot.setNavigationOnClickListener {
            binding.activityBottomBarDrawerLayout.open()
        }
        
        binding.activityBottomBarBottomBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_restaurants -> {
                    loadFragment(RestaurantsFragment())
                    true
                }
                
                R.id.navigation_workmates -> {
                    loadFragment(CoworkersFragment())
                    true
                }
                
                else -> {
                    loadFragment(MapFragment())
                    true
                }
            }
        }
        
        binding.activityBottomBarNavigationViewProfile.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.your_lunch -> {
                    Log.i("Monokouma", "your lunch")
                    binding.activityBottomBarDrawerLayout.close()
                }
                
                R.id.settings -> {
                    val intent = UserSettingsActivity.navigate(this)
                    
                    startActivity(
                        intent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this,
                            Pair(
                                headerBinding.navigationDrawerTextViewUserName,
                                headerBinding.navigationDrawerTextViewUserName.transitionName
                            ).toAndroidXPair(),
                            Pair(
                                headerBinding.navigationDrawerTextViewUserMail,
                                headerBinding.navigationDrawerTextViewUserMail.transitionName
                            ).toAndroidXPair(),
                            Pair(
                                headerBinding.navigationDrawerImageViewUserImage,
                                headerBinding.navigationDrawerImageViewUserImage.transitionName
                            ).toAndroidXPair()
                        ).toBundle()
                    )
                    binding.activityBottomBarNavigationViewProfile.postDelayed(500) {
                        binding.activityBottomBarDrawerLayout.close()
                    }
                }
                
                R.id.logout -> {
                    viewModel.onDisconnectUser()
                    binding.activityBottomBarDrawerLayout.close()
                }
            }
            
            true
        }
        
        
        
        viewModel.viewState.observe(this) {
            Glide.with(headerBinding.navigationDrawerImageViewUserImage).load(it.userImage)
                .into(headerBinding.navigationDrawerImageViewUserImage)
            headerBinding.navigationDrawerTextViewUserMail.text = it.emailAddress
            headerBinding.navigationDrawerTextViewUserName.text = it.username
            
            // Specify the types of place data to return.
            autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
            autocompleteFragment.setTypesFilter(listOf("restaurant"))
            autocompleteFragment.setCountries("FR")
            
            autocompleteFragment.setLocationRestriction(
                RectangularBounds.newInstance(
                    LatLngBounds(
                        calculateBounds(
                            it.userLatLn,
                            5000.0
                        ).second,
                        calculateBounds(
                            it.userLatLn,
                            5000.0
                        ).first
                    )
                )
            )
            
            // Set up a PlaceSelectionListener to handle the response.
            autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    Log.i("Monokouma", "${place.id} ${place.name}")
                }
                
                override fun onError(status: Status) {
                    Log.i("Monokouma", "An error occurred: $status")
                }
            })
        }
        
        viewModel.viewAction.observe(this) {
            when (val action = it.getContentIfNotHandled()) {
                
                is BottomBarAction.Error -> Toast.makeText(
                    this,
                    getString(action.message),
                    Toast.LENGTH_SHORT
                ).show()
                
                BottomBarAction.OnDisconnect -> {
                    startActivity(LoginActivity.navigate(this))
                    finishAffinity()
                }
                
                BottomBarAction.SuccessWorker -> Toast.makeText(
                    this,
                    getString(R.string.success_worker),
                    Toast.LENGTH_SHORT
                ).show()
                
                else -> Unit
            }
        }
        
    }
    
    
    private fun calculateBounds(center: LatLng, distanceInMeters: Double): Pair<LatLng, LatLng> {
        val earthRadius = 6371000.0 // Earth's radius in meters
        
        val lat = Math.toRadians(center.latitude)
        val lon = Math.toRadians(center.longitude)
        
        val distanceInRadians = distanceInMeters / earthRadius
        
        // Calculate new latitude and longitude for each direction
        val north = Math.toDegrees(lat + distanceInRadians)
        val south = Math.toDegrees(lat - distanceInRadians)
        val east = Math.toDegrees(lon + distanceInRadians / Math.cos(lat))
        val west = Math.toDegrees(lon - distanceInRadians / Math.cos(lat))
        
        val northeast = LatLng(north, east)
        val southwest = LatLng(south, west)
        
        return Pair(northeast, southwest)
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        
        outState.putInt(
            KEY_BOTTOM_NAV_BAR_SELECTED_ITEM_ID,
            binding.activityBottomBarBottomBar.selectedItemId
        )
    }
    
    @SuppressLint("CommitTransaction")
    private fun loadFragment(fragment: Fragment) {
        val previousFragment = supportFragmentManager.findFragmentByTag(fragment.javaClass.name)
        if (previousFragment == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(binding.activityBottomBarFrameLayout.id, fragment, fragment.javaClass.name)
                .commit()
        }
    }
    
    private fun changeVisibilityWithAnimation(view: View) {
        val isViewActuallyVisible = view.visibility == View.VISIBLE
        TransitionManager.endTransitions(binding.root)
        TransitionManager.beginDelayedTransition(binding.root)
        if (isViewActuallyVisible) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }
    }
    
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    
    companion object {
        private const val KEY_BOTTOM_NAV_BAR_SELECTED_ITEM_ID =
            "KEY_BOTTOM_NAV_BAR_SELECTED_ITEM_ID"
        
        fun navigate(context: Context) = Intent(
            context,
            BottomBarActivity::class.java
        )
    }
}