package com.despaircorp.ui.main.coworkers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.despaircorp.ui.databinding.CoworkersListItemsBinding
import com.despaircorp.ui.utils.setTextColorRes

class CoworkersAdapter(
) : ListAdapter<CoworkersViewStateItems, CoworkersAdapter.CoworkersViewHolder>(
    CoworkersDiffUtil
) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CoworkersViewHolder(
        CoworkersListItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
    
    override fun onBindViewHolder(holder: CoworkersViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class CoworkersViewHolder(private val binding: CoworkersListItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(
            coworkersViewStateItems: CoworkersViewStateItems,
        ) {
            
            Glide.with(binding.coworkersListItemsShapeableImageViewCoworkerImage)
                .load(coworkersViewStateItems.picture)
                .into(binding.coworkersListItemsShapeableImageViewCoworkerImage)
            
            binding.coworkersListItemsTextViewCoworkerName.setTextColorRes(coworkersViewStateItems.textColor)
            binding.coworkersListItemsTextViewCoworkerName.text =
                coworkersViewStateItems.eatingMessage
        }
    }
    
    object CoworkersDiffUtil : DiffUtil.ItemCallback<CoworkersViewStateItems>() {
        override fun areItemsTheSame(
            oldItem: CoworkersViewStateItems,
            newItem: CoworkersViewStateItems
        ): Boolean =
            oldItem.id == newItem.id
        
        override fun areContentsTheSame(
            oldItem: CoworkersViewStateItems,
            newItem: CoworkersViewStateItems
        ) = oldItem == newItem
    }
}