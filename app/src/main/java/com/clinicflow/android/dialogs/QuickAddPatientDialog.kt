package com.clinicflow.android.dialogs

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.clinicflow.android.R
import com.clinicflow.android.databinding.DialogQuickAddPatientBinding
import com.clinicflow.android.models.Patient
import com.clinicflow.android.services.FirebaseService
import java.text.SimpleDateFormat
import java.util.*

class QuickAddPatientDialog(
    private val context: Context,
    private val initialSearchQuery: String = "",
    private val onPatientAdded: (Patient) -> Unit
) {
    
    private lateinit var binding: DialogQuickAddPatientBinding
    private lateinit var dialog: AlertDialog
    private val firebaseService = FirebaseService()
    private var selectedDate: Date? = null
    
    fun show() {
        binding = DialogQuickAddPatientBinding.inflate(LayoutInflater.from(context))
        
        setupUI()
        setupGenderSpinner()
        setupDatePicker()
        prefillFromSearchQuery()
        
        dialog = AlertDialog.Builder(context)
            .setTitle("إضافة مريض جديد")
            .setView(binding.root)
            .setPositiveButton("حفظ", null) // We'll set this manually to prevent auto-dismiss
            .setNegativeButton("إلغاء") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                savePatient()
            }
        }
        
        dialog.show()
    }
    
    private fun setupUI() {
        // Focus on name field
        binding.editTextName.requestFocus()
        
        // Setup phone number formatting
        binding.editTextPhone.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                formatPhoneNumber()
            }
        }
    }
    
    private fun setupGenderSpinner() {
        val genderOptions = arrayOf("اختر الجنس", "ذكر", "أنثى")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = adapter
    }
    
    private fun setupDatePicker() {
        binding.editTextDateOfBirth.setOnClickListener {
            showDatePicker()
        }
        
        binding.editTextDateOfBirth.isFocusable = false
        binding.editTextDateOfBirth.isClickable = true
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        if (selectedDate != null) {
            calendar.time = selectedDate!!
        } else {
            // Default to 30 years ago
            calendar.add(Calendar.YEAR, -30)
        }
        
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                selectedDate = selectedCalendar.time
                
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("ar"))
                binding.editTextDateOfBirth.setText(dateFormat.format(selectedDate!!))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        // Set max date to today
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        
        datePickerDialog.show()
    }
    
    private fun prefillFromSearchQuery() {
        if (initialSearchQuery.isNotEmpty()) {
            // Try to determine if it's a phone number or name
            if (initialSearchQuery.matches(Regex("^[0-9+\\-\\s]+$"))) {
                // Looks like a phone number
                binding.editTextPhone.setText(initialSearchQuery)
                binding.editTextName.requestFocus()
            } else {
                // Assume it's a name
                binding.editTextName.setText(initialSearchQuery)
                binding.editTextPhone.requestFocus()
            }
        }
    }
    
    private fun formatPhoneNumber() {
        val phone = binding.editTextPhone.text.toString().trim()
        if (phone.isNotEmpty()) {
            val formattedPhone = when {
                phone.startsWith("05") && phone.length == 10 -> phone
                phone.startsWith("5") && phone.length == 9 -> "0$phone"
                phone.startsWith("966") -> phone.replaceFirst("966", "0")
                phone.startsWith("+966") -> phone.replaceFirst("+966", "0")
                else -> phone
            }
            binding.editTextPhone.setText(formattedPhone)
        }
    }
    
    private fun validateInput(): Boolean {
        var isValid = true
        
        // Validate name
        if (binding.editTextName.text.toString().trim().isEmpty()) {
            binding.textInputLayoutName.error = "الاسم مطلوب"
            isValid = false
        } else {
            binding.textInputLayoutName.error = null
        }
        
        // Validate phone
        val phone = binding.editTextPhone.text.toString().trim()
        if (phone.isEmpty()) {
            binding.textInputLayoutPhone.error = "رقم الهاتف مطلوب"
            isValid = false
        } else if (!phone.matches(Regex("^05[0-9]{8}$"))) {
            binding.textInputLayoutPhone.error = "رقم الهاتف غير صحيح (يجب أن يبدأ بـ 05 ويتكون من 10 أرقام)"
            isValid = false
        } else {
            binding.textInputLayoutPhone.error = null
        }
        
        // Validate gender
        if (binding.spinnerGender.selectedItemPosition == 0) {
            Toast.makeText(context, "يرجى اختيار الجنس", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        
        return isValid
    }
    
    private fun savePatient() {
        if (!validateInput()) {
            return
        }
        
        // Show loading
        binding.progressBar.visibility = android.view.View.VISIBLE
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = false
        
        val patient = Patient(
            id = "", // Will be generated by Firestore
            name = binding.editTextName.text.toString().trim(),
            phone = binding.editTextPhone.text.toString().trim(),
            email = binding.editTextEmail.text.toString().trim(),
            dateOfBirth = selectedDate,
            gender = when (binding.spinnerGender.selectedItemPosition) {
                1 -> "ذكر"
                2 -> "أنثى"
                else -> ""
            },
            address = binding.editTextAddress.text.toString().trim(),
            emergencyContact = binding.editTextEmergencyContact.text.toString().trim(),
            emergencyContactPhone = binding.editTextEmergencyPhone.text.toString().trim(),
            createdAt = Date(),
            updatedAt = Date()
        )
        
        firebaseService.addPatient(patient) { success, patientId, error ->
            context.runOnUiThread {
                binding.progressBar.visibility = android.view.View.GONE
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = true
                
                if (success && patientId != null) {
                    Toast.makeText(context, "تم إضافة المريض بنجاح", Toast.LENGTH_SHORT).show()
                    val savedPatient = patient.copy(id = patientId)
                    onPatientAdded(savedPatient)
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "خطأ في إضافة المريض: ${error ?: "خطأ غير معروف"}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun Context.runOnUiThread(action: () -> Unit) {
        if (this is android.app.Activity) {
            runOnUiThread(action)
        } else {
            action()
        }
    }
}

