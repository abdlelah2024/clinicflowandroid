package com.clinicflow.android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.clinicflow.android.R
import com.clinicflow.android.databinding.ItemTimeSlotBinding

class TimeSlotAdapter(
    private val onTimeSlotSelected: (String) -> Unit
) : RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder>() {
    
    private var timeSlots = mutableListOf<Pair<String, Boolean>>() // (time, isAvailable)
    private var selectedTimeSlot: String? = null
    
    fun updateTimeSlots(slots: List<Pair<String, Boolean>>) {
        timeSlots.clear()
        timeSlots.addAll(slots)
        selectedTimeSlot = null
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val binding = ItemTimeSlotBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TimeSlotViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        holder.bind(timeSlots[position])
    }
    
    override fun getItemCount(): Int = timeSlots.size
    
    inner class TimeSlotViewHolder(
        private val binding: ItemTimeSlotBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(timeSlot: Pair<String, Boolean>) {
            val time = timeSlot.first
            val isAvailable = timeSlot.second
            val isSelected = time == selectedTimeSlot
            
            binding.textViewTime.text = time
            
            // Set appearance based on availability and selection
            when {
                isSelected -> {
                    // Selected state
                    binding.root.setBackgroundResource(R.drawable.time_slot_selected_background)
                    binding.textViewTime.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.white)
                    )
                    binding.root.isEnabled = true
                }
                isAvailable -> {
                    // Available state
                    binding.root.setBackgroundResource(R.drawable.time_slot_available_background)
                    binding.textViewTime.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.primary_blue)
                    )
                    binding.root.isEnabled = true
                }
                else -> {
                    // Unavailable state
                    binding.root.setBackgroundResource(R.drawable.time_slot_unavailable_background)
                    binding.textViewTime.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.text_secondary)
                    )
                    binding.root.isEnabled = false
                }
            }
            
            // Set click listener only for available slots
            if (isAvailable) {
                binding.root.setOnClickListener {
                    val previousSelected = selectedTimeSlot
                    selectedTimeSlot = if (isSelected) null else time
                    
                    // Notify changes for previous and current selection
                    if (previousSelected != null) {
                        val previousIndex = timeSlots.indexOfFirst { it.first == previousSelected }
                        if (previousIndex != -1) {
                            notifyItemChanged(previousIndex)
                        }
                    }
                    
                    notifyItemChanged(adapterPosition)
                    
                    // Notify selection
                    if (selectedTimeSlot != null) {
                        onTimeSlotSelected(selectedTimeSlot!!)
                    }
                }
            } else {
                binding.root.setOnClickListener(null)
            }
        }
    }
}

