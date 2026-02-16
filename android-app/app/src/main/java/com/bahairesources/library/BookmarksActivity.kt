package com.bahairesources.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.graphics.Color
import androidx.cardview.widget.CardView
import android.content.SharedPreferences
import android.content.Intent

class BookmarksActivity : AppCompatActivity() {
    
    private lateinit var bookmarksLayout: LinearLayout
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var prefs: SharedPreferences
    private lateinit var documentManager: PDFDocumentManager
    private var isDarkMode = false
    private var currentFontSize = SettingsManager.FONT_MEDIUM
    private var realBookmarks = mutableListOf<DocumentBookmark>()
    
    // Sample bookmarks - shown if no real bookmarks exist
    private val sampleBookmarks = listOf(
        Bookmark(
            "The Hidden Words",
            "Arabic #1", 
            "O Son of Spirit! My first counsel is this: Possess a pure, kindly and radiant heart...",
            "Central Figures",
            System.currentTimeMillis() - 86400000L // 1 day ago
        ),
        Bookmark(
            "The Kitab-i-Aqdas",
            "Paragraph 1",
            "The first duty prescribed by God for His servants is the recognition of Him Who is the Dayspring of His Revelation...",
            "Central Figures", 
            System.currentTimeMillis() - 172800000L // 2 days ago
        ),
        Bookmark(
            "Some Answered Questions", 
            "Chapter 27",
            "Question: What is the wisdom of prayer? What is the reason for it? Answer: Prayer is the human spirit's conversation with God...",
            "Central Figures",
            System.currentTimeMillis() - 259200000L // 3 days ago
        )
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize settings and document manager
        isDarkMode = SettingsManager.isDarkMode(this)
        currentFontSize = SettingsManager.getFontSize(this)
        documentManager = PDFDocumentManager(this)
        
        prefs = getSharedPreferences("BookmarksPrefs", MODE_PRIVATE)
        
        // Load real bookmarks from all documents
        loadAllBookmarks()
        
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 30, 20, 30) 
            setBackgroundColor(if (isDarkMode) Color.parseColor("#121212") else Color.parseColor("#F8F9FA"))
        }
        
        // Header
        val headerLayout = createHeader()
        
        // Statistics bar
        val statsBar = createStatsBar()
        
        // Add bookmark button
        val addButton = Button(this).apply {
            text = "➕ Add New Bookmark"
            setBackgroundColor(Color.parseColor("#4CAF50"))
            setTextColor(Color.WHITE)
            textSize = 16f
            setPadding(30, 20, 30, 20)
            setOnClickListener { showAddBookmarkDialog() }
        }
        
        val addButtonCard = CardView(this).apply {
            radius = 8f
            cardElevation = 4f
            setCardBackgroundColor(Color.parseColor("#4CAF50"))
            addView(addButton)
        }
        
        // Empty state (shown when no bookmarks)
        emptyStateLayout = createEmptyState()
        
        // Bookmarks container
        bookmarksLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        
        mainLayout.addView(headerLayout)
        mainLayout.addView(statsBar)
        mainLayout.addView(createSpacing(20))
        mainLayout.addView(addButtonCard)
        mainLayout.addView(createSpacing(20))
        mainLayout.addView(emptyStateLayout)
        mainLayout.addView(bookmarksLayout)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
        
        // Load and display bookmarks
        displayBookmarks()
    }
    
    private fun createHeader(): LinearLayout {
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(10, 10, 10, 20)
        }
        
        // Top navigation row
        val navRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
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
            text = "🔖 My Bookmarks"
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
                startActivity(Intent(this@BookmarksActivity, SettingsActivity::class.java))
            }
        }
        
        navRow.addView(backButton)
        navRow.addView(titleText)
        navRow.addView(settingsButton)
        
        // Search row
        val searchRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 15, 0, 0)
        }
        
        val searchInput = EditText(this).apply {
            hint = "Search bookmarks..."
            textSize = currentFontSize
            setPadding(15, 12, 15, 12)
            setBackgroundColor(if (isDarkMode) Color.parseColor("#2D2D2D") else Color.parseColor("#FFFFFF"))
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            setHintTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val searchButton = Button(this).apply {
            text = "🔍"
            textSize = currentFontSize
            setBackgroundColor(if (isDarkMode) Color.parseColor("#4CAF50") else Color.parseColor("#2E7D32"))
            setTextColor(Color.WHITE)
            setPadding(15, 12, 15, 12)
            setOnClickListener { performBookmarkSearch(searchInput.text.toString()) }
        }
        
        searchRow.addView(searchInput)
        searchRow.addView(searchButton)
        
        headerLayout.addView(navRow)
        headerLayout.addView(searchRow)
        
        return headerLayout
    }
    
    private fun createStatsBar(): CardView {
        val card = CardView(this).apply {
            radius = 8f
            cardElevation = 3f
            setCardBackgroundColor(Color.parseColor("#E8F5E8"))
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(20, 15, 20, 15)
        }
        
        val statsText = TextView(this).apply {
            text = "📊 ${sampleBookmarks.size} bookmarks saved • Last added ${getRelativeTime(sampleBookmarks.maxByOrNull { it.timestamp }?.timestamp ?: 0)}"
            textSize = 14f
            setTextColor(Color.parseColor("#2E7D32"))
        }
        
        layout.addView(statsText)
        card.addView(layout)
        
        return card
    }
    
    private fun createEmptyState(): LinearLayout {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 60, 40, 60)
            gravity = android.view.Gravity.CENTER
            visibility = if (sampleBookmarks.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        }
        
        val iconText = TextView(this).apply {
            text = "🔖"
            textSize = 48f
            gravity = android.view.Gravity.CENTER
            setPadding(0, 0, 0, 20)
        }
        
        val titleText = TextView(this).apply {
            text = "No Bookmarks Yet"
            textSize = 20f
            setTextColor(Color.parseColor("#666666"))
            gravity = android.view.Gravity.CENTER
            setPadding(0, 0, 0, 10)
        }
        
        val descText = TextView(this).apply {
            text = "Start exploring documents and bookmark your favorite passages!"
            textSize = 14f
            setTextColor(Color.parseColor("#999999"))
            gravity = android.view.Gravity.CENTER
        }
        
        layout.addView(iconText)
        layout.addView(titleText)
        layout.addView(descText)
        
        return layout
    }
    
    private fun loadAllBookmarks() {
        realBookmarks.clear()
        // Load bookmarks from all documents
        PDFDocumentManager.RESEARCH_DOCUMENTS.forEach { docSource ->
            val docBookmarks = documentManager.getBookmarks(docSource.id)
            realBookmarks.addAll(docBookmarks)
        }
    }
    
    private fun displayBookmarks() {
        bookmarksLayout.removeAllViews()
        
        // Use real bookmarks if available
        if (realBookmarks.isNotEmpty()) {
            // Convert DocumentBookmarks to display format
            realBookmarks.sortedByDescending { it.timestamp.time }.forEach { docBookmark ->
                val card = createRealBookmarkCard(docBookmark)
                bookmarksLayout.addView(card)
                bookmarksLayout.addView(createSpacing(12))
            }
            emptyStateLayout.visibility = android.view.View.GONE
        } else if (sampleBookmarks.isNotEmpty()) {
            // Show sample bookmarks
            sampleBookmarks.sortedByDescending { it.timestamp }.forEach { bookmark ->
                val card = createBookmarkCard(bookmark)
                bookmarksLayout.addView(card)
                bookmarksLayout.addView(createSpacing(12))
            }
            emptyStateLayout.visibility = android.view.View.GONE
        } else {
            // Show empty state if no bookmarks at all
            emptyStateLayout.visibility = android.view.View.VISIBLE
        }
    }
    
    private fun createRealBookmarkCard(bookmark: DocumentBookmark): CardView {
        val card = CardView(this).apply {
            radius = 8f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#2D2D2D") else Color.WHITE)
            isClickable = true
            isFocusable = true
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        
        // Find document title from ID
        val docTitle = PDFDocumentManager.RESEARCH_DOCUMENTS
            .find { it.id == bookmark.id.substringBefore("_") }?.title ?: "Unknown Document"
        
        val titleView = TextView(this).apply {
            text = docTitle
            textSize = 16f
            setTextColor(if (isDarkMode) Color.parseColor("#64B5F6") else Color.parseColor("#1976D2"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val timeView = TextView(this).apply {
            text = getRelativeTime(bookmark.timestamp.time)
            textSize = 12f
            setTextColor(if (isDarkMode) Color.parseColor("#888888") else Color.parseColor("#999999"))
        }
        
        headerLayout.addView(titleView)
        headerLayout.addView(timeView)
        
        val locationView = TextView(this).apply {
            text = "📍 Position ${bookmark.position} • Real Bookmark"
            textSize = 12f
            setTextColor(if (isDarkMode) Color.parseColor("#FFB74D") else Color.parseColor("#FF9800"))
            setPadding(0, 5, 0, 10)
        }
        
        val contentView = TextView(this).apply {
            text = if (bookmark.text.length > 200) {
                bookmark.text.take(200) + "..."
            } else {
                bookmark.text
            }
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            setPadding(0, 0, 0, 10)
        }
        
        // Add note if it exists
        if (bookmark.note.isNotBlank()) {
            val noteView = TextView(this).apply {
                text = "📝 ${bookmark.note}"
                textSize = currentFontSize - 1f
                setTextColor(if (isDarkMode) Color.parseColor("#A5A5A5") else Color.parseColor("#666666"))
                setPadding(0, 5, 0, 10)
            }
            layout.addView(noteView)
        }
        
        val actionsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        
        val editButton = Button(this).apply {
            text = "✏️ Edit"
            textSize = 12f
            setBackgroundColor(if (isDarkMode) Color.parseColor("#FFB74D") else Color.parseColor("#FF9800"))
            setTextColor(Color.WHITE)
            setPadding(15, 8, 15, 8)
            setOnClickListener { showEditBookmarkDialog(bookmark) }
        }
        
        val deleteButton = Button(this).apply {
            text = "🗑️ Delete"
            textSize = 12f
            setBackgroundColor(Color.parseColor("#F44336"))
            setTextColor(Color.WHITE)
            setPadding(15, 8, 15, 8)
            setOnClickListener { deleteBookmark(bookmark) }
        }
        
        actionsLayout.addView(editButton)
        actionsLayout.addView(createSpacing(10))
        actionsLayout.addView(deleteButton)
        
        layout.addView(headerLayout)
        layout.addView(locationView)
        layout.addView(contentView)
        layout.addView(actionsLayout)
        
        card.addView(layout)
        
        return card
    }
    
    private fun createBookmarkCard(bookmark: Bookmark): CardView {
        val card = CardView(this).apply {
            radius = 8f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#2D2D2D") else Color.WHITE)
            isClickable = true
            isFocusable = true
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        
        val titleView = TextView(this).apply {
            text = bookmark.documentTitle
            textSize = 16f
            setTextColor(if (isDarkMode) Color.parseColor("#64B5F6") else Color.parseColor("#1976D2"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val timeView = TextView(this).apply {
            text = getRelativeTime(bookmark.timestamp)
            textSize = 12f
            setTextColor(if (isDarkMode) Color.parseColor("#888888") else Color.parseColor("#999999"))
        }
        
        headerLayout.addView(titleView)
        headerLayout.addView(timeView)
        
        val locationView = TextView(this).apply {
            text = "📍 ${bookmark.location} • ${bookmark.category}"
            textSize = 12f
            setTextColor(if (isDarkMode) Color.parseColor("#FFB74D") else Color.parseColor("#FF9800"))
            setPadding(0, 5, 0, 10)
        }
        
        val contentView = TextView(this).apply {
            text = bookmark.content
            textSize = 14f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            maxLines = 3
            ellipsize = android.text.TextUtils.TruncateAt.END
        }
        
        val actionLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 15, 0, 0)
        }
        
        val readButton = Button(this).apply {
            text = "Read Full"
            textSize = 12f
            setBackgroundColor(Color.parseColor("#4CAF50"))
            setTextColor(Color.WHITE)
            setPadding(20, 10, 20, 10)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 20, 0) }
        }
        
        val shareButton = Button(this).apply {
            text = "Share"
            textSize = 12f
            setBackgroundColor(Color.parseColor("#2196F3"))
            setTextColor(Color.WHITE)
            setPadding(20, 10, 20, 10)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 20, 0) }
        }
        
        val deleteButton = Button(this).apply {
            text = "Remove"
            textSize = 12f
            setBackgroundColor(Color.parseColor("#F44336"))
            setTextColor(Color.WHITE)
            setPadding(20, 10, 20, 10)
        }
        
        actionLayout.addView(readButton)
        actionLayout.addView(shareButton) 
        actionLayout.addView(deleteButton)
        
        layout.addView(headerLayout)
        layout.addView(locationView)
        layout.addView(contentView)
        layout.addView(actionLayout)
        card.addView(layout)
        
        // Click handlers
        readButton.setOnClickListener { showBookmarkDetails(bookmark) }
        shareButton.setOnClickListener { shareBookmark(bookmark) }
        deleteButton.setOnClickListener { showDeleteConfirmation(bookmark) }
        
        card.setOnClickListener { showBookmarkDetails(bookmark) }
        
        return card
    }
    
    private fun showAddBookmarkDialog() {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("➕ Add Bookmark")
            .setMessage("Add a new bookmark to your collection")
            .setView(createAddBookmarkLayout())
            .setPositiveButton("Add Bookmark") { _, _ -> }
            .setNegativeButton("Cancel", null)
            .create()
        
        dialog.show()
        
        // Override the positive button to handle form validation
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            handleAddBookmark(dialog)
        }
    }
    
    private fun createAddBookmarkLayout(): android.view.View {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 10)
        }
        
        // Document selection spinner
        val docLabel = TextView(this).apply {
            text = "Select Document:"
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
        }
        
        val docSpinner = Spinner(this).apply {
            tag = "document_spinner"
            val adapter = ArrayAdapter(this@BookmarksActivity, 
                android.R.layout.simple_spinner_item,
                PDFDocumentManager.RESEARCH_DOCUMENTS.map { it.title }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            setAdapter(adapter)
        }
        
        // Text input
        val textLabel = TextView(this).apply {
            text = "Bookmark Text:"
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            setPadding(0, 10, 0, 5)
        }
        
        val textInput = EditText(this).apply {
            tag = "text_input"
            hint = "Enter the text you want to bookmark..."
            setHintTextColor(if (isDarkMode) Color.parseColor("#888888") else Color.parseColor("#AAAAAA"))
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            minLines = 3
            maxLines = 5
        }
        
        // Note input
        val noteLabel = TextView(this).apply {
            text = "Personal Note (Optional):"
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            setPadding(0, 10, 0, 5)
        }
        
        val noteInput = EditText(this).apply {
            tag = "note_input"
            hint = "Add your thoughts or notes..."
            setHintTextColor(if (isDarkMode) Color.parseColor("#888888") else Color.parseColor("#AAAAAA"))
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            minLines = 2
        }
        
        layout.addView(docLabel)
        layout.addView(docSpinner)
        layout.addView(textLabel)
        layout.addView(textInput)
        layout.addView(noteLabel)
        layout.addView(noteInput)
        
        return layout
    }
    
    private fun handleAddBookmark(dialog: android.app.AlertDialog) {
        val layout = dialog.findViewById<LinearLayout>(android.R.id.content)?.getChildAt(0) as? LinearLayout
        
        val docSpinner = layout?.findViewWithTag<Spinner>("document_spinner")
        val textInput = layout?.findViewWithTag<EditText>("text_input")
        val noteInput = layout?.findViewWithTag<EditText>("note_input")
        
        val selectedDocIndex = docSpinner?.selectedItemPosition ?: 0
        val selectedDoc = PDFDocumentManager.RESEARCH_DOCUMENTS[selectedDocIndex]
        val text = textInput?.text?.toString()?.trim() ?: ""
        val note = noteInput?.text?.toString()?.trim() ?: ""
        
        if (text.isBlank()) {
            Toast.makeText(this, "Please enter some text to bookmark", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Add the bookmark using PDFDocumentManager
        documentManager.addBookmark(selectedDoc.id, 0, text, note)
        
        Toast.makeText(this, "✅ Bookmark added successfully!", Toast.LENGTH_SHORT).show()
        
        // Refresh bookmarks display
        loadAllBookmarks()
        displayBookmarks()
        
        dialog.dismiss()
    }
    
    private fun showEditBookmarkDialog(bookmark: DocumentBookmark) {
        // Implementation similar to add but with pre-filled values
        Toast.makeText(this, "Edit functionality coming soon!", Toast.LENGTH_SHORT).show()
    }
    
    private fun deleteBookmark(bookmark: DocumentBookmark) {
        android.app.AlertDialog.Builder(this)
            .setTitle("Delete Bookmark")
            .setMessage("Are you sure you want to delete this bookmark?")
            .setPositiveButton("Delete") { _, _ ->
                // For now, show feedback - full delete would require PDFDocumentManager update
                Toast.makeText(this, "Delete functionality coming soon!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showBookmarkDetails(bookmark: Bookmark) {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("📖 ${bookmark.documentTitle}")
            .setMessage("Location: ${bookmark.location}\n" +
                       "Category: ${bookmark.category}\n" +
                       "Bookmarked: ${getFullTime(bookmark.timestamp)}\n\n" +
                       "Content:\n${bookmark.content}\n\n" +
                       "📝 Full reading interface coming soon!\n\n" +
                       "You'll be able to:\n" +
                       "• Read the complete document\n" +
                       "• Add notes and highlights\n" +
                       "• Navigate to related passages\n" +
                       "• Listen to audio narration")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .setNeutralButton("Edit Bookmark") { _, _ ->
                Toast.makeText(this, "Edit functionality coming soon!", Toast.LENGTH_SHORT).show()
            }
            .create()
        
        dialog.show()
    }
    
    private fun shareBookmark(bookmark: Bookmark) {
        val shareText = """"${bookmark.content}"

— ${bookmark.documentTitle} (${bookmark.location})

Shared from Bahá'í Resource Library"""
        Toast.makeText(this, "Share functionality: $shareText", Toast.LENGTH_LONG).show()
    }
    
    private fun showDeleteConfirmation(bookmark: Bookmark) {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Remove Bookmark?")
            .setMessage("Are you sure you want to remove this bookmark?\n\n\"${bookmark.content.take(50)}...\"")
            .setPositiveButton("Remove") { _, _ ->
                Toast.makeText(this, "Bookmark removed (feature in development)", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
        
        dialog.show()
    }
    
    private fun getRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return when {
            diff < 3600000L -> "${diff / 60000L}m ago" // Less than 1 hour
            diff < 86400000L -> "${diff / 3600000L}h ago" // Less than 1 day
            diff < 604800000L -> "${diff / 86400000L}d ago" // Less than 1 week
            else -> "${diff / 604800000L}w ago" // Weeks
        }
    }
    
    private fun getFullTime(timestamp: Long): String {
        return android.text.format.DateFormat.format("MMM dd, yyyy 'at' h:mm a", timestamp).toString()
    }
    
    private fun createSpacing(height: Int): android.view.View {
        return android.view.View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                height
            )
        }
    }
    
    private fun performBookmarkSearch(query: String) {
        if (query.trim().isEmpty()) {
            Toast.makeText(this, "Please enter search terms", Toast.LENGTH_SHORT).show()
            return
        }
        
        val searchResults = sampleBookmarks.filter { bookmark ->
            bookmark.documentTitle.lowercase().contains(query.lowercase()) ||
            bookmark.content.lowercase().contains(query.lowercase()) ||
            bookmark.location.lowercase().contains(query.lowercase()) ||
            bookmark.category.lowercase().contains(query.lowercase())
        }
        
        if (searchResults.isEmpty()) {
            Toast.makeText(this, "No bookmarks found matching '$query'", Toast.LENGTH_SHORT).show()
        } else {
            showSearchResults(searchResults, query)
        }
    }
    
    private fun showSearchResults(results: List<Bookmark>, query: String) {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("🔍 Search Results: \"$query\"")
            .setMessage("Found ${results.size} matching bookmarks:")
            .create()
        
        val scrollView = ScrollView(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        results.forEach { bookmark ->
            val bookmarkCard = createBookmarkCard(bookmark)
            layout.addView(bookmarkCard)
            layout.addView(createSpacing(10))
        }
        
        scrollView.addView(layout)
        dialog.setView(scrollView)
        dialog.show()
    }
}

data class Bookmark(
    val documentTitle: String,
    val location: String,
    val content: String,
    val category: String,
    val timestamp: Long
)