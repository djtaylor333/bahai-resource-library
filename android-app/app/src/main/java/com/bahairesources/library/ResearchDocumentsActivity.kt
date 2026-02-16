package com.bahairesources.library

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.graphics.Color
import android.graphics.Typeface
import androidx.cardview.widget.CardView
import android.view.View
import kotlinx.coroutines.*
import java.text.DateFormat

/**
 * RESEARCH DOCUMENTS ACTIVITY
 * ⚠️  IMPORTANT LEGAL NOTICE ⚠️
 * These documents are for RESEARCH/TESTING purposes only.
 * This is a demonstration of document management features.
 * For authentic content, please visit official sources.
 */
class ResearchDocumentsActivity : AppCompatActivity() {
    
    private var isDarkMode = false
    private var currentFontSize = SettingsManager.FONT_MEDIUM
    private lateinit var pdfManager: PDFDocumentManager
    private lateinit var documentsLayout: LinearLayout
    private lateinit var statusText: TextView
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize settings
        isDarkMode = SettingsManager.isDarkMode(this)
        currentFontSize = SettingsManager.getFontSize(this)
        pdfManager = PDFDocumentManager(this)
        
        val scrollView = ScrollView(this).apply {
            setBackgroundColor(if (isDarkMode) Color.parseColor("#121212") else Color.parseColor("#F5F5F5"))
        }
        
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(10, 0, 10, 20)
            setBackgroundColor(if (isDarkMode) Color.parseColor("#121212") else Color.parseColor("#F5F5F5"))
        }
        
        // Header
        val headerLayout = createHeader()
        
        // Legal disclaimer
        val disclaimerCard = createDisclaimerCard()
        
        // Status area
        statusText = TextView(this).apply {
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            setPadding(20, 10, 20, 10)
            text = "📚 Ready to download research documents"
        }
        
        // Control buttons
        val controlsCard = createControlsCard()
        
        // Documents list
        documentsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        
        val documentsCard = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#FFFFFF"))
        }
        
        documentsCard.addView(documentsLayout)
        
        mainLayout.addView(headerLayout)
        mainLayout.addView(createSpacing(15))
        mainLayout.addView(disclaimerCard)
        mainLayout.addView(createSpacing(10))
        mainLayout.addView(statusText)
        mainLayout.addView(createSpacing(15))
        mainLayout.addView(controlsCard)
        mainLayout.addView(createSpacing(15))
        mainLayout.addView(documentsCard)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
        
        // Load existing documents
        updateDocumentsList()
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
            text = "🔬 Research Documents"
            textSize = currentFontSize + 6f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(20, 10, 0, 10)
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        headerLayout.addView(backButton)
        headerLayout.addView(titleText)
        return headerLayout
    }
    
    private fun createDisclaimerCard(): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#3E2723") else Color.parseColor("#FFF3E0"))
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 15, 20, 15)
        }
        
        val warningIcon = TextView(this).apply {
            text = "⚠️"
            textSize = currentFontSize + 4f
            setPadding(0, 0, 0, 10)
        }
        
        val disclaimerText = TextView(this).apply {
            text = """
                IMPORTANT LEGAL NOTICE
                
                These documents are for RESEARCH and TESTING purposes only as part of application development. This feature demonstrates document management capabilities including bookmarks, notes, and search functionality.
                
                For authentic, complete texts please visit:
                • Official Bahá'í Reference Library: bahai.org/library
                • Local Bahá'í institutions and libraries
                • Official Bahá'í publishing trusts
                
                These mock documents are NOT suitable for study, distribution, or any purpose other than testing the application's document management features.
            """.trimIndent()
            textSize = currentFontSize - 1f
            setTextColor(if (isDarkMode) Color.parseColor("#FFCC80") else Color.parseColor("#E65100"))
            setLineSpacing(4f, 1.2f)
        }
        
        layout.addView(warningIcon)
        layout.addView(disclaimerText)
        card.addView(layout)
        return card
    }
    
    private fun createControlsCard(): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#FFFFFF"))
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        val titleText = TextView(this).apply {
            text = "📥 Document Management"
            textSize = currentFontSize + 1f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(0, 0, 0, 15)
            setTypeface(typeface, Typeface.BOLD)
        }
        
        val buttonLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        
        val downloadAllButton = Button(this).apply {
            text = "📚 Download All Documents"
            setBackgroundColor(if (isDarkMode) Color.parseColor("#2E7D32") else Color.parseColor("#4CAF50"))
            setTextColor(Color.WHITE)
            setPadding(20, 15, 20, 15)
            textSize = currentFontSize
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                rightMargin = 10
            }
            setOnClickListener { downloadAllDocuments() }
        }
        
        val refreshButton = Button(this).apply {
            text = "🔄"
            setBackgroundColor(if (isDarkMode) Color.parseColor("#37474F") else Color.parseColor("#607D8B"))
            setTextColor(Color.WHITE)
            setPadding(15, 15, 15, 15)
            textSize = currentFontSize
            setOnClickListener { updateDocumentsList() }
        }
        
        buttonLayout.addView(downloadAllButton)
        buttonLayout.addView(refreshButton)
        
        layout.addView(titleText)
        layout.addView(buttonLayout)
        card.addView(layout)
        return card
    }
    
    private fun downloadAllDocuments() {
        statusText.text = "📥 Downloading research documents..."
        statusText.setTextColor(if (isDarkMode) Color.parseColor("#FFC107") else Color.parseColor("#FF8F00"))
        
        coroutineScope.launch {
            try {
                var successCount = 0
                for (docSource in PDFDocumentManager.RESEARCH_DOCUMENTS) {
                    statusText.text = "📥 Processing: ${docSource.title}..."
                    
                    when (val result = pdfManager.downloadDocument(docSource)) {
                        is DownloadResult.Success -> {
                            successCount++
                            updateDocumentsList()
                        }
                        is DownloadResult.Error -> {
                            Toast.makeText(this@ResearchDocumentsActivity, "❌ ${result.message}", Toast.LENGTH_SHORT).show()
                        }
                        is DownloadResult.Progress -> {
                            statusText.text = "📥 ${docSource.title}: ${result.percent}%"
                        }
                    }
                }
                
                statusText.text = "✅ Downloaded $successCount documents successfully!"
                statusText.setTextColor(if (isDarkMode) Color.parseColor("#4CAF50") else Color.parseColor("#2E7D32"))
                
                Toast.makeText(this@ResearchDocumentsActivity, "🎉 Research documents ready for testing!", Toast.LENGTH_LONG).show()
                
            } catch (e: Exception) {
                statusText.text = "❌ Download error: ${e.message}"
                statusText.setTextColor(if (isDarkMode) Color.parseColor("#F44336") else Color.parseColor("#D32F2F"))
            }
        }
    }
    
    private fun updateDocumentsList() {
        documentsLayout.removeAllViews()
        
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 10)
        }
        
        val headerText = TextView(this).apply {
            text = "📖 Available Research Documents"
            textSize = currentFontSize + 1f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setTypeface(typeface, Typeface.BOLD)
        }
        
        headerLayout.addView(headerText)
        documentsLayout.addView(headerLayout)
        
        val documents = pdfManager.getAvailableDocuments()
        
        if (documents.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = """
                    📥 No documents downloaded yet
                    
                    Tap "Download All Documents" to get started with research documents for testing the document management features.
                """.trimIndent()
                textSize = currentFontSize
                setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
                setPadding(20, 20, 20, 20)
            }
            documentsLayout.addView(emptyText)
        } else {
            documents.forEach { document ->
                val docCard = createDocumentCard(document)
                documentsLayout.addView(docCard)
                documentsLayout.addView(createSpacing(10))
            }
        }
    }
    
    private fun createDocumentCard(document: DocumentInfo): CardView {
        val card = CardView(this).apply {
            radius = 8f
            cardElevation = 2f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#2D2D2D") else Color.parseColor("#F8F9FA"))
            setPadding(5, 5, 5, 5)
            isClickable = true
            setOnClickListener { openDocument(document) }
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(15, 15, 15, 15)
        }
        
        val titleText = TextView(this).apply {
            text = "📄 ${document.title}"
            textSize = currentFontSize + 1f
            setTextColor(if (isDarkMode) Color.parseColor("#64B5F6") else Color.parseColor("#1976D2"))
            setPadding(0, 0, 0, 5)
            setTypeface(typeface, Typeface.BOLD)
        }
        
        val categoryText = TextView(this).apply {
            text = "📂 ${document.category}"
            textSize = currentFontSize - 1f
            setTextColor(if (isDarkMode) Color.parseColor("#A5D6A7") else Color.parseColor("#4CAF50"))
            setPadding(0, 0, 0, 8)
        }
        
        val descText = TextView(this).apply {
            text = document.description
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
            setPadding(0, 0, 0, 8)
        }
        
        val infoText = TextView(this).apply {
            text = "📅 Downloaded: ${DateFormat.getDateTimeInstance().format(document.downloadDate)}"
            textSize = currentFontSize - 2f
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
            setPadding(0, 0, 0, 5)
        }
        
        val sizeText = TextView(this).apply {
            text = "📏 Size: ${document.fileSize / 1024} KB"
            textSize = currentFontSize - 2f
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
            setPadding(0, 0, 0, 8)
        }
        
        val openHint = TextView(this).apply {
            text = "💡 Tap to open with bookmarks, notes & search"
            textSize = currentFontSize - 1f
            setTextColor(if (isDarkMode) Color.parseColor("#81C784") else Color.parseColor("#4CAF50"))
        }
        
        layout.addView(titleText)
        layout.addView(categoryText)
        layout.addView(descText)
        layout.addView(infoText)
        layout.addView(sizeText)
        layout.addView(openHint)
        card.addView(layout)
        
        return card
    }
    
    private fun openDocument(document: DocumentInfo) {
        val intent = Intent(this, DocumentReaderActivity::class.java).apply {
            putExtra("document_title", document.title)
            putExtra("document_category", document.category)
            putExtra("document_filename", "${document.id}.txt")
        }
        startActivity(intent)
    }
    
    private fun createSpacing(height: Int): View {
        return View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                height
            )
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}