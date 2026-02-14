package com.bahairesources.library.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Document entity representing a Bahai text/document
 */
@Entity(tableName = "documents")
@Parcelize
data class Document(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String,
    val category: String,
    val description: String = "",
    val filePath: String,
    val fileHash: String,
    val pageCount: Int = 0,
    val wordCount: Int = 0,
    val extractedDate: String = "",
    val sourceUrl: String = ""
) : Parcelable

/**
 * Document categories enum
 */
enum class DocumentCategory(val displayName: String) {
    HOLY_TEXT("Holy Text"),
    LETTERS("Letters"),
    COMPILATIONS("Compilations"),
    ADMINISTRATIVE_TEXT("Administrative Text"),
    HISTORICAL_TEXT("Historical Text"),
    PRAYER_BOOK("Prayer Book"),
    GUIDANCE("Guidance"),
    OTHER("Other");
    
    companion object {
        fun fromString(category: String): DocumentCategory {
            return values().find { it.displayName.equals(category, ignoreCase = true) } 
                ?: OTHER
        }
    }
}

/**
 * Document authors enum
 */
enum class DocumentAuthor(val displayName: String) {
    BAHAULLAH("Bahá'u'lláh"),
    ABDUL_BAHA("'Abdu'l-Bahá"),
    SHOGHI_EFFENDI("Shoghi Effendi"),
    UNIVERSAL_HOUSE_OF_JUSTICE("Universal House of Justice"),
    NATIONAL_SPIRITUAL_ASSEMBLY("National Spiritual Assembly"),
    HANDS_OF_CAUSE("Hands of the Cause"),
    COMPILATION("Compilation"),
    OTHER("Other");
    
    companion object {
        fun fromString(author: String): DocumentAuthor {
            return values().find { it.displayName.equals(author, ignoreCase = true) } 
                ?: OTHER
        }
    }
}