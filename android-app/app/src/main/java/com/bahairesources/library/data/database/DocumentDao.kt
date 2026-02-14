package com.bahairesources.library.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bahairesources.library.data.models.Document
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Document operations
 */
@Dao
interface DocumentDao {
    
    @Query("SELECT * FROM documents ORDER BY title ASC")
    fun getAllDocuments(): Flow<List<Document>>
    
    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getDocumentById(id: Long): Document?
    
    @Query("SELECT * FROM documents WHERE author = :author ORDER BY title ASC")
    fun getDocumentsByAuthor(author: String): Flow<List<Document>>
    
    @Query("SELECT * FROM documents WHERE category = :category ORDER BY title ASC")
    fun getDocumentsByCategory(category: String): Flow<List<Document>>
    
    @Query("SELECT DISTINCT author FROM documents ORDER BY author ASC")
    suspend fun getAllAuthors(): List<String>
    
    @Query("SELECT DISTINCT category FROM documents ORDER BY category ASC")
    suspend fun getAllCategories(): List<String>
    
    @Query("""
        SELECT * FROM documents 
        WHERE title LIKE '%' || :query || '%' 
        OR author LIKE '%' || :query || '%'
        OR description LIKE '%' || :query || '%'
        ORDER BY title ASC
    """)
    fun searchDocuments(query: String): Flow<List<Document>>
    
    @Query("SELECT COUNT(*) FROM documents")
    suspend fun getDocumentCount(): Int
    
    @Query("SELECT SUM(page_count) FROM documents")
    suspend fun getTotalPageCount(): Int
    
    @Query("SELECT SUM(word_count) FROM documents")
    suspend fun getTotalWordCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: Document): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(documents: List<Document>)
    
    @Update
    suspend fun updateDocument(document: Document)
    
    @Delete
    suspend fun deleteDocument(document: Document)
    
    @Query("DELETE FROM documents")
    suspend fun deleteAllDocuments()
}