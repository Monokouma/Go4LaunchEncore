package com.despaircorp.ui.main.chat.menu.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.despaircorp.ui.databinding.ExstingConversationItemsBinding
import com.despaircorp.ui.main.chat.menu.ChatMenuListener


class ChatMenuMessageAdapter(
    private val chatListener: ChatMenuListener

) : ListAdapter<ChatMenuMessagesViewStateItems, ChatMenuMessageAdapter.ChatMenuMessageViewHolder>(
    ChatMenuMessageDiffUtil
) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ChatMenuMessageViewHolder(
        ExstingConversationItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
    
    override fun onBindViewHolder(holder: ChatMenuMessageViewHolder, position: Int) {
        holder.bind(getItem(position), chatListener)
    }
    
    class ChatMenuMessageViewHolder(private val binding: ExstingConversationItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(
            chatUsersViewStateItems: ChatMenuMessagesViewStateItems,
            chatListener: ChatMenuListener,
        ) {
            Glide.with(binding.exstingConversationItemsShapeableImageViewUserImage)
                .load(chatUsersViewStateItems.receiverImage)
                .into(binding.exstingConversationItemsShapeableImageViewUserImage)
            
            binding.exstingConversationItemsTextViewLastMessage.text =
                chatUsersViewStateItems.message
            binding.exstingConversationItemsTextViewUsername.text =
                chatUsersViewStateItems.receiverName
            
            binding.root.setOnClickListener {
                chatListener.onConversationClicked(chatUsersViewStateItems.receiverId)
            }
        }
    }
    
    object ChatMenuMessageDiffUtil : DiffUtil.ItemCallback<ChatMenuMessagesViewStateItems>() {
        override fun areItemsTheSame(
            oldItem: ChatMenuMessagesViewStateItems,
            newItem: ChatMenuMessagesViewStateItems
        ): Boolean =
            oldItem.convId == newItem.convId
        
        override fun areContentsTheSame(
            oldItem: ChatMenuMessagesViewStateItems,
            newItem: ChatMenuMessagesViewStateItems
        ) = oldItem == newItem
    }
}


