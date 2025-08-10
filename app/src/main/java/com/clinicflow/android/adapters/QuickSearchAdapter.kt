package com.clinicflow.android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clinicflow.android.R
import com.clinicflow.android.databinding.ItemSearchResultBinding
import com.clinicflow.android.models.SearchResult

class QuickSearchAdapter(
    private val onItemClick: (SearchResult) -> Unit
) : RecyclerView.Adapter<QuickSearchAdapter.SearchResultViewHolder>() {
    
    private var searchResults = mutableListOf<SearchResult>()
    
    fun updateResults(results: List<SearchResult>) {
        searchResults.clear()
        searchResults.addAll(results)
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding = ItemSearchResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchResultViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(searchResults[position])
    }
    
    override fun getItemCount(): Int = searchResults.size
    
    inner class SearchResultViewHolder(
        private val binding: ItemSearchResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(searchResult: SearchResult) {
            binding.textViewTitle.text = searchResult.title
            binding.textViewSubtitle.text = searchResult.subtitle
            
            // Set appropriate icon based on result type
            when (searchResult.type) {
                SearchResult.Type.PATIENT -> {
                    binding.imageViewIcon.setImageResource(R.drawable.ic_person)
                }
                SearchResult.Type.APPOINTMENT -> {
                    binding.imageViewIcon.setImageResource(R.drawable.ic_calendar)
                }
            }
            
            // Set click listener
            binding.root.setOnClickListener {
                onItemClick(searchResult)
            }
            
            // Add ripple effect for better UX
            binding.root.isClickable = true
            binding.root.isFocusable = true
        }
    }
}

