package com.despaircorp.ui.splash

import android.app.ActivityOptions
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.despaircorp.ui.databinding.ActivitySplashScreenBinding
import com.despaircorp.ui.login.LoginActivity
import com.despaircorp.ui.notification_preferences.NotificationsPreferencesActivity
import com.despaircorp.ui.username.ChoseUsernameActivity
import com.despaircorp.ui.utils.viewBinding
import com.despaircorp.ui.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
    private val binding by viewBinding { ActivitySplashScreenBinding.inflate(it) }
    private val viewModel: SplashScreenViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        
        viewModel.viewAction.observe(this) {
            when (val action = it.getContentIfNotHandled()) {
                
                SplashScreenAction.ChoseUsername -> {
                    startActivity(
                        ChoseUsernameActivity.navigate(this),
                        ActivityOptions.makeSceneTransitionAnimation(
                            this@SplashScreenActivity,
                            binding.activitySplashScreenImageViewLogo,
                            "activity_login_ImageView_logo"
                        ).toBundle()
                    )
                }
                
                SplashScreenAction.EnableNotifications -> {
                    startActivity(
                        NotificationsPreferencesActivity.navigate(this),
                        ActivityOptions.makeSceneTransitionAnimation(
                            this@SplashScreenActivity,
                            binding.activitySplashScreenImageViewLogo,
                            "activity_login_ImageView_logo"
                        ).toBundle()
                    )
                }
                
                is SplashScreenAction.Error -> {
                    Toast.makeText(this, action.message, Toast.LENGTH_SHORT).show()
                }
                
                SplashScreenAction.GoToLogin -> {
                    startActivity(
                        LoginActivity.navigate(this),
                        ActivityOptions.makeSceneTransitionAnimation(
                            this@SplashScreenActivity,
                            binding.activitySplashScreenImageViewLogo,
                            "activity_login_ImageView_logo"
                        ).toBundle()
                    )
                }
                
                SplashScreenAction.GoToWelcome -> {
                    startActivity(
                        WelcomeActivity.navigate(this),
                        ActivityOptions.makeSceneTransitionAnimation(
                            this@SplashScreenActivity,
                            binding.activitySplashScreenImageViewLogo,
                            "activity_login_ImageView_logo"
                        ).toBundle()
                    )
                }
                
                else -> {
                
                }
            }
        }
    }
}