package com.despaircorp.ui.main.bottom_bar

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.transition.Fade
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.despaircorp.ui.R
import com.despaircorp.ui.databinding.ActivityBottomBarBinding
import com.despaircorp.ui.databinding.HeaderNavigationDrawerBinding
import com.despaircorp.ui.login.LoginActivity
import com.despaircorp.ui.main.coworkers.CoworkersFragment
import com.despaircorp.ui.main.map.MapFragment
import com.despaircorp.ui.main.restaurants.list.RestaurantsFragment
import com.despaircorp.ui.main.settings.UserSettingsActivity
import com.despaircorp.ui.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BottomBarActivity : AppCompatActivity() {
    private val binding by viewBinding { ActivityBottomBarBinding.inflate(it) }
    private val viewModel: BottomBarViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        
        val headerBinding = HeaderNavigationDrawerBinding.bind(
            binding.activityBottomBarNavigationViewProfile.getHeaderView(0)
        )
        
        val fade = Fade().apply {
            duration = 2000
        }
        window.enterTransition = fade
        
        setSupportActionBar(binding.activityBottomBarToolbarRoot)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        
        getLocationPermission()
        
        loadFragment(MapFragment())
        
        binding.activityBottomBarBottomBar.selectedItemId =
            savedInstanceState?.getInt(KEY_BOTTOM_NAV_BAR_SELECTED_ITEM_ID, R.id.navigation_map)
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
                R.id.your_lunch -> Log.i("Monokouma", "your lunch")
                R.id.settings -> {
                    val intent = UserSettingsActivity.navigate(this).apply {
                        putExtra(
                            binding.activityBottomBarImageViewLogo.transitionName,
                            binding.activityBottomBarImageViewLogo.transitionName
                        )
                        putExtra(
                            headerBinding.navigationDrawerImageViewUserImage.transitionName,
                            headerBinding.navigationDrawerImageViewUserImage.transitionName
                        )
                        putExtra(
                            headerBinding.navigationDrawerTextViewUserName.transitionName,
                            headerBinding.navigationDrawerTextViewUserName.transitionName
                        )
                        putExtra(
                            headerBinding.navigationDrawerTextViewUserMail.transitionName,
                            headerBinding.navigationDrawerTextViewUserMail.transitionName
                        )
                        
                    }
                    
                    startActivity(
                        intent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
                    )
                    
                }
                
                R.id.logout -> {
                    viewModel.onDisconnectUser()
                }
            }
            binding.activityBottomBarDrawerLayout.close()
            true
        }
        
        
        
        viewModel.viewState.observe(this) {
            Glide.with(headerBinding.navigationDrawerImageViewUserImage).load(it.userImage)
                .into(headerBinding.navigationDrawerImageViewUserImage)
            headerBinding.navigationDrawerTextViewUserMail.text = it.emailAddress
            headerBinding.navigationDrawerTextViewUserName.text = it.username
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
    
    private fun getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
            return
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