package com.despaircorp.ui.main.restaurants.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.despaircorp.ui.R
import com.despaircorp.ui.databinding.FragmentRestaurantsBinding
import com.despaircorp.ui.utils.viewBinding


class RestaurantsFragment : Fragment(R.layout.fragment_restaurants) {
    private val binding by viewBinding { FragmentRestaurantsBinding.bind(it) }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
    }
}