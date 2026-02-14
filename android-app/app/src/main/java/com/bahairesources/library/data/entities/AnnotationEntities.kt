package com.bahairesources.library.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.Date


/**
 * Entity representing a bookmark in a document
 */
@Entity(
    tableName = "bookmarks",
    foreignKeys = [
        ForeignKey(
            entity = Document::class,
            parentColumns = ["id"],
            childColumns = ["documentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["documentId"])]
)
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val documentId: Long,
    val pageNumber: Int,
    val title: String,
    val description: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

/**
 * Entity representing a digital note/highlight
 */
@Entity(
    tableName = "annotations",
    foreignKeys = [
        ForeignKey(
            entity = Document::class,
            parentColumns = ["id"],
            childColumns = ["documentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["documentId"]), Index(value = ["type"])]
)
data class Annotation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val documentId: Long,
    val pageNumber: Int,
    val type: AnnotationType,
    val selectedText: String,
    val note: String? = null,
    val color: String = "#FFFF00", // Default yellow highlight
    val startPosition: Int,
    val endPosition: Int,
    val coordinates: String? = null, // JSON string for position coordinates
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

/**
 * Types of annotations supported
 */
enum class AnnotationType {
    HIGHLIGHT,
    NOTE,
    UNDERLINE,
    STRIKETHROUGH,
    BOOKMARK_HIGHLIGHT
}

/**
 * Entity for storing user reading preferences
 */
@Entity(tableName = "reading_preferences")
data class ReadingPreference(
    @PrimaryKey
    val id: Long = 1, // Single row for global preferences
    val fontSize: Float = 14f,
    val fontFamily: String = "serif",
    val lineSpacing: Float = 1.2f,
    val backgroundColor: String = "#FFFFFF",
    val textColor: String = "#000000",
    val nightMode: Boolean = false,
    val autoBookmark: Boolean = true,
    val highlightColor: String = "#FFFF00"
)

/**
 * Entity for reading statistics and progress
 */
@Entity(
    tableName = "reading_progress",
    foreignKeys = [
        ForeignKey(
            entity = Document::class,
            parentColumns = ["id"],
            childColumns = ["documentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["documentId"])]
)
data class ReadingProgress(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val documentId: Long,
    val lastReadPage: Int = 1,
    val totalPages: Int,
    val progressPercentage: Float = 0f,
    val timeSpentReading: Long = 0, // In milliseconds
    val lastReadAt: Date = Date(),
    val isCompleted: Boolean = false
)