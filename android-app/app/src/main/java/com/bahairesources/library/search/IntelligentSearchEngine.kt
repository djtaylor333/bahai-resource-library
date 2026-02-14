package com.bahairesources.library.search

import me.xdrop.fuzzywuzzy.FuzzySearch
import me.xdrop.fuzzywuzzy.algorithms.WeightedRatio
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.*
import org.apache.lucene.index.*
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.*
import org.apache.lucene.store.ByteBuffersDirectory
import org.apache.lucene.store.Directory
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Enhanced search engine with fuzzy logic and semantic capabilities
 */
@Singleton
class IntelligentSearchEngine @Inject constructor() {
    
    private val analyzer = StandardAnalyzer()
    private val directory: Directory = ByteBuffersDirectory()
    private val documentIndex = ConcurrentHashMap<String, SearchableDocument>()
    
    // Lucene components
    private lateinit var indexWriter: IndexWriter
    private lateinit var indexReader: DirectoryReader
    private lateinit var indexSearcher: IndexSearcher
    
    // Bahá'í specific terms and themes for enhanced search
    private val bahaiThemes = mapOf(
        "unity" to listOf("oneness", "unification", "harmony", "accord", "togetherness", "solidarity"),
        "justice" to listOf("fairness", "equity", "righteousness", "moral", "ethical", "right"),
        "spiritual" to listOf("soul", "divine", "sacred", "holy", "mystical", "transcendent"),
        "prayer" to listOf("devotion", "worship", "meditation", "contemplation", "communion"),
        "service" to listOf("work", "contribute", "help", "assist", "volunteer", "duty"),
        "education" to listOf("knowledge", "learning", "wisdom", "understanding", "enlightenment"),
        "world order" to listOf("civilization", "society", "global", "international", "world unity"),
        "covenant" to listOf("agreement", "promise", "commitment", "bond", "pledge"),
        "consultation" to listOf("discussion", "conference", "deliberation", "counsel", "advice"),
        "progressive revelation" to listOf("evolution", "development", "advancement", "progress", "unfoldment")
    )
    
    // Common synonyms and alternative spellings
    private val synonymMap = mapOf(
        "baha'u'llah" to listOf("bahá'u'lláh", "bahaullah", "blessed beauty"),
        "abdul-baha" to listOf("'abdu'l-bahá", "abdul baha", "abdulbaha", "master"),
        "bab" to listOf("báb", "the bab", "his holiness the báb"),
        "bahai" to listOf("bahá'í", "baha'i", "bahaism"),
        "aqdas" to listOf("kitab-i-aqdas", "kitáb-i-aqdas", "most holy book"),
        "iqan" to listOf("kitab-i-iqan", "kitáb-i-íqán", "book of certitude")
    )
    
    init {
        initializeLuceneIndex()
    }
    
    private fun initializeLuceneIndex() {
        val config = IndexWriterConfig(analyzer)
        indexWriter = IndexWriter(directory, config)
    }
    
    /**
     * Add a document to the search index
     */
    suspend fun addDocument(searchableDocument: SearchableDocument) = withContext(Dispatchers.IO) {
        documentIndex[searchableDocument.id] = searchableDocument
        
        val doc = Document().apply {
            add(StringField("id", searchableDocument.id, Field.Store.YES))
            add(TextField("title", searchableDocument.title, Field.Store.YES))
            add(TextField("author", searchableDocument.author, Field.Store.YES))
            add(TextField("content", searchableDocument.content, Field.Store.YES))
            add(TextField("category", searchableDocument.category, Field.Store.YES))
            add(StoredField("path", searchableDocument.path))
        }
        
        indexWriter.addDocument(doc)
        indexWriter.commit()
        refreshIndexReader()
    }
    
    private fun refreshIndexReader() {
        if (::indexReader.isInitialized && indexReader.isCurrent) return
        
        indexReader = DirectoryReader.open(directory)
        indexSearcher = IndexSearcher(indexReader)
    }
    
    /**
     * Perform intelligent search with multiple strategies
     */
    suspend fun performIntelligentSearch(
        query: String,
        searchType: SearchType = SearchType.INTELLIGENT,
        maxResults: Int = 50
    ): SearchResults = withContext(Dispatchers.Default) {
        
        val results = mutableListOf<IntelligentSearchResult>()
        
        when (searchType) {
            SearchType.EXACT -> {
                results.addAll(performExactSearch(query, maxResults))
            }
            SearchType.FUZZY -> {
                results.addAll(performFuzzySearch(query, maxResults))
            }
            SearchType.SEMANTIC -> {
                results.addAll(performSemanticSearch(query, maxResults))
            }
            SearchType.INTELLIGENT -> {
                // Combine multiple search strategies
                results.addAll(performExactSearch(query, maxResults / 3))
                results.addAll(performFuzzySearch(query, maxResults / 3))
                results.addAll(performSemanticSearch(query, maxResults / 3))
                results.addAll(performThematicSearch(query, maxResults / 3))
            }
        }
        
        // Remove duplicates and sort by relevance
        val uniqueResults = results
            .distinctBy { it.documentId }
            .sortedByDescending { it.score }
            .take(maxResults)
        
        SearchResults(
            query = query,
            results = uniqueResults,
            totalResults = uniqueResults.size,
            searchType = searchType
        )
    }
    
    private suspend fun performExactSearch(query: String, maxResults: Int): List<IntelligentSearchResult> {
        if (!::indexSearcher.isInitialized) return emptyList()
        
        val results = mutableListOf<IntelligentSearchResult>()
        
        try {
            val queryParser = QueryParser("content", analyzer)
            val luceneQuery = queryParser.parse(QueryParser.escape(query))
            val topDocs = indexSearcher.search(luceneQuery, maxResults)
            
            for (scoreDoc in topDocs.scoreDocs) {
                val doc = indexSearcher.doc(scoreDoc.doc)
                val snippet = highlightSnippet(doc.get("content"), query)
                
                results.add(
                    IntelligentSearchResult(
                        documentId = doc.get("id"),
                        title = doc.get("title"),
                        author = doc.get("author"),
                        snippet = snippet,
                        score = scoreDoc.score.toDouble(),
                        searchType = "Exact Match",
                        category = doc.get("category")
                    )
                )
            }
        } catch (e: Exception) {
            // Handle parsing errors for complex queries
        }
        
        return results
    }
    
    private fun performFuzzySearch(query: String, maxResults: Int): List<IntelligentSearchResult> {
        val results = mutableListOf<IntelligentSearchResult>()
        val threshold = 70 // Minimum similarity score
        
        for (document in documentIndex.values) {
            // Search in title
            val titleScore = FuzzySearch.weightedRatio(query.toLowerCase(), document.title.toLowerCase())
            if (titleScore >= threshold) {
                results.add(
                    IntelligentSearchResult(
                        documentId = document.id,
                        title = document.title,
                        author = document.author,
                        snippet = document.title,
                        score = titleScore.toDouble(),
                        searchType = "Fuzzy Title Match",
                        category = document.category
                    )
                )
            }
            
            // Search in content with sliding window
            val contentScore = findBestContentMatch(query, document.content)
            if (contentScore.score >= threshold - 20) { // Lower threshold for content
                results.add(
                    IntelligentSearchResult(
                        documentId = document.id,
                        title = document.title,
                        author = document.author,
                        snippet = contentScore.snippet,
                        score = contentScore.score,
                        searchType = "Fuzzy Content Match",
                        category = document.category
                    )
                )
            }
        }
        
        return results.sortedByDescending { it.score }.take(maxResults)
    }
    
    private fun findBestContentMatch(query: String, content: String): FuzzyContentResult {
        val words = content.split("\\s+".toRegex())
        val windowSize = query.split(" ").size + 5 // Context around query
        var bestScore = 0.0
        var bestSnippet = ""
        
        for (i in 0..words.size - windowSize) {
            val window = words.subList(i, minOf(i + windowSize, words.size)).joinToString(" ")
            val score = FuzzySearch.weightedRatio(query.toLowerCase(), window.toLowerCase())
            
            if (score > bestScore) {
                bestScore = score.toDouble()
                bestSnippet = highlightSnippet(window, query)
            }
        }
        
        return FuzzyContentResult(bestScore, bestSnippet)
    }
    
    private fun performSemanticSearch(query: String, maxResults: Int): List<IntelligentSearchResult> {
        val results = mutableListOf<IntelligentSearchResult>()
        val expandedQuery = expandQueryWithSynonyms(query)
        
        for (document in documentIndex.values) {
            var maxScore = 0.0
            var bestSnippet = ""
            
            // Check against expanded query terms
            for (term in expandedQuery) {
                val contentScore = findBestContentMatch(term, document.content)
                if (contentScore.score > maxScore) {
                    maxScore = contentScore.score
                    bestSnippet = contentScore.snippet
                }
            }
            
            if (maxScore >= 60) {
                results.add(
                    IntelligentSearchResult(
                        documentId = document.id,
                        title = document.title,
                        author = document.author,
                        snippet = bestSnippet,
                        score = maxScore,
                        searchType = "Semantic Match",
                        category = document.category
                    )
                )
            }
        }
        
        return results.sortedByDescending { it.score }.take(maxResults)
    }
    
    private fun performThematicSearch(query: String, maxResults: Int): List<IntelligentSearchResult> {
        val results = mutableListOf<IntelligentSearchResult>()
        val queryLower = query.toLowerCase()
        
        // Find matching themes
        val relevantThemes = bahaiThemes.entries.filter { (theme, synonyms) ->
            queryLower.contains(theme) || synonyms.any { queryLower.contains(it) }
        }
        
        if (relevantThemes.isEmpty()) return results
        
        // Search for documents containing thematic terms
        for (document in documentIndex.values) {
            val contentLower = document.content.toLowerCase()
            var totalScore = 0.0
            var themeMatches = 0
            
            for ((theme, synonyms) in relevantThemes) {
                var themeScore = 0
                if (contentLower.contains(theme)) themeScore += 5
                
                synonyms.forEach { synonym ->
                    if (contentLower.contains(synonym)) themeScore += 3
                }
                
                if (themeScore > 0) {
                    themeMatches++
                    totalScore += themeScore
                }
            }
            
            if (themeMatches > 0) {
                val averageScore = (totalScore / themeMatches) * 10 // Scale up
                results.add(
                    IntelligentSearchResult(
                        documentId = document.id,
                        title = document.title,
                        author = document.author,
                        snippet = extractThematicSnippet(document.content, relevantThemes.keys),
                        score = averageScore,
                        searchType = "Thematic Match",
                        category = document.category
                    )
                )
            }
        }
        
        return results.sortedByDescending { it.score }.take(maxResults)
    }
    
    private fun expandQueryWithSynonyms(query: String): Set<String> {
        val expandedTerms = mutableSetOf<String>()
        expandedTerms.add(query)
        
        val queryLower = query.toLowerCase()
        
        // Add synonyms
        synonymMap.entries.forEach { (key, synonyms) ->
            if (queryLower.contains(key)) {
                expandedTerms.addAll(synonyms)
            } else {
                synonyms.forEach { synonym ->
                    if (queryLower.contains(synonym.toLowerCase())) {
                        expandedTerms.add(key)
                        expandedTerms.addAll(synonyms)
                    }
                }
            }
        }
        
        return expandedTerms
    }
    
    private fun highlightSnippet(text: String, query: String, maxLength: Int = 200): String {
        val queryTerms = query.toLowerCase().split("\\s+".toRegex())
        val words = text.split("\\s+".toRegex())
        
        // Find the best position to start the snippet
        var bestStart = 0
        var maxMatches = 0
        
        for (i in words.indices) {
            val window = words.subList(i, minOf(i + 30, words.size))
            val matches = window.count { word ->
                queryTerms.any { term -> word.toLowerCase().contains(term) }
            }
            if (matches > maxMatches) {
                maxMatches = matches
                bestStart = i
            }
        }
        
        // Create snippet
        val snippetWords = words.subList(
            maxOf(0, bestStart - 3), 
            minOf(words.size, bestStart + 27)
        )
        
        var snippet = snippetWords.joinToString(" ")
        
        // Highlight matching terms (simplified highlighting)
        queryTerms.forEach { term ->
            snippet = snippet.replace(
                "(?i)\\b$term\\b".toRegex(),
                "**$term**"
            )
        }
        
        return if (snippet.length > maxLength) {
            snippet.take(maxLength - 3) + "..."
        } else {
            snippet
        }
    }
    
    private fun extractThematicSnippet(content: String, themes: Set<String>): String {
        val sentences = content.split("[.!?]+".toRegex())
        
        for (sentence in sentences) {
            val sentenceLower = sentence.toLowerCase()
            if (themes.any { theme -> sentenceLower.contains(theme) }) {
                return sentence.trim().take(200) + if (sentence.length > 200) "..." else ""
            }
        }
        
        return content.take(200) + if (content.length > 200) "..." else ""
    }
    
    fun cleanup() {
        if (::indexWriter.isInitialized) {
            indexWriter.close()
        }
        if (::indexReader.isInitialized) {
            indexReader.close()
        }
        directory.close()
    }
}

/**
 * Data classes for search functionality
 */
data class SearchableDocument(
    val id: String,
    val title: String,
    val author: String,
    val content: String,
    val category: String,
    val path: String
)

data class IntelligentSearchResult(
    val documentId: String,
    val title: String,
    val author: String,
    val snippet: String,
    val score: Double,
    val searchType: String,
    val category: String
)

data class SearchResults(
    val query: String,
    val results: List<IntelligentSearchResult>,
    val totalResults: Int,
    val searchType: SearchType
)

data class FuzzyContentResult(
    val score: Double,
    val snippet: String
)

enum class SearchType {
    EXACT,
    FUZZY,
    SEMANTIC,
    INTELLIGENT
}