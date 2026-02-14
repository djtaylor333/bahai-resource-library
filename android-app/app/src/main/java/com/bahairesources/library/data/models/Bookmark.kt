package com.bahairesources.library.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Bookmark entity for favorites and notes
 */
@Entity(
    tableName = "bookmarks",
    foreignKeys = [ForeignKey(
        entity = Document::class,
        parentColumns = ["id"],
        childColumns = ["documentId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("documentId")]
)
@Parcelize
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val documentId: Long,
    val pageNumber: Int? = null,
    val bookmarkText: String = "",
    val note: String = "",
    val createdDate: String = ""
) : Parcelable

/**
 * Data class for bookmark with document information
 */
@Parcelize
data class BookmarkWithDocument(
    val bookmark: Bookmark,
    val document: Document
) : Parcelable