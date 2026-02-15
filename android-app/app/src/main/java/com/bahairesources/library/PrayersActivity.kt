package com.bahairesources.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.graphics.Color
import androidx.cardview.widget.CardView

class PrayersActivity : AppCompatActivity() {
    
    private lateinit var categoryLayout: LinearLayout
    private var isDarkMode: Boolean = false
    
    // Prayer categories
    private val prayerCategories = listOf(
        PrayerCategory("🌅 Morning", "Start your day with devotion", listOf(
            Prayer("Morning Prayer", "Bahá'u'lláh", "I have wakened this morning by Thy grace, O my God..."),
            Prayer("Prayer for Awakening", "Bahá'u'lláh", "Praised be Thou, O Lord my God! I implore Thee...")
        )),
        PrayerCategory("🌙 Evening", "End your day in reflection", listOf(
            Prayer("Evening Prayer", "Bahá'u'lláh", "I have attained unto Thee, O my God, after I had..."),
            Prayer("Prayer of Gratitude", "Bahá'u'lláh", "All praise be to Thee, O my God, for having...")
        )),
        PrayerCategory("🍽️ Before Meals", "Prayers of thanksgiving", listOf(
            Prayer("Blessed is Thou, O God", "Bahá'u'lláh", "Blessed is Thou, O God, as Thou hast nourished my body..."),
            Prayer("Prayer of Sustenance", "Bahá'u'lláh", "He is God! Thou hast given us food for sustenance...")
        )),
        PrayerCategory("🌍 For Humanity", "Universal prayers", listOf(
            Prayer("Prayer for Mankind", "Bahá'u'lláh", "O Thou kind Lord! Thou hast created all humanity..."),
            Prayer("Unity Prayer", "Abdul-Bahá", "O my God! O my God! Unite the hearts of Thy servants...")
        )),
        PrayerCategory("🙏 General Prayers", "Daily devotions", listOf(
            Prayer("Short Obligatory Prayer", "Bahá'u'lláh", "(To be said once in twenty-four hours, at noon)"),
            Prayer("Remover of Difficulties", "The Báb", "Is there any Remover of difficulties save God?..."),
            Prayer("Prayer for Assistance", "Bahá'u'lláh", "O God, refresh and gladden my spirit...")
        )),
        PrayerCategory("💝 For Special Occasions", "Life events and occasions", listOf(
            Prayer("For the Departed", "Bahá'u'lláh", "O my God! O Thou forgiver of sins..."),
            Prayer("For the Sick", "Bahá'u'lláh", "Thy name is my healing, O my God..."),
            Prayer("For Youth", "Abdul-Bahá", "O Thou peerless Lord! Grant that these children...")
        ))
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check for dark mode preference (we'll implement this toggle later)
        isDarkMode = false
        
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 30, 20, 30)
            setBackgroundColor(if (isDarkMode) Color.parseColor("#121212") else Color.parseColor("#F8F9FA"))
        }
        
        // Header
        val headerLayout = createHeader()
        
        // Info card
        val infoCard = createInfoCard()
        
        // Categories container
        categoryLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        
        mainLayout.addView(headerLayout)
        mainLayout.addView(infoCard)
        mainLayout.addView(createSpacing(20))
        mainLayout.addView(categoryLayout)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
        
        // Display prayer categories
        displayPrayerCategories()
    }
    
    private fun createHeader(): LinearLayout {
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(10, 10, 10, 30)
        }
        
        val backButton = Button(this).apply {
            text = "← Back"
            setBackgroundColor(Color.parseColor("#1976D2"))
            setTextColor(Color.WHITE)
            setPadding(20, 10, 20, 10)
            setOnClickListener { finish() }
        }
        
        val titleText = TextView(this).apply {
            text = "🙏 Prayers"
            textSize = 20f
            setTextColor(Color.parseColor("#1976D2"))
            setPadding(30, 15, 0, 0)
        }
        
        val modeButton = Button(this).apply {
            text = if (isDarkMode) "☀️" else "🌙"
            setBackgroundColor(Color.parseColor("#FF9800"))
            setTextColor(Color.WHITE)
            setPadding(15, 10, 15, 10)
            setOnClickListener { toggleDarkMode() }
        }
        
        headerLayout.addView(backButton)
        headerLayout.addView(titleText)
        
        val spacer = android.view.View(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, 0, 1f)
        }
        headerLayout.addView(spacer)
        headerLayout.addView(modeButton)
        
        return headerLayout
    }
    
    private fun createInfoCard(): CardView {
        val card = CardView(this).apply {
            radius = 8f
            cardElevation = 3f
            setCardBackgroundColor(Color.parseColor("#E3F2FD"))
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 15, 20, 15)
        }
        
        val infoText = TextView(this).apply {
            text = "📖 Prayer samples and structures are provided for demonstration. For complete official prayers, please visit bahai.org/library or consult official Bahá'í publications."
            textSize = 12f
            setTextColor(Color.parseColor("#1976D2"))
        }
        
        layout.addView(infoText)
        card.addView(layout)
        
        return card
    }
    
    private fun displayPrayerCategories() {
        prayerCategories.forEach { category ->
            val card = createCategoryCard(category)
            categoryLayout.addView(card)
            categoryLayout.addView(createSpacing(15))
        }
    }
    
    private fun createCategoryCard(category: PrayerCategory): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 6f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#2C2C2C") else Color.WHITE)
            isClickable = true
            isFocusable = true
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(25, 25, 25, 25)
        }
        
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        
        val titleView = TextView(this).apply {
            text = category.name
            textSize = 18f
            setTextColor(if (isDarkMode) Color.WHITE else Color.parseColor("#212121"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val countView = TextView(this).apply {
            text = "${category.prayers.size} prayers"
            textSize = 12f
            setTextColor(Color.parseColor("#1976D2"))
            setPadding(10, 0, 0, 0)
        }
        
        headerLayout.addView(titleView)
        headerLayout.addView(countView)
        
        val descView = TextView(this).apply {
            text = category.description
            textSize = 14f
            setTextColor(if (isDarkMode) Color.parseColor("#CCCCCC") else Color.parseColor("#757575"))
            setPadding(0, 8, 0, 0)
        }
        
        layout.addView(headerLayout)
        layout.addView(descView)
        card.addView(layout)
        
        card.setOnClickListener {
            showPrayerList(category)
        }
        
        return card
    }
    
    private fun showPrayerList(category: PrayerCategory) {
        val prayersList = category.prayers.joinToString("\\n\\n") { prayer ->
            "• ${prayer.title}\\n  — ${prayer.author}\\n  ${prayer.preview.take(50)}..."
        }
        
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle(category.name)
            .setMessage("${category.description}\\n\\nPrayers in this category:\\n\\n$prayersList\\n\\n" +
                       "📖 Tap 'Read Prayer' to view a sample structure.\\n\\n" +
                       "For complete official prayers, visit:\\n" +
                       "• bahai.org/library\\n" +
                       "• Local Bahá'í centers\\n" +
                       "• Official Bahá'í publications")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .setNeutralButton("Read Prayer") { _, _ ->
                if (category.prayers.isNotEmpty()) {
                    showPrayerReader(category.prayers[0])
                }
            }
            .create()
        
        dialog.show()
    }
    
    private fun showPrayerReader(prayer: Prayer) {
        // Create a full-screen prayer reading activity (simplified version)
        val intent = android.content.Intent(this, PrayerReaderActivity::class.java).apply {
            putExtra("prayer_title", prayer.title)
            putExtra("prayer_author", prayer.author)
            putExtra("prayer_text", prayer.preview)
        }
        startActivity(intent)
    }
    
    private fun toggleDarkMode() {
        // This will be implemented with proper preferences later
        Toast.makeText(this, "Dark/Light mode toggle coming in next update!", Toast.LENGTH_SHORT).show()
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

data class PrayerCategory(
    val name: String,
    val description: String,
    val prayers: List<Prayer>
)

data class Prayer(
    val title: String,
    val author: String,
    val preview: String  // Short preview or beginning of prayer
)