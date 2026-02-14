package com.bahairesources.library.ui.reader

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bahairesources.library.data.entities.Bookmark
import com.bahairesources.library.data.entities.Annotation
import com.bahairesources.library.data.entities.AnnotationType
import com.bahairesources.library.databinding.ItemBookmarkBinding
import com.bahairesources.library.databinding.ItemAnnotationBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying bookmarks in the PDF reader
 */
class BookmarkAdapter(
    private val onItemClick: (Bookmark) -> Unit
) : ListAdapter<Bookmark, BookmarkAdapter.BookmarkViewHolder>(BookmarkDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val binding = ItemBookmarkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookmarkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BookmarkViewHolder(
        private val binding: ItemBookmarkBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(bookmark: Bookmark) {
            binding.apply {
                bookmarkTitle.text = bookmark.title
                bookmarkDescription.text = bookmark.description ?: "Page ${bookmark.pageNumber}"
                pageNumber.text = "Page ${bookmark.pageNumber}"
                
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                bookmarkDate.text = dateFormat.format(bookmark.createdAt)
                
                root.setOnClickListener {
                    onItemClick(bookmark)
                }
            }
        }
    }

    private class BookmarkDiffCallback : DiffUtil.ItemCallback<Bookmark>() {
        override fun areItemsTheSame(oldItem: Bookmark, newItem: Bookmark): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Bookmark, newItem: Bookmark): Boolean {
            return oldItem == newItem
        }
    }
}

/**
 * Adapter for displaying annotations in the PDF reader
 */
class AnnotationAdapter(
    private val onItemClick: (Annotation) -> Unit,
    private val onEditClick: (Annotation) -> Unit,
    private val onDeleteClick: (Annotation) -> Unit
) : ListAdapter<Annotation, AnnotationAdapter.AnnotationViewHolder>(AnnotationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnotationViewHolder {
        val binding = ItemAnnotationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AnnotationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnnotationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AnnotationViewHolder(
        private val binding: ItemAnnotationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(annotation: Annotation) {
            binding.apply {
                // Set annotation type icon and color
                when (annotation.type) {
                    AnnotationType.HIGHLIGHT -> {
                        annotationIcon.setImageResource(android.R.drawable.ic_menu_edit)
                        annotationIcon.setColorFilter(Color.parseColor(annotation.color))
                        annotationTypeLabel.text = "Highlight"
                    }
                    AnnotationType.NOTE -> {
                        annotationIcon.setImageResource(android.R.drawable.ic_menu_info_details)
                        annotationIcon.setColorFilter(Color.BLUE)
                        annotationTypeLabel.text = "Note"
                    }
                    AnnotationType.UNDERLINE -> {
                        annotationIcon.setImageResource(android.R.drawable.ic_menu_edit)
                        annotationIcon.setColorFilter(Color.RED)
                        annotationTypeLabel.text = "Underline"
                    }
                    AnnotationType.STRIKETHROUGH -> {
                        annotationIcon.setImageResource(android.R.drawable.ic_menu_delete)
                        annotationIcon.setColorFilter(Color.GRAY)
                        annotationTypeLabel.text = "Strikethrough"
                    }
                    AnnotationType.BOOKMARK_HIGHLIGHT -> {
                        annotationIcon.setImageResource(android.R.drawable.btn_star_big_on)
                        annotationIcon.setColorFilter(Color.YELLOW)
                        annotationTypeLabel.text = "Bookmark"
                    }
                }
                
                // Set text content
                selectedText.text = if (annotation.selectedText.length > 100) {
                    annotation.selectedText.take(100) + "..."
                } else {
                    annotation.selectedText
                }
                
                // Show note if available
                if (!annotation.note.isNullOrBlank()) {
                    annotationNote.text = annotation.note
                    annotationNote.visibility = android.view.View.VISIBLE
                } else {
                    annotationNote.visibility = android.view.View.GONE
                }
                
                // Page number
                pageNumber.text = "Page ${annotation.pageNumber}"
                
                // Date
                val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                annotationDate.text = dateFormat.format(annotation.createdAt)
                
                // Click listeners
                root.setOnClickListener {
                    onItemClick(annotation)
                }
                
                editButton.setOnClickListener {
                    onEditClick(annotation)
                }
                
                deleteButton.setOnClickListener {
                    onDeleteClick(annotation)
                }
            }
        }
    }

    private class AnnotationDiffCallback : DiffUtil.ItemCallback<Annotation>() {
        override fun areItemsTheSame(oldItem: Annotation, newItem: Annotation): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Annotation, newItem: Annotation): Boolean {
            return oldItem == newItem
        }
    }
}