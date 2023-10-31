package com.despaircorp.ui.main.coworkers

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.despaircorp.ui.R
import com.despaircorp.ui.databinding.FragmentCoworkersBinding
import com.despaircorp.ui.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoworkersFragment : Fragment(R.layout.fragment_coworkers) {
    private val binding by viewBinding { FragmentCoworkersBinding.bind(it) }
    private val viewModel: CoworkersViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val adapter = CoworkersAdapter()
        
        binding.fragmentCoworkersRecyclerViewCoworkers.adapter = adapter
        
        viewModel.viewState.observe(viewLifecycleOwner) {
            binding.fragmentCoworkersProgressIndicator.isVisible = it.isSpinnerVisible
            adapter.submitList(it.coworkersViewStateItems)
        }
    }
    
}