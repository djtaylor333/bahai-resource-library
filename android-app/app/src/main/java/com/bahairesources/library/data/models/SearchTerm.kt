package com.bahairesources.library.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

/**
 * Search term entity for autocomplete and search suggestions
 */
@Entity(
    tableName = "search_terms",
    indices = [Index("term", unique = true)]
)
data class SearchTerm(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val term: String,
    val frequency: Int = 1,
    val category: String = ""
)