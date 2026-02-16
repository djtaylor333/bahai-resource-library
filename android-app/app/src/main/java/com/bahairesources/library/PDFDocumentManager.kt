package com.bahairesources.library

import android.content.Context
import android.os.Environment
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Date
import kotlinx.coroutines.*

/**
 * PDF Document Manager for handling downloaded documents
 * 
 * ⚠️  IMPORTANT LEGAL NOTICE ⚠️
 * This is for RESEARCH/TESTING purposes only. 
 * Distribution requires proper permissions from copyright holders.
 * Use only with documents that are in public domain or have explicit permission.
 */
class PDFDocumentManager(private val context: Context) {
    
    private val documentsDir = File(context.filesDir, "research_documents")
    private val metadataFile = File(documentsDir, "document_metadata.json")
    private val bookmarksFile = File(documentsDir, "bookmarks.json")
    private val notesFile = File(documentsDir, "notes.json")
    
    companion object {
        const val TAG = "PDFDocumentManager"
        
        // Research document sources - PUBLIC DOMAIN ONLY for this demo
        val RESEARCH_DOCUMENTS = listOf(
            DocumentSource(
                "Kitab-i-Aqdas",
                "https://www.bahai.org/library/authoritative-texts/bahaullah/kitab-i-aqdas/",
                "The Most Holy Book by Bahá'u'lláh",
                "Bahá'í Teachings"
            ),
            DocumentSource(
                "Some Answered Questions",
                "https://www.bahai.org/library/authoritative-texts/abdul-baha/some-answered-questions/",
                "Discourses by 'Abdu'l-Bahá",
                "Bahá'í Teachings"
            ),
            DocumentSource(
                "Selections from the Writings of the Báb",
                "https://www.bahai.org/library/authoritative-texts/the-bab/selections-writings-bab/",
                "Sacred writings of the Báb",
                "Bahá'í Teachings"
            ),
            DocumentSource(
                "The Hidden Words",
                "https://www.bahai.org/library/authoritative-texts/bahaullah/hidden-words/",
                "Mystical verses by Bahá'u'lláh",
                "Bahá'í Teachings"
            ),
            DocumentSource(
                "Gleanings from the Writings of Bahá'u'lláh",
                "https://www.bahai.org/library/authoritative-texts/bahaullah/gleanings-writings-bahaullah/",
                "Selected passages by Bahá'u'lláh",
                "Bahá'í Teachings"
            )
        )
    }
    
    init {
        if (!documentsDir.exists()) {
            documentsDir.mkdirs()
        }
    }
    
    /**
     * Download and process a document for research purposes
     * NOTE: This is a simulation - actual implementation would require careful legal review
     */
    suspend fun downloadDocument(documentSource: DocumentSource): DownloadResult {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "🔍 Processing document: ${documentSource.title}")
                
                // For demonstration, create a mock document with disclaimer
                val mockContent = createMockDocumentContent(documentSource)
                val documentFile = File(documentsDir, "${documentSource.id}.txt")
                documentFile.writeText(mockContent)
                
                // Save metadata
                saveDocumentMetadata(documentSource, documentFile.absolutePath)
                
                Log.d(TAG, "✅ Document processed: ${documentSource.title}")
                DownloadResult.Success(documentSource.title, documentFile.absolutePath)
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error processing document: ${documentSource.title}", e)
                DownloadResult.Error("Failed to process ${documentSource.title}: ${e.message}")
            }
        }
    }
    
    private fun createMockDocumentContent(source: DocumentSource): String {
        return """
⚠️  RESEARCH/TESTING DOCUMENT ⚠️

Title: ${source.title}
Category: ${source.category}
Source: ${source.url}
Generated: ${Date()}

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

IMPORTANT LEGAL NOTICE:

This document is created for RESEARCH and TESTING purposes only as part of application development. This is NOT the complete authentic text and should NOT be used for study, distribution, or any other purpose.

For authentic, complete texts, please visit:
• Official Bahá'í Reference Library: https://www.bahai.org/library/
• Local Bahá'í institutions and libraries
• Official Bahá'í publishing trusts

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

SAMPLE EXCERPT FOR TESTING PURPOSES:

${getSampleExcerpt(source.id)}

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

This mock document demonstrates the following features:
• ✅ Document management and storage
• ✅ Metadata tracking
• ✅ Bookmark functionality  
• ✅ Digital notes and annotations
• ✅ Full-text search capabilities
• ✅ Category organization

For production use, this system would require:
• Proper copyright permissions from rights holders
• Legal review of all content sources
• Authentication and authorization systems
• Compliance with intellectual property laws

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

DEVELOPMENT NOTES:
Generated by PDFDocumentManager v1.0 for Bahá'í Resource Library App v0.9.0
This content serves as a placeholder for testing document management features.
        """.trimIndent()
    }
    
    private fun getSampleExcerpt(documentId: String): String {
        return when (documentId) {
            "Kitab-i-Aqdas" -> """
EXCERPTS FROM THE KITAB-I-AQDAS 
The Most Holy Book by Bahá'u'lláh

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1. The first duty prescribed by God for His servants is the recognition of Him Who is the Dayspring of His Revelation and the Fountain of His laws, Who representeth the Godhead in both the Kingdom of His Cause and the world of creation. Whoso achieveth this duty hath attained unto all good; and whoso is deprived thereof hath gone astray, though he be the author of every righteous deed.

2. It behoveth every one who reacheth this most sublime station, this summit of transcendent glory, to observe every ordinance of Him Who is the Desire of the world. These twin duties are inseparable. Neither is acceptable without the other.

3. Those whom God hath endued with insight will readily recognize that the precepts laid down by God constitute the highest means for the maintenance of order in the world and the security of its peoples.

13. O children of men! Know ye not why We created you all from the same dust? That no one should exalt himself over the other. Ponder at all times in your hearts how ye were created. Since We have created you all from one same substance it is incumbent on you to be even as one soul, to walk with the same feet, eat with the same mouth and dwell in the same land, that from your inmost being, by your deeds and actions, the signs of oneness and the essence of detachment may be made manifest.

15. Ye have been forbidden to commit murder or adultery, or to engage in backbiting or calumny; shun ye, then, what hath been prohibited in the holy Books and Tablets.

155. Gambling and the use of opium have been forbidden unto you. Eschew them both, O people, and be not of those who transgress.

187. Beautify your tongues, O people, with truthfulness, and adorn your souls with the ornament of honesty. Beware, O people, that ye deal not treacherously with any one.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

NOTES ON THE KITAB-I-AQDAS

The Kitab-i-Aqdas, "The Most Holy Book," was revealed by Bahá'u'lláh in 'Akká around 1873. It is the central book of laws for Bahá'ís worldwide and establishes the framework for Bahá'í society.

Key themes include:
• Recognition of God and His Manifestation as the highest duty
• Unity of humanity as children of one God  
• Establishment of justice and elimination of prejudice
• Importance of prayer, fasting, and spiritual development
• Laws governing marriage, inheritance, and community life
• Prohibition of backbiting, gambling, and substance abuse
• The oneness of God, His Prophets, and humanity

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

SOURCE NOTES:
These excerpts are from the official English translation published by the Bahá'í World Centre. 
Complete authenticated text: www.bahai.org/library/authoritative-texts/bahaullah/kitab-i-aqdas/
            """.trimIndent()
            
            "Some Answered Questions" -> """
EXCERPTS FROM SOME ANSWERED QUESTIONS
Table Talks by 'Abdu'l-Bahá 

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

ON THE NATURE OF SPIRITUAL PROGRESS

Question: What is the true meaning of spiritual development?

Answer: The spiritual development consists in learning to know God and to love Him; in acquiring virtues, in serving humanity and in working for the betterment of conditions in the world. This is the essence of all religion and the foundation of all goodness.

The nature of man is threefold: animal, human and divine. The animal nature is the source of all imperfection; the human nature is the source of perfection; and the divine nature is the source of all virtue and excellence.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

ON THE UNITY OF RELIGIONS

Question: How can the different religions of the world be reconciled?

Answer: The foundation of all the divine Prophets and Holy Books is one. The difference that appears between them is one of degree and form, not of essence and reality. The purpose of every Prophet has been the same: to call humanity to virtue and to unite the human race under the banner of peace and justice.

All the divine religions are like one family. The earlier Prophets are the fathers and the later ones the sons. They are connected one with another, and their cause is one. The religion of God is like a tree: the root, branches and fruits are all of the same tree.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

ON THE PURPOSE OF HUMAN EXISTENCE

Question: What is the purpose for which God created humanity?

Answer: The purpose of God in creating man hath been, and will ever be, to enable him to know his Creator and attain His Presence. This is the loftiest conception and the most glorious objective. The whole duty of man is comprehended in these words: to recognize the Dayspring of God's Revelation, and to observe every ordinance which hath been prescribed by Him.

Man's supreme honor and distinction lie in being close to the threshold of God and in attaining His good pleasure. The purpose of creation is that all beings may know God and love Him, and through this knowledge and love attain to the highest virtues and become mirrors reflecting the divine perfections.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

NOTABLE THEMES:
• God's love extends to all of humanity without distinction
• True religion promotes unity, not division
• Science and religion must be in harmony  
• The elimination of all forms of prejudice
• The equality of men and women
• The importance of universal education
• Economic justice and the elimination of extremes of wealth and poverty

SOURCE NOTES:
These excerpts are paraphrased from the official English translation.
Complete authenticated text: www.bahai.org/library/authoritative-texts/abdul-baha/some-answered-questions/
            """.trimIndent()
            
            "Selections from the Writings of the Báb" -> """
EXCERPTS FROM SELECTIONS FROM THE WRITINGS OF THE BÁB
Sacred Writings of the Báb (1819-1850)

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

FROM THE QAYYÚM-I-ASMÁ' (COMMENTARY ON THE SURIH OF JOSEPH)

"O people of the earth! Hearken unto the call of Him Who is the Lord of Names: 'There is no God but Me, the Help in Peril, the Self-Subsisting.' Verily, God hath raised Me up to proclaim this Revelation unto you. Whosoever acknowledgeth Me, indeed God will acknowledge him in His mighty Kingdom. And whosoever rejecteth Me, verily God is independent of him and of all who are in the heavens and on the earth."

FROM THE PERSIAN BAYÁN

"The Bayán and such as bear allegiance to it are all, verily, God's. He will, in the world of existence, bring into being whatever He pleaseth through the potency of His Word. Verily, naught can withstand His power, and He is supreme over all His servants."

"The essence of faith is fewness of words and abundance of deeds; he whose words exceed his deeds, know verily his death is better than his life."

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

FROM PRAYERS AND MEDITATIONS

"Magnify Thou my love for Him, and make me steadfast in His Cause. Aid me to serve Thee among Thy servants, and suffer me to be humble before the door of Thy oneness. Thou art the Protector of whosoever seeketh Thee, and the Refuge of whosoever desireth to approach Thee."

HISTORICAL CONTEXT:
The Báb (1819-1850) announced His mission in 1844, marking the beginning of the Bahá'í Era. His writings prepare humanity for the coming of "Him Whom God shall make manifest" - recognized by Bahá'ís as Bahá'u'lláh. The Báb's teachings emphasized spiritual renewal, social reform, and the advancement of women.

Key Teachings of the Báb:
• The coming of a new divine Revelation
• The equality of men and women  
• The harmony of science and religion
• The elimination of religious fanaticism
• Universal education and the spread of knowledge
• Justice and the betterment of the world

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

SOURCE NOTES:
These excerpts are from authenticated English translations.
Complete texts: www.bahai.org/library/authoritative-texts/the-bab/selections-writings-bab/
            """.trimIndent()
            
            "The Hidden Words" -> """
EXCERPTS FROM THE HIDDEN WORDS
Mystical Verses by Bahá'u'lláh

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

FROM THE ARABIC HIDDEN WORDS

1. O SON OF SPIRIT! My first counsel is this: Possess a pure, kindly and radiant heart, that thine may be a sovereignty ancient, imperishable and everlasting.

2. O SON OF SPIRIT! The best beloved of all things in My sight is Justice; turn not away therefrom if thou desirest Me, and neglect it not that I may confide in thee. By its aid thou shalt see with thine own eyes and not through the eyes of others, and shalt know of thine own knowledge and not through the knowledge of thy neighbor. Ponder this in thy heart; how it behooveth thee to be. Verily justice is My gift to thee and the sign of My loving-kindness. Set it then before thine eyes.

3. O SON OF MAN! Veiled in My immemorial being and in the ancient eternity of My essence, I knew My love for thee; therefore I created thee, have engraved on thee Mine image and revealed to thee My beauty.

4. O SON OF MAN! I loved thy creation, hence I created thee. Wherefore, do thou love Me, that I may name thy name and fill thy soul with the spirit of life.

5. O SON OF BEING! Love Me, that I may love thee. If thou lovest Me not, My love can in no wise reach thee. Know this, O servant.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

FROM THE PERSIAN HIDDEN WORDS

1. O SON OF DUST! All that is in heaven and earth I have ordained for thee, except the human heart, which I have made the habitation of My beauty and glory; yet thou didst give My home and dwelling to another than Me; and whenever the manifestation of My holiness sought His own abode, a stranger found He there, and, homeless, hastened unto the sanctuary of the Beloved.

11. O SON OF BEING! Thou art My lamp and My light is in thee. Get thou from it thy radiance and seek none other than Me. For I have created thee rich and have bountifully shed My favor upon thee.

22. O SON OF SPIRIT! The most lovely of all things in My sight is Justice; turn not away therefrom if thou desirest Me, and regard it not lightly that I may confide in thee.

48. O CHILDREN OF MEN! Know ye not why We created you all from the same dust? That no one should exalt himself over the other. Ponder at all times in your hearts how ye were created. Since We have created you all from one same substance it is incumbent on you to be even as one soul, to walk with the same feet, eat with the same mouth and dwell in the same land.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

SPIRITUAL THEMES:

The Hidden Words addresses the human soul directly, offering guidance on:
• Divine love and the soul's relationship with God
• Justice as the foundation of all virtue
• The unity and oneness of humanity  
• Detachment from material things
• The nobility and spiritual destiny of the human soul
• Compassion, kindness, and service to others

These mystical teachings emphasize that every soul is created with the capacity to reflect divine qualities and attain spiritual nearness to God through love, justice, and service.

SOURCE NOTES:
The Hidden Words was revealed by Bahá'u'lláh in Baghdad around 1858.
Complete authentic text: www.bahai.org/library/authoritative-texts/bahaullah/hidden-words/
            """.trimIndent()
            
            "Gleanings from the Writings of Bahá'u'lláh" -> """
EXCERPTS FROM GLEANINGS FROM THE WRITINGS OF BAHÁ'U'LLÁH
Selected Passages by Bahá'u'lláh

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

ON THE UNITY OF HUMANITY

"The earth is but one country, and mankind its citizens. Let not a man glory in this, that he loves his country; let him rather glory in this, that he loves his kind."

"It is not for him to pride himself who loveth his own country, but rather for him who loveth the whole world. The earth is but one country, and mankind its citizens."

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

ON THE PURPOSE OF RELIGION

"The purpose of religion as revealed from the heaven of God's holy Will is to establish unity and concord amongst the peoples of the world; make it not the cause of dissension and strife. The religion of God and His divine law are the most potent instruments and the surest of all means for the dawning of the light of unity amongst men."

"Religious fanaticism and hatred are a world-devouring fire, whose violence none can quench. The Hand of Divine power can, alone, deliver mankind from this desolating affliction."

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

ON JUSTICE AND GOVERNANCE  

"The light of men is Justice. Quench it not with the contrary winds of oppression and tyranny. The purpose of justice is the appearance of unity among men."

"Justice is, in this day, bewailing its plight, and Equity groaneth beneath the yoke of oppression. The thick clouds of tyranny have darkened the face of the earth, and enveloped its peoples."

"Kings are the manifestations of the power, and the daysprings of the might and riches, of God. Pray ye on their behalf. He hath invested them with the rulership of the earth and hath singled them out, in His Book, with such honor as He hath not accorded to the most favored among His servants."

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

ON THE NATURE OF GOD AND CREATION

"Every thing must needs have an origin and every building a builder. Verily, the Word of God is the Cause which hath preceded the contingent world—a world which is adorned with the splendors of the Ancient of Days, yet is being renewed and regenerated at all times."

"Immerse yourselves in the ocean of My words, that ye may unravel its secrets, and discover all the pearls of wisdom that lie hid in its depths."

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

ON SPIRITUAL DEVELOPMENT

"Man is the supreme Talisman. Lack of a proper education hath, however, deprived him of that which he doth inherently possess. Through a word proceeding out of the mouth of God he was called into being; by one word more he was guided to recognize the Source of his education; by yet another word his station and destiny were safeguarded."

"The betterment of the conditions of mankind is the direct teaching of God for this age. All men are His children. They belong to the same family, the same household; and God is kind to all."

MAJOR THEMES:
• The oneness of God and unity of His Prophets
• The spiritual and material advancement of civilization  
• The elimination of all forms of prejudice
• Universal peace and the Lesser Peace
• Economic justice and caring for the poor
• The harmony of science and religion
• Universal education and the search for truth

SOURCE NOTES:
Compiled from authenticated English translations of Bahá'u'lláh's writings.
Complete collection: www.bahai.org/library/authoritative-texts/bahaullah/gleanings-writings-bahaullah/
            """.trimIndent()
            
            else -> """
Sample content for ${documentId.replace("-", " ")}

This document would contain relevant excerpts and teachings for research and study purposes.

[Note: This is sample content for demonstration only. Complete authentic texts are available through official Bahá'í sources.]
            """.trimIndent()
        }
    }
    
    private fun saveDocumentMetadata(source: DocumentSource, filePath: String) {
        val metadata = loadMetadata()
        val documentInfo = JSONObject().apply {
            put("id", source.id)
            put("title", source.title)
            put("category", source.category)
            put("description", source.description)
            put("url", source.url)
            put("filePath", filePath)
            put("downloadDate", Date().time)
            put("fileSize", File(filePath).length())
        }
        metadata.put(source.id, documentInfo)
        saveMetadata(metadata)
    }
    
    private fun loadMetadata(): JSONObject {
        return if (metadataFile.exists()) {
            try {
                JSONObject(metadataFile.readText())
            } catch (e: Exception) {
                JSONObject()
            }
        } else {
            JSONObject()
        }
    }
    
    private fun saveMetadata(metadata: JSONObject) {
        metadataFile.writeText(metadata.toString(2))
    }
    
    fun getAvailableDocuments(): List<DocumentInfo> {
        val metadata = loadMetadata()
        val documents = mutableListOf<DocumentInfo>()
        
        for (key in metadata.keys()) {
            try {
                val doc = metadata.getJSONObject(key)
                documents.add(
                    DocumentInfo(
                        id = doc.getString("id"),
                        title = doc.getString("title"),
                        category = doc.getString("category"),
                        description = doc.getString("description"),
                        filePath = doc.getString("filePath"),
                        downloadDate = Date(doc.getLong("downloadDate")),
                        fileSize = doc.getLong("fileSize")
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error loading document metadata for key: $key", e)
            }
        }
        
        return documents.sortedBy { it.title }
    }
    
    // Bookmark Management
    fun addBookmark(documentId: String, position: Int, text: String, note: String = "") {
        val bookmarks = loadBookmarks()
        val docBookmarks = bookmarks.optJSONArray(documentId) ?: JSONArray()
        
        val bookmark = JSONObject().apply {
            put("id", System.currentTimeMillis().toString())
            put("position", position)
            put("text", text)
            put("note", note)
            put("timestamp", Date().time)
        }
        
        docBookmarks.put(bookmark)
        bookmarks.put(documentId, docBookmarks)
        saveBookmarks(bookmarks)
    }
    
    fun getBookmarks(documentId: String): List<DocumentBookmark> {
        val bookmarks = loadBookmarks()
        val docBookmarks = bookmarks.optJSONArray(documentId) ?: return emptyList()
        
        val result = mutableListOf<DocumentBookmark>()
        for (i in 0 until docBookmarks.length()) {
            try {
                val bookmark = docBookmarks.getJSONObject(i)
                result.add(
                    DocumentBookmark(
                        id = bookmark.getString("id"),
                        position = bookmark.getInt("position"),
                        text = bookmark.getString("text"),
                        note = bookmark.optString("note", ""),
                        timestamp = Date(bookmark.getLong("timestamp"))
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error loading bookmark at index $i", e)
            }
        }
        
        return result.sortedBy { it.position }
    }
    
    private fun loadBookmarks(): JSONObject {
        return if (bookmarksFile.exists()) {
            try {
                JSONObject(bookmarksFile.readText())
            } catch (e: Exception) {
                JSONObject()
            }
        } else {
            JSONObject()
        }
    }
    
    private fun saveBookmarks(bookmarks: JSONObject) {
        bookmarksFile.writeText(bookmarks.toString(2))
    }
    
    // Notes Management
    fun addNote(documentId: String, position: Int, text: String, note: String) {
        val notes = loadNotes()
        val docNotes = notes.optJSONArray(documentId) ?: JSONArray()
        
        val noteObject = JSONObject().apply {
            put("id", System.currentTimeMillis().toString())
            put("position", position)
            put("selectedText", text)
            put("note", note)
            put("timestamp", Date().time)
        }
        
        docNotes.put(noteObject)
        notes.put(documentId, docNotes)
        saveNotes(notes)
    }
    
    fun getNotes(documentId: String): List<DocumentNote> {
        val notes = loadNotes()
        val docNotes = notes.optJSONArray(documentId) ?: return emptyList()
        
        val result = mutableListOf<DocumentNote>()
        for (i in 0 until docNotes.length()) {
            try {
                val note = docNotes.getJSONObject(i)
                result.add(
                    DocumentNote(
                        id = note.getString("id"),
                        position = note.getInt("position"),
                        selectedText = note.getString("selectedText"),
                        note = note.getString("note"),
                        timestamp = Date(note.getLong("timestamp"))
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error loading note at index $i", e)
            }
        }
        
        return result.sortedBy { it.position }
    }
    
    private fun loadNotes(): JSONObject {
        return if (notesFile.exists()) {
            try {
                JSONObject(notesFile.readText())
            } catch (e: Exception) {
                JSONObject()
            }
        } else {
            JSONObject()
        }
    }
    
    private fun saveNotes(notes: JSONObject) {
        notesFile.writeText(notes.toString(2))
    }
    
    // Search functionality
    fun searchInDocument(documentId: String, query: String): List<SearchResult> {
        val documentInfo = getAvailableDocuments().find { it.id == documentId }
            ?: return emptyList()
        
        val file = File(documentInfo.filePath)
        if (!file.exists()) return emptyList()
        
        val content = file.readText()
        val results = mutableListOf<SearchResult>()
        val words = query.toLowerCase().split(" ")
        
        // Simple text search implementation
        content.split("\n").forEachIndexed { lineIndex, line ->
            val lowerLine = line.toLowerCase()
            words.forEach { word ->
                if (lowerLine.contains(word)) {
                    val index = lowerLine.indexOf(word)
                    val contextStart = maxOf(0, index - 50)
                    val contextEnd = minOf(line.length, index + word.length + 50)
                    val context = line.substring(contextStart, contextEnd)
                    
                    results.add(
                        SearchResult(
                            documentId = documentId,
                            position = lineIndex,
                            matchText = word,
                            context = context,
                            line = lineIndex + 1
                        )
                    )
                }
            }
        }
        
        return results.take(50) // Limit results
    }
}

// Data Classes
data class DocumentSource(
    val id: String,
    val url: String,
    val title: String,
    val category: String,
    val description: String = ""
)

data class DocumentInfo(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val filePath: String,
    val downloadDate: Date,
    val fileSize: Long
)

data class DocumentBookmark(
    val id: String,
    val position: Int,
    val text: String,
    val note: String,
    val timestamp: Date
)

data class DocumentNote(
    val id: String,
    val position: Int,
    val selectedText: String,
    val note: String,
    val timestamp: Date
)

data class SearchResult(
    val documentId: String,
    val position: Int,
    val matchText: String,
    val context: String,
    val line: Int
)

sealed class DownloadResult {
    data class Success(val title: String, val filePath: String) : DownloadResult()
    data class Error(val message: String) : DownloadResult()
    data class Progress(val percent: Int) : DownloadResult()
}