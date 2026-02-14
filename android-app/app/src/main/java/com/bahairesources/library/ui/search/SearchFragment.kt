package com.bahairesources.library.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.bahairesources.library.databinding.FragmentSearchBinding
import com.bahairesources.library.data.models.SearchResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Search fragment for full-text search of Bahai documents
 */
@AndroidEntryPoint
class SearchFragment : Fragment() {
    
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var searchAdapter: SearchResultsAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSearchView()
        observeViewModel()
        setupFilters()
    }
    
    private fun setupRecyclerView() {
        searchAdapter = SearchResultsAdapter { searchResult ->
            onSearchResultClicked(searchResult)
        }
        
        with(binding.recyclerViewResults) {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
            addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            )
        }
    }
    
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { 
                    if (it.isNotBlank()) {
                        performSearch(it)
                    }
                }
                binding.searchView.clearFocus()
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                // Auto-search as user types (with debounce in ViewModel)
                newText?.let { query ->
                    if (query.length >= 2) {
                        viewModel.searchDocuments(query)
                    } else {
                        viewModel.clearResults()
                    }
                }
                return true
            }
        })
        
        // Set search hint
        binding.searchView.queryHint = "Search Bahá'í texts..."
    }
    
    private fun setupFilters() {
        // Author filter
        binding.chipGroupAuthors.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedAuthors = checkedIds.mapNotNull { id ->
                group.findViewById<com.google.android.material.chip.Chip>(id)?.text?.toString()
            }
            viewModel.setAuthorFilter(selectedAuthors)
        }
        
        // Category filter  
        binding.chipGroupCategories.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedCategories = checkedIds.mapNotNull { id ->
                group.findViewById<com.google.android.material.chip.Chip>(id)?.text?.toString()
            }
            viewModel.setCategoryFilter(selectedCategories)
        }
        
        // Clear filters button
        binding.buttonClearFilters.setOnClickListener {
            binding.chipGroupAuthors.clearCheck()
            binding.chipGroupCategories.clearCheck()
            viewModel.clearFilters()
        }
    }
    
    private fun observeViewModel() {
        // Search results
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            searchAdapter.submitList(results)
            updateResultsVisibility(results)
        }
        
        // Loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        // Error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
        
        // Search statistics
        viewModel.searchStats.observe(viewLifecycleOwner) { stats ->
            stats?.let {
                binding.textViewResultCount.text = "${it.totalResults} results found"
                binding.textViewSearchTime.text = "Search completed in ${it.queryTime}ms"
            }
        }
        
        // Available filters
        viewModel.availableAuthors.observe(viewLifecycleOwner) { authors ->
            populateAuthorChips(authors)
        }
        
        viewModel.availableCategories.observe(viewLifecycleOwner) { categories ->
            populateCategoryChips(categories)
        }
    }
    
    private fun performSearch(query: String) {
        binding.searchView.clearFocus()
        viewModel.searchDocuments(query)
        
        // Hide keyboard
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) 
            as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchView.windowToken, 0)
    }
    
    private fun updateResultsVisibility(results: List<SearchResult>) {
        if (results.isEmpty()) {
            binding.recyclerViewResults.visibility = View.GONE
            binding.layoutNoResults.visibility = View.VISIBLE
            binding.textViewResultCount.visibility = View.GONE
        } else {
            binding.recyclerViewResults.visibility = View.VISIBLE
            binding.layoutNoResults.visibility = View.GONE
            binding.textViewResultCount.visibility = View.VISIBLE
        }
    }
    
    private fun populateAuthorChips(authors: List<String>) {
        binding.chipGroupAuthors.removeAllViews()
        authors.forEach { author ->
            val chip = com.google.android.material.chip.Chip(context)
            chip.text = author
            chip.isCheckable = true
            binding.chipGroupAuthors.addView(chip)
        }
    }
    
    private fun populateCategoryChips(categories: List<String>) {
        binding.chipGroupCategories.removeAllViews()
        categories.forEach { category ->
            val chip = com.google.android.material.chip.Chip(context)
            chip.text = category
            chip.isCheckable = true
            binding.chipGroupCategories.addView(chip)
        }
    }
    
    private fun onSearchResultClicked(searchResult: SearchResult) {
        // Navigate to document reader
        val action = SearchFragmentDirections.actionSearchToReader(
            searchResult.documentId,
            null
        )
        findNavController().navigate(action)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}