package com.despaircorp.ui.main.restaurants.list

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.despaircorp.ui.R
import com.despaircorp.ui.databinding.FragmentRestaurantsBinding
import com.despaircorp.ui.main.restaurants.details.RestaurantDetailsActivity
import com.despaircorp.ui.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RestaurantsFragment : Fragment(R.layout.fragment_restaurants), RestaurantsListener {
    private val binding by viewBinding { FragmentRestaurantsBinding.bind(it) }
    private val viewModel: RestaurantsViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val adapter = RestaurantsAdapter(this)
        
        binding.fragmentRestaurantsRecyclerViewRestaurants.adapter = adapter
        
        viewModel.viewState.observe(viewLifecycleOwner) {
            binding.fragmentRestaurantsCircularProgressIndicatorLoading.isVisible =
                it.isSpinnerVisible
            adapter.submitList(it.restaurants)
        }
    }
    
    override fun onRestaurantClick(placeId: String) {
        startActivity(RestaurantDetailsActivity.navigate(requireContext(), placeId))
    }
}