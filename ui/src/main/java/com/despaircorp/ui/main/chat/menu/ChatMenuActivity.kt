package com.despaircorp.ui.main.chat.menu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.despaircorp.ui.databinding.ActivityChatBinding
import com.despaircorp.ui.main.chat.details.ChatDetailsActivity
import com.despaircorp.ui.main.chat.menu.messages.ChatMenuMessageAdapter
import com.despaircorp.ui.main.chat.menu.online_users.ChatMenuOnlineUserAdapter
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
        
        val onlineUserAdapter = ChatMenuOnlineUserAdapter(this)
        val messagesAdapter = ChatMenuMessageAdapter(this)
        
        binding.activityChatRecyclerViewUserOnline.adapter = onlineUserAdapter
        binding.activityChatRecyclerViewConv.adapter = messagesAdapter
        
        viewModel.viewState.observe(this) {
            onlineUserAdapter.submitList(it.chatMenuOnlineUserViewStateItems)
            messagesAdapter.submitList(it.chatMessagesViewStateItems)
        }
        
        viewModel.viewAction.observe(this) {
            when (val action = it.getContentIfNotHandled()) {
                
                is ChatMenuAction.Error -> {
                    action.error.showAsToast(this, Toast.LENGTH_SHORT).show()
                    finish()
                }
                
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
        startActivity(
            ChatDetailsActivity.navigate(
                this,
                uid
            )
        )
    }
    
    override fun onConversationClicked(conversationId: String) {
        startActivity(
            ChatDetailsActivity.navigate(
                this,
                conversationId
            )
        )
    }
}