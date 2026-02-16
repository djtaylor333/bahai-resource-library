package com.bahairesources.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.graphics.Color
import android.util.Log
import android.view.View
import android.content.Intent
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.button.MaterialButton
import android.util.TypedValue

class MainActivity : AppCompatActivity() {
    
    private var isDarkMode = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            // Initialize theme from SettingsManager
            isDarkMode = SettingsManager.isDarkMode(this)
            Log.d("BahaiApp", "MainActivity started")
            createUI()
        } catch (e: Exception) {
            Log.e("BahaiApp", "Error in MainActivity", e)
            createFallbackUI()
        }
    }
    
    /**
     * Helper function to get theme colors using Material Design 3 attributes
     */
    private fun getThemeColor(attr: Int): Int {
        val typedValue = TypedValue()
        val theme = this.theme
        theme.resolveAttribute(attr, typedValue, true)
        return ContextCompat.getColor(this, typedValue.resourceId)
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh theme in case it was changed in settings
        val currentDarkMode = SettingsManager.isDarkMode(this)
        if (currentDarkMode != isDarkMode) {
            isDarkMode = currentDarkMode
            recreate() // Recreate activity with new theme
        }
    }
    
    private fun createUI() {
        val scrollView = ScrollView(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val screenMargin = resources.getDimensionPixelSize(R.dimen.screen_margin_large)
            setPadding(screenMargin, screenMargin, screenMargin, screenMargin)
            setBackgroundColor(if (isDarkMode) Color.parseColor("#1D1B20") else Color.parseColor("#FEF7FF"))
        }
        
        // Header Section - Using Material Card
        val headerCard = MaterialCardView(this).apply {
            radius = resources.getDimensionPixelSize(R.dimen.card_corner_radius_large).toFloat()
            cardElevation = resources.getDimensionPixelSize(R.dimen.card_elevation_resting).toFloat()
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1D1B20") else Color.parseColor("#FFFFFF"))
            val cardPadding = resources.getDimensionPixelSize(R.dimen.card_content_padding)
            setPadding(cardPadding, cardPadding, cardPadding, cardPadding)
            useCompatPadding = true
        }
        
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val cardPadding = resources.getDimensionPixelSize(R.dimen.card_content_padding)
            setPadding(cardPadding, cardPadding, cardPadding, cardPadding)
        }
        
        val titleText = TextView(this).apply {
            text = "Bahá'í Resource Library"
            setTextAppearance(R.style.TextAppearance_App_HeadlineMedium)
            setTextColor(if (isDarkMode) Color.parseColor("#D0BCFF") else Color.parseColor("#6750A4"))
            val bottomMargin = resources.getDimensionPixelSize(R.dimen.spacing_sm)
            setPadding(0, 0, 0, bottomMargin)
        }
        
        val versionText = TextView(this).apply {
            text = "v0.11.0 - Improved UX & Enhanced Functionality!"
            setTextAppearance(R.style.TextAppearance_App_BodyMedium)
            setTextColor(if (isDarkMode) Color.parseColor("#CAC4D0") else Color.parseColor("#49454F"))
            val bottomMargin = resources.getDimensionPixelSize(R.dimen.spacing_lg)
            setPadding(0, 0, 0, bottomMargin)
        }
        
        val quoteText = TextView(this).apply {
            text = "\"The earth is but one country, and mankind its citizens.\" - Bahá'u'lláh"
            setTextAppearance(R.style.TextAppearance_App_Quote)
            setTextColor(if (isDarkMode) Color.parseColor("#E6E0E9") else Color.parseColor("#1D1B20"))
            setPadding(0, 0, 0, 0)
        }
        
        headerLayout.addView(titleText)
        headerLayout.addView(versionText)
        headerLayout.addView(quoteText)
        headerCard.addView(headerLayout)
        
        // Theme Toggle Section - Using Material Design components
        // Remove theme display card - dark mode toggle moved to navigation
        
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
            radius = 12f
            cardElevation = if (isDarkMode) 6f else 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#2D2D2D") else Color.parseColor("#FFF3E0"))
        }
        
        val navLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(15, 15, 15, 15)
        }
        
        // Main navigation row
        val mainNavRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_HORIZONTAL
        }
        
        val homeBtn = createNavButton("🏠", "Home", true)
        val searchBtn = createNavButton("🔍", "Search") { showSearchInterface() }
        val browseBtn = createNavButton("📚", "Browse") { showBrowseInterface() }
        val prayersBtn = createNavButton("🙏", "Prayers") { showPrayersInterface() }
        val calendarBtn = createNavButton("📅", "Calendar") { showCalendarInterface() }
        val bookmarksBtn = createNavButton("🔖", "Bookmarks") { showBookmarksInterface() }
        
        mainNavRow.addView(homeBtn)
        mainNavRow.addView(searchBtn)
        mainNavRow.addView(browseBtn)
        mainNavRow.addView(prayersBtn)
        mainNavRow.addView(calendarBtn)
        mainNavRow.addView(bookmarksBtn)
        
        // Additional features row
        val featuresRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_HORIZONTAL
            setPadding(0, 10, 0, 0)
        }
        
        val feastBtn = createNavButton("🌙", "Feasts") { showFeastInterface() }
        val researchBtn = createNavButton("🔬", "Research") { showResearchInterface() }
        val linksBtn = createNavButton("🔗", "Links") { showLinksInterface() }
        val settingsBtn = createNavButton("⚙️", "Settings") { showSettingsInterface() }
        val aboutBtn = createNavButton("ℹ️", "About") { showAboutInterface() }
        val themeBtn = createNavButton(
            if (isDarkMode) "🌙" else "☀️", 
            if (isDarkMode) "Dark" else "Light"
        ) { toggleDarkMode() }
        
        featuresRow.addView(feastBtn)
        featuresRow.addView(researchBtn)
        featuresRow.addView(linksBtn)
        featuresRow.addView(settingsBtn)
        featuresRow.addView(aboutBtn)
        featuresRow.addView(themeBtn)
        
        navLayout.addView(mainNavRow)
        navLayout.addView(featuresRow)
        navigationCard.addView(navLayout)
        
        // Add new feature cards for the main sections
        val feastCard = createFeatureCard(
            "🌙 Feast Resources",
            "Complete guide to nineteen-day feasts with all Bahá'í month information",
            "#6A1B9A"
        ) { showFeastInterface() }
        
        val linksCard = createFeatureCard(
            "🔗 Official Links",
            "Access authoritative Bahá'í websites and institutions",
            "#00796B"
        ) { showLinksInterface() }
        
        val settingsCard = createFeatureCard(
            "⚙️ Settings",
            "Customize theme, font size, religious holidays, and app preferences",
            "#FF6F00"
        ) { showSettingsInterface() }
        
        val aboutCard = createFeatureCard(
            "ℹ️ About",
            "App information, version details, and credits",
            "#5D4037"
        ) { showAboutInterface() }
        
        // Assemble the layout with proper Material Design spacing
        layout.addView(headerCard)
        // Theme card removed - dark mode toggle moved to navigation
        
        // Add navigation card back (was missing)
        layout.addView(navigationCard)
        
        // Feature cards with consistent spacing (handled by card margins)
        layout.addView(searchCard)
        layout.addView(browseCard)
        layout.addView(readingCard)
        layout.addView(bookmarksCard)
        layout.addView(feastCard)
        layout.addView(linksCard)
        layout.addView(settingsCard)
        layout.addView(aboutCard)
        
        // Stats card with extra top margin
        val statsMarginParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            val extraMargin = resources.getDimensionPixelSize(R.dimen.spacing_xl)
            setMargins(0, extraMargin, 0, 0)
        }
        statsCard.layoutParams = statsMarginParams
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
    
    private fun createFeatureCard(title: String, description: String, color: String, onClick: () -> Unit): MaterialCardView {
        val card = MaterialCardView(this).apply {
            radius = resources.getDimensionPixelSize(R.dimen.card_corner_radius).toFloat()
            cardElevation = resources.getDimensionPixelSize(R.dimen.card_elevation_resting).toFloat()
            // Fixed contrast: Dark mode uses darker background, light mode stays white
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#2A2A2A") else Color.parseColor("#FFFFFF"))
            isClickable = true
            isFocusable = true
            useCompatPadding = true
            // Add ripple effect
            foreground = ContextCompat.getDrawable(this@MainActivity, android.R.drawable.btn_default)
            // Set proper margins
            val marginParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                val marginVertical = resources.getDimensionPixelSize(R.dimen.card_margin_vertical)
                setMargins(0, marginVertical, 0, marginVertical)
            }
            layoutParams = marginParams
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            val padding = resources.getDimensionPixelSize(R.dimen.card_content_padding)
            setPadding(padding, padding, padding, padding)
            gravity = android.view.Gravity.CENTER_VERTICAL
            minimumHeight = resources.getDimensionPixelSize(R.dimen.list_item_height_two_line)
        }
        
        // Color indicator bar - more subtle and modern
        val colorBar = View(this).apply {
            setBackgroundColor(Color.parseColor(color))
            val barWidth = resources.getDimensionPixelSize(R.dimen.spacing_xs)
            layoutParams = LinearLayout.LayoutParams(barWidth, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                val barMarginEnd = resources.getDimensionPixelSize(R.dimen.spacing_md)
                setMargins(0, 0, barMarginEnd, 0)
            }
        }
        
        val textLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val titleView = TextView(this).apply {
            text = title
            setTextAppearance(R.style.TextAppearance_App_TitleMedium)
            // Enhanced contrast: Pure white on dark, pure black on light
            setTextColor(if (isDarkMode) Color.parseColor("#FFFFFF") else Color.parseColor("#000000"))
            val bottomMargin = resources.getDimensionPixelSize(R.dimen.spacing_xs)
            setPadding(0, 0, 0, bottomMargin)
            // Add text shadow for better readability in dark mode
            if (isDarkMode) {
                setShadowLayer(2f, 1f, 1f, Color.parseColor("#AA000000"))
            }
        }
        
        val descView = TextView(this).apply {
            text = description
            setTextAppearance(R.style.TextAppearance_App_BodyMedium)
            // Enhanced contrast for description text
            setTextColor(if (isDarkMode) Color.parseColor("#F0F0F0") else Color.parseColor("#222222"))
            maxLines = 2
            // Add subtle text shadow for better readability in dark mode
            if (isDarkMode) {
                setShadowLayer(1f, 0.5f, 0.5f, Color.parseColor("#88000000"))
            }
        }
        
        textLayout.addView(titleView)
        textLayout.addView(descView)
        layout.addView(colorBar)
        layout.addView(textLayout)
        card.addView(layout)
        
        card.setOnClickListener { 
            // Add haptic feedback for better user experience
            card.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
            onClick() 
        }
        
        return card
    }
    
    private fun createStatsCard(): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#E3F2FD"))
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(25, 25, 25, 25)
        }
        
        val titleView = TextView(this).apply {
            text = "📚 Library Contents"
            textSize = 18f
            setTextColor(if (isDarkMode) Color.parseColor("#64B5F6") else Color.parseColor("#1976D2"))
            setPadding(0, 0, 0, 15)
        }
        
        val statsText = TextView(this).apply {
            text = "• Central Figures: 10 documents\n" +
                  "• Administrative Writings: 6 documents\n" +
                  "• Ruhi Institute Books: 7 documents\n" +
                  "• Devotional Materials: 2 collections\n" +
                  "• Study Materials: 1 guide\n" +
                  "• Compilations: 2 documents\n\n" +
                  "Total: 28 official Bahá'í texts"
            textSize = 14f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#424242"))
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
                setBackgroundColor(if (isDarkMode) Color.parseColor("#2D2D2D") else Color.parseColor("#E3F2FD"))
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
            setTextColor(if (isActive) 
                (if (isDarkMode) Color.parseColor("#64B5F6") else Color.parseColor("#1976D2")) 
                else (if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666")))
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
    
    private fun showBookmarksInterface() {
        val intent = Intent(this, BookmarksActivity::class.java)
        startActivity(intent)
    }
    
    private fun showFeastInterface() {
        val intent = Intent(this, FeastResourcesActivity::class.java)
        startActivity(intent)
    }
    
    private fun showResearchInterface() {
        try {
            val intent = Intent(this, ResearchDocumentsActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Research feature temporarily unavailable. Please try again later.", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun showLinksInterface() {
        val intent = Intent(this, LinksActivity::class.java)
        startActivity(intent)
    }
    
    private fun showAboutInterface() {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }
    
    private fun showSettingsInterface() {
        val intent = Intent(this, SettingsActivity::class.java)
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
    
    private fun toggleDarkMode() {
        // Toggle the dark mode setting
        SettingsManager.setDarkMode(this, !isDarkMode)
        // Recreate the activity to apply the new theme
        recreate()
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
