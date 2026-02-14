package com.bahairesources.library.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bahairesources.library.MainActivity
import com.bahairesources.library.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Splash screen with Bahai nine-pointed star and app branding
 */
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    private val splashTimeOut: Long = 3000 // 3 seconds
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Hide the action bar
        supportActionBar?.hide()
        
        // Animate the nine-pointed star
        animateNinePointedStar()
        
        // Navigate to main activity after delay
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, splashTimeOut)
    }
    
    private fun animateNinePointedStar() {
        // Animate the star with a gentle rotation and scale effect
        binding.ninePointedStar.apply {
            scaleX = 0f
            scaleY = 0f
            alpha = 0f
            
            animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .rotation(360f)
                .setDuration(2000)
                .setStartDelay(500)
                .start()
        }
        
        // Animate the app title
        binding.appTitle.apply {
            alpha = 0f
            translationY = 100f
            
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(1500)
                .setStartDelay(1000)
                .start()
        }
        
        // Animate the subtitle
        binding.appSubtitle.apply {
            alpha = 0f
            translationY = 50f
            
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(1500)
                .setStartDelay(1500)
                .start()
        }
    }
}