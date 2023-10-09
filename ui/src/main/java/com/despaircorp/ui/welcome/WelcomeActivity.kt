package com.despaircorp.ui.welcome

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.despaircorp.ui.databinding.ActivityWelcomeBinding
import com.despaircorp.ui.main.bottom_bar.BottomBarActivity
import com.despaircorp.ui.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeActivity : AppCompatActivity() {
    private val binding by viewBinding { ActivityWelcomeBinding.inflate(it) }
    private val viewModel: WelcomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val fade = Fade().apply {
            excludeTarget(binding.activityWelcomeImageViewLogo, true)
            duration = 2000
        }
        window.enterTransition = fade
        window.exitTransition = fade
        
        viewModel.viewState.observe(this) {
            binding.activityWelcomeTextViewUsernameName.text = it.username
        }
        
        viewModel.viewAction.observe(this) {
            when (it.getContentIfNotHandled()) {
                WelcomeViewAction.Continue -> {
                    startActivity(
                        BottomBarActivity.navigate(this),
                        ActivityOptions.makeSceneTransitionAnimation(
                            this@WelcomeActivity,
                            binding.activityWelcomeImageViewLogo,
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
            WelcomeActivity::class.java
        )
    }
}