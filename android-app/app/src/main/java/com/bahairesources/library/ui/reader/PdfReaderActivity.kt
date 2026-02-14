package com.bahairesources.library.ui.reader

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bahairesources.library.R
import com.bahairesources.library.data.entities.Annotation
import com.bahairesources.library.data.entities.AnnotationType
import com.bahairesources.library.data.entities.Bookmark
import com.bahairesources.library.databinding.ActivityPdfReaderBinding
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnTapListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlinx.coroutines.*

/**
 * PDF Reader Activity with bookmarking and annotation capabilities
 */
@AndroidEntryPoint
class PdfReaderActivity : AppCompatActivity(), OnLoadCompleteListener, OnPageChangeListener, OnTapListener {
    
    private lateinit var binding: ActivityPdfReaderBinding
    private val viewModel: PdfReaderViewModel by viewModels()
    
    private var documentId: Long = -1
    private var pdfPath: String? = null
    private var currentPage: Int = 0
    private var totalPages: Int = 0
    private var isBookmarkMenuOpen = false
    private var isAnnotationMenuOpen = false
    
    // Adapters for bookmarks and annotations
    private lateinit var bookmarkAdapter: BookmarkAdapter
    private lateinit var annotationAdapter: AnnotationAdapter
    
    companion object {
        private const val EXTRA_DOCUMENT_ID = "document_id"
        private const val EXTRA_PDF_PATH = "pdf_path"
        private const val EXTRA_DOCUMENT_TITLE = "document_title"
        
        fun createIntent(context: Context, documentId: Long, pdfPath: String, title: String): Intent {
            return Intent(context, PdfReaderActivity::class.java).apply {
                putExtra(EXTRA_DOCUMENT_ID, documentId)
                putExtra(EXTRA_PDF_PATH, pdfPath)
                putExtra(EXTRA_DOCUMENT_TITLE, title)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Extract intent data
        documentId = intent.getLongExtra(EXTRA_DOCUMENT_ID, -1)
        pdfPath = intent.getStringExtra(EXTRA_PDF_PATH)
        val documentTitle = intent.getStringExtra(EXTRA_DOCUMENT_TITLE) ?: "Document"
        
        if (documentId == -1L || pdfPath == null) {
            Toast.makeText(this, "Error loading document", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        setupToolbar(documentTitle)
        setupPdfViewer()
        setupSideMenus()
        setupObservers()
        
        // Initialize the document in ViewModel
        viewModel.initializeDocument(documentId, pdfPath!!)
    }
    
    private fun setupToolbar(title: String) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setTitle(title)
        }
    }
    
    private fun setupPdfViewer() {
        binding.pdfView.apply {
            fromFile(File(pdfPath!!))
                .defaultPage(0)
                .onPageChange(this@PdfReaderActivity)
                .onLoad(this@PdfReaderActivity)
                .onTap(this@PdfReaderActivity)
                .scrollHandle(DefaultScrollHandle(this@PdfReaderActivity))
                .spacing(10) // Space between pages
                .pageFitPolicy(FitPolicy.WIDTH)
                .load()
        }
    }
    
    private fun setupSideMenus() {
        // Setup bookmark RecyclerView
        bookmarkAdapter = BookmarkAdapter { bookmark ->
            // Navigate to bookmarked page
            binding.pdfView.jumpTo(bookmark.pageNumber)
            toggleBookmarkMenu()
        }
        
        binding.bookmarkRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@PdfReaderActivity)
            adapter = bookmarkAdapter
        }
        
        // Setup annotation RecyclerView
        annotationAdapter = AnnotationAdapter(
            onItemClick = { annotation ->
                // Navigate to annotation page
                binding.pdfView.jumpTo(annotation.pageNumber)
                toggleAnnotationMenu()
            },
            onEditClick = { annotation ->
                showEditAnnotationDialog(annotation)
            },
            onDeleteClick = { annotation ->
                viewModel.deleteAnnotation(annotation)
            }
        )
        
        binding.annotationRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@PdfReaderActivity)
            adapter = annotationAdapter
        }
        
        // Setup menu click listeners
        binding.bookmarkMenuToggle.setOnClickListener { toggleBookmarkMenu() }
        binding.annotationMenuToggle.setOnClickListener { toggleAnnotationMenu() }
        binding.bookmarkOverlay.setOnClickListener { toggleBookmarkMenu() }
        binding.annotationOverlay.setOnClickListener { toggleAnnotationMenu() }
    }
    
    private fun setupObservers() {
        // Observe bookmarks
        viewModel.bookmarks.observe(this, Observer { bookmarks ->
            bookmarkAdapter.submitList(bookmarks)
            updateBookmarkCount(bookmarks.size)
        })
        
        // Observe annotations
        viewModel.annotations.observe(this, Observer { annotations ->
            annotationAdapter.submitList(annotations)
            updateAnnotationCount(annotations.size)
        })
        
        // Observe reading progress
        viewModel.readingProgress.observe(this, Observer { progress ->
            progress?.let {
                // Update progress indicator if needed
                val progressPercentage = it.progressPercentage
                binding.progressIndicator.progress = progressPercentage.toInt()
            }
        })
    }
    
    private fun updateBookmarkCount(count: Int) {
        binding.bookmarkCount.text = count.toString()
        binding.bookmarkCount.visibility = if (count > 0) View.VISIBLE else View.GONE
    }
    
    private fun updateAnnotationCount(count: Int) {
        binding.annotationCount.text = count.toString()
        binding.annotationCount.visibility = if (count > 0) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.pdf_reader_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_add_bookmark -> {
                addBookmarkForCurrentPage()
                true
            }
            R.id.action_search -> {
                showSearchDialog()
                true
            }
            R.id.action_settings -> {
                showReaderSettings()
                true
            }
            R.id.action_share -> {
                shareCurrentPage()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    // PDF Viewer Callbacks
    override fun loadComplete(nbPages: Int) {
        totalPages = nbPages
        binding.pageIndicator.text = "Page 1 of $nbPages"
        
        // Update reading progress
        viewModel.updateReadingProgress(documentId, 1, totalPages)
        
        // Load last read page if available
        viewModel.readingProgress.value?.let { progress ->
            if (progress.lastReadPage > 1) {
                binding.pdfView.jumpTo(progress.lastReadPage - 1) // 0-based indexing
            }
        }
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        currentPage = page
        binding.pageIndicator.text = "Page ${page + 1} of $pageCount"
        
        // Update reading progress
        val progressPercentage = ((page + 1).toFloat() / pageCount) * 100
        viewModel.updateReadingProgress(documentId, page + 1, pageCount, progressPercentage)
    }

    override fun onTap(e: MotionEvent?): Boolean {
        // Handle text selection for annotations
        // This would be implemented with a custom selection handler
        return false
    }
    
    private fun toggleBookmarkMenu() {
        if (isBookmarkMenuOpen) {
            binding.bookmarkMenu.animate().translationX(-binding.bookmarkMenu.width.toFloat()).setDuration(300).start()
            binding.bookmarkOverlay.visibility = View.GONE
        } else {
            binding.bookmarkMenu.animate().translationX(0f).setDuration(300).start()
            binding.bookmarkOverlay.visibility = View.VISIBLE
        }
        isBookmarkMenuOpen = !isBookmarkMenuOpen
    }
    
    private fun toggleAnnotationMenu() {
        if (isAnnotationMenuOpen) {
            binding.annotationMenu.animate().translationX(binding.annotationMenu.width.toFloat()).setDuration(300).start()
            binding.annotationOverlay.visibility = View.GONE
        } else {
            binding.annotationMenu.animate().translationX(0f).setDuration(300).start()
            binding.annotationOverlay.visibility = View.VISIBLE
        }
        isAnnotationMenuOpen = !isAnnotationMenuOpen
    }
    
    private fun addBookmarkForCurrentPage() {
        val pageNumber = currentPage + 1 // Convert to 1-based
        val title = "Page $pageNumber"
        
        val bookmark = Bookmark(
            documentId = documentId,
            pageNumber = pageNumber,
            title = title,
            description = "Bookmarked from reader"
        )
        
        viewModel.addBookmark(bookmark)
        Toast.makeText(this, "Bookmark added", Toast.LENGTH_SHORT).show()
    }
    
    private fun showSearchDialog() {
        // TODO: Implement in-document search
        Toast.makeText(this, "Search feature coming soon", Toast.LENGTH_SHORT).show()
    }
    
    private fun showReaderSettings() {
        // TODO: Show reader settings dialog
        Toast.makeText(this, "Reader settings coming soon", Toast.LENGTH_SHORT).show()
    }
    
    private fun shareCurrentPage() {
        // TODO: Implement page sharing
        Toast.makeText(this, "Share feature coming soon", Toast.LENGTH_SHORT).show()
    }
    
    private fun showEditAnnotationDialog(annotation: Annotation) {
        // TODO: Show edit annotation dialog
        Toast.makeText(this, "Edit annotation coming soon", Toast.LENGTH_SHORT).show()
    }
    
    override fun onPause() {
        super.onPause()
        // Save current reading position
        viewModel.updateReadingProgress(documentId, currentPage + 1, totalPages)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Update total reading time
        viewModel.saveReadingSession(documentId)
    }
}