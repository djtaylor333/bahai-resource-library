package com.bahairesources.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import androidx.cardview.widget.CardView
import android.content.Intent

class SearchActivity : AppCompatActivity() {
    
    private lateinit var searchInput: EditText
    private lateinit var resultsLayout: LinearLayout
    private lateinit var resultsCount: TextView
    private var isDarkMode = false
    private var currentFontSize = SettingsManager.FONT_MEDIUM
    
    // Sample document data - in a real app this would come from a database
    private val documents = listOf(
        Document("The Kitab-i-Aqdas", "Central Figures", "The Most Holy Book - the central book of laws of the Bahá'í Faith"),
        Document("The Book of Certitude", "Central Figures", "Kitab-i-Iqan - Bahá'u'lláh's primary theological work"),
        Document("Gleanings", "Central Figures", "A selection of passages from the writings of Bahá'u'lláh"),
        Document("The Seven Valleys", "Central Figures", "Bahá'u'lláh's mystical treatise on the spiritual journey"),
        Document("The Hidden Words", "Central Figures", "Spiritual aphorisms revealed by Bahá'u'lláh"),
        Document("Prayers and Meditations", "Central Figures", "A collection of prayers revealed by Bahá'u'lláh"),
        Document("Some Answered Questions", "Central Figures", "Abdul-Bahá's explanations of Bahá'í teachings"),
        Document("The Secret of Divine Civilization", "Central Figures", "Abdul-Bahá's treatise on social progress"),
        Document("Selections from the Writings of Abdul-Bahá", "Central Figures", "Key passages from Abdul-Bahá's works"),
        Document("The Will and Testament", "Central Figures", "Abdul-Bahá's final written legacy"),
        Document("God Passes By", "Administrative", "Shoghi Effendi's history of the first Bahá'í century"),
        Document("The World Order of Bahá'u'lláh", "Administrative", "Shoghi Effendi's vision of the Bahá'í administrative order"),
        Document("The Advent of Divine Justice", "Administrative", "Shoghi Effendi's call to the American Bahá'ís"),
        Document("Citadel of Faith", "Administrative", "Messages from Shoghi Effendi to America"),
        Document("Messages to the Bahá'í World", "Administrative", "Communications from the Universal House of Justice"),
        Document("The Promise of World Peace", "Administrative", "Statement to the peoples of the world"),
        Document("Ruhi Book 1", "Ruhi Institute", "Reflections on the Life of the Spirit"),
        Document("Ruhi Book 2", "Ruhi Institute", "Arising to Serve"),
        Document("Ruhi Book 3", "Ruhi Institute", "Teaching Children's Classes"),
        Document("Ruhi Book 4", "Ruhi Institute", "The Twin Manifestations"),
        Document("Ruhi Book 5", "Ruhi Institute", "Releasing the Powers of Junior Youth"),
        Document("Ruhi Book 6", "Ruhi Institute", "Teaching the Cause"),
        Document("Ruhi Book 7", "Ruhi Institute", "Walking Together on a Path of Service"),
        Document("Bahá'í Prayers", "Devotional", "A collection of prayers for various occasions"),
        Document("Fire and Light", "Devotional", "Selections from the writings for devotional gatherings"),
        Document("The Bahá'í Faith: A Short Study", "Study Materials", "Introduction to Bahá'í beliefs and practices"),
        Document("Compilation on Scholarship", "Compilations", "Guidance on Bahá'í scholarship and academic pursuits"),
        Document("Compilation on Women", "Compilations", "Bahá'í teachings on the equality of women and men")
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
            setPadding(10, 10, 10, 20)
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
            text = "🔍 Search Library"
            textSize = currentFontSize + 4f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(20, 10, 0, 10)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val settingsButton = Button(this).apply {
            text = "⚙️"
            textSize = currentFontSize
            setBackgroundColor(if (isDarkMode) Color.parseColor("#37474F") else Color.parseColor("#E0E0E0"))
            setTextColor(if (isDarkMode) Color.WHITE else Color.parseColor("#333333"))
            setPadding(15, 10, 15, 10)
            setOnClickListener {
                startActivity(Intent(this@SearchActivity, SettingsActivity::class.java))
            }
        }
        
        headerLayout.addView(backButton)
        headerLayout.addView(titleText)
        headerLayout.addView(settingsButton)
        
        // Search Input
        searchInput = EditText(this).apply {
            hint = "Search documents, authors, topics..."
            textSize = 16f
            setPadding(20, 20, 20, 20)
            setBackgroundColor(if (isDarkMode) Color.parseColor("#2D2D2D") else Color.WHITE)
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            setHintTextColor(if (isDarkMode) Color.parseColor("#888888") else Color.parseColor("#999999"))
        }
        
        val searchCard = CardView(this).apply {
            radius = 8f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#2D2D2D") else Color.WHITE)
            addView(searchInput)
        }
        
        // Results count
        resultsCount = TextView(this).apply {
            text = "Enter search terms to find documents"
            textSize = 14f
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
            setPadding(10, 20, 10, 10)
        }
        
        // Results container
        resultsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        
        mainLayout.addView(headerLayout)
        mainLayout.addView(searchCard)
        mainLayout.addView(resultsCount)
        mainLayout.addView(resultsLayout)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
        
        // Set up search functionality
        setupSearch()
        
        // Show all documents initially
        displayResults(documents)
    }
    
    private fun setupSearch() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim() ?: ""
                if (query.isEmpty()) {
                    displayResults(documents)
                    return
                }
                
                val filteredDocs = documents.filter { doc ->
                    doc.title.contains(query, ignoreCase = true) ||
                    doc.category.contains(query, ignoreCase = true) ||
                    doc.description.contains(query, ignoreCase = true)
                }
                
                displayResults(filteredDocs)
            }
        })
    }
    
    private fun displayResults(docs: List<Document>) {
        resultsLayout.removeAllViews()
        
        resultsCount.text = if (docs.isEmpty()) {
            "No documents found"
        } else {
            "Found ${docs.size} document${if (docs.size != 1) "s" else ""}"
        }
        
        docs.forEach { doc ->
            val card = createDocumentCard(doc)
            resultsLayout.addView(card)
            resultsLayout.addView(createSpacing(10))
        }
    }
    
    private fun createDocumentCard(doc: Document): CardView {
        val card = CardView(this).apply {
            radius = 8f
            cardElevation = 3f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#2D2D2D") else Color.WHITE)
            isClickable = true
            isFocusable = true
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        val titleView = TextView(this).apply {
            text = doc.title
            textSize = 16f
            setTextColor(if (isDarkMode) Color.parseColor("#64B5F6") else Color.parseColor("#1976D2"))
            setPadding(0, 0, 0, 8)
        }
        
        val categoryView = TextView(this).apply {
            text = "📂 ${doc.category}"
            textSize = 12f
            setTextColor(if (isDarkMode) Color.parseColor("#FFB74D") else Color.parseColor("#FF9800"))
            setPadding(0, 0, 0, 8)
        }
        
        val descView = TextView(this).apply {
            text = doc.description
            textSize = 14f
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
        }
        
        layout.addView(titleView)
        layout.addView(categoryView)
        layout.addView(descView)
        card.addView(layout)
        
        card.setOnClickListener {
            openDocumentReader(doc)
        }
        
        return card
    }
    
    private fun openDocumentReader(doc: Document) {
        val intent = Intent(this, DocumentReaderActivity::class.java).apply {
            putExtra("document_title", doc.title)
            putExtra("document_category", doc.category)
            putExtra("document_filename", doc.title.lowercase().replace(" ", "_").replace("'", "").replace("-", "_"))
        }
        startActivity(intent)
    }
    
    private fun showDocumentDialog(doc: Document) {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("📖 ${doc.title}")
            .setMessage("Category: ${doc.category}\n\n${doc.description}\n\n" +
                       "Ready to read! Features include:\n" +
                       "• Full text reading with adjustable font size\n" +
                       "• Bookmark and note-taking capabilities\n" +
                       "• Search within document\n" +
                       "• Night mode for comfortable reading")
            .setPositiveButton("Read Document") { _, _ ->
                openDocumentReader(doc)
            }
            .setNeutralButton("Add Bookmark") { _, _ ->
                Toast.makeText(this, "Bookmark added for ${doc.title}!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
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

data class Document(
    val title: String,
    val category: String, 
    val description: String
)