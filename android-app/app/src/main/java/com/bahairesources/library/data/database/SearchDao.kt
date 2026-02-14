package com.bahairesources.library.data.database

import androidx.room.*
import com.bahairesources.library.data.models.SearchResult
import com.bahairesources.library.data.models.SearchTerm
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for full-text search operations
 */
@Dao
interface SearchDao {
    
    /**
     * Perform full-text search using FTS5
     */
    @Query("""
        SELECT 
            document_search.rowid as documentId,
            document_search.title,
            document_search.author,
            document_search.category,
            snippet(document_search, 2, '<mark>', '</mark>', '...', 32) as snippet,
            rank as relevanceScore
        FROM document_search 
        WHERE document_search MATCH :query
        ORDER BY rank
        LIMIT :limit OFFSET :offset
    """)
    suspend fun searchDocuments(
        query: String, 
        limit: Int = 50, 
        offset: Int = 0
    ): List<SearchResult>
    
    /**
     * Search with author filter
     */
    @Query("""
        SELECT 
            document_search.rowid as documentId,
            document_search.title,
            document_search.author,
            document_search.category,
            snippet(document_search, 2, '<mark>', '</mark>', '...', 32) as snippet,
            rank as relevanceScore
        FROM document_search 
        WHERE document_search MATCH :query 
        AND author = :author
        ORDER BY rank
        LIMIT :limit OFFSET :offset
    """)
    suspend fun searchDocumentsByAuthor(
        query: String, 
        author: String,
        limit: Int = 50, 
        offset: Int = 0
    ): List<SearchResult>
    
    /**
     * Search with category filter
     */
    @Query("""
        SELECT 
            document_search.rowid as documentId,
            document_search.title,
            document_search.author,
            document_search.category,
            snippet(document_search, 2, '<mark>', '</mark>', '...', 32) as snippet,
            rank as relevanceScore
        FROM document_search 
        WHERE document_search MATCH :query 
        AND category = :category
        ORDER BY rank
        LIMIT :limit OFFSET :offset
    """)
    suspend fun searchDocumentsByCategory(
        query: String, 
        category: String,
        limit: Int = 50, 
        offset: Int = 0
    ): List<SearchResult>
    
    /**
     * Get search term suggestions for autocomplete
     */
    @Query("""
        SELECT * FROM search_terms 
        WHERE term LIKE :prefix || '%' 
        ORDER BY frequency DESC, term ASC 
        LIMIT :limit
    """)
    suspend fun getSearchSuggestions(prefix: String, limit: Int = 10): List<SearchTerm>
    
    /**
     * Get popular search terms
     */
    @Query("""
        SELECT * FROM search_terms 
        WHERE category = :category
        ORDER BY frequency DESC 
        LIMIT :limit
    """)
    suspend fun getPopularTermsByCategory(category: String, limit: Int = 20): List<SearchTerm>
    
    /**
     * Get all popular search terms
     */
    @Query("""
        SELECT * FROM search_terms 
        ORDER BY frequency DESC 
        LIMIT :limit
    """)
    suspend fun getPopularTerms(limit: Int = 50): List<SearchTerm>
    
    /**
     * Count total search results
     */
    @Query(\"SELECT COUNT(*) FROM document_search WHERE document_search MATCH :query\")
    suspend fun getSearchResultCount(query: String): Int
    
    /**
     * Insert search term
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchTerm(searchTerm: SearchTerm)
    
    /**
     * Update search term frequency
     */
    @Query(\"UPDATE search_terms SET frequency = frequency + 1 WHERE term = :term\")
    suspend fun incrementTermFrequency(term: String)
}