package com.bahairesources.library.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Search result data class
 */
@Parcelize
data class SearchResult(
    val documentId: Long,
    val title: String,
    val author: String,
    val category: String,
    val snippet: String,
    val pageNumber: Int? = null,
    val relevanceScore: Float = 0f
) : Parcelable

/**
 * Search query parameters
 */
data class SearchQuery(
    val query: String,
    val author: String? = null,
    val category: String? = null,
    val limit: Int = 50,
    val offset: Int = 0
)

/**
 * Search statistics
 */
data class SearchStats(
    val totalResults: Int,
    val queryTime: Long,
    val categories: Map<String, Int>,
    val authors: Map<String, Int>
)