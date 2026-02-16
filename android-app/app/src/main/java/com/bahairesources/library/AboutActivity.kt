package com.bahairesources.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.graphics.Color
import androidx.cardview.widget.CardView
import android.content.Intent
import android.net.Uri

class AboutActivity : AppCompatActivity() {
    
    private var isDarkMode = false
    private var currentFontSize = SettingsManager.FONT_MEDIUM
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize settings
        isDarkMode = SettingsManager.isDarkMode(this)
        currentFontSize = SettingsManager.getFontSize(this)
        
        val scrollView = ScrollView(this).apply {
            setBackgroundColor(if (isDarkMode) Color.parseColor("#121212") else Color.parseColor("#F5F5F5"))
        }
        
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 0, 20, 20)
            setBackgroundColor(if (isDarkMode) Color.parseColor("#121212") else Color.parseColor("#F5F5F5"))
        }
        
        // Header
        val headerLayout = createHeader()
        
        // App info card
        val appInfoCard = createAppInfoCard()
        
        // Purpose card
        val purposeCard = createPurposeCard()
        
        // Features card
        val featuresCard = createFeaturesCard()
        
        // Version info card
        val versionCard = createVersionCard()
        
        // Credits card
        val creditsCard = createCreditsCard()
        
        // Links card
        val linksCard = createLinksCard()
        
        mainLayout.addView(headerLayout)
        mainLayout.addView(createSpacing(20))
        mainLayout.addView(appInfoCard)
        mainLayout.addView(createSpacing(15))
        mainLayout.addView(purposeCard)
        mainLayout.addView(createSpacing(15))
        mainLayout.addView(featuresCard)
        mainLayout.addView(createSpacing(15))
        mainLayout.addView(versionCard)
        mainLayout.addView(createSpacing(15))
        mainLayout.addView(creditsCard)
        mainLayout.addView(createSpacing(15))
        mainLayout.addView(linksCard)
        
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
            text = "ℹ️ About"
            textSize = currentFontSize + 6f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(20, 10, 0, 10)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        headerLayout.addView(backButton)
        headerLayout.addView(titleText)
        return headerLayout
    }
    
    private fun createAppInfoCard(): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#E3F2FD"))
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        val iconLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_HORIZONTAL
            setPadding(0, 0, 0, 15)
        }
        
        val iconView = TextView(this).apply {
            text = "🌟"
            textSize = currentFontSize + 20f
            setPadding(0, 0, 15, 0)
        }
        
        val appNameView = TextView(this).apply {
            text = "Bahá'í Resource Library"
            textSize = currentFontSize + 4f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
        }
        
        iconLayout.addView(iconView)
        iconLayout.addView(appNameView)
        
        val taglineView = TextView(this).apply {
            text = "Your companion for spiritual study and devotion"
            textSize = currentFontSize + 1f
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
            gravity = android.view.Gravity.CENTER_HORIZONTAL
            setPadding(0, 5, 0, 15)
        }
        
        val descriptionView = TextView(this).apply {
            text = """
                The Bahá'í Resource Library is a comprehensive mobile application designed to support Bahá'í study, devotion, and community life. This app provides access to prayers, sacred texts, calendar information, feast resources, and authoritative links to official Bahá'í institutions.
                
                Developed with love and service to the Bahá'í community worldwide.
            """.trimIndent()
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
        }
        
        layout.addView(iconLayout)
        layout.addView(taglineView)
        layout.addView(descriptionView)
        card.addView(layout)
        return card
    }
    
    private fun createPurposeCard(): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#E8F5E8"))
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        val titleView = TextView(this).apply {
            text = "🎯 Purpose & Mission"
            textSize = currentFontSize + 2f
            setTextColor(if (isDarkMode) Color.parseColor("#81C784") else Color.parseColor("#2E7D32"))
            setPadding(0, 0, 0, 15)
        }
        
        val missionText = TextView(this).apply {
            text = """
                ✨ To make Bahá'í prayers, writings, and resources easily accessible to believers worldwide
                
                🌍 To support the spiritual education and devotional life of the Bahá'í community
                
                📚 To provide comprehensive tools for study, reflection, and community participation
                
                🤝 To strengthen unity and support community life through shared resources
                
                💡 To assist in the teaching and sharing of the Bahá'í Faith through readily available materials
            """.trimIndent()
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
        }
        
        layout.addView(titleView)
        layout.addView(missionText)
        card.addView(layout)
        return card
    }
    
    private fun createFeaturesCard(): CardView {
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
            text = "⚡ Key Features"
            textSize = currentFontSize + 2f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(0, 0, 0, 15)
        }
        
        val featuresText = TextView(this).apply {
            text = """
                🙏 Extensive Prayer Collection: Over 80 prayers across 14 categories from Bahá'u'lláh, Abdul-Bahá, and the Báb
                
                📖 Document Reader: Browse and search through sacred texts and writings with bookmark functionality
                
                📅 Bahá'í Calendar: Complete calendar with holy days, feast information, and local adaptations
                
                🌙 Feast Resources: Comprehensive guide to the nineteen-day feast with all monthly information
                
                🔗 Official Links: Direct access to authoritative Bahá'í websites and institutions
                
                🎨 Customizable Interface: Dark/light themes, adjustable font sizes, and personalized settings
                
                🔍 Advanced Search: Search through prayers, texts, and resources by keyword, author, or theme
                
                📱 Offline Access: Many features work without internet connection for convenient use anywhere
            """.trimIndent()
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
        }
        
        layout.addView(titleView)
        layout.addView(featuresText)
        card.addView(layout)
        return card
    }
    
    private fun createVersionCard(): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#FFF3E0"))
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        val titleView = TextView(this).apply {
            text = "📱 Version Information"
            textSize = currentFontSize + 2f
            setTextColor(if (isDarkMode) Color.parseColor("#FFB74D") else Color.parseColor("#F57C00"))
            setPadding(0, 0, 0, 15)
        }
        
        val versionText = TextView(this).apply {
            text = """
                Version: 0.8.0 "Revolutionary Edition"
                Release Date: February 2026
                Build: Dual Calendar & Religious Inclusivity
                
                📊 What's New in v0.8.0:
                • Universal dark mode support
                • Comprehensive settings system  
                • Vastly expanded prayer collection (80+ prayers)
                • Enhanced feast resources section
                • Official links directory
                • Improved search functionality
                • Font size customization
                • Better calendar integration
                • 9-pointed star official icon
                • Enhanced document reader
            """.trimIndent()
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
        }
        
        layout.addView(titleView)
        layout.addView(versionText)
        card.addView(layout)
        return card
    }
    
    private fun createCreditsCard(): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#F3E5F5"))
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        val titleView = TextView(this).apply {
            text = "🌟 Credits & Acknowledgments"
            textSize = currentFontSize + 2f
            setTextColor(if (isDarkMode) Color.parseColor("#CE93D8") else Color.parseColor("#7B1FA2"))
            setPadding(0, 0, 0, 15)
        }
        
        val creditsText = TextView(this).apply {
            text = """
                📚 Sacred Texts: All prayers and writings are from authorized Bahá'í sources and translations
                
                🏛️ Official Sources: Content verified against official Bahá'í reference materials including bahai.org
                
                💖 Development: Created with dedication and service to the worldwide Bahá'í community
                
                🀄 Transliteration: Arabic and Persian names follow standard Bahá'í transliteration guidelines
                
                📱 Platform: Built using Android SDK with modern material design principles
                
                🔗 Integration: Direct links to official Bahá'í institutions and authoritative websites
                
                Special thanks to all who provided feedback and suggestions for improvement.
            """.trimIndent()
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
        }
        
        layout.addView(titleView)
        layout.addView(creditsText)
        card.addView(layout)
        return card
    }
    
    private fun createLinksCard(): CardView {
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
            text = "🔗 Related Resources"
            textSize = currentFontSize + 2f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(0, 0, 0, 15)
        }
        
        val linksLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        
        val linkData = listOf(
            Pair("🌐 Official Bahá'í Website", "https://www.bahai.org"),
            Pair("📚 Bahá'í Reference Library", "https://www.bahai.org/library"),
            Pair("📰 Bahá'í World News", "https://news.bahai.org"),
            Pair("🏛️ Universal House of Justice", "https://www.bahai.org/faq/universal-house-justice")
        )
        
        linkData.forEach { (title, url) ->
            val linkButton = Button(this).apply {
                text = title
                textSize = currentFontSize
                setBackgroundColor(if (isDarkMode) Color.parseColor("#1565C0") else Color.parseColor("#1976D2"))
                setTextColor(Color.WHITE)
                setPadding(15, 12, 15, 12)
                setOnClickListener { openUrl(url) }
            }
            linksLayout.addView(linkButton)
            linksLayout.addView(createSpacing(8))
        }
        
        val disclaimerText = TextView(this).apply {
            text = "Note: External links require internet connection and will open in your default browser."
            textSize = currentFontSize - 2f
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
            setPadding(0, 10, 0, 0)
        }
        
        layout.addView(titleView)
        layout.addView(linksLayout)
        layout.addView(disclaimerText)
        card.addView(layout)
        return card
    }
    
    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to open link: ${e.message}", Toast.LENGTH_SHORT).show()
        }
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