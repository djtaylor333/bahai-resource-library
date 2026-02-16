package com.bahairesources.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.graphics.Color
import androidx.cardview.widget.CardView
import java.text.SimpleDateFormat
import java.util.*

class FeastResourcesActivity : AppCompatActivity() {
    
    private var isDarkMode = false
    private var currentFontSize = SettingsManager.FONT_MEDIUM
    
    // Bahá'í feast information
    private val feastData = listOf(
        FeastInfo("Bahá", "Splendor", "Mar 21 - Apr 8", 
            "The first month of the Bahá'í year, representing the splendor and glory of God. A time of spiritual renewal and new beginnings."),
        FeastInfo("Jalál", "Glory", "Apr 9 - Apr 27", 
            "Signifies the glory and majesty of God. A time to reflect on divine attributes and spiritual advancement."),
        FeastInfo("Jamál", "Beauty", "Apr 28 - May 16", 
            "Represents divine beauty and perfection. A month for contemplating the beauty of creation and spiritual truth."),
        FeastInfo("'Aẓamat", "Grandeur", "May 17 - Jun 4", 
            "Embodies the grandeur and magnificence of the divine. A time for recognizing God's greatness in all things."),
        FeastInfo("Núr", "Light", "Jun 5 - Jun 23", 
            "The month of divine light and illumination. A period for seeking spiritual enlightenment and guidance."),
        FeastInfo("Raḥmat", "Mercy", "Jun 24 - Jul 12", 
            "Represents God's infinite mercy and compassion. A time for extending kindness and forgiveness to all."),
        FeastInfo("Kalimát", "Words", "Jul 13 - Jul 31", 
            "Honors the power of divine words and revelation. A month for studying sacred writings and spiritual discourse."),
        FeastInfo("Kamál", "Perfection", "Aug 1 - Aug 19", 
            "Represents divine perfection and completion. A time for striving toward spiritual excellence and unity."),
        FeastInfo("Asmá'", "Names", "Aug 20 - Sep 7", 
            "Celebrates the names and attributes of God. A period for meditation on divine qualities and characteristics."),
        FeastInfo("'Izzat", "Might", "Sep 8 - Sep 26", 
            "Embodies divine might and power. A time for drawing strength from faith and serving the common good."),
        FeastInfo("Mashíyyat", "Will", "Sep 27 - Oct 15", 
            "Represents the divine will and purpose. A month for aligning human will with God's will."),
        FeastInfo("'Ilm", "Knowledge", "Oct 16 - Nov 3", 
            "The month of divine knowledge and wisdom. A time for learning, education, and the pursuit of truth."),
        FeastInfo("Qudrat", "Power", "Nov 4 - Nov 22", 
            "Signifies divine power and omnipotence. A period for recognizing God's power in transformation and growth."),
        FeastInfo("Qawl", "Speech", "Nov 23 - Dec 11", 
            "Honors divine speech and communication. A month for thoughtful discourse and meaningful conversation."),
        FeastInfo("Masá'il", "Questions", "Dec 12 - Dec 30", 
            "Represents inquiry and spiritual questioning. A time for deepening understanding through investigation."),
        FeastInfo("Sharaf", "Honor", "Dec 31 - Jan 18", 
            "Embodies divine honor and dignity. A month for recognizing the nobility of the human station."),
        FeastInfo("Sulṭán", "Sovereignty", "Jan 19 - Feb 6", 
            "Represents divine sovereignty and rule. A time for acknowledging God's supreme authority over all creation."),
        FeastInfo("Mulk", "Dominion", "Feb 7 - Feb 25", 
            "Signifies God's dominion over all existence. A period for understanding divine governance and order."),
        FeastInfo("'Alá'", "Loftiness", "Mar 2 - Mar 20", 
            "The month of divine loftiness and elevation. A time for spiritual ascent and drawing closer to God.",
            isLastMonth = true)
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
        
        // Introduction card
        val introCard = createIntroductionCard()
        
        // Current feast info
        val currentFeastCard = createCurrentFeastCard()
        
        // All feasts list
        val allFeastsCard = createAllFeastsCard()
        
        // Feast preparation guide
        val preparationCard = createPreparationCard()
        
        mainLayout.addView(headerLayout)
        mainLayout.addView(createSpacing(20))
        mainLayout.addView(introCard)
        mainLayout.addView(createSpacing(15))
        mainLayout.addView(currentFeastCard)
        mainLayout.addView(createSpacing(15))
        mainLayout.addView(allFeastsCard)
        mainLayout.addView(createSpacing(15))
        mainLayout.addView(preparationCard)
        
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
            text = "🌙 Feast Resources"
            textSize = currentFontSize + 6f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(20, 10, 0, 10)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        headerLayout.addView(backButton)
        headerLayout.addView(titleText)
        return headerLayout
    }
    
    private fun createIntroductionCard(): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#E8F5E8"))
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        val introText = TextView(this).apply {
            text = """
                📅 The Nineteen Day Feast
                
                The Nineteen Day Feast is held on the first day of each Bahá'í month and serves three essential purposes:
                
                • Devotional: Prayers and readings from sacred texts
                • Administrative: Community consultation and announcements  
                • Social: Fellowship and unity among community members
                
                Each feast begins at sunset and creates an atmosphere of spiritual communion and community unity.
            """.trimIndent()
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#2E7D32"))
        }
        
        layout.addView(introText)
        card.addView(layout)
        return card
    }
    
    private fun getCurrentFeast(): FeastInfo {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        // Simplified logic - in a real app this would use proper Bahá'í calendar calculations
        return when {
            (month == 3 && day >= 21) || (month == 4 && day <= 8) -> feastData[0] // Bahá
            (month == 4 && day >= 9 && day <= 27) -> feastData[1] // Jalál
            (month == 4 && day >= 28) || (month == 5 && day <= 16) -> feastData[2] // Jamál
            else -> feastData[0] // Default to Bahá
        }
    }
    
    private fun createCurrentFeastCard(): CardView {
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
            text = "🌟 Current Feast Period"
            textSize = currentFontSize + 2f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(0, 0, 0, 15)
        }
        
        val currentFeast = getCurrentFeast()
        val feastCard = createFeastCard(currentFeast, true)
        
        layout.addView(titleView)
        layout.addView(feastCard)
        card.addView(layout)
        return card
    }
    
    private fun createAllFeastsCard(): CardView {
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
            text = "📅 All Bahá'í Feasts (19 Months)"
            textSize = currentFontSize + 2f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(0, 0, 0, 15)
        }
        
        layout.addView(titleView)
        
        feastData.forEach { feast ->
            val feastCard = createFeastCard(feast, false)
            layout.addView(feastCard)
            layout.addView(createSpacing(10))
        }
        
        card.addView(layout)
        return card
    }
    
    private fun createFeastCard(feast: FeastInfo, isHighlighted: Boolean): LinearLayout {
        val feastLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(15, 15, 15, 15)
            setBackgroundColor(
                if (isHighlighted) {
                    if (isDarkMode) Color.parseColor("#2E7D32") else Color.parseColor("#E8F5E8")
                } else {
                    if (isDarkMode) Color.parseColor("#2D2D2D") else Color.parseColor("#F8F9FA")
                }
            )
        }
        
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        
        val nameView = TextView(this).apply {
            text = feast.arabicName
            textSize = currentFontSize + 1f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val englishView = TextView(this).apply {
            text = feast.englishMeaning
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
        }
        
        headerLayout.addView(nameView)
        headerLayout.addView(englishView)
        
        val datesView = TextView(this).apply {
            text = "📅 ${feast.dates}"
            textSize = currentFontSize - 1f
            setTextColor(if (isDarkMode) Color.parseColor("#81C784") else Color.parseColor("#4CAF50"))
            setPadding(0, 5, 0, 5)
        }
        
        val meaningView = TextView(this).apply {
            text = feast.meaning
            textSize = currentFontSize - 1f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            setPadding(0, 5, 0, 0)
        }
        
        feastLayout.addView(headerLayout)
        feastLayout.addView(datesView)
        feastLayout.addView(meaningView)
        
        if (feast.isLastMonth) {
            val specialView = TextView(this).apply {
                text = "Note: Followed by Ayyám-i-Há (Intercalary Days) Feb 26 - Mar 1"
                textSize = currentFontSize - 2f
                setTextColor(if (isDarkMode) Color.parseColor("#FFB74D") else Color.parseColor("#FF9800"))
                setPadding(0, 10, 0, 0)
            }
            feastLayout.addView(specialView)
        }
        
        return feastLayout
    }
    
    private fun createPreparationCard(): CardView {
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
            text = "📝 Feast Preparation Guide"
            textSize = currentFontSize + 2f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(0, 0, 0, 15)
        }
        
        val guideText = TextView(this).apply {
            text = """
                🕊️ Devotional Portion:
                • Select prayers and readings from Bahá'í writings
                • Include prayers for unity, spiritual progress, and the community
                • Consider readings that reflect the month's spiritual theme
                
                🏛️ Administrative Portion:
                • Community announcements and news
                • Consultation on local activities and initiatives  
                • Reports from various committees and projects
                • Planning for upcoming events and holy days
                
                🤝 Social Portion:
                • Fellowship and refreshments
                • Music, arts, and cultural expressions
                • Time for community members to connect and unite
                • Welcoming of guests and new community members
                
                ⏰ Timing:
                • Begin at sunset on the first day of each Bahá'í month
                • Allow 1.5-2 hours for all three portions
                • Ensure balanced time for each element
            """.trimIndent()
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
        }
        
        layout.addView(titleView)
        layout.addView(guideText)
        card.addView(layout)
        return card
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

data class FeastInfo(
    val arabicName: String,
    val englishMeaning: String, 
    val dates: String,
    val meaning: String,
    val isLastMonth: Boolean = false
)