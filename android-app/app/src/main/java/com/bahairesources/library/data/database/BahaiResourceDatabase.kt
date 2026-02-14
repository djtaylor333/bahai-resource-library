package com.bahairesources.library.data.database

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bahairesources.library.data.dao.*
import com.bahairesources.library.data.entities.*
import java.util.Date

/**
 * Type converters for Room database
 */
class DatabaseTypeConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun fromAnnotationType(type: AnnotationType): String = type.name
    
    @TypeConverter
    fun toAnnotationType(type: String): AnnotationType = AnnotationType.valueOf(type)
}

/**
 * Main Room Database for the Bahai Resource Library
 */
@Database(
    entities = [
        Document::class,
        Page::class,
        SearchTerm::class,
        Bookmark::class,
        Annotation::class,
        ReadingProgress::class,
        ReadingPreference::class
    ],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
    ]
)
@TypeConverters(DatabaseTypeConverters::class)
abstract class BahaiResourceDatabase : RoomDatabase() {
    
    // Document and Search DAOs
    abstract fun documentDao(): DocumentDao
    abstract fun searchDao(): SearchDao
    
    // Annotation and Reading DAOs
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun annotationDao(): AnnotationDao
    abstract fun readingProgressDao(): ReadingProgressDao
    abstract fun readingPreferenceDao(): ReadingPreferenceDao

    companion object {
        const val DATABASE_NAME = "bahai_resource_database"
        
        /**
         * Migration from version 1 to 2
         * Adds bookmark and reading progress tables
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create bookmarks table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS bookmarks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        documentId INTEGER NOT NULL,
                        pageNumber INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        FOREIGN KEY(documentId) REFERENCES documents(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                
                database.execSQL("CREATE INDEX IF NOT EXISTS index_bookmarks_documentId ON bookmarks(documentId)")
                
                // Create reading preferences table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS reading_preferences (
                        id INTEGER PRIMARY KEY NOT NULL,
                        fontSize REAL NOT NULL DEFAULT 14.0,
                        fontFamily TEXT NOT NULL DEFAULT 'serif',
                        lineSpacing REAL NOT NULL DEFAULT 1.2,
                        backgroundColor TEXT NOT NULL DEFAULT '#FFFFFF',
                        textColor TEXT NOT NULL DEFAULT '#000000',
                        nightMode INTEGER NOT NULL DEFAULT 0,
                        autoBookmark INTEGER NOT NULL DEFAULT 1,
                        highlightColor TEXT NOT NULL DEFAULT '#FFFF00'
                    )
                """.trimIndent())
                
                // Insert default preferences
                database.execSQL("""
                    INSERT INTO reading_preferences (id) VALUES (1)
                """.trimIndent())
            }
        }
        
        /**
         * Migration from version 2 to 3
         * Adds annotations and reading progress tables
         */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create annotations table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS annotations (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        documentId INTEGER NOT NULL,
                        pageNumber INTEGER NOT NULL,
                        type TEXT NOT NULL,
                        selectedText TEXT NOT NULL,
                        note TEXT,
                        color TEXT NOT NULL DEFAULT '#FFFF00',
                        startPosition INTEGER NOT NULL,
                        endPosition INTEGER NOT NULL,
                        coordinates TEXT,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        FOREIGN KEY(documentId) REFERENCES documents(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                
                database.execSQL("CREATE INDEX IF NOT EXISTS index_annotations_documentId ON annotations(documentId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_annotations_type ON annotations(type)")
                
                // Create reading progress table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS reading_progress (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        documentId INTEGER NOT NULL,
                        lastReadPage INTEGER NOT NULL DEFAULT 1,
                        totalPages INTEGER NOT NULL,
                        progressPercentage REAL NOT NULL DEFAULT 0.0,
                        timeSpentReading INTEGER NOT NULL DEFAULT 0,
                        lastReadAt INTEGER NOT NULL,
                        isCompleted INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY(documentId) REFERENCES documents(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                
                database.execSQL("CREATE INDEX IF NOT EXISTS index_reading_progress_documentId ON reading_progress(documentId)")
            }
        }
    }
}