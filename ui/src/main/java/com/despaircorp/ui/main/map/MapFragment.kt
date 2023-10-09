package com.despaircorp.ui.main.map

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.despaircorp.ui.R
import com.despaircorp.ui.databinding.FragmentMapBinding
import com.despaircorp.ui.utils.viewBinding


class MapFragment : Fragment(R.layout.fragment_map) {
    private val binding by viewBinding { FragmentMapBinding.bind(it) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
    }
}