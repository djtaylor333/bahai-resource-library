package com.bahairesources.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.graphics.Color
import androidx.cardview.widget.CardView
import android.app.AlertDialog

class SettingsActivity : AppCompatActivity() {
    
    private var isDarkMode = false
    private var currentFontSize = SettingsManager.FONT_MEDIUM
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize settings
        isDarkMode = SettingsManager.isDarkMode(this)
        currentFontSize = SettingsManager.getFontSize(this)
        
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 0, 20, 20)
            setBackgroundColor(if (isDarkMode) Color.parseColor("#121212") else Color.parseColor("#F5F5F5"))
        }
        
        // Header
        val headerLayout = createHeader()
        
        // Theme Settings
        val themeCard = createThemeSettings()
        
        // Font Settings
        val fontCard = createFontSettings()
        
        // Location Settings
        val locationCard = createLocationSettings()
        
        // Calendar Settings
        val calendarCard = createCalendarSettings()
        
        // About Section
        val aboutCard = createAboutSection()
        
        // Reset Settings
        val resetCard = createResetSection()
        
        mainLayout.addView(headerLayout)
        mainLayout.addView(createSpacing(20))
        mainLayout.addView(themeCard)
        mainLayout.addView(createSpacing(15))
        mainLayout.addView(fontCard)
        mainLayout.addView(createSpacing(15))
        mainLayout.addView(locationCard)
        mainLayout.addView(createSpacing(15))
        mainLayout.addView(calendarCard)
        mainLayout.addView(createSpacing(15))
        mainLayout.addView(aboutCard)
        mainLayout.addView(createSpacing(15))
        mainLayout.addView(resetCard)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
    
    private fun createHeader(): LinearLayout {
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(10, 30, 10, 20)
        }
        
        val backButton = Button(this).apply {
            text = "← Back"
            setBackgroundColor(if (isDarkMode) Color.parseColor("#1565C0") else Color.parseColor("#1976D2"))
            setTextColor(Color.WHITE)
            setPadding(20, 10, 20, 10)
            textSize = currentFontSize
            setOnClickListener { finish() }
        }
        
        val titleText = TextView(this).apply {
            text = "⚙️ Settings"
            textSize = currentFontSize + 6f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(20, 10, 0, 10)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        headerLayout.addView(backButton)
        headerLayout.addView(titleText)
        return headerLayout
    }
    
    private fun createThemeSettings(): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#FFFFFF"))
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        val titleView = TextView(this).apply {
            text = "🎨 Theme Settings"
            textSize = currentFontSize + 2f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(0, 0, 0, 15)
        }
        
        val themeRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 10, 0, 10)
        }
        
        val themeLabel = TextView(this).apply {
            text = "Dark Mode"
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val themeSwitch = Switch(this).apply {
            isChecked = isDarkMode
            setOnCheckedChangeListener { _, isChecked ->
                SettingsManager.setDarkMode(this@SettingsActivity, isChecked)
                showRestartDialog("Theme changed. Restart the app to apply changes.")
            }
        }
        
        themeRow.addView(themeLabel)
        themeRow.addView(themeSwitch)
        
        layout.addView(titleView)
        layout.addView(themeRow)
        card.addView(layout)
        
        return card
    }
    
    private fun createFontSettings(): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#FFFFFF"))
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        val titleView = TextView(this).apply {
            text = "📝 Font Settings"
            textSize = currentFontSize + 2f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(0, 0, 0, 15)
        }
        
        val fontRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 10, 0, 10)
        }
        
        val fontLabel = TextView(this).apply {
            text = "Font Size: ${SettingsManager.getFontSizeName(this@SettingsActivity)}"
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val fontButton = Button(this).apply {
            text = "Change"
            textSize = currentFontSize - 2f
            setBackgroundColor(if (isDarkMode) Color.parseColor("#1565C0") else Color.parseColor("#1976D2"))
            setTextColor(Color.WHITE)
            setPadding(15, 8, 15, 8)
            setOnClickListener { showFontSizeDialog() }
        }
        
        fontRow.addView(fontLabel)
        fontRow.addView(fontButton)
        
        layout.addView(titleView)
        layout.addView(fontRow)
        card.addView(layout)
        
        return card
    }
    
    private fun createLocationSettings(): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#FFFFFF"))
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        val titleView = TextView(this).apply {
            text = "📍 Location Settings"
            textSize = currentFontSize + 2f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(0, 0, 0, 15)
        }
        
        // Auto Location Toggle
        val autoRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 5, 0, 5)
        }
        
        val autoLabel = TextView(this).apply {
            text = "Use Device Location"
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val autoSwitch = Switch(this).apply {
            isChecked = SettingsManager.getAutoLocation(this@SettingsActivity)
            setOnCheckedChangeListener { _, isChecked ->
                SettingsManager.setAutoLocation(this@SettingsActivity, isChecked)
            }
        }
        
        autoRow.addView(autoLabel)
        autoRow.addView(autoSwitch)
        
        // Manual Location
        val manualRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 10, 0, 10)
        }
        
        val manualLabel = TextView(this).apply {
            text = "Manual Location:"
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            setPadding(0, 0, 0, 5)
        }
        
        val locationText = TextView(this).apply {
            text = SettingsManager.getManualLocation(this@SettingsActivity)
            textSize = currentFontSize - 1f
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
            setPadding(0, 0, 0, 10)
        }
        
        val changeLocationButton = Button(this).apply {
            text = "Change Location"
            textSize = currentFontSize - 2f
            setBackgroundColor(if (isDarkMode) Color.parseColor("#1565C0") else Color.parseColor("#1976D2"))
            setTextColor(Color.WHITE)
            setPadding(15, 8, 15, 8)
            setOnClickListener { showLocationDialog() }
        }
        
        layout.addView(titleView)
        layout.addView(autoRow)
        layout.addView(manualLabel)
        layout.addView(locationText)
        layout.addView(changeLocationButton)
        card.addView(layout)
        
        return card
    }
    
    private fun createCalendarSettings(): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#FFFFFF"))
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        val titleView = TextView(this).apply {
            text = "📅 Calendar Settings"
            textSize = currentFontSize + 2f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(0, 0, 0, 15)
        }
        
        val otherReligionsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 10, 0, 10)
        }
        
        val otherReligionsLabel = TextView(this).apply {
            text = "Show Other Religious Holidays"
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val otherReligionsSwitch = Switch(this).apply {
            isChecked = SettingsManager.getShowOtherReligions(this@SettingsActivity)
            setOnCheckedChangeListener { _, isChecked ->
                SettingsManager.setShowOtherReligions(this@SettingsActivity, isChecked)
            }
        }
        
        otherReligionsRow.addView(otherReligionsLabel)
        otherReligionsRow.addView(otherReligionsSwitch)
        
        layout.addView(titleView)
        layout.addView(otherReligionsRow)
        card.addView(layout)
        
        return card
    }
    
    private fun createAboutSection(): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#FFFFFF"))
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        val titleView = TextView(this).apply {
            text = "ℹ️ About"
            textSize = currentFontSize + 2f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(0, 0, 0, 15)
        }
        
        val aboutText = TextView(this).apply {
            text = """
                Bahá'í Resource Library
                Version 0.7.0
                
                Developed by: David Taylor
                Date: February 2026
                
                A comprehensive digital library for the Bahá'í Faith, including prayers, writings, calendar, and resources for spiritual growth and community service.
                
                For official Bahá'í texts and authoritative sources, visit bahai.org
            """.trimIndent()
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
        }
        
        layout.addView(titleView)
        layout.addView(aboutText)
        card.addView(layout)
        
        return card
    }
    
    private fun createResetSection(): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#FFFFFF"))
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        val resetButton = Button(this).apply {
            text = "🔄 Reset All Settings"
            textSize = currentFontSize
            setBackgroundColor(Color.parseColor("#F44336"))
            setTextColor(Color.WHITE)
            setPadding(20, 15, 20, 15)
            setOnClickListener { showResetDialog() }
        }
        
        layout.addView(resetButton)
        card.addView(layout)
        
        return card
    }
    
    private fun showFontSizeDialog() {
        val options = arrayOf("Small", "Medium", "Large", "Extra Large")
        val sizes = arrayOf(SettingsManager.FONT_SMALL, SettingsManager.FONT_MEDIUM, SettingsManager.FONT_LARGE, SettingsManager.FONT_EXTRA_LARGE)
        
        val currentIndex = sizes.indexOf(currentFontSize)
        
        AlertDialog.Builder(this)
            .setTitle("Select Font Size")
            .setSingleChoiceItems(options, currentIndex) { dialog, which ->
                SettingsManager.setFontSize(this, sizes[which])
                showRestartDialog("Font size changed. Restart the app to apply changes.")
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showLocationDialog() {
        val input = EditText(this).apply {
            setText(SettingsManager.getManualLocation(this@SettingsActivity))
            hint = "e.g. New York, NY, USA"
        }
        
        AlertDialog.Builder(this)
            .setTitle("Set Manual Location")
            .setMessage("Enter city, state/province, country:")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val location = input.text.toString().trim()
                if (location.isNotEmpty()) {
                    SettingsManager.setManualLocation(this, location)
                    recreate() // Refresh the settings screen
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showResetDialog() {
        AlertDialog.Builder(this)
            .setTitle("Reset Settings")
            .setMessage("Are you sure you want to reset all settings to defaults? This cannot be undone.")
            .setPositiveButton("Reset") { _, _ ->
                SettingsManager.resetToDefaults(this)
                showRestartDialog("Settings reset. Restart the app to apply changes.")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showRestartDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Restart Required")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> finish() }
            .show()
    }
    
    private fun createSpacing(height: Int): android.view.View {
        return android.view.View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                height
            )
        }
    }
}