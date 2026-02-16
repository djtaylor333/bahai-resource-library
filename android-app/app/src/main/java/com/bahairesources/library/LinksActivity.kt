package com.bahairesources.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.graphics.Color
import androidx.cardview.widget.CardView
import android.content.Intent
import android.net.Uri

class LinksActivity : AppCompatActivity() {
    
    private var isDarkMode = false
    private var currentFontSize = SettingsManager.FONT_MEDIUM
    
    // Important Bahá'í links
    private val bahaiLinks = listOf(
        BahaiLink("🌐 Bahá'í World Centre", "https://bahai.org", "Official website of the Bahá'í Faith"),
        BahaiLink("📚 Bahá'í Reference Library", "https://bahai.org/library", "Complete collection of Bahá'í writings"),
        BahaiLink("📰 Bahá'í World News", "https://news.bahai.org", "Latest news from the Bahá'í community"),
        BahaiLink("🏛️ Universal House of Justice", "https://bahai.org/institutions/universal-house-of-justice", "The supreme governing body"),
        BahaiLink("🎓 Ruhi Institute", "https://bahai.org/action/devotional-community-gatherings/ruhi-institute", "Study materials and courses"),
        BahaiLink("🌍 Bahá'í International Community", "https://bic.org", "UN representative body"),
        BahaiLink("📖 Ocean Interfaith E-Library", "https://www.bahai-library.org", "Comprehensive text database"),
        BahaiLink("🎵 Bahá'í Music and Arts", "https://bahai.us/bahai-music-arts/", "Creative expressions of faith"),
        BahaiLink("👥 Local Bahá'í Communities", "https://bahai.org/action/community-building", "Find your local community"),
        BahaiLink("📝 Devotional Resources", "https://bahai.org/action/devotional-life", "Prayer and devotional materials"),
        BahaiLink("🔍 Bahá'í Topics", "https://bahai.org/beliefs", "Core teachings and principles"),
        BahaiLink("🌟 Bahá'í Media Bank", "https://media.bahai.org", "Photos, videos, and media resources"),
        BahaiLink("🕊️ Office of Public Affairs", "https://bahai.org/action/external-affairs", "Social action and engagement"),
        BahaiLink("📚 Bahá'í Publishing", "https://store.bahai.us", "Official publications and books"),
        BahaiLink("🌱 Junior Youth Empowerment", "https://bahai.org/action/community-building/junior-youth", "Youth development programs")
    )
    
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
        
        // Description card
        val descCard = createDescriptionCard()
        
        // Links sections
        val officialLinksCard = createLinksSection("🌟 Official Bahá'í Links", bahaiLinks)
        
        mainLayout.addView(headerLayout)
        mainLayout.addView(createSpacing(20))
        mainLayout.addView(descCard)
        mainLayout.addView(createSpacing(20))
        mainLayout.addView(officialLinksCard)
        
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
            text = "🔗 Bahá'í Links"
            textSize = currentFontSize + 6f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(20, 10, 0, 10)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        headerLayout.addView(backButton)
        headerLayout.addView(titleText)
        return headerLayout
    }
    
    private fun createDescriptionCard(): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#E8F5E8"))
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        val descText = TextView(this).apply {
            text = """
                Explore official Bahá'í websites and resources for authentic information, texts, news, and community connections.
                
                These links connect you to authoritative sources maintained by Bahá'í institutions worldwide.
            """.trimIndent()
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#2E7D32"))
        }
        
        layout.addView(descText)
        card.addView(layout)
        return card
    }
    
    private fun createLinksSection(title: String, links: List<BahaiLink>): CardView {
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
            text = title
            textSize = currentFontSize + 2f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(0, 0, 0, 15)
        }
        
        layout.addView(titleView)
        
        links.forEach { link ->
            val linkCard = createLinkCard(link)
            layout.addView(linkCard)
            layout.addView(createSpacing(10))
        }
        
        card.addView(layout)
        return card
    }
    
    private fun createLinkCard(link: BahaiLink): LinearLayout {
        val linkLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(15, 15, 15, 15)
            setBackgroundColor(if (isDarkMode) Color.parseColor("#2D2D2D") else Color.parseColor("#F8F9FA"))
            isClickable = true
            isFocusable = true
            setOnClickListener { openLink(link.url) }
        }
        
        val titleView = TextView(this).apply {
            text = link.name
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#64B5F6") else Color.parseColor("#1976D2"))
            setPadding(0, 0, 0, 5)
        }
        
        val descView = TextView(this).apply {
            text = link.description
            textSize = currentFontSize - 2f
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
            setPadding(0, 0, 0, 5)
        }
        
        val urlView = TextView(this).apply {
            text = link.url
            textSize = currentFontSize - 3f
            setTextColor(if (isDarkMode) Color.parseColor("#81C784") else Color.parseColor("#4CAF50"))
        }
        
        linkLayout.addView(titleView)
        linkLayout.addView(descView)
        linkLayout.addView(urlView)
        
        return linkLayout
    }
    
    private fun openLink(url: String) {
        if (url.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No browser available to open link", Toast.LENGTH_SHORT).show()
            }
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

data class BahaiLink(val name: String, val url: String, val description: String)