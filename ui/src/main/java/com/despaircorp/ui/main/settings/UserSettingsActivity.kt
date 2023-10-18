package com.despaircorp.ui.main.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.despaircorp.ui.R
import com.despaircorp.ui.databinding.ActivityUserSettingsBinding
import com.despaircorp.ui.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserSettingsActivity : AppCompatActivity() {
    private val binding by viewBinding { ActivityUserSettingsBinding.inflate(it) }
    private val viewModel: UserSettingsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.activityUserSettingsToolbarRoot)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        
        binding.activityUserSettingsToolbarRoot.setNavigationOnClickListener {
            finish()
        }
        
        
        supportPostponeEnterTransition()
        
        binding.activityUserSettingsImageViewLogo.viewTreeObserver.addOnPreDrawListener {
            startPostponedEnterTransition()
            true
        }
        binding.activityUserSettingsImageViewUserImage.viewTreeObserver.addOnPreDrawListener {
            startPostponedEnterTransition()
            true
        }
        binding.activityUserSettingsTextViewDisplayName.viewTreeObserver.addOnPreDrawListener {
            startPostponedEnterTransition()
            true
        }
        binding.activityUserSettingsTextViewEmail.viewTreeObserver.addOnPreDrawListener {
            startPostponedEnterTransition()
            true
        }
        
        binding.activityUserSettingsSwitchNotif.setOnCheckedChangeListener { _, b ->
            viewModel.onSwitchChanged(b)
        }
        
        binding.activityUserSettingsTextViewDisplayName.setOnClickListener {
            
        }
        
        viewModel.viewState.observe(this) {
            Glide.with(binding.activityUserSettingsImageViewUserImage).load(it.userImageUrl)
                .into(binding.activityUserSettingsImageViewUserImage)
            binding.activityUserSettingsTextViewDisplayName.text = it.displayName
            binding.activityUserSettingsTextViewEmail.text = it.emailAddress
            binding.activityUserSettingsSwitchNotif.isChecked = it.isNotificationsChecked
        }
        
        viewModel.viewAction.observe(this) {
            when (val action = it.getContentIfNotHandled()) {
                
                is UserSettingsAction.Error -> Toast.makeText(
                    this,
                    getString(action.message),
                    Toast.LENGTH_SHORT
                ).show()
                
                UserSettingsAction.ModificationsSuccess -> Toast.makeText(
                    this,
                    getString(R.string.preferences_saved),
                    Toast.LENGTH_SHORT
                ).show()
                
                else -> Unit
            }
        }
    }
    
    companion object {
        fun navigate(context: Context) = Intent(
            context,
            UserSettingsActivity::class.java
        )
    }
}