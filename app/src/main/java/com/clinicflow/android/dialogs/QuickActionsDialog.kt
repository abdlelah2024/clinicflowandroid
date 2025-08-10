package com.clinicflow.android.dialogs

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.clinicflow.android.R
import com.clinicflow.android.databinding.DialogQuickActionsBinding
import com.clinicflow.android.models.Patient

class QuickActionsDialog(
    private val context: Context,
    private val patient: Patient,
    private val onActionSelected: (Action) -> Unit
) {
    
    enum class Action {
        ADD_APPOINTMENT,
        VIEW_HISTORY,
        EDIT_PATIENT
    }
    
    private lateinit var binding: DialogQuickActionsBinding
    private lateinit var dialog: AlertDialog
    
    fun show() {
        binding = DialogQuickActionsBinding.inflate(LayoutInflater.from(context))
        
        setupUI()
        setupClickListeners()
        
        dialog = AlertDialog.Builder(context)
            .setTitle("إجراءات سريعة")
            .setView(binding.root)
            .setNegativeButton("إلغاء") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        
        dialog.show()
    }
    
    private fun setupUI() {
        // Display patient information
        binding.textViewPatientName.text = patient.getDisplayName()
        binding.textViewPatientPhone.text = "هاتف: ${patient.getFormattedPhone()}"
        
        if (patient.email.isNotEmpty()) {
            binding.textViewPatientEmail.text = "بريد: ${patient.email}"
            binding.textViewPatientEmail.visibility = android.view.View.VISIBLE
        } else {
            binding.textViewPatientEmail.visibility = android.view.View.GONE
        }
        
        patient.getAge()?.let { age ->
            binding.textViewPatientAge.text = "العمر: $age سنة"
            binding.textViewPatientAge.visibility = android.view.View.VISIBLE
        } ?: run {
            binding.textViewPatientAge.visibility = android.view.View.GONE
        }
        
        if (patient.gender.isNotEmpty()) {
            binding.textViewPatientGender.text = "الجنس: ${patient.gender}"
            binding.textViewPatientGender.visibility = android.view.View.VISIBLE
        } else {
            binding.textViewPatientGender.visibility = android.view.View.GONE
        }
    }
    
    private fun setupClickListeners() {
        binding.cardAddAppointment.setOnClickListener {
            onActionSelected(Action.ADD_APPOINTMENT)
            dialog.dismiss()
        }
        
        binding.cardViewHistory.setOnClickListener {
            onActionSelected(Action.VIEW_HISTORY)
            dialog.dismiss()
        }
        
        binding.cardEditPatient.setOnClickListener {
            onActionSelected(Action.EDIT_PATIENT)
            dialog.dismiss()
        }
    }
}

