package com.bahairesources.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.graphics.Color
import android.util.Log
import android.view.View
import android.content.Intent
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            Log.d("BahaiApp", "MainActivity started")
            createUI()
        } catch (e: Exception) {
            Log.e("BahaiApp", "Error in MainActivity", e)
            createFallbackUI()
        }
    }
    
    private fun createUI() {
        val scrollView = ScrollView(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(30, 50, 30, 50)
            setBackgroundColor(Color.parseColor("#F8F9FA"))
        }
        
        // Header Section
        val headerCard = CardView(this).apply {
            radius = 16f
            cardElevation = 4f
            setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            setPadding(20, 20, 20, 20)
        }
        
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(30, 30, 30, 30)
        }
        
        val titleText = TextView(this).apply {
            text = "Bahá'í Resource Library"
            textSize = 28f
            setTextColor(Color.parseColor("#1976D2"))
            setPadding(0, 0, 0, 10)
        }
        
        val versionText = TextView(this).apply {
            text = "v0.5.0 - Now with Prayers & Calendar!"
            textSize = 14f
            setTextColor(Color.parseColor("#757575"))
            setPadding(0, 0, 0, 20)
        }
        
        val quoteText = TextView(this).apply {
            text = "\"The earth is but one country, and mankind its citizens.\" - Bahá'u'lláh"
            textSize = 16f
            setTextColor(Color.parseColor("#424242"))
            setPadding(0, 0, 0, 0)
        }
        
        headerLayout.addView(titleText)
        headerLayout.addView(versionText)
        headerLayout.addView(quoteText)
        headerCard.addView(headerLayout)
        
        // Search Section
        val searchCard = createFeatureCard(
            "🔍 Search Library",
            "Search across all 28 Bahá'í texts",
            "#4CAF50"
        ) { showSearchInterface() }
        
        // Browse Section
        val browseCard = createFeatureCard(
            "📚 Browse Documents",
            "Explore Central Figures, Administrative Writings, Ruhi Books",
            "#FF9800"
        ) { showBrowseInterface() }
        
        // Reading Section
        val readingCard = createFeatureCard(
            "📖 Continue Reading",
            "Resume your last reading session",
            "#9C27B0"
        ) { showReadingInterface() }
        
        // Bookmarks Section
        val bookmarksCard = createFeatureCard(
            "🔖 My Bookmarks",
            "Access your saved passages and notes",
            "#F44336"
        ) { showBookmarksInterface() }
        
        // Library Stats
        val statsCard = createStatsCard()
        
        // Navigation Section
        val navigationCard = CardView(this).apply {
            radius = 8f
            cardElevation = 3f
            setCardBackgroundColor(Color.parseColor("#FFF3E0"))
        }
        
        val navLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(15, 15, 15, 15)
            gravity = android.view.Gravity.CENTER_HORIZONTAL
        }
        
        val homeBtn = createNavButton("🏠", "Home", true)
        val searchBtn = createNavButton("🔍", "Search") { showSearchInterface() }
        val browseBtn = createNavButton("📚", "Browse") { showBrowseInterface() }
        val prayersBtn = createNavButton("🙏", "Prayers") { showPrayersInterface() }
        val calendarBtn = createNavButton("📅", "Calendar") { showCalendarInterface() }
        val bookmarksBtn = createNavButton("🔖", "Bookmarks") { showBookmarksInterface() }
        
        navLayout.addView(homeBtn)
        navLayout.addView(searchBtn)
        navLayout.addView(browseBtn)
        navLayout.addView(prayersBtn)
        navLayout.addView(calendarBtn)
        navLayout.addView(bookmarksBtn)
        navigationCard.addView(navLayout)
        
        layout.addView(headerCard)
        layout.addView(createSpacing(15))
        layout.addView(navigationCard)
        layout.addView(createSpacing(20))
        layout.addView(searchCard)
        layout.addView(createSpacing(15))
        layout.addView(browseCard)
        layout.addView(createSpacing(15))
        layout.addView(readingCard)
        layout.addView(createSpacing(15))
        layout.addView(bookmarksCard)
        layout.addView(createSpacing(20))
        layout.addView(statsCard)
        
        scrollView.addView(layout)
        setContentView(scrollView)
    }
    
    private fun createFallbackUI() {
        val layout = LinearLayout(this)
        val text = TextView(this).apply {
            text = "Bahá'í Resource Library v0.3.3 - App Loaded Successfully!"
            textSize = 18f
            setPadding(20, 50, 20, 20)
        }
        layout.addView(text)
        setContentView(layout)
    }
    
    private fun createFeatureCard(title: String, description: String, color: String, onClick: () -> Unit): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 6f
            setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            isClickable = true
            isFocusable = true
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(25, 25, 25, 25)
        }
        
        val colorBar = View(this).apply {
            setBackgroundColor(Color.parseColor(color))
            layoutParams = LinearLayout.LayoutParams(8, LinearLayout.LayoutParams.MATCH_PARENT)
        }
        
        val textLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 0, 0, 0)
        }
        
        val titleView = TextView(this).apply {
            text = title
            textSize = 18f
            setTextColor(Color.parseColor("#212121"))
            setPadding(0, 0, 0, 8)
        }
        
        val descView = TextView(this).apply {
            text = description
            textSize = 14f
            setTextColor(Color.parseColor("#757575"))
        }
        
        textLayout.addView(titleView)
        textLayout.addView(descView)
        layout.addView(colorBar)
        layout.addView(textLayout)
        card.addView(layout)
        
        card.setOnClickListener { onClick() }
        return card
    }
    
    private fun createStatsCard(): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(Color.parseColor("#E3F2FD"))
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(25, 25, 25, 25)
        }
        
        val titleView = TextView(this).apply {
            text = "📚 Library Contents"
            textSize = 18f
            setTextColor(Color.parseColor("#1976D2"))
            setPadding(0, 0, 0, 15)
        }
        
        val statsText = TextView(this).apply {
            text = "• Central Figures: 10 documents\\n" +
                  "• Administrative Writings: 6 documents\\n" +
                  "• Ruhi Institute Books: 7 documents\\n" +
                  "• Devotional Materials: 2 collections\\n" +
                  "• Study Materials: 1 guide\\n" +
                  "• Compilations: 2 documents\\n\\n" +
                  "Total: 28 official Bahá'í texts"
            textSize = 14f
            setTextColor(Color.parseColor("#424242"))
        }
        
        layout.addView(titleView)
        layout.addView(statsText)
        card.addView(layout)
        return card
    }
    
    private fun createSpacing(height: Int): View {
        return View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                height
            )
        }
    }
    
    private fun createNavButton(icon: String, label: String, isActive: Boolean = false, onClick: (() -> Unit)? = null): LinearLayout {
        val buttonLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(15, 10, 15, 10)
            gravity = android.view.Gravity.CENTER
            isClickable = onClick != null
            isFocusable = onClick != null
            if (isActive) {
                setBackgroundColor(Color.parseColor("#E3F2FD"))
            }
        }
        
        val iconView = TextView(this).apply {
            text = icon
            textSize = 20f
            gravity = android.view.Gravity.CENTER
        }
        
        val labelView = TextView(this).apply {
            text = label
            textSize = 10f
            setTextColor(if (isActive) Color.parseColor("#1976D2") else Color.parseColor("#666666"))
            gravity = android.view.Gravity.CENTER
            setPadding(0, 4, 0, 0)
        }
        
        buttonLayout.addView(iconView)
        buttonLayout.addView(labelView)
        
        onClick?.let { click ->
            buttonLayout.setOnClickListener { click() }
        }
        
        return buttonLayout
    }
    
    private fun showPrayersInterface() {
        val intent = Intent(this, PrayersActivity::class.java)
        startActivity(intent)
    }
    
    private fun showCalendarInterface() {
        val intent = Intent(this, CalendarActivity::class.java)
        startActivity(intent)
    }
    
    private fun showSearchInterface() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }
    
    private fun showBrowseInterface() {
        val intent = Intent(this, BrowseActivity::class.java)
        startActivity(intent)
    }
    
    private fun showReadingInterface() {
        showFeatureDialog("📖 Reading Interface",
            "Reading interface in development!\n\n" +
            "Features will include:\n" +
            "• PDF and text reading\n" +
            "• Night mode\n" +
            "• Font size adjustment\n" +
            "• Reading progress tracking")
    }
    
    private fun showBookmarksInterface() {
        val intent = Intent(this, BookmarksActivity::class.java)
        startActivity(intent)
    }
    
    private fun showFeatureDialog(title: String, message: String) {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .setNeutralButton("GitHub") { _, _ -> 
                Toast.makeText(this, "Visit our GitHub for updates!", Toast.LENGTH_SHORT).show()
            }
            .create()
        
        dialog.show()
    }
}
