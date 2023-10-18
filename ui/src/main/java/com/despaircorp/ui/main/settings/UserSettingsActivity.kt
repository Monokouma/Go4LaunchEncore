package com.despaircorp.ui.main.settings

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.transition.addListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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

        postponeEnterTransition()



        binding.activityUserSettingsToolbarRoot.setNavigationOnClickListener {
            finish()
        }

        binding.activityUserSettingsSwitchNotif.setOnCheckedChangeListener { _, b ->
            viewModel.onSwitchChanged(b)
        }

        binding.activityUserSettingsTextViewDisplayName.setOnClickListener {

        }

        viewModel.viewState.observe(this) {
            Glide.with(binding.activityUserSettingsImageViewUserImage)
                .load(it.userImageUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        startPostponedEnterTransition()

                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        startPostponedEnterTransition()

                        return false
                    }
                })
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

        window.sharedElementEnterTransition.addListener(
            onEnd = {
                binding.activityUserSettingsImageViewUserImageEdit.startAnimation(
                    AlphaAnimation(0F, 0.7F).apply {
                        interpolator = LinearInterpolator()
                        duration = 300
                    }
                )
            }
        )
    }

    companion object {
        fun navigate(context: Context) = Intent(
            context,
            UserSettingsActivity::class.java
        )
    }
}