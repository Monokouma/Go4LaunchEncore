package com.despaircorp.ui.main.coworkers

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.despaircorp.ui.R
import com.despaircorp.ui.databinding.FragmentCoworkersBinding
import com.despaircorp.ui.utils.viewBinding

class CoworkersFragment : Fragment(R.layout.fragment_coworkers) {
    private val binding by viewBinding { FragmentCoworkersBinding.bind(it) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
    }
    
}