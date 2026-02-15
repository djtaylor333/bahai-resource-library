package com.bahairesources.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.graphics.Color
import androidx.cardview.widget.CardView
import android.content.Intent

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
        
        // Initialize dark mode
        isDarkMode = ThemeManager.isDarkMode(this)
        
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
            setBackgroundColor(if (isDarkMode) Color.parseColor("#1565C0") else Color.parseColor("#1976D2"))
            setTextColor(Color.WHITE)
            setPadding(20, 10, 20, 10)
            setOnClickListener { finish() }
        }
        
        val titleText = TextView(this).apply {
            text = "🙏 Prayers Collection"
            textSize = 20f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(20, 10, 0, 10)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val toggleButton = Button(this).apply {
            text = if (isDarkMode) "🌙" else "☀️"
            textSize = 16f
            setBackgroundColor(if (isDarkMode) Color.parseColor("#37474F") else Color.parseColor("#E0E0E0"))
            setTextColor(if (isDarkMode) Color.WHITE else Color.parseColor("#333333"))
            setPadding(15, 10, 15, 10)
            setOnClickListener {
                ThemeManager.toggleDarkMode(this@PrayersActivity)
                recreate()
            }
        }
        
        headerLayout.addView(backButton)
        headerLayout.addView(titleText)
        headerLayout.addView(toggleButton)
        return headerLayout
    }
    
    private fun createInfoCard(): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#E8F5E8"))
        }
        
        val infoText = TextView(this).apply {
            text = "Explore prayers from Bahá'u'lláh, Abdul-Bahá, and the Báb organized by purpose and occasion. Tap any category to see the available prayers."
            textSize = 14f
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#2E7D32"))
            setPadding(20, 20, 20, 20)
        }
        
        card.addView(infoText)
        return card
    }
    
    private fun displayPrayerCategories() {
        categoryLayout.removeAllViews()
        
        prayerCategories.forEach { category ->
            val categoryCard = createCategoryCard(category)
            categoryLayout.addView(categoryCard)
            categoryLayout.addView(createSpacing(15))
        }
    }
    
    private fun createCategoryCard(category: PrayerCategory): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 6f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#FFFFFF"))
            isClickable = true
            isFocusable = true
            setOnClickListener { showPrayerList(category) }
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        val titleView = TextView(this).apply {
            text = category.name
            textSize = 18f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(0, 0, 0, 8)
        }
        
        val descView = TextView(this).apply {
            text = category.description
            textSize = 14f
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
            setPadding(0, 0, 0, 8)
        }
        
        val countView = TextView(this).apply {
            text = "${category.prayers.size} prayers available"
            textSize = 12f
            setTextColor(if (isDarkMode) Color.parseColor("#81C784") else Color.parseColor("#4CAF50"))
        }
        
        layout.addView(titleView)
        layout.addView(descView)
        layout.addView(countView)
        card.addView(layout)
        
        return card
    }
    
    private fun showPrayerList(category: PrayerCategory) {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle(category.name)
            .setMessage("Select a prayer to read:")
            .create()
        
        val scrollView = ScrollView(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        category.prayers.forEach { prayer ->
            val prayerButton = Button(this).apply {
                text = "${prayer.title}\nby ${prayer.author}"
                textSize = 14f
                setBackgroundColor(if (isDarkMode) Color.parseColor("#1565C0") else Color.parseColor("#1976D2"))
                setTextColor(Color.WHITE)
                setPadding(15, 15, 15, 15)
                setOnClickListener {
                    dialog.dismiss()
                    showPrayerReader(prayer)
                }
            }
            layout.addView(prayerButton)
            layout.addView(createSpacing(10))
        }
        
        scrollView.addView(layout)
        dialog.setView(scrollView)
        dialog.show()
    }
    
    private fun showPrayerReader(prayer: Prayer) {
        val intent = Intent(this, PrayerReaderActivity::class.java).apply {
            putExtra("prayer_title", prayer.title)
            putExtra("prayer_author", prayer.author)
            putExtra("prayer_text", prayer.text)
        }
        startActivity(intent)
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

// Data models
data class PrayerCategory(val name: String, val description: String, val prayers: List<Prayer>)
data class Prayer(val title: String, val author: String, val text: String)