package com.bahairesources.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.graphics.Color
import androidx.cardview.widget.CardView
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup

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
                // Apply theme change immediately without restart
                applyThemeChange(isChecked)
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
            text = "📅 Religious Holidays Display"
            textSize = currentFontSize + 2f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(0, 0, 0, 15)
        }
        
        val descriptionView = TextView(this).apply {
            text = "Choose which religious traditions to display in the calendar alongside Bahá'í holy days:"
            textSize = currentFontSize - 2f
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
            setPadding(0, 0, 0, 15)
        }
        
        // Christianity Settings
        val christianRow = createReligionSetting(
            "✝️ Christianity", 
            "Christmas, Easter, Good Friday, etc.",
            SettingsManager.getShowChristianHolidays(this)
        ) { isChecked ->
            SettingsManager.setShowChristianHolidays(this, isChecked)
        }
        
        // Islam Settings
        val islamRow = createReligionSetting(
            "☪️ Islam", 
            "Eid al-Fitr, Eid al-Adha, Ramadan, etc.",
            SettingsManager.getShowIslamicHolidays(this)
        ) { isChecked ->
            SettingsManager.setShowIslamicHolidays(this, isChecked)
        }
        
        // Judaism Settings
        val jewishRow = createReligionSetting(
            "✡️ Judaism", 
            "Passover, Yom Kippur, Rosh Hashanah, etc.",
            SettingsManager.getShowJewishHolidays(this)
        ) { isChecked ->
            SettingsManager.setShowJewishHolidays(this, isChecked)
        }
        
        // Hinduism Settings
        val hinduRow = createReligionSetting(
            "🕉️ Hinduism", 
            "Diwali, Holi, Ram Navami, etc.",
            SettingsManager.getShowHinduHolidays(this)
        ) { isChecked ->
            SettingsManager.setShowHinduHolidays(this, isChecked)
        }
        
        // Buddhism Settings
        val buddhismRow = createReligionSetting(
            "☸️ Buddhism", 
            "Buddha's Birthday, Vesak Day, etc.",
            SettingsManager.getShowBuddhistHolidays(this)
        ) { isChecked ->
            SettingsManager.setShowBuddhistHolidays(this, isChecked)
        }
        
        // Secular Holidays Settings
        val secularRow = createReligionSetting(
            "🌍 Secular/National", 
            "New Year's Day, Independence Days, etc.",
            SettingsManager.getShowSecularHolidays(this)
        ) { isChecked ->
            SettingsManager.setShowSecularHolidays(this, isChecked)
        }
        
        // All/None quick options
        val quickOptionsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 20, 0, 10)
            gravity = android.view.Gravity.CENTER
        }
        
        val selectAllButton = Button(this).apply {
            text = "Select All"
            textSize = currentFontSize - 2f
            setBackgroundColor(Color.parseColor("#4CAF50"))
            setTextColor(Color.WHITE)
            setPadding(20, 10, 20, 10)
            setOnClickListener { setAllReligiousHolidays(true) }
        }
        
        val selectNoneButton = Button(this).apply {
            text = "Select None"
            textSize = currentFontSize - 2f
            setBackgroundColor(Color.parseColor("#FF5722"))
            setTextColor(Color.WHITE)
            setPadding(20, 10, 20, 10)
            setOnClickListener { setAllReligiousHolidays(false) }
        }
        
        quickOptionsRow.addView(selectAllButton)
        quickOptionsRow.addView(android.view.View(this).apply { 
            layoutParams = LinearLayout.LayoutParams(20, 0) 
        })
        quickOptionsRow.addView(selectNoneButton)
        
        layout.addView(titleView)
        layout.addView(descriptionView)
        layout.addView(christianRow)
        layout.addView(islamRow)
        layout.addView(jewishRow)
        layout.addView(hinduRow)
        layout.addView(buddhismRow)
        layout.addView(secularRow)
        layout.addView(quickOptionsRow)
        card.addView(layout)
        
        return card
    }
    
    private fun createReligionSetting(
        title: String, 
        description: String, 
        isChecked: Boolean, 
        onCheckedChange: (Boolean) -> Unit
    ): LinearLayout {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 8, 0, 8)
        }
        
        val titleRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 5, 0, 5)
        }
        
        val titleLabel = TextView(this).apply {
            text = title
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val switch = Switch(this).apply {
            this.isChecked = isChecked
            setOnCheckedChangeListener { _, checked ->
                onCheckedChange(checked)
            }
        }
        
        val descriptionLabel = TextView(this).apply {
            text = description
            textSize = currentFontSize - 2f
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
            setPadding(0, 2, 0, 5)
        }
        
        titleRow.addView(titleLabel)
        titleRow.addView(switch)
        row.addView(titleRow)
        row.addView(descriptionLabel)
        
        return row
    }
    
    private fun setAllReligiousHolidays(enabled: Boolean) {
        SettingsManager.setShowChristianHolidays(this, enabled)
        SettingsManager.setShowIslamicHolidays(this, enabled)
        SettingsManager.setShowJewishHolidays(this, enabled)
        SettingsManager.setShowHinduHolidays(this, enabled)
        SettingsManager.setShowBuddhistHolidays(this, enabled)
        SettingsManager.setShowSecularHolidays(this, enabled)
        
        // Update all switches to reflect new state
        updateReligiousHolidaySwitches(enabled)
        
        Toast.makeText(this, 
            if (enabled) "All religious holidays enabled" else "All religious holidays disabled", 
            Toast.LENGTH_SHORT
        ).show()
        
        // No need to restart app - UI is updated in place
    }
    
    private fun updateReligiousHolidaySwitches(enabled: Boolean) {
        // Find and update all religious holiday switches
        try {
            val scrollView = findViewById<ScrollView>(android.R.id.content)
            val rootLayout = scrollView?.getChildAt(0) as? LinearLayout
            rootLayout?.let { layout ->
                updateSwitchesRecursively(layout, enabled)
            }
        } catch (e: Exception) {
            // Fallback - just recreate the activity if we can't find switches
            recreate()
        }
    }
    
    private fun updateSwitchesRecursively(viewGroup: ViewGroup, enabled: Boolean) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            when (child) {
                is Switch -> {
                    // Check if this is a religious holiday switch by looking at parent or tags
                    val parentView = child.parent as? ViewGroup
                    val isReligiousSwitch = parentView?.let { parent ->
                        // Look for switches in rows that contain religious holiday text
                        (0 until parent.childCount).any { j ->
                            val sibling = parent.getChildAt(j)
                            if (sibling is TextView) {
                                val text = sibling.text.toString().toLowerCase()
                                text.contains("christian") || text.contains("islamic") || 
                                text.contains("jewish") || text.contains("hindu") || 
                                text.contains("buddhist") || text.contains("secular")
                            } else false
                        }
                    } ?: false
                    
                    if (isReligiousSwitch) {
                        child.isChecked = enabled
                    }
                }
                is ViewGroup -> {
                    updateSwitchesRecursively(child, enabled)
                }
            }
        }
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
            val aboutContent = """
                Bahá'í Resource Library
                Version 0.8.0
                
                Developed by: David Taylor
                Date: February 2026
                
                A comprehensive digital library for the Bahá'í Faith, including prayers, writings, calendar, and resources for spiritual growth and community service.
                
                For official Bahá'í texts and authoritative sources, visit bahai.org
            """.trimIndent()
            setTextWithClickableUrls(this, aboutContent)
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
                Toast.makeText(this, "Font size updated", Toast.LENGTH_SHORT).show()
                recreate() // Recreate activity to apply font changes
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
            .setMessage("Enter or select your location:")
            .setView(createLocationInputLayout())
            .setPositiveButton("Save") { _, _ -> }
            .setNegativeButton("Cancel", null)
            .create()
            .apply {
                show()
                // Override the positive button to handle autocomplete
                getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    handleLocationSave(this)
                }
            }
    }
    
    private fun createLocationInputLayout(): LinearLayout {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 10)
        }
        
        // Free text location input - accepts any location worldwide
        val input = EditText(this).apply {
            tag = "location_input"
            hint = "Enter any city, town, or location worldwide"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS
            
            // Set current location if exists
            val currentLocation = SettingsManager.getManualLocation(this@SettingsActivity)
            if (currentLocation.isNotEmpty()) {
                setText(currentLocation)
            }
        }
        
        // Quick selection buttons for common locations
        val quickSelectLabel = TextView(this).apply {
            text = "Examples:"
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            textSize = 14f
            setPadding(0, 15, 0, 10)
        }
        
        val exampleLocationsText = TextView(this).apply {
            text = "New York, NY • London, UK • Tokyo, Japan • Sydney, Australia • Any city, town, or location worldwide"
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
            textSize = 12f
            setPadding(0, 5, 0, 15)
        }
        
        val helpLabel = TextView(this).apply {
            text = "💡 Tip: You can now enter ANY location worldwide - cities, towns, villages, or specific addresses."
            setTextColor(if (isDarkMode) Color.parseColor("#888888") else Color.parseColor("#666666"))
            textSize = 12f
            setPadding(0, 15, 0, 0)
        }
        
        layout.addView(input)
        layout.addView(quickSelectLabel)
        layout.addView(exampleLocationsText)
        layout.addView(helpLabel)
        
        return layout
    }
    
    private fun handleLocationSave(dialog: AlertDialog) {
        val layout = dialog.findViewById<LinearLayout>(android.R.id.content)?.getChildAt(0) as? LinearLayout
        val input = layout?.findViewWithTag<EditText>("location_input")
        
        val location = input?.text?.toString()?.trim() ?: ""
        if (location.isNotEmpty()) {
            SettingsManager.setManualLocation(this, location)
            Toast.makeText(this, "Location saved: $location", Toast.LENGTH_SHORT).show()
            recreate() // Refresh the settings screen
            dialog.dismiss()
        } else {
            Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showResetDialog() {
        AlertDialog.Builder(this)
            .setTitle("Reset Settings")
            .setMessage("Are you sure you want to reset all settings to defaults? This cannot be undone.")
            .setPositiveButton("Reset") { _, _ ->
                SettingsManager.resetToDefaults(this)
                Toast.makeText(this, "Settings reset to defaults", Toast.LENGTH_SHORT).show()
                recreate() // Recreate activity to apply changes
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showRestartDialog(message: String) {
        // Show brief toast message about the change
        Toast.makeText(this, "Settings updated - restarting app...", Toast.LENGTH_SHORT).show()
        
        // Restart the app automatically
        restartApp()
    }
    
    private fun restartApp() {
        // Create intent to restart the main activity
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        
        // Finish current activity and start main activity
        finish()
        startActivity(intent)
        
        // Apply animation for smooth transition
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        
        // Force kill current process to ensure clean restart
        android.os.Process.killProcess(android.os.Process.myPid())
    }
    
    private fun createSpacing(height: Int): android.view.View {
        return android.view.View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                height
            )
        }
    }
    
    private fun setTextWithClickableUrls(textView: TextView, text: String) {
        val spannableString = SpannableString(text)
        
        // Find all URLs in the text and make them clickable
        val urlPattern = Regex("https?://[^\\s]+|bahai\\.org(?:/[^\\s]*)?")
        val matches = urlPattern.findAll(text)
        
        for (match in matches) {
            val start = match.range.first
            val end = match.range.last + 1
            val url = match.value
            
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    try {
                        val fullUrl = if (url.startsWith("http")) url else "https://$url"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl))
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(this@SettingsActivity, "Could not open link", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            
            spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#4285F4")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
    
    private fun applyThemeChange(darkMode: Boolean) {
        isDarkMode = darkMode
        
        // Show feedback to user
        Toast.makeText(this, 
            "Theme changed to ${if (darkMode) "Dark" else "Light"} mode", 
            Toast.LENGTH_SHORT
        ).show()
        
        // Recreate the activity to apply theme changes
        recreate()
    }
}