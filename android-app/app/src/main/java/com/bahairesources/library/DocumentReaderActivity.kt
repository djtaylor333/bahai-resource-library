package com.bahairesources.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.graphics.Color
import android.graphics.Typeface
import androidx.cardview.widget.CardView
import android.view.View
import java.io.InputStreamReader
import android.content.Intent
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.util.Linkify

class DocumentReaderActivity : AppCompatActivity() {
    
    private lateinit var documentTextView: TextView
    private var currentTextSize = 16f
    private var isDarkMode = false
    private var currentFontSize = SettingsManager.FONT_MEDIUM
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize settings
        isDarkMode = SettingsManager.isDarkMode(this)
        currentFontSize = SettingsManager.getFontSize(this)
        currentTextSize = currentFontSize
        
        val title = intent.getStringExtra("document_title") ?: "Document"
        val category = intent.getStringExtra("document_category") ?: "Unknown"
        val fileName = intent.getStringExtra("document_filename") ?: ""
        
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, 0)
            setBackgroundColor(if (isDarkMode) Color.parseColor("#121212") else Color.parseColor("#FAFAFA"))
        }
        
        // Header with controls
        val headerCard = createHeader(title, category)
        
        // Document content area
        val contentCard = CardView(this).apply {
            radius = 0f
            cardElevation = 0f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.WHITE)
        }
        
        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(30, 40, 30, 40)
        }
        
        // Category and source info
        val infoView = TextView(this).apply {
            text = "📂 $category"
            textSize = currentFontSize - 2f
            setTextColor(if (isDarkMode) Color.parseColor("#AAAAAA") else Color.parseColor("#666666"))
            setPadding(0, 0, 0, 20)
            setTypeface(typeface, Typeface.ITALIC)
        }
        
        // Document text
        documentTextView = TextView(this).apply {
            val contentText = loadDocumentContent(fileName)
            setTextWithClickableUrls(this, contentText)
            textSize = currentTextSize
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            setLineSpacing(8f, 1.5f)
        }
        
        contentLayout.addView(infoView)
        contentLayout.addView(documentTextView)
        contentCard.addView(contentLayout)
        
        mainLayout.addView(headerCard)
        mainLayout.addView(contentCard)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
    
    private fun createHeader(title: String, category: String): CardView {
        val headerCard = CardView(this).apply {
            radius = 0f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#2C2C2C") else Color.parseColor("#1976D2"))
        }
        
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 30, 20, 20)
        }
        
        // Top row with back button and controls
        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        
        val backButton = Button(this).apply {
            text = "← Back"
            setBackgroundColor(Color.parseColor("#1565C0"))
            setTextColor(Color.WHITE)
            setPadding(20, 10, 20, 10)
            textSize = currentFontSize
            setOnClickListener { finish() }
        }
        
        val spacer = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, 0, 1f)
        }
        
        val settingsButton = Button(this).apply {
            text = "⚙️"
            setBackgroundColor(Color.parseColor("#37474F"))
            setTextColor(Color.WHITE)
            setPadding(15, 10, 15, 10)
            textSize = currentFontSize
            setOnClickListener {
                startActivity(Intent(this@DocumentReaderActivity, SettingsActivity::class.java))
            }
        }
        
        val bookmarkButton = Button(this).apply {
            text = "🔖"
            setBackgroundColor(Color.parseColor("#4CAF50"))
            setTextColor(Color.WHITE)
            setPadding(15, 10, 15, 10)
            textSize = currentFontSize
            setOnClickListener { addBookmark() }
        }
        
        val shareButton = Button(this).apply {
            text = "📤"
            setBackgroundColor(Color.parseColor("#FF9800"))
            setTextColor(Color.WHITE)
            setPadding(15, 10, 15, 10)
            textSize = currentFontSize
            setOnClickListener { shareDocument() }
        }
        
        val modeButton = Button(this).apply {
            text = if (isDarkMode) "☀️" else "🌙"
            setBackgroundColor(Color.parseColor("#9C27B0"))
            setTextColor(Color.WHITE)
            setPadding(15, 10, 15, 10)
            textSize = currentFontSize
            setOnClickListener { toggleMode() }
        }
        
        topRow.addView(backButton)
        topRow.addView(spacer)
        topRow.addView(settingsButton)
        topRow.addView(View(this).apply { layoutParams = LinearLayout.LayoutParams(10, 0) })
        topRow.addView(bookmarkButton)
        topRow.addView(View(this).apply { layoutParams = LinearLayout.LayoutParams(10, 0) })
        topRow.addView(shareButton)
        topRow.addView(View(this).apply { layoutParams = LinearLayout.LayoutParams(10, 0) })
        topRow.addView(modeButton)
        
        // Title
        val titleView = TextView(this).apply {
            text = title
            textSize = currentFontSize + 4f
            setTextColor(Color.WHITE)
            setPadding(0, 20, 0, 5)
            setTypeface(typeface, Typeface.BOLD)
        }
        
        // Category
        val categoryView = TextView(this).apply {
            text = category
            textSize = currentFontSize
            setTextColor(Color.parseColor("#BBDEFB"))
            setPadding(0, 0, 0, 10)
        }
        
        // Text size controls
        val controlsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 15, 0, 0)
        }
        
        val sizeLabel = TextView(this).apply {
            text = "Text Size: "
            textSize = currentFontSize - 2f
            setTextColor(Color.WHITE)
        }
        
        val minusButton = Button(this).apply {
            text = "A-"
            setBackgroundColor(Color.parseColor("#1565C0"))
            setTextColor(Color.WHITE)
            setPadding(15, 5, 15, 5)
            textSize = currentFontSize - 2f
            setOnClickListener { adjustTextSize(-2f) }
        }
        
        val plusButton = Button(this).apply {
            text = "A+"
            setBackgroundColor(Color.parseColor("#1565C0"))
            setTextColor(Color.WHITE)
            setPadding(15, 5, 15, 5)
            textSize = currentFontSize - 2f
            setOnClickListener { adjustTextSize(2f) }
        }
        
        val searchButton = Button(this).apply {
            text = "🔍 Search in Doc"
            setBackgroundColor(Color.parseColor("#1565C0"))
            setTextColor(Color.WHITE) 
            setPadding(20, 5, 20, 5)
            textSize = currentFontSize - 2f
            setOnClickListener { searchInDocument() }
        }
        
        controlsLayout.addView(sizeLabel)
        controlsLayout.addView(minusButton)
        controlsLayout.addView(View(this).apply { layoutParams = LinearLayout.LayoutParams(10, 0) })
        controlsLayout.addView(plusButton)
        controlsLayout.addView(View(this).apply { layoutParams = LinearLayout.LayoutParams(20, 0) })
        controlsLayout.addView(searchButton)
        
        headerLayout.addView(topRow)
        headerLayout.addView(titleView)
        headerLayout.addView(categoryView)
        headerLayout.addView(controlsLayout)
        
        headerCard.addView(headerLayout)
        return headerCard
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
                        Toast.makeText(this@DocumentReaderActivity, "Could not open link", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            
            spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#4285F4")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
    
    private fun loadDocumentContent(fileName: String): String {
        return try {
            // For demo purposes, return sample content with placeholder for actual files
            when {
                fileName.contains("Kitab-i-Aqdas") -> {
                    """THE KITAB-I-AQDAS

The Most Holy Book

By Bahá'u'lláh

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1. The first duty prescribed by God for His servants is the recognition of Him Who is the Dayspring of His Revelation and the Fountain of His laws, Who representeth the Godhead in both the Kingdom of His Cause and the world of creation.

2. It behoveth every one who reacheth this most sublime station, this summit of transcendent glory, to observe every ordinance of Him Who is the Desire of the world.

[This is sample content for demonstration. The complete Kitab-i-Aqdas is available through official Bahá'í sources at bahai.org/library]

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 

COPYRIGHT NOTICE

This text is available from official Bahá'í sources. For complete authentic texts, please visit:
• https://www.bahai.org/library/
• Local Bahá'í centers and libraries
• Official Bahá'í publishing trusts"""
                }
                fileName.contains("Some Answered Questions") -> {
                    "SOME ANSWERED QUESTIONS\\n\\nBy Abdul-Bahá\\n\\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\\n\\nCHAPTER 1 - THE DIVINE UNITY\\n\\nQuestion: What is the reality of Divinity, and what are the divine perfections?\\n\\nAnswer: This is a vast question and a weighty problem. Know that the Reality of Divinity or the substance of the Essence of Unity is pure sanctity and absolute holiness—that is to say, it is sanctified and exempt from all praise.\\n\\n[This is sample content for demonstration. Complete text available through official Bahá'í sources at bahai.org/library]\\n\\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                }
                fileName.contains("Ruhi") -> {
                    "RUHI INSTITUTE BOOK\\n\\nStudy Materials for Spiritual Education\\n\\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\\n\\nUNIT 1 - UNDERSTANDING THE NATURE OF STUDY\\n\\nThe purpose of this book is to develop the capabilities needed to serve the Faith with devotion, effectiveness, and unity.\\n\\n[Sample content for demonstration. Complete Ruhi materials available through official channels]\\n\\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                }
                else -> {
                    "BAHÁ'Í TEXT READER\\n\\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\\n\\nThis document reader is designed to display Bahá'í texts in a beautiful, readable format.\\n\\nFor complete official texts, please visit:\\n• bahai.org/library\\n• Local Bahá'í centers\\n• Official Bahá'í publications\\n\\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\\n\\n[Document content would be loaded here from official sources]"
                }
            }
        } catch (e: Exception) {
            "Error loading document: ${e.message}\\n\\nPlease try again or contact support."
        }
    }
    
    private fun adjustTextSize(delta: Float) {
        currentTextSize = (currentTextSize + delta).coerceIn(12f, 32f)
        documentTextView.textSize = currentTextSize
        
        Toast.makeText(this, "Text size: ${currentTextSize.toInt()}sp", Toast.LENGTH_SHORT).show()
    }
    
    private fun addBookmark() {
        val currentDocument = intent.getStringExtra("document_filename") ?: ""
        val documentId = currentDocument.replace(".txt", "").replace("-", "_")
        
        if (currentDocument.isEmpty()) {
            Toast.makeText(this, "No document loaded", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Enhanced bookmark dialog
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("📑 Add Bookmark")
            .setMessage("Add a bookmark at current position")
            
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 30, 50, 30)
        }
        
        val noteInput = EditText(this).apply {
            hint = "Add optional note..."
            textSize = currentFontSize
            setPadding(15, 15, 15, 15)
            setBackgroundColor(if (isDarkMode) Color.parseColor("#2D2D2D") else Color.parseColor("#F5F5F5"))
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
        }
        
        layout.addView(TextView(this).apply {
            text = "Document: ${intent.getStringExtra("document_title")}"
            textSize = currentFontSize - 1f
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
            setPadding(0, 0, 0, 15)
        })
        
        layout.addView(noteInput)
        
        dialog.setView(layout)
            .setPositiveButton("📌 Save Bookmark") { _, _ ->
                val note = noteInput.text.toString().trim()
                val selectedText = getSelectedText() ?: "Bookmark at current position"
                val position = 0 // In a real implementation, get scroll position
                
                saveBookmark(documentId, position, selectedText, note)
                Toast.makeText(this, "✅ Bookmark saved!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }
    
    private fun saveBookmark(documentId: String, position: Int, text: String, note: String) {
        try {
            val pdfManager = PDFDocumentManager(this)
            pdfManager.addBookmark(documentId, position, text, note)
        } catch (e: Exception) {
            Toast.makeText(this, "❌ Error saving bookmark: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun getSelectedText(): String? {
        // In a real implementation, this would get the currently selected text
        // For demo purposes, return a sample
        return "Sample selected text for bookmark"
    }
    
    private fun shareDocument() {
        Toast.makeText(this, "Share functionality coming soon!", Toast.LENGTH_SHORT).show()
    }
    
    private fun searchInDocument() {
        val searchInput = EditText(this).apply {
            hint = "Enter search term..."
            textSize = currentFontSize
            setPadding(20, 20, 20, 20)
            setBackgroundColor(if (isDarkMode) Color.parseColor("#2D2D2D") else Color.parseColor("#FFFFFF"))
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            setHintTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
        }
        
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("🔍 Search in Document")
            .setMessage("Enter a word or phrase to search for in this document:")
            .setView(searchInput)
            .setPositiveButton("Search") { dialog, _ ->
                val searchTerm = searchInput.text.toString().trim()
                if (searchTerm.isNotEmpty()) {
                    performDocumentSearch(searchTerm)
                } else {
                    Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .setNeutralButton("Bookmarks") { _, _ -> showDocumentBookmarks() }
            .create()
        
        dialog.show()
    }
    
    private fun toggleMode() {
        isDarkMode = !isDarkMode
        Toast.makeText(this, "${if (isDarkMode) "Dark" else "Light"} mode - full implementation coming soon!", Toast.LENGTH_SHORT).show()
    }
    
    private fun performDocumentSearch(searchTerm: String) {
        try {
            val currentDocument = intent.getStringExtra("document_filename") ?: ""
            val documentId = currentDocument.replace(".txt", "").replace("-", "_")
            
            if (currentDocument.isNotEmpty()) {
                // Use enhanced PDF Document Manager search
                val pdfManager = PDFDocumentManager(this)
                val searchResults = pdfManager.searchInDocument(documentId, searchTerm)
                
                if (searchResults.isNotEmpty()) {
                    showEnhancedSearchResults(searchTerm, searchResults)
                    return
                }
            }
        } catch (e: Exception) {
            // Fall back to basic search on error
        }
        
        // Fallback to basic text search
        val documentText = documentTextView.text.toString()
        val lowercaseText = documentText.lowercase()
        val lowercaseTerm = searchTerm.lowercase()
        
        var startIndex = 0
        val matches = mutableListOf<Pair<Int, String>>()
        
        while (true) {
            val foundIndex = lowercaseText.indexOf(lowercaseTerm, startIndex)
            if (foundIndex == -1) break
            
            // Get context around the match (30 characters before and after)
            val contextStart = (foundIndex - 30).coerceAtLeast(0)
            val contextEnd = (foundIndex + searchTerm.length + 30).coerceAtMost(documentText.length)
            val context = documentText.substring(contextStart, contextEnd)
            
            matches.add(Pair(foundIndex, context))
            startIndex = foundIndex + 1
        }
        
        if (matches.isEmpty()) {
            Toast.makeText(this, "No matches found for '$searchTerm'", Toast.LENGTH_LONG).show()
        } else {
            showSearchResults(searchTerm, matches)
        }
    }
    
    private fun showEnhancedSearchResults(searchTerm: String, results: List<SearchResult>) {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("🔍 Search Results for '$searchTerm'")
            .setMessage("Found ${results.size} matches:")
            .create()
        
        val scrollView = ScrollView(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        results.take(10).forEachIndexed { index, result ->
            val resultCard = CardView(this).apply {
                radius = 8f
                cardElevation = 2f
                setCardBackgroundColor(if (isDarkMode) Color.parseColor("#2D2D2D") else Color.parseColor("#F5F5F5"))
                setPadding(15, 15, 15, 15)
                isClickable = true
                setOnClickListener {
                    dialog.dismiss()
                    highlightTextAtPosition(result.position, searchTerm)
                }
            }
            
            val resultLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(15, 15, 15, 15)
            }
            
            val matchNumber = TextView(this).apply {
                text = "📍 Line ${result.line} - Match ${index + 1}:"
                textSize = currentFontSize - 1f
                setTextColor(if (isDarkMode) Color.parseColor("#81C784") else Color.parseColor("#4CAF50"))
                setPadding(0, 0, 0, 8)
            }
            
            val contextText = TextView(this).apply {
                text = "...${result.context.trim()}..."
                textSize = currentFontSize
                setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            }
            
            val actionHint = TextView(this).apply {
                text = "💡 Tap to jump to this location"
                textSize = currentFontSize - 2f
                setTextColor(if (isDarkMode) Color.parseColor("#64B5F6") else Color.parseColor("#1976D2"))
                setPadding(0, 5, 0, 0)
            }
            
            resultLayout.addView(matchNumber)
            resultLayout.addView(contextText)
            resultLayout.addView(actionHint)
            resultCard.addView(resultLayout)
            layout.addView(resultCard)
            layout.addView(View(this).apply { 
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 10)
            })
        }
        
        if (results.size > 10) {
            val moreText = TextView(this).apply {
                text = "... and ${results.size - 10} more matches"
                textSize = currentFontSize - 1f
                setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
                setPadding(10, 20, 10, 0)
            }
            layout.addView(moreText)
        }
        
        scrollView.addView(layout)
        dialog.setView(scrollView)
        dialog.show()
    }
    
    private fun showSearchResults(searchTerm: String, matches: List<Pair<Int, String>>) {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("🔍 Search Results: \"$searchTerm\"")
            .setMessage("Found ${matches.size} matches:")
            .create()
        
        val scrollView = ScrollView(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        matches.forEachIndexed { index, (position, context) ->
            val matchCard = CardView(this).apply {
                radius = 8f
                cardElevation = 2f
                setCardBackgroundColor(if (isDarkMode) Color.parseColor("#2D2D2D") else Color.parseColor("#F5F5F5"))
                setPadding(15, 15, 15, 15)
                isClickable = true
                setOnClickListener {
                    dialog.dismiss()
                    highlightTextAtPosition(position, searchTerm)
                }
            }
            
            val matchLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(15, 15, 15, 15)
            }
            
            val matchNumber = TextView(this).apply {
                text = "Match ${index + 1}:"
                textSize = currentFontSize - 1f
                setTextColor(if (isDarkMode) Color.parseColor("#81C784") else Color.parseColor("#4CAF50"))
                setPadding(0, 0, 0, 8)
            }
            
            val contextText = TextView(this).apply {
                text = "...${context.trim()}..."
                textSize = currentFontSize
                setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            }
            
            matchLayout.addView(matchNumber)
            matchLayout.addView(contextText)
            matchCard.addView(matchLayout)
            layout.addView(matchCard)
            layout.addView(View(this).apply { 
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 10)
            })
        }
        
        val instructionText = TextView(this).apply {
            text = "💡 Tap any match to jump to that location in the document"
            textSize = currentFontSize - 2f
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
            setPadding(10, 20, 10, 0)
        }
        
        layout.addView(instructionText)
        scrollView.addView(layout)
        dialog.setView(scrollView)
        dialog.show()
    }
    
    private fun highlightTextAtPosition(position: Int, searchTerm: String) {
        // For demonstration, just scroll to position and show a toast
        Toast.makeText(this, "Jumped to match: '$searchTerm' at position $position", Toast.LENGTH_LONG).show()
    }
    
    private fun showDocumentBookmarks() {
        try {
            val currentDocument = intent.getStringExtra("document_filename") ?: ""
            val documentId = currentDocument.replace(".txt", "").replace("-", "_")
            
            if (currentDocument.isEmpty()) {
                Toast.makeText(this, "No document loaded", Toast.LENGTH_SHORT).show()
                return
            }
            
            val pdfManager = PDFDocumentManager(this)
            val bookmarks = pdfManager.getBookmarks(documentId)
            
            if (bookmarks.isEmpty()) {
                Toast.makeText(this, "📑 No bookmarks found. Add some by tapping the bookmark button!", Toast.LENGTH_LONG).show()
                return
            }
            
            val dialog = android.app.AlertDialog.Builder(this)
                .setTitle("📚 Document Bookmarks")
                .setMessage("${bookmarks.size} bookmark(s) found:")
                .create()
            
            val scrollView = ScrollView(this)
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(20, 20, 20, 20)
            }
            
            bookmarks.forEach { bookmark ->
                val bookmarkCard = CardView(this).apply {
                    radius = 8f
                    cardElevation = 3f
                    setCardBackgroundColor(if (isDarkMode) Color.parseColor("#2D2D2D") else Color.parseColor("#FFF8E1"))
                    setPadding(15, 15, 15, 15)
                    isClickable = true
                    setOnClickListener {
                        dialog.dismiss()
                        highlightTextAtPosition(bookmark.position, bookmark.text)
                        Toast.makeText(this@DocumentReaderActivity, "📍 Jumped to bookmark: ${bookmark.text.take(30)}...", Toast.LENGTH_SHORT).show()
                    }
                }
                
                val bookmarkLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(15, 15, 15, 15)
                }
                
                val bookmarkIcon = TextView(this).apply {
                    text = "🔖"
                    textSize = currentFontSize + 2f
                    setPadding(0, 0, 0, 8)
                }
                
                val bookmarkText = TextView(this).apply {
                    text = bookmark.text.take(80) + if (bookmark.text.length > 80) "..." else ""
                    textSize = currentFontSize
                    setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1B5E20"))
                    setPadding(0, 0, 0, 5)
                    setTypeface(typeface, Typeface.BOLD)
                }
                
                if (bookmark.note.isNotEmpty()) {
                    val noteText = TextView(this).apply {
                        text = "📝 Note: ${bookmark.note}"
                        textSize = currentFontSize - 1f
                        setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#424242"))
                        setPadding(0, 5, 0, 5)
                        setTypeface(typeface, Typeface.ITALIC)
                    }
                    bookmarkLayout.addView(noteText)
                }
                
                val timestampText = TextView(this).apply {
                    text = "📅 Created: ${java.text.DateFormat.getDateTimeInstance().format(bookmark.timestamp)}"
                    textSize = currentFontSize - 2f
                    setTextColor(if (isDarkMode) Color.parseColor("#888888") else Color.parseColor("#666666"))
                    setPadding(0, 5, 0, 0)
                }
                
                val tapHint = TextView(this).apply {
                    text = "💡 Tap to jump to this bookmark"
                    textSize = currentFontSize - 2f
                    setTextColor(if (isDarkMode) Color.parseColor("#64B5F6") else Color.parseColor("#1976D2"))
                    setPadding(0, 5, 0, 0)
                }
                
                bookmarkLayout.addView(bookmarkIcon)
                bookmarkLayout.addView(bookmarkText)
                bookmarkLayout.addView(timestampText)
                bookmarkLayout.addView(tapHint)
                bookmarkCard.addView(bookmarkLayout)
                
                layout.addView(bookmarkCard)
                layout.addView(View(this).apply { 
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 12)
                })
            }
            
            val instructionText = TextView(this).apply {
                text = "✨ Total: ${bookmarks.size} bookmark(s)\n💡 Tap any bookmark to jump to its location"
                textSize = currentFontSize - 1f
                setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
                setPadding(10, 20, 10, 0)
            }
            
            layout.addView(instructionText)
            scrollView.addView(layout)
            dialog.setView(scrollView)
            dialog.show()
            
        } catch (e: Exception) {
            Toast.makeText(this, "❌ Error loading bookmarks: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}