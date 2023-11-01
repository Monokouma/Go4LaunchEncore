package com.despaircorp.ui.main.chat.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.despaircorp.ui.databinding.ActivityChatDetailsBinding
import com.despaircorp.ui.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatDetailsActivity : AppCompatActivity() {
    private val binding by viewBinding { ActivityChatDetailsBinding.inflate(it) }
    private val viewModel: ChatDetailsViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.activityChatDetailsToolbarRoot)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.activityChatDetailsToolbarRoot.setNavigationOnClickListener {
            finish()
        }
        
        binding.activityChatDetailsTextInputEditChat.addTextChangedListener {
            viewModel.onChatTextChanged(it.toString())
        }
        
        viewModel.viewState.observe(this) {
            binding.activityChatDetailsTextViewUserName.text = it.receiverName
            Glide.with(binding.activityChatDetailsShapeableImageViewUserImage)
                .load(it.receiverPicture)
                .into(binding.activityChatDetailsShapeableImageViewUserImage)
            binding.activityChatDetailsImageViewSend.setImageResource(it.sendImageResources)
            binding.activityChatDetailsImageViewDot.setImageResource(it.onlineDotResources)
        }
    }
    
    companion object {
        private const val ARG_TARGET_USER_UID = "ARG_TARGET_USER_UID"
        fun navigate(context: Context, targetUid: String) = Intent(
            context,
            ChatDetailsActivity::class.java
        ).apply {
            putExtra(ARG_TARGET_USER_UID, targetUid)
        }
    }
}