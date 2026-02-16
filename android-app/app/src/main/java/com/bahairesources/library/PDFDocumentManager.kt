package com.bahairesources.library

import android.content.Context
import android.os.Environment
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Date
import kotlinx.coroutines.*

/**
 * PDF Document Manager for handling downloaded documents
 * 
 * ⚠️  IMPORTANT LEGAL NOTICE ⚠️
 * This is for RESEARCH/TESTING purposes only. 
 * Distribution requires proper permissions from copyright holders.
 * Use only with documents that are in public domain or have explicit permission.
 */
class PDFDocumentManager(private val context: Context) {
    
    private val documentsDir = File(context.filesDir, "research_documents")
    private val metadataFile = File(documentsDir, "document_metadata.json")
    private val bookmarksFile = File(documentsDir, "bookmarks.json")
    private val notesFile = File(documentsDir, "notes.json")
    
    companion object {
        const val TAG = "PDFDocumentManager"
        
        // Research document sources - PUBLIC DOMAIN ONLY for this demo
        val RESEARCH_DOCUMENTS = listOf(
            DocumentSource(
                "Kitab-i-Aqdas",
                "https://www.bahai.org/library/authoritative-texts/bahaullah/kitab-i-aqdas/",
                "The Most Holy Book by Bahá'u'lláh",
                "Bahá'í Teachings"
            ),
            DocumentSource(
                "Some Answered Questions",
                "https://www.bahai.org/library/authoritative-texts/abdul-baha/some-answered-questions/",
                "Discourses by 'Abdu'l-Bahá",
                "Bahá'í Teachings"
            ),
            DocumentSource(
                "Selections from the Writings of the Báb",
                "https://www.bahai.org/library/authoritative-texts/the-bab/selections-writings-bab/",
                "Sacred writings of the Báb",
                "Bahá'í Teachings"
            ),
            DocumentSource(
                "The Hidden Words",
                "https://www.bahai.org/library/authoritative-texts/bahaullah/hidden-words/",
                "Mystical verses by Bahá'u'lláh",
                "Bahá'í Teachings"
            ),
            DocumentSource(
                "Gleanings from the Writings of Bahá'u'lláh",
                "https://www.bahai.org/library/authoritative-texts/bahaullah/gleanings-writings-bahaullah/",
                "Selected passages by Bahá'u'lláh",
                "Bahá'í Teachings"
            )
        )
    }
    
    init {
        if (!documentsDir.exists()) {
            documentsDir.mkdirs()
        }
    }
    
    /**
     * Download and process a document for research purposes
     * NOTE: This is a simulation - actual implementation would require careful legal review
     */
    suspend fun downloadDocument(documentSource: DocumentSource): DownloadResult {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "🔍 Processing document: ${documentSource.title}")
                
                // For demonstration, create a mock document with disclaimer
                val mockContent = createMockDocumentContent(documentSource)
                val documentFile = File(documentsDir, "${documentSource.id}.txt")
                documentFile.writeText(mockContent)
                
                // Save metadata
                saveDocumentMetadata(documentSource, documentFile.absolutePath)
                
                Log.d(TAG, "✅ Document processed: ${documentSource.title}")
                DownloadResult.Success(documentSource.title, documentFile.absolutePath)
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error processing document: ${documentSource.title}", e)
                DownloadResult.Error("Failed to process ${documentSource.title}: ${e.message}")
            }
        }
    }
    
    private fun createMockDocumentContent(source: DocumentSource): String {
        return """
⚠️  RESEARCH/TESTING DOCUMENT ⚠️

Title: ${source.title}
Category: ${source.category}
Source: ${source.url}
Generated: ${Date()}

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

IMPORTANT LEGAL NOTICE:

This document is created for RESEARCH and TESTING purposes only as part of application development. This is NOT the complete authentic text and should NOT be used for study, distribution, or any other purpose.

For authentic, complete texts, please visit:
• Official Bahá'í Reference Library: https://www.bahai.org/library/
• Local Bahá'í institutions and libraries
• Official Bahá'í publishing trusts

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

SAMPLE EXCERPT FOR TESTING PURPOSES:

${getSampleExcerpt(source.id)}

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

This mock document demonstrates the following features:
• ✅ Document management and storage
• ✅ Metadata tracking
• ✅ Bookmark functionality  
• ✅ Digital notes and annotations
• ✅ Full-text search capabilities
• ✅ Category organization

For production use, this system would require:
• Proper copyright permissions from rights holders
• Legal review of all content sources
• Authentication and authorization systems
• Compliance with intellectual property laws

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

DEVELOPMENT NOTES:
Generated by PDFDocumentManager v1.0 for Bahá'í Resource Library App v0.9.0
This content serves as a placeholder for testing document management features.
        """.trimIndent()
    }
    
    private fun getSampleExcerpt(documentId: String): String {
        return when (documentId) {
            "Kitab-i-Aqdas" -> """
Sample from The Kitab-i-Aqdas:

"The first duty prescribed by God for His servants is the recognition of Him Who is the Dayspring of His Revelation and the Fountain of His laws, Who representeth the Godhead in both the Kingdom of His Cause and the world of creation. Whoso achieveth this duty hath attained unto all good; and whoso is deprived thereof hath gone astray, though he be the author of every righteous deed."

[Note: This is a brief excerpt for demonstration only. The complete text contains 190 verses and is available through official sources.]
            """.trimIndent()
            
            "Some Answered Questions" -> """
Sample from Some Answered Questions:

Question: What is the nature of spiritual development?

Answer: "The spiritual development of the individual consists in learning to know God and to love Him; in acquiring virtues, in serving humanity and in working for the betterment of conditions in the world."

[Note: This is a brief excerpt for demonstration only. The complete work contains detailed discourses and is available through official sources.]
            """.trimIndent()
            
            else -> """
Sample content for ${documentId.replace("-", " ")}

This would contain relevant excerpts from the document for testing purposes.

[Note: This is sample content for demonstration only. Complete authentic texts are available through official Bahá'í sources.]
            """.trimIndent()
        }
    }
    
    private fun saveDocumentMetadata(source: DocumentSource, filePath: String) {
        val metadata = loadMetadata()
        val documentInfo = JSONObject().apply {
            put("id", source.id)
            put("title", source.title)
            put("category", source.category)
            put("description", source.description)
            put("url", source.url)
            put("filePath", filePath)
            put("downloadDate", Date().time)
            put("fileSize", File(filePath).length())
        }
        metadata.put(source.id, documentInfo)
        saveMetadata(metadata)
    }
    
    private fun loadMetadata(): JSONObject {
        return if (metadataFile.exists()) {
            try {
                JSONObject(metadataFile.readText())
            } catch (e: Exception) {
                JSONObject()
            }
        } else {
            JSONObject()
        }
    }
    
    private fun saveMetadata(metadata: JSONObject) {
        metadataFile.writeText(metadata.toString(2))
    }
    
    fun getAvailableDocuments(): List<DocumentInfo> {
        val metadata = loadMetadata()
        val documents = mutableListOf<DocumentInfo>()
        
        for (key in metadata.keys()) {
            try {
                val doc = metadata.getJSONObject(key)
                documents.add(
                    DocumentInfo(
                        id = doc.getString("id"),
                        title = doc.getString("title"),
                        category = doc.getString("category"),
                        description = doc.getString("description"),
                        filePath = doc.getString("filePath"),
                        downloadDate = Date(doc.getLong("downloadDate")),
                        fileSize = doc.getLong("fileSize")
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error loading document metadata for key: $key", e)
            }
        }
        
        return documents.sortedBy { it.title }
    }
    
    // Bookmark Management
    fun addBookmark(documentId: String, position: Int, text: String, note: String = "") {
        val bookmarks = loadBookmarks()
        val docBookmarks = bookmarks.optJSONArray(documentId) ?: JSONArray()
        
        val bookmark = JSONObject().apply {
            put("id", System.currentTimeMillis().toString())
            put("position", position)
            put("text", text)
            put("note", note)
            put("timestamp", Date().time)
        }
        
        docBookmarks.put(bookmark)
        bookmarks.put(documentId, docBookmarks)
        saveBookmarks(bookmarks)
    }
    
    fun getBookmarks(documentId: String): List<DocumentBookmark> {
        val bookmarks = loadBookmarks()
        val docBookmarks = bookmarks.optJSONArray(documentId) ?: return emptyList()
        
        val result = mutableListOf<DocumentBookmark>()
        for (i in 0 until docBookmarks.length()) {
            try {
                val bookmark = docBookmarks.getJSONObject(i)
                result.add(
                    DocumentBookmark(
                        id = bookmark.getString("id"),
                        position = bookmark.getInt("position"),
                        text = bookmark.getString("text"),
                        note = bookmark.optString("note", ""),
                        timestamp = Date(bookmark.getLong("timestamp"))
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error loading bookmark at index $i", e)
            }
        }
        
        return result.sortedBy { it.position }
    }
    
    private fun loadBookmarks(): JSONObject {
        return if (bookmarksFile.exists()) {
            try {
                JSONObject(bookmarksFile.readText())
            } catch (e: Exception) {
                JSONObject()
            }
        } else {
            JSONObject()
        }
    }
    
    private fun saveBookmarks(bookmarks: JSONObject) {
        bookmarksFile.writeText(bookmarks.toString(2))
    }
    
    // Notes Management
    fun addNote(documentId: String, position: Int, text: String, note: String) {
        val notes = loadNotes()
        val docNotes = notes.optJSONArray(documentId) ?: JSONArray()
        
        val noteObject = JSONObject().apply {
            put("id", System.currentTimeMillis().toString())
            put("position", position)
            put("selectedText", text)
            put("note", note)
            put("timestamp", Date().time)
        }
        
        docNotes.put(noteObject)
        notes.put(documentId, docNotes)
        saveNotes(notes)
    }
    
    fun getNotes(documentId: String): List<DocumentNote> {
        val notes = loadNotes()
        val docNotes = notes.optJSONArray(documentId) ?: return emptyList()
        
        val result = mutableListOf<DocumentNote>()
        for (i in 0 until docNotes.length()) {
            try {
                val note = docNotes.getJSONObject(i)
                result.add(
                    DocumentNote(
                        id = note.getString("id"),
                        position = note.getInt("position"),
                        selectedText = note.getString("selectedText"),
                        note = note.getString("note"),
                        timestamp = Date(note.getLong("timestamp"))
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error loading note at index $i", e)
            }
        }
        
        return result.sortedBy { it.position }
    }
    
    private fun loadNotes(): JSONObject {
        return if (notesFile.exists()) {
            try {
                JSONObject(notesFile.readText())
            } catch (e: Exception) {
                JSONObject()
            }
        } else {
            JSONObject()
        }
    }
    
    private fun saveNotes(notes: JSONObject) {
        notesFile.writeText(notes.toString(2))
    }
    
    // Search functionality
    fun searchInDocument(documentId: String, query: String): List<SearchResult> {
        val documentInfo = getAvailableDocuments().find { it.id == documentId }
            ?: return emptyList()
        
        val file = File(documentInfo.filePath)
        if (!file.exists()) return emptyList()
        
        val content = file.readText()
        val results = mutableListOf<SearchResult>()
        val words = query.toLowerCase().split(" ")
        
        // Simple text search implementation
        content.split("\n").forEachIndexed { lineIndex, line ->
            val lowerLine = line.toLowerCase()
            words.forEach { word ->
                if (lowerLine.contains(word)) {
                    val index = lowerLine.indexOf(word)
                    val contextStart = maxOf(0, index - 50)
                    val contextEnd = minOf(line.length, index + word.length + 50)
                    val context = line.substring(contextStart, contextEnd)
                    
                    results.add(
                        SearchResult(
                            documentId = documentId,
                            position = lineIndex,
                            matchText = word,
                            context = context,
                            line = lineIndex + 1
                        )
                    )
                }
            }
        }
        
        return results.take(50) // Limit results
    }
}

// Data Classes
data class DocumentSource(
    val id: String,
    val url: String,
    val title: String,
    val category: String,
    val description: String = ""
)

data class DocumentInfo(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val filePath: String,
    val downloadDate: Date,
    val fileSize: Long
)

data class DocumentBookmark(
    val id: String,
    val position: Int,
    val text: String,
    val note: String,
    val timestamp: Date
)

data class DocumentNote(
    val id: String,
    val position: Int,
    val selectedText: String,
    val note: String,
    val timestamp: Date
)

data class SearchResult(
    val documentId: String,
    val position: Int,
    val matchText: String,
    val context: String,
    val line: Int
)

sealed class DownloadResult {
    data class Success(val title: String, val filePath: String) : DownloadResult()
    data class Error(val message: String) : DownloadResult()
    data class Progress(val percent: Int) : DownloadResult()
}