package com.despaircorp.ui.main.restaurants.details

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.despaircorp.ui.databinding.ActivityRestaurantDetailsBinding
import com.despaircorp.ui.utils.viewBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RestaurantDetailsActivity : AppCompatActivity() {
    private val binding by viewBinding { ActivityRestaurantDetailsBinding.inflate(it) }
    private val viewModel: RestaurantDetailsViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        
        setSupportActionBar(binding.restaurantDetailsToolBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        
        binding.restaurantDetailsToolBar.setNavigationOnClickListener {
            finish()
        }
        
        
        val snackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_SHORT)
        
        val adapter = RestaurantDetailsCoworkerAdapter()
        binding.restaurantDetailsRecyclerViewCoworker.adapter = adapter
        
        viewModel.viewState.observe(this) { viewState ->
            binding.restaurantDetailsTextViewRestaurantName.text = viewState.name
            binding.restaurantDetailsTextViewRestaurantName.text = viewState.name
            binding.restaurantDetailsRatingBar.rating = viewState.rating?.toFloat() ?: 0f
            Glide.with(binding.restaurantDetailsImageViewRestaurantImage)
                .load(viewState.photoUrl)
                .into(binding.restaurantDetailsImageViewRestaurantImage)
            binding.restaurantDetailsTextViewRestaurantType.text = viewState.vicinity
            adapter.submitList(viewState.coworkersInside)
            
            binding.restaurantDetailsFloatingActionButtonEatHere.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    viewState.fabIcon
                )
            )
            
            
            binding.restaurantDetailsViewWebsite.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(viewState.websiteUrl))
                startActivity(browserIntent)
            }
            
            if (viewState.isSnackBarVisible) {
                snackBar.setText(viewState.snackBarMessage?.toCharSequence(this) ?: return@observe)
                snackBar.setBackgroundTint(getColor(viewState.snackBarColor))
                snackBar.show()
            } else {
                snackBar.dismiss()
            }
            
            binding.restaurantDetailsFloatingActionButtonEatHere.setOnClickListener {
                viewState.onFabClicked.invoke()
            }
            
            binding.restaurantDetailsImageViewStar.setImageResource(viewState.likeIcon)
            
            binding.restaurantDetailsViewStar.setOnClickListener {
                viewState.onLikeClicked.invoke()
            }
            
            binding.restaurantDetailsViewPhone.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + viewState.phoneNumber)
                startActivity(intent)
            }
        }
    }
    
    companion object {
        private const val ARG_PLACE_ID = "ARG_PLACE_ID"
        
        fun navigate(
            context: Context,
            placeId: String
        ) = Intent(
            context,
            RestaurantDetailsActivity::class.java
        ).apply {
            putExtra(ARG_PLACE_ID, placeId)
        }
    }
}