package com.despaircorp.ui.main.chat.menu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.despaircorp.ui.databinding.ActivityChatBinding
import com.despaircorp.ui.main.chat.details.ChatDetailsActivity
import com.despaircorp.ui.utils.showAsToast
import com.despaircorp.ui.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatMenuActivity : AppCompatActivity(), ChatMenuListener {
    private val binding by viewBinding { ActivityChatBinding.inflate(it) }
    private val viewModel: ChatMenuViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.activityChatToolbarRoot)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        
        binding.activityChatToolbarRoot.setNavigationOnClickListener {
            finish()
        }
        val adapter = ChatMenuOnlineUserAdapter(this)
        
        binding.activityChatRecyclerViewUserOnline.adapter = adapter
        
        viewModel.viewState.observe(this) {
            adapter.submitList(it.chatUserViewStateItems)
        }
        
        viewModel.viewAction.observe(this) {
            when (val action = it.getContentIfNotHandled()) {
                
                is ChatMenuAction.Error -> action.error.showAsToast(this, Toast.LENGTH_SHORT).show()
                is ChatMenuAction.Success -> startActivity(
                    ChatDetailsActivity.navigate(
                        this,
                        action.uid
                    )
                )
                
                else -> Unit
            }
        }
    }
    
    companion object {
        
        fun navigate(context: Context) = Intent(
            context,
            ChatMenuActivity::class.java
        )
    }
    
    override fun onUserClicked(uid: String) {
        viewModel.createConversation(uid)
    }
}