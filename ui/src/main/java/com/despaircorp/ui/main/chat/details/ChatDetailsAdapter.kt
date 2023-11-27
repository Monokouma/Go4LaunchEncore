package com.despaircorp.ui.main.chat.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.despaircorp.ui.databinding.ChatItemViewBinding


class ChatDetailsAdapter(
) : ListAdapter<ChatDetailsViewStateItems, ChatDetailsAdapter.ChatDetailViewHolder>(
    ChatDetailDiffUtils
) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ChatDetailViewHolder(
        ChatItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
    
    override fun onBindViewHolder(holder: ChatDetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ChatDetailViewHolder(private val binding: ChatItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(
            chatDetailsViewStateItems: ChatDetailsViewStateItems,
        ) {
            if (!chatDetailsViewStateItems.isOnRight) {
                binding.chatItemViewCardViewReceiverChat.isVisible = false
                binding.chatItemViewCardViewSenderChat.isVisible = true
                binding.chatItemViewTextViewSenderValue.text =
                    chatDetailsViewStateItems.messageValue
            } else {
                binding.chatItemViewCardViewReceiverChat.isVisible = true
                binding.chatItemViewCardViewSenderChat.isVisible = false
                binding.chatItemViewTextViewReceiverValue.text =
                    chatDetailsViewStateItems.messageValue
            }
            
        }
    }
    
    object ChatDetailDiffUtils : DiffUtil.ItemCallback<ChatDetailsViewStateItems>() {
        override fun areItemsTheSame(
            oldItem: ChatDetailsViewStateItems,
            newItem: ChatDetailsViewStateItems
        ): Boolean =
            oldItem.timestamp == newItem.timestamp
        
        override fun areContentsTheSame(
            oldItem: ChatDetailsViewStateItems,
            newItem: ChatDetailsViewStateItems
        ) = oldItem == newItem
    }
}