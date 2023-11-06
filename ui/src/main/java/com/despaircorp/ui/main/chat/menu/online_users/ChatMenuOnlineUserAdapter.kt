package com.despaircorp.ui.main.chat.menu.online_users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.despaircorp.ui.databinding.OnlineUserItemsBinding
import com.despaircorp.ui.main.chat.menu.ChatMenuListener

class ChatMenuOnlineUserAdapter(
    private val chatListener: ChatMenuListener

) : ListAdapter<ChatMenuOnlineUserViewStateItems, ChatMenuOnlineUserAdapter.ChatUserViewHolder>(
    ChatUserDiffUtil
) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ChatUserViewHolder(
        OnlineUserItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
    
    override fun onBindViewHolder(holder: ChatUserViewHolder, position: Int) {
        holder.bind(getItem(position), chatListener)
    }
    
    class ChatUserViewHolder(private val binding: OnlineUserItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(
            chatUsersViewStateItems: ChatMenuOnlineUserViewStateItems,
            chatListener: ChatMenuListener,
        ) {
            Glide.with(binding.onlineUserItemsShapeableImageViewUserImage)
                .load(chatUsersViewStateItems.pictureUrl)
                .into(binding.onlineUserItemsShapeableImageViewUserImage)
            binding.onlineUserItemsShapeableImageViewUserImage.alpha =
                chatUsersViewStateItems.userImageAlpha
            binding.onlineUserItemsImageViewDot.setImageResource(chatUsersViewStateItems.dotDrawable)
            binding.onlineUserItemsTextViewUserName.text = chatUsersViewStateItems.name
            
            binding.onlineUserItemsShapeableImageViewUserImage.setOnClickListener {
                chatListener.onUserClicked(chatUsersViewStateItems.uid)
            }
        }
    }
    
    object ChatUserDiffUtil : DiffUtil.ItemCallback<ChatMenuOnlineUserViewStateItems>() {
        override fun areItemsTheSame(
            oldItem: ChatMenuOnlineUserViewStateItems,
            newItem: ChatMenuOnlineUserViewStateItems
        ): Boolean =
            oldItem.uid == newItem.uid
        
        override fun areContentsTheSame(
            oldItem: ChatMenuOnlineUserViewStateItems,
            newItem: ChatMenuOnlineUserViewStateItems
        ) = oldItem == newItem
    }
}