package com.despaircorp.ui.main.restaurants.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.despaircorp.ui.databinding.CoworkersListItemsBinding

class RestaurantDetailsCoworkerAdapter(
) : ListAdapter<RestaurantDetailsCoworkerViewStateItems, RestaurantDetailsCoworkerAdapter.RestaurantDetailsCoworkersViewHolder>(
    RestaurantDetailsCoworkersDiffUtil
) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RestaurantDetailsCoworkersViewHolder(
            CoworkersListItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    
    override fun onBindViewHolder(holder: RestaurantDetailsCoworkersViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class RestaurantDetailsCoworkersViewHolder(private val binding: CoworkersListItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(
            coworkersViewStateItems: RestaurantDetailsCoworkerViewStateItems,
        ) {
            
            Glide.with(binding.coworkersListItemsShapeableImageViewCoworkerImage)
                .load(coworkersViewStateItems.picture)
                .into(binding.coworkersListItemsShapeableImageViewCoworkerImage)
            
            binding.coworkersListItemsTextViewCoworkerName.text =
                coworkersViewStateItems.coworkerName
        }
    }
    
    object RestaurantDetailsCoworkersDiffUtil :
        DiffUtil.ItemCallback<RestaurantDetailsCoworkerViewStateItems>() {
        override fun areItemsTheSame(
            oldItem: RestaurantDetailsCoworkerViewStateItems,
            newItem: RestaurantDetailsCoworkerViewStateItems
        ): Boolean =
            oldItem.id == newItem.id
        
        override fun areContentsTheSame(
            oldItem: RestaurantDetailsCoworkerViewStateItems,
            newItem: RestaurantDetailsCoworkerViewStateItems
        ): Boolean =
            oldItem == newItem
    }
}