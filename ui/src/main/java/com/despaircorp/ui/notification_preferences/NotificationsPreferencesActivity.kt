package com.despaircorp.ui.notification_preferences

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.despaircorp.ui.R
import com.despaircorp.ui.databinding.ActivityNotificationsPreferencesBinding
import com.despaircorp.ui.utils.viewBinding
import com.despaircorp.ui.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsPreferencesActivity : AppCompatActivity() {
    private val binding by viewBinding { ActivityNotificationsPreferencesBinding.inflate(it) }
    private val viewModel: NotificationsPreferencesViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        
        val fade = Fade().apply {
            excludeTarget(binding.activityNotificationsPreferencesImageViewLogo, true)
            duration = 2000
        }
        window.enterTransition = fade
        window.exitTransition = fade
        
        binding.activityNotificationsPreferencesMaterialButtonNegative.setOnClickListener {
            viewModel.onNotificationStateChanged(false)
        }
        
        binding.activityNotificationsPreferencesMaterialButtonPositive.setOnClickListener {
            viewModel.onNotificationStateChanged(true)
        }
        
        viewModel.viewAction.observe(this) {
            when (val action = it.getContentIfNotHandled()) {
                
                is NotificationsPreferencesAction.Error -> Toast.makeText(
                    this,
                    getString(action.message),
                    Toast.LENGTH_SHORT
                ).show()
                
                NotificationsPreferencesAction.Success -> {
                    Toast.makeText(this, getString(R.string.preferences_saved), Toast.LENGTH_SHORT)
                        .show()
                    startActivity(
                        WelcomeActivity.navigate(this),
                        ActivityOptions.makeSceneTransitionAnimation(
                            this@NotificationsPreferencesActivity,
                            binding.activityNotificationsPreferencesImageViewLogo,
                            "activity_login_ImageView_logo"
                        ).toBundle()
                    )
                }
                
                else -> Unit
            }
        }
    }
    
    companion object {
        
        fun navigate(context: Context) = Intent(
            context,
            NotificationsPreferencesActivity::class.java
        )
    }
}