package com.despaircorp.ui.main.bottom_bar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.toAndroidXPair
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.despaircorp.ui.R
import com.despaircorp.ui.databinding.ActivityBottomBarBinding
import com.despaircorp.ui.databinding.HeaderNavigationDrawerBinding
import com.despaircorp.ui.login.LoginActivity
import com.despaircorp.ui.main.coworkers.CoworkersFragment
import com.despaircorp.ui.main.map.MapFragment
import com.despaircorp.ui.main.restaurants.details.RestaurantDetailsActivity
import com.despaircorp.ui.main.restaurants.list.RestaurantsFragment
import com.despaircorp.ui.main.settings.UserSettingsActivity
import com.despaircorp.ui.utils.viewBinding
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BottomBarActivity : AppCompatActivity() {
    private val binding by viewBinding { ActivityBottomBarBinding.inflate(it) }
    private val viewModel: BottomBarViewModel by viewModels()
    
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        
        setSupportActionBar(binding.activityBottomBarToolbarRoot)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        
        val headerBinding = HeaderNavigationDrawerBinding.bind(
            binding.activityBottomBarNavigationViewProfile.getHeaderView(0)
        )
        
        val fade = Fade().apply {
            duration = 2000
        }
        
        window.enterTransition = fade
        
        loadFragment(MapFragment())
        
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment
        
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
        autocompleteFragment.setTypesFilter(listOf("restaurant"))
        
        
        binding.activityBottomBarImageViewToggleSearch.setOnClickListener {
            
            changeVisibilityWithAnimation(binding.activityBottomBarConstraintLayoutAutoComplete)
            changeVisibilityWithAnimation(binding.activityBottomBarImageViewLogo)
            
            if (binding.activityBottomBarConstraintLayoutAutoComplete.isVisible) {
                binding.activityBottomBarImageViewToggleSearch.setImageResource(R.drawable.close)
            } else {
                binding.activityBottomBarImageViewToggleSearch.setImageResource(R.drawable.baseline_search_24)
            }
        }
        
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
                    if (!binding.activityBottomBarImageViewToggleSearch.isVisible) {
                        changeVisibilityWithAnimation(binding.activityBottomBarImageViewToggleSearch)
                    }
                    true
                }
                
                R.id.navigation_workmates -> {
                    loadFragment(CoworkersFragment())
                    if (binding.activityBottomBarImageViewToggleSearch.isVisible) {
                        changeVisibilityWithAnimation(binding.activityBottomBarImageViewToggleSearch)
                    }
                    true
                }
                
                else -> {
                    loadFragment(MapFragment())
                    if (!binding.activityBottomBarImageViewToggleSearch.isVisible) {
                        changeVisibilityWithAnimation(binding.activityBottomBarImageViewToggleSearch)
                    }
                    true
                }
            }
        }
        
        viewModel.viewState.observe(this) {
            
            Glide.with(headerBinding.navigationDrawerImageViewUserImage).load(it.userImage)
                .into(headerBinding.navigationDrawerImageViewUserImage)
            headerBinding.navigationDrawerTextViewUserMail.text = it.emailAddress
            headerBinding.navigationDrawerTextViewUserName.text = it.username
            autocompleteFragment.setMenuVisibility(false)
            autocompleteFragment.setLocationBias(RectangularBounds.newInstance(it.autoCompleteSearchAreaLatLngBounds))
            
            autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    startActivity(
                        RestaurantDetailsActivity.navigate(
                            this@BottomBarActivity,
                            placeId = place.id ?: return
                        )
                    )
                    
                    binding.activityBottomBarConstraintLayoutAutoComplete.isVisible =
                        !binding.activityBottomBarConstraintLayoutAutoComplete.isVisible
                    binding.activityBottomBarImageViewLogo.isVisible =
                        !binding.activityBottomBarConstraintLayoutAutoComplete.isVisible
                    
                    if (binding.activityBottomBarConstraintLayoutAutoComplete.isVisible) {
                        binding.activityBottomBarImageViewToggleSearch.setImageResource(R.drawable.close)
                    } else {
                        binding.activityBottomBarImageViewToggleSearch.setImageResource(R.drawable.baseline_search_24)
                    }
                }
                
                override fun onError(status: Status) {
                    Toast.makeText(this@BottomBarActivity, status.statusMessage, Toast.LENGTH_SHORT)
                        .show()
                }
            })
            
            binding.activityBottomBarNavigationViewProfile.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.your_lunch -> {
                        makeSnackBar(it.yourLunchSentence)
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
                
                else -> Unit
            }
        }
        
    }
    
    private fun makeSnackBar(username: String) {
        val snackBar = Snackbar.make(binding.root, username, Snackbar.LENGTH_INDEFINITE)
            .setActionTextColor(getColor(R.color.dark_purple))
            .setTextColor(getColor(R.color.baby_powder))
        
        snackBar.setAction(getString(R.string.dismiss)) {
            snackBar.dismiss()
        }
        
        snackBar.show()
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
    
    companion object {
        private const val KEY_BOTTOM_NAV_BAR_SELECTED_ITEM_ID =
            "KEY_BOTTOM_NAV_BAR_SELECTED_ITEM_ID"
        
        fun navigate(context: Context) = Intent(
            context,
            BottomBarActivity::class.java
        )
    }
}