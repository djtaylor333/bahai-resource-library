package com.bahairesources.library.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bahairesources.library.data.entities.Bookmark
import com.bahairesources.library.data.entities.Annotation
import com.bahairesources.library.data.entities.AnnotationType
import com.bahairesources.library.data.entities.ReadingProgress
import com.bahairesources.library.data.entities.ReadingPreference
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks WHERE documentId = :documentId ORDER BY pageNumber ASC")
    fun getBookmarksForDocument(documentId: Long): Flow<List<Bookmark>>
    
    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<Bookmark>>
    
    @Query("SELECT * FROM bookmarks WHERE documentId = :documentId AND pageNumber = :pageNumber")
    suspend fun getBookmarkForPage(documentId: Long, pageNumber: Int): Bookmark?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: Bookmark): Long
    
    @Update
    suspend fun updateBookmark(bookmark: Bookmark)
    
    @Delete
    suspend fun deleteBookmark(bookmark: Bookmark)
    
    @Query("DELETE FROM bookmarks WHERE documentId = :documentId")
    suspend fun deleteBookmarksForDocument(documentId: Long)
    
    @Query("SELECT COUNT(*) FROM bookmarks WHERE documentId = :documentId")
    suspend fun getBookmarkCountForDocument(documentId: Long): Int
}

@Dao
interface AnnotationDao {
    @Query("SELECT * FROM annotations WHERE documentId = :documentId ORDER BY pageNumber ASC, startPosition ASC")
    fun getAnnotationsForDocument(documentId: Long): Flow<List<Annotation>>
    
    @Query("SELECT * FROM annotations WHERE documentId = :documentId AND pageNumber = :pageNumber ORDER BY startPosition ASC")
    fun getAnnotationsForPage(documentId: Long, pageNumber: Int): Flow<List<Annotation>>
    
    @Query("SELECT * FROM annotations WHERE type = :type ORDER BY createdAt DESC")
    fun getAnnotationsByType(type: AnnotationType): Flow<List<Annotation>>
    
    @Query("SELECT * FROM annotations ORDER BY createdAt DESC LIMIT 50")
    fun getRecentAnnotations(): Flow<List<Annotation>>
    
    @Query("SELECT * FROM annotations WHERE note IS NOT NULL AND note != '' ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<Annotation>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnotation(annotation: Annotation): Long
    
    @Update
    suspend fun updateAnnotation(annotation: Annotation)
    
    @Delete
    suspend fun deleteAnnotation(annotation: Annotation)
    
    @Query("DELETE FROM annotations WHERE documentId = :documentId")
    suspend fun deleteAnnotationsForDocument(documentId: Long)
    
    @Query("SELECT COUNT(*) FROM annotations WHERE documentId = :documentId")
    suspend fun getAnnotationCountForDocument(documentId: Long): Int
    
    @Query("SELECT COUNT(*) FROM annotations WHERE type = :type")
    suspend fun getAnnotationCountByType(type: AnnotationType): Int
}

@Dao
interface ReadingProgressDao {
    @Query("SELECT * FROM reading_progress WHERE documentId = :documentId")
    suspend fun getProgressForDocument(documentId: Long): ReadingProgress?
    
    @Query("SELECT * FROM reading_progress ORDER BY lastReadAt DESC")
    fun getAllReadingProgress(): Flow<List<ReadingProgress>>
    
    @Query("SELECT * FROM reading_progress WHERE isCompleted = 1 ORDER BY lastReadAt DESC")
    fun getCompletedDocuments(): Flow<List<ReadingProgress>>
    
    @Query("SELECT * FROM reading_progress WHERE isCompleted = 0 ORDER BY lastReadAt DESC")
    fun getInProgressDocuments(): Flow<List<ReadingProgress>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: ReadingProgress)
    
    @Update
    suspend fun updateProgress(progress: ReadingProgress)
    
    @Query("UPDATE reading_progress SET lastReadPage = :pageNumber, progressPercentage = :percentage, lastReadAt = :lastReadAt WHERE documentId = :documentId")
    suspend fun updateReadingPosition(documentId: Long, pageNumber: Int, percentage: Float, lastReadAt: java.util.Date)
    
    @Query("UPDATE reading_progress SET timeSpentReading = timeSpentReading + :additionalTime WHERE documentId = :documentId")
    suspend fun addReadingTime(documentId: Long, additionalTime: Long)
    
    @Delete
    suspend fun deleteProgress(progress: ReadingProgress)
}

@Dao
interface ReadingPreferenceDao {
    @Query("SELECT * FROM reading_preferences WHERE id = 1")
    suspend fun getPreferences(): ReadingPreference?
    
    @Query("SELECT * FROM reading_preferences WHERE id = 1")
    fun getPreferencesFlow(): Flow<ReadingPreference?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePreferences(preferences: ReadingPreference)
    
    @Query("UPDATE reading_preferences SET fontSize = :fontSize WHERE id = 1")
    suspend fun updateFontSize(fontSize: Float)
    
    @Query("UPDATE reading_preferences SET nightMode = :nightMode WHERE id = 1")
    suspend fun updateNightMode(nightMode: Boolean)
    
    @Query("UPDATE reading_preferences SET highlightColor = :color WHERE id = 1")
    suspend fun updateHighlightColor(color: String)
}