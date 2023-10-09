package com.despaircorp.ui.main.bottom_bar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import androidx.appcompat.app.AppCompatActivity
import com.despaircorp.ui.databinding.ActivityBottomBarBinding
import com.despaircorp.ui.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomBarActivity : AppCompatActivity() {
    private val binding by viewBinding { ActivityBottomBarBinding.inflate(it) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val fade = Fade().apply {
            duration = 2000
        }
        window.enterTransition = fade
        
    }
    
    companion object {
        
        fun navigate(context: Context) = Intent(
            context,
            BottomBarActivity::class.java
        )
    }
}