package com.despaircorp.ui.main.bottom_bar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.toAndroidXPair
import androidx.core.view.postDelayed
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
    
    @SuppressLint("MissingPermission")
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
    
    
    companion object {
        private const val KEY_BOTTOM_NAV_BAR_SELECTED_ITEM_ID =
            "KEY_BOTTOM_NAV_BAR_SELECTED_ITEM_ID"
        
        fun navigate(context: Context) = Intent(
            context,
            BottomBarActivity::class.java
        )
    }
}