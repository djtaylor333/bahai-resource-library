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
    private var isDarkMode = false
    private var currentFontSize = SettingsManager.FONT_MEDIUM
    
    // Sample bookmarks - in a real app these would be stored in a database
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
        
        // Initialize settings
        isDarkMode = SettingsManager.isDarkMode(this)
        currentFontSize = SettingsManager.getFontSize(this)
        
        prefs = getSharedPreferences("BookmarksPrefs", MODE_PRIVATE)
        
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
    
    private fun displayBookmarks() {
        bookmarksLayout.removeAllViews()
        
        if (sampleBookmarks.isEmpty()) {
            emptyStateLayout.visibility = android.view.View.VISIBLE
            return
        } else {
            emptyStateLayout.visibility = android.view.View.GONE
        }
        
        sampleBookmarks.sortedByDescending { it.timestamp }.forEach { bookmark ->
            val card = createBookmarkCard(bookmark)
            bookmarksLayout.addView(card)
            bookmarksLayout.addView(createSpacing(12))
        }
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
            .setMessage("Bookmark functionality is being developed!\n\n" +
                       "In the full version you'll be able to:\n\n" +
                       "• Bookmark any passage while reading\n" +
                       "• Add personal notes and tags\n" +
                       "• Organize bookmarks into collections\n" +
                       "• Search through your bookmarks\n" +
                       "• Export bookmarks for sharing\n\n" +
                       "For now, explore the sample bookmarks above to see how the feature will work!")
            .setPositiveButton("Got it!") { dialog, _ -> dialog.dismiss() }
            .create()
        
        dialog.show()
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