package com.bahairesources.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.graphics.Color
import androidx.cardview.widget.CardView
import android.content.Intent

class BrowseActivity : AppCompatActivity() {
    
    private lateinit var categoryLayout: LinearLayout
    private var isDarkMode = false
    private var currentFontSize = SettingsManager.FONT_MEDIUM
    
    // Document categories with counts
    private val categories = listOf(
        Category("📚 Central Figures", 10, "Writings of Bahá'u'lláh, Abdul-Bahá, and the Báb", "#1976D2"),
        Category("🏛️ Administrative Writings", 6, "Guidance from Shoghi Effendi and the Universal House of Justice", "#388E3C"),
        Category("🎓 Ruhi Institute Books", 7, "Study materials for spiritual education and service", "#F57C00"),
        Category("🙏 Devotional Materials", 2, "Prayers and devotional readings", "#7B1FA2"),
        Category("📖 Study Materials", 1, "Introductory guides and study aids", "#303F9F"),
        Category("📋 Compilations", 2, "Thematic collections of writings", "#D32F2F")
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize settings
        isDarkMode = SettingsManager.isDarkMode(this)
        currentFontSize = SettingsManager.getFontSize(this)
        
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL  
            setPadding(20, 30, 20, 30)
            setBackgroundColor(if (isDarkMode) Color.parseColor("#121212") else Color.parseColor("#F8F9FA"))
        }
        
        // Header
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(10, 10, 10, 30)
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
            text = "📚 Browse Documents"
            textSize = currentFontSize + 4f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(30, 15, 0, 0)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val settingsButton = Button(this).apply {
            text = "⚙️"
            textSize = currentFontSize
            setBackgroundColor(if (isDarkMode) Color.parseColor("#37474F") else Color.parseColor("#E0E0E0"))
            setTextColor(if (isDarkMode) Color.WHITE else Color.parseColor("#333333"))
            setPadding(15, 10, 15, 10)
            setOnClickListener {
                startActivity(Intent(this@BrowseActivity, SettingsActivity::class.java))
            }
        }
        
        headerLayout.addView(backButton)
        headerLayout.addView(titleText)
        headerLayout.addView(settingsButton)
        
        // Instructions
        val instructionText = TextView(this).apply {
            text = "Browse the Bahá'í Resource Library by category. Tap any category to explore the documents within."
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
            setPadding(10, 0, 10, 20)
        }
        
        // Categories container
        categoryLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        
        mainLayout.addView(headerLayout)
        mainLayout.addView(instructionText)
        mainLayout.addView(categoryLayout)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
        
        // Display categories
        displayCategories()
    }
    
    private fun displayCategories() {
        categories.forEach { category ->
            val card = createCategoryCard(category)
            categoryLayout.addView(card)
            categoryLayout.addView(createSpacing(15))
        }
    }
    
    private fun createCategoryCard(category: Category): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 6f
            setCardBackgroundColor(Color.WHITE)
            isClickable = true
            isFocusable = true
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(25, 25, 25, 25)
        }
        
        val colorBar = android.view.View(this).apply {
            setBackgroundColor(Color.parseColor(category.color))
            layoutParams = LinearLayout.LayoutParams(8, LinearLayout.LayoutParams.MATCH_PARENT)
        }
        
        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 0, 0, 0)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val titleView = TextView(this).apply {
            text = category.name
            textSize = 18f
            setTextColor(Color.parseColor("#212121"))
            setPadding(0, 0, 0, 8)
        }
        
        val countView = TextView(this).apply {
            text = "${category.count} documents"
            textSize = 14f
            setTextColor(Color.parseColor(category.color))
            setPadding(0, 0, 0, 8)
        }
        
        val descView = TextView(this).apply {
            text = category.description
            textSize = 14f
            setTextColor(Color.parseColor("#757575"))
        }
        
        val arrowView = TextView(this).apply {
            text = "›"
            textSize = 28f
            setTextColor(Color.parseColor("#CCCCCC"))
            setPadding(10, 0, 0, 0)
        }
        
        contentLayout.addView(titleView)
        contentLayout.addView(countView)
        contentLayout.addView(descView)
        
        layout.addView(colorBar)
        layout.addView(contentLayout)
        layout.addView(arrowView)
        
        card.addView(layout)
        
        card.setOnClickListener {
            showCategoryDetails(category)
        }
        
        return card
    }
    
    private fun showCategoryDetails(category: Category) {
        val documents = when (category.name.substringAfter(" ")) {
            "Central Figures" -> listOf(
                "The Kitab-i-Aqdas", "The Book of Certitude", "Gleanings from the Writings of Bahá'u'lláh",
                "The Seven Valleys", "The Hidden Words", "Prayers and Meditations",
                "Some Answered Questions", "The Secret of Divine Civilization",
                "Selections from the Writings of Abdul-Bahá", "The Will and Testament"
            )
            "Administrative Writings" -> listOf(
                "God Passes By", "The World Order of Bahá'u'lláh", "The Advent of Divine Justice",
                "Citadel of Faith", "Messages to the Bahá'í World", "The Promise of World Peace"
            )
            "Ruhi Institute Books" -> listOf(
                "Book 1: Reflections on the Life of the Spirit",
                "Book 2: Arising to Serve", 
                "Book 3: Teaching Children's Classes",
                "Book 4: The Twin Manifestations",
                "Book 5: Releasing the Powers of Junior Youth",
                "Book 6: Teaching the Cause",
                "Book 7: Walking Together on a Path of Service"
            )
            "Devotional Materials" -> listOf(
                "Bahá'í Prayers", "Fire and Light"
            )
            "Study Materials" -> listOf(
                "The Bahá'í Faith: A Short Study"
            )
            "Compilations" -> listOf(
                "Compilation on Scholarship", "Compilation on Women"
            )
            else -> emptyList()
        }
        
        val documentList = documents.joinToString("\n• ", "• ")
        
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle(category.name)
            .setMessage("${category.description}\n\n" +
                       "Documents in this category:\n\n$documentList\n\n" +
                       "📖 Reading interface coming in the next update!\n\n" +
                       "Each document will include:\n" +
                       "• Full text with search and highlighting\n" +
                       "• Bookmark and annotation features\n" +
                       "• Audio narration (where available)\n" +
                       "• Cross-references and study aids")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .setNeutralButton("Search This Category") { _, _ ->
                Toast.makeText(this, "Category search coming soon!", Toast.LENGTH_SHORT).show()
            }
            .create()
        
        dialog.show()
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

data class Category(
    val name: String,
    val count: Int,
    val description: String,
    val color: String
)