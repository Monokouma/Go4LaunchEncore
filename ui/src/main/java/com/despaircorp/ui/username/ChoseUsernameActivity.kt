package com.despaircorp.ui.username

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.despaircorp.ui.databinding.ActivityChoseUsernameBinding
import com.despaircorp.ui.utils.viewBinding
import com.despaircorp.ui.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChoseUsernameActivity : AppCompatActivity() {
    private val binding by viewBinding { ActivityChoseUsernameBinding.inflate(it) }
    private val viewModel: ChoseUsernameViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        startPostponedEnterTransition()
        
        val fade = Fade().apply {
            excludeTarget(binding.activityChoseUsernameImageViewLogo, true)
            duration = 2000
            
        }
        window.exitTransition = fade
        window.enterTransition = fade
        
        binding.activityChoseUsernameTextInputEditTextUsername.addTextChangedListener {
            viewModel.onUsernameTextChanged(it.toString())
        }
        
        binding.activityChoseUsernameMaterialButtonGo.setOnClickListener {
            viewModel.onContinueButtonClicked()
        }
        
        viewModel.viewAction.observe(this) {
            when (val action = it.getContentIfNotHandled()) {
                ChoseUsernameAction.Continue -> {
                    startActivity(
                        WelcomeActivity.navigate(this),
                        ActivityOptions.makeSceneTransitionAnimation(
                            this@ChoseUsernameActivity,
                            binding.activityChoseUsernameImageViewLogo,
                            "activity_login_ImageView_logo"
                        ).toBundle()
                    )
                    
                    
                }
                
                is ChoseUsernameAction.Error -> Toast.makeText(
                    this,
                    getString(action.message),
                    Toast.LENGTH_SHORT
                ).show()
                
                else -> Unit
            }
        }
    }
    
    
    companion object {
        
        fun navigate(context: Context) = Intent(
            context,
            ChoseUsernameActivity::class.java
        )
    }
}