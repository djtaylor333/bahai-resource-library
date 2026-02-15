package com.bahairesources.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.LinearLayout
import android.graphics.Color

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create simple UI programmatically
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 100, 50, 50)
            setBackgroundColor(Color.parseColor("#F5F5F5"))
        }
        
        val titleText = TextView(this).apply {
            text = "Bahá'í Resource Library v0.3.1"
            textSize = 24f
            setTextColor(Color.parseColor("#2E4057"))
            setPadding(0, 0, 0, 40)
        }
        
        val subtitleText = TextView(this).apply {
            text = "The earth is but one country, and mankind its citizens. - Bahá'u'lláh"
            textSize = 16f
            setTextColor(Color.parseColor("#5C6B73"))
            setPadding(0, 0, 0, 40)
        }
        
        val descriptionText = TextView(this).apply {
            text = "Welcome to the Bahá'í Resource Library!\n\nThis APK contains references to 28 official Bahá'í texts:\n• Central Figures (10 documents)\n• Administrative Writings (6 documents)\n• Ruhi Institute Books (7 documents)\n• Devotional Materials (2 collections)\n• Study Materials (1 guide)\n• Compilations (2 documents)\n\nFeatures in development:\n• PDF reading with annotations\n• Intelligent search across all texts\n• Bookmark and notes system\n• Material 3 design with nine-pointed star\n\nThis is a working APK build demonstrating the project structure.\nFull functionality coming in future updates!"
            textSize = 14f
            setTextColor(Color.parseColor("#333333"))
        }
        
        val footerText = TextView(this).apply {
            text = "Built with love for the Bahá'í community worldwide"
            textSize = 12f
            setTextColor(Color.parseColor("#888888"))
            setPadding(0, 60, 0, 0)
        }
        
        layout.addView(titleText)
        layout.addView(subtitleText) 
        layout.addView(descriptionText)
        layout.addView(footerText)
        
        setContentView(layout)
    }
}
