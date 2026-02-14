package com.bahairesources.library.ui.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bahairesources.library.data.dao.*
import com.bahairesources.library.data.entities.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel for the PDF Reader with bookmarking and annotation functionality
 */
@HiltViewModel
class PdfReaderViewModel @Inject constructor(
    private val bookmarkDao: BookmarkDao,
    private val annotationDao: AnnotationDao,
    private val readingProgressDao: ReadingProgressDao,
    private val readingPreferenceDao: ReadingPreferenceDao
) : ViewModel() {
    
    private var currentDocumentId: Long = -1
    private var currentPdfPath: String = ""
    private var sessionStartTime: Long = 0
    
    private val _bookmarks = MutableLiveData<List<Bookmark>>()
    val bookmarks: LiveData<List<Bookmark>> = _bookmarks
    
    private val _annotations = MutableLiveData<List<Annotation>>()
    val annotations: LiveData<List<Annotation>> = _annotations
    
    private val _readingProgress = MutableLiveData<ReadingProgress>()
    val readingProgress: LiveData<ReadingProgress> = _readingProgress
    
    private val _readingPreferences = MutableLiveData<ReadingPreference>()
    val readingPreferences: LiveData<ReadingPreference> = _readingPreferences
    
    fun initializeDocument(documentId: Long, pdfPath: String) {
        currentDocumentId = documentId
        currentPdfPath = pdfPath
        sessionStartTime = System.currentTimeMillis()
        
        loadBookmarks()
        loadAnnotations()
        loadReadingProgress()
        loadReadingPreferences()
    }
    
    private fun loadBookmarks() {
        if (currentDocumentId == -1L) return
        
        viewModelScope.launch {
            bookmarkDao.getBookmarksForDocument(currentDocumentId).collect { bookmarkList ->
                _bookmarks.value = bookmarkList
            }
        }
    }
    
    private fun loadAnnotations() {
        if (currentDocumentId == -1L) return
        
        viewModelScope.launch {
            annotationDao.getAnnotationsForDocument(currentDocumentId).collect { annotationList ->
                _annotations.value = annotationList
            }
        }
    }
    
    private fun loadReadingProgress() {
        if (currentDocumentId == -1L) return
        
        viewModelScope.launch {
            val progress = readingProgressDao.getProgressForDocument(currentDocumentId)
            _readingProgress.value = progress
        }
    }
    
    private fun loadReadingPreferences() {
        viewModelScope.launch {
            readingPreferenceDao.getPreferencesFlow().collect { preferences ->
                _readingPreferences.value = preferences ?: getDefaultPreferences()
            }
        }
    }
    
    // Bookmark operations
    fun addBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            try {
                bookmarkDao.insertBookmark(bookmark)
            } catch (e: Exception) {
                // Handle error - bookmark might already exist
                updateBookmark(bookmark)
            }
        }
    }
    
    fun updateBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            bookmarkDao.updateBookmark(bookmark)
        }
    }
    
    fun deleteBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            bookmarkDao.deleteBookmark(bookmark)
        }
    }
    
    fun isPageBookmarked(pageNumber: Int): Boolean {
        return _bookmarks.value?.any { it.pageNumber == pageNumber } ?: false
    }
    
    // Annotation operations
    fun addAnnotation(annotation: Annotation) {
        viewModelScope.launch {
            annotationDao.insertAnnotation(annotation)
        }
    }
    
    fun updateAnnotation(annotation: Annotation) {
        viewModelScope.launch {
            annotationDao.updateAnnotation(annotation.copy(updatedAt = Date()))
        }
    }
    
    fun deleteAnnotation(annotation: Annotation) {
        viewModelScope.launch {
            annotationDao.deleteAnnotation(annotation)
        }
    }
    
    fun addHighlight(
        pageNumber: Int,
        selectedText: String,
        startPosition: Int,
        endPosition: Int,
        color: String = "#FFFF00"
    ) {
        val annotation = Annotation(
            documentId = currentDocumentId,
            pageNumber = pageNumber,
            type = AnnotationType.HIGHLIGHT,
            selectedText = selectedText,
            startPosition = startPosition,
            endPosition = endPosition,
            color = color
        )
        addAnnotation(annotation)
    }
    
    fun addNote(
        pageNumber: Int,
        selectedText: String,
        noteText: String,
        startPosition: Int,
        endPosition: Int
    ) {
        val annotation = Annotation(
            documentId = currentDocumentId,
            pageNumber = pageNumber,
            type = AnnotationType.NOTE,
            selectedText = selectedText,
            note = noteText,
            startPosition = startPosition,
            endPosition = endPosition
        )
        addAnnotation(annotation)
    }
    
    // Reading progress operations
    fun updateReadingProgress(documentId: Long, currentPage: Int, totalPages: Int, progressPercentage: Float = 0f) {
        viewModelScope.launch {
            val existingProgress = readingProgressDao.getProgressForDocument(documentId)
            val calculatedProgress = if (progressPercentage == 0f) {
                (currentPage.toFloat() / totalPages) * 100f
            } else {
                progressPercentage
            }
            
            val isCompleted = calculatedProgress >= 95f // Consider 95% as completed
            
            if (existingProgress != null) {
                val updatedProgress = existingProgress.copy(
                    lastReadPage = currentPage,
                    totalPages = totalPages,
                    progressPercentage = calculatedProgress,
                    lastReadAt = Date(),
                    isCompleted = isCompleted
                )
                readingProgressDao.insertOrUpdateProgress(updatedProgress)
                _readingProgress.value = updatedProgress
            } else {
                val newProgress = ReadingProgress(
                    documentId = documentId,
                    lastReadPage = currentPage,
                    totalPages = totalPages,
                    progressPercentage = calculatedProgress,
                    lastReadAt = Date(),
                    isCompleted = isCompleted
                )
                readingProgressDao.insertOrUpdateProgress(newProgress)
                _readingProgress.value = newProgress
            }
        }
    }
    
    fun saveReadingSession(documentId: Long) {
        viewModelScope.launch {
            val sessionDuration = System.currentTimeMillis() - sessionStartTime
            readingProgressDao.addReadingTime(documentId, sessionDuration)
        }
    }
    
    // Reading preferences operations
    fun updateFontSize(fontSize: Float) {
        viewModelScope.launch {
            readingPreferenceDao.updateFontSize(fontSize)
        }
    }
    
    fun updateNightMode(nightMode: Boolean) {
        viewModelScope.launch {
            readingPreferenceDao.updateNightMode(nightMode)
        }
    }
    
    fun updateHighlightColor(color: String) {
        viewModelScope.launch {
            readingPreferenceDao.updateHighlightColor(color)
        }
    }
    
    fun updateReadingPreferences(preferences: ReadingPreference) {
        viewModelScope.launch {
            readingPreferenceDao.insertOrUpdatePreferences(preferences)
        }
    }
    
    // Search functionality within document
    fun searchInDocument(query: String): List<SearchResult> {
        // TODO: Implement full-text search within the current document
        // This would integrate with the PDF content extraction and FTS
        return emptyList()
    }
    
    // Statistics and analytics
    fun getReadingStats(): ReadingStats {
        val progress = _readingProgress.value
        val bookmarkCount = _bookmarks.value?.size ?: 0
        val annotationCount = _annotations.value?.size ?: 0
        val highlightCount = _annotations.value?.count { it.type == AnnotationType.HIGHLIGHT } ?: 0
        val noteCount = _annotations.value?.count { it.type == AnnotationType.NOTE } ?: 0
        
        return ReadingStats(
            progressPercentage = progress?.progressPercentage ?: 0f,
            timeSpentReading = progress?.timeSpentReading ?: 0,
            bookmarkCount = bookmarkCount,
            annotationCount = annotationCount,
            highlightCount = highlightCount,
            noteCount = noteCount,
            lastReadPage = progress?.lastReadPage ?: 1,
            totalPages = progress?.totalPages ?: 0,
            isCompleted = progress?.isCompleted ?: false
        )
    }
    
    private fun getDefaultPreferences(): ReadingPreference {
        return ReadingPreference(
            id = 1,
            fontSize = 14f,
            fontFamily = "serif",
            lineSpacing = 1.2f,
            backgroundColor = "#FFFFFF",
            textColor = "#000000",
            nightMode = false,
            autoBookmark = true,
            highlightColor = "#FFFF00"
        )
    }
}

/**
 * Data class for search results within a document
 */
data class SearchResult(
    val pageNumber: Int,
    val text: String,
    val context: String,
    val position: Int
)

/**
 * Data class for reading statistics
 */
data class ReadingStats(
    val progressPercentage: Float,
    val timeSpentReading: Long,
    val bookmarkCount: Int,
    val annotationCount: Int,
    val highlightCount: Int,
    val noteCount: Int,
    val lastReadPage: Int,
    val totalPages: Int,
    val isCompleted: Boolean
)