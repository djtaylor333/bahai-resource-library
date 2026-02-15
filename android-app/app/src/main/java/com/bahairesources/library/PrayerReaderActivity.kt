package com.bahairesources.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.graphics.Color
import android.graphics.Typeface
import androidx.cardview.widget.CardView
import android.view.View

class PrayerReaderActivity : AppCompatActivity() {
    
    private lateinit var prayerTextView: TextView
    private var currentTextSize = 16f
    private var isDarkMode = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize dark mode from ThemeManager
        isDarkMode = ThemeManager.isDarkMode(this)
        
        val title = intent.getStringExtra("prayer_title") ?: "Prayer"
        val author = intent.getStringExtra("prayer_author") ?: "Unknown"
        val prayerText = intent.getStringExtra("prayer_text") ?: "Prayer text not available"
        
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, 0)
            setBackgroundColor(if (isDarkMode) Color.parseColor("#121212") else Color.parseColor("#FAFAFA"))
        }
        
        // Header with controls
        val headerCard = createHeader(title)
        
        // Prayer content area
        val contentCard = CardView(this).apply {
            radius = 0f
            cardElevation = 0f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.WHITE)
        }
        
        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(30, 40, 30, 40)
        }
        
        // Author attribution
        val authorView = TextView(this).apply {
            text = "— Author: $author"
            textSize = 14f
            setTextColor(Color.parseColor("#666666"))
            gravity = android.view.Gravity.CENTER
            setPadding(0, 0, 0, 30)
        }
        
        // Prayer text
        prayerTextView = TextView(this).apply {
            text = getFormattedPrayerText(prayerText)
            textSize = currentTextSize
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            setLineSpacing(8f, 1.4f)
            setPadding(0, 0, 0, 30)
        }
        
        // Sample note about official sources
        val noteView = TextView(this).apply {
            text = "This is a sample prayer structure. For complete official prayers, please visit bahai.org/library or consult authorized Bahá'í publications."
            textSize = 12f
            setTextColor(if (isDarkMode) Color.parseColor("#888888") else Color.parseColor("#666666"))
            setBackgroundColor(if (isDarkMode) Color.parseColor("#2C2C2C") else Color.parseColor("#F5F5F5"))
            setPadding(20, 15, 20, 15)
            setTypeface(typeface, Typeface.ITALIC)
        }
        
        contentLayout.addView(authorView)
        contentLayout.addView(prayerTextView)
        contentLayout.addView(noteView)
        contentCard.addView(contentLayout)
        
        mainLayout.addView(headerCard)
        mainLayout.addView(contentCard)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
    
    private fun createHeader(title: String): CardView {
        val headerCard = CardView(this).apply {
            radius = 0f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#2C2C2C") else Color.parseColor("#1976D2"))
        }
        
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 30, 20, 20)
        }
        
        // Top row with back button and controls
        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        
        val backButton = Button(this).apply {
            text = "← Back"
            setBackgroundColor(Color.parseColor("#1565C0"))
            setTextColor(Color.WHITE)
            setPadding(20, 10, 20, 10)
            textSize = 14f
            setOnClickListener { finish() }
        }
        
        val spacer = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, 0, 1f)
        }
        
        val modeButton = Button(this).apply {
            text = if (isDarkMode) "☀️" else "🌙"
            setBackgroundColor(Color.parseColor("#FF9800"))
            setTextColor(Color.WHITE)
            setPadding(15, 10, 15, 10)
            textSize = 14f
            setOnClickListener { toggleMode() }
        }
        
        topRow.addView(backButton)
        topRow.addView(spacer)
        topRow.addView(modeButton)
        
        // Title
        val titleView = TextView(this).apply {
            text = title
            textSize = 20f
            setTextColor(Color.WHITE)
            setPadding(0, 20, 0, 10)
            setTypeface(typeface, Typeface.BOLD)
        }
        
        // Text size controls
        val controlsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 15, 0, 0)
        }
        
        val sizeLabel = TextView(this).apply {
            text = "Text Size: "
            textSize = 12f
            setTextColor(Color.WHITE)
        }
        
        val minusButton = Button(this).apply {
            text = "A-"
            setBackgroundColor(Color.parseColor("#1565C0"))
            setTextColor(Color.WHITE)
            setPadding(15, 5, 15, 5)
            textSize = 12f
            setOnClickListener { adjustTextSize(-2f) }
        }
        
        val plusButton = Button(this).apply {
            text = "A+"
            setBackgroundColor(Color.parseColor("#1565C0"))
            setTextColor(Color.WHITE)
            setPadding(15, 5, 15, 5)
            textSize = 12f
            setOnClickListener { adjustTextSize(2f) }
        }
        
        controlsLayout.addView(sizeLabel)
        controlsLayout.addView(minusButton)
        controlsLayout.addView(View(this).apply { 
            layoutParams = LinearLayout.LayoutParams(20, 0) 
        })
        controlsLayout.addView(plusButton)
        
        headerLayout.addView(topRow)
        headerLayout.addView(titleView)
        headerLayout.addView(controlsLayout)
        
        headerCard.addView(headerLayout)
        return headerCard
    }
    
    private fun getFormattedPrayerText(text: String): String {
        return when {
            text.contains("Short Obligatory Prayer") -> {
                "I bear witness, O my God, that Thou hast created me to know Thee and to worship Thee. I testify, at this moment, to my powerlessness and to Thy might, to my poverty and to Thy wealth.\\n\\nThere is none other God but Thee, the Help in Peril, the Self-Subsisting.\\n\\n[Complete prayer available in official publications]"
            }
            text.contains("Remover of Difficulties") -> {
                "Is there any Remover of difficulties save God? Say: Praise be God! He is God! All are His servants, and all abide by His bidding!\\n\\n[This prayer may be repeated as desired]"
            }
            text.contains("morning") -> {
                "O God! Refresh and gladden my spirit. Purify my heart. Illuminate my powers.\\n\\nI lay all my affairs in Thy hand. Thou art my Guide and my Refuge. I will no longer be sorrowful and grieved; I will be a happy and joyful being.\\n\\n[Sample morning prayer structure]"
            }
            text.contains("evening") -> {
                "Praised be Thou, O Lord my God! The night has drawn nigh unto its close, and the day-star of Thy Unity has shone forth above the horizon of Thy will.\\n\\nI pray Thee, by Thy Name through which Thou didst lift up the ensigns of Thy Cause...\\n\\n[Sample evening prayer structure]"
            }
            else -> {
                "$text\\n\\n[This is a sample prayer structure. Complete official prayers are available through:\\n• bahai.org/library\\n• Official Bahá'í publications\\n• Local Bahá'í centers]"
            }
        }
    }
    
    private fun adjustTextSize(delta: Float) {
        currentTextSize = (currentTextSize + delta).coerceIn(12f, 28f)
        prayerTextView.textSize = currentTextSize
        
        Toast.makeText(this, "Text size: ${currentTextSize.toInt()}sp", Toast.LENGTH_SHORT).show()
    }
    
    private fun toggleMode() {
        isDarkMode = !isDarkMode
        Toast.makeText(this, "${if (isDarkMode) "Dark" else "Light"} mode - full implementation coming soon!", Toast.LENGTH_SHORT).show()
    }
}