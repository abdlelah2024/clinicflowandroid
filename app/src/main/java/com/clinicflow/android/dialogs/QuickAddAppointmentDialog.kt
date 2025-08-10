package com.clinicflow.android.dialogs

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.clinicflow.android.R
import com.clinicflow.android.adapters.TimeSlotAdapter
import com.clinicflow.android.databinding.DialogQuickAddAppointmentBinding
import com.clinicflow.android.models.Appointment
import com.clinicflow.android.models.Patient
import com.clinicflow.android.services.FirebaseService
import java.text.SimpleDateFormat
import java.util.*

class QuickAddAppointmentDialog(
    private val context: Context,
    private val patient: Patient? = null,
    private val initialSearchQuery: String = "",
    private val onAppointmentAdded: (Appointment) -> Unit
) {
    
    private lateinit var binding: DialogQuickAddAppointmentBinding
    private lateinit var dialog: AlertDialog
    private val firebaseService = FirebaseService()
    private var selectedDate: Date = Date()
    private var selectedTime: String = ""
    private lateinit var timeSlotAdapter: TimeSlotAdapter
    
    fun show() {
        binding = DialogQuickAddAppointmentBinding.inflate(LayoutInflater.from(context))
        
        setupUI()
        setupPatientInfo()
        setupDatePicker()
        setupTimeSlots()
        setupAppointmentTypeSpinner()
        
        dialog = AlertDialog.Builder(context)
            .setTitle("إضافة موعد جديد")
            .setView(binding.root)
            .setPositiveButton("حفظ", null)
            .setNegativeButton("إلغاء") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                saveAppointment()
            }
        }
        
        dialog.show()
    }
    
    private fun setupUI() {
        // Set default date to today
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("ar"))
        binding.editTextDate.setText(dateFormat.format(selectedDate))
        
        // Focus on appropriate field
        if (patient == null) {
            binding.editTextPatientName.requestFocus()
        } else {
            // Patient is already selected, focus on date or notes
            binding.editTextNotes.requestFocus()
        }
    }
    
    private fun setupPatientInfo() {
        if (patient != null) {
            // Pre-fill patient information
            binding.editTextPatientName.setText(patient.name)
            binding.editTextPatientPhone.setText(patient.phone)
            
            // Disable editing if patient is already selected
            binding.editTextPatientName.isEnabled = false
            binding.editTextPatientPhone.isEnabled = false
            
            binding.textViewPatientInfo.text = "المريض: ${patient.name}"
            binding.textViewPatientInfo.visibility = android.view.View.VISIBLE
        } else if (initialSearchQuery.isNotEmpty()) {
            // Try to determine if it's a phone number or name
            if (initialSearchQuery.matches(Regex("^[0-9+\\-\\s]+$"))) {
                binding.editTextPatientPhone.setText(initialSearchQuery)
                binding.editTextPatientName.requestFocus()
            } else {
                binding.editTextPatientName.setText(initialSearchQuery)
                binding.editTextPatientPhone.requestFocus()
            }
        }
    }
    
    private fun setupDatePicker() {
        binding.editTextDate.setOnClickListener {
            showDatePicker()
        }
        
        binding.editTextDate.isFocusable = false
        binding.editTextDate.isClickable = true
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.time = selectedDate
        
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                selectedDate = selectedCalendar.time
                
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("ar"))
                binding.editTextDate.setText(dateFormat.format(selectedDate))
                
                // Refresh time slots for the new date
                refreshTimeSlots()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        // Set min date to today
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        
        datePickerDialog.show()
    }
    
    private fun setupTimeSlots() {
        timeSlotAdapter = TimeSlotAdapter { timeSlot ->
            selectedTime = timeSlot
            binding.textViewSelectedTime.text = "الوقت المحدد: $timeSlot"
            binding.textViewSelectedTime.visibility = android.view.View.VISIBLE
        }
        
        binding.recyclerViewTimeSlots.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = timeSlotAdapter
        }
        
        refreshTimeSlots()
    }
    
    private fun refreshTimeSlots() {
        val availableSlots = Appointment.getAvailableTimeSlots()
        
        // Check availability for each slot
        val slotsWithAvailability = mutableListOf<Pair<String, Boolean>>()
        var checkedSlots = 0
        
        for (slot in availableSlots) {
            firebaseService.checkTimeSlotAvailability(selectedDate, slot) { isAvailable, _ ->
                slotsWithAvailability.add(Pair(slot, isAvailable))
                checkedSlots++
                
                if (checkedSlots == availableSlots.size) {
                    // All slots checked, update adapter
                    context.runOnUiThread {
                        timeSlotAdapter.updateTimeSlots(slotsWithAvailability.sortedBy { it.first })
                    }
                }
            }
        }
    }
    
    private fun setupAppointmentTypeSpinner() {
        val appointmentTypes = Appointment.AppointmentType.values().map { it.displayName }
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, appointmentTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAppointmentType.adapter = adapter
    }
    
    private fun validateInput(): Boolean {
        var isValid = true
        
        // Validate patient name
        if (binding.editTextPatientName.text.toString().trim().isEmpty()) {
            binding.textInputLayoutPatientName.error = "اسم المريض مطلوب"
            isValid = false
        } else {
            binding.textInputLayoutPatientName.error = null
        }
        
        // Validate patient phone
        val phone = binding.editTextPatientPhone.text.toString().trim()
        if (phone.isEmpty()) {
            binding.textInputLayoutPatientPhone.error = "رقم الهاتف مطلوب"
            isValid = false
        } else if (!phone.matches(Regex("^05[0-9]{8}$"))) {
            binding.textInputLayoutPatientPhone.error = "رقم الهاتف غير صحيح"
            isValid = false
        } else {
            binding.textInputLayoutPatientPhone.error = null
        }
        
        // Validate time selection
        if (selectedTime.isEmpty()) {
            Toast.makeText(context, "يرجى اختيار وقت الموعد", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        
        return isValid
    }
    
    private fun saveAppointment() {
        if (!validateInput()) {
            return
        }
        
        // Show loading
        binding.progressBar.visibility = android.view.View.VISIBLE
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = false
        
        val appointmentType = Appointment.AppointmentType.values()[binding.spinnerAppointmentType.selectedItemPosition]
        
        val appointment = Appointment(
            id = "", // Will be generated by Firestore
            patientId = patient?.id ?: "", // Will be updated if patient is created
            patientName = binding.editTextPatientName.text.toString().trim(),
            patientPhone = binding.editTextPatientPhone.text.toString().trim(),
            doctorId = firebaseService.getCurrentUser()?.uid ?: "",
            doctorName = firebaseService.getCurrentUser()?.displayName ?: "طبيب",
            appointmentDate = selectedDate,
            appointmentTime = selectedTime,
            duration = 30, // Default duration
            type = appointmentType,
            status = Appointment.AppointmentStatus.SCHEDULED,
            notes = binding.editTextNotes.text.toString().trim(),
            createdAt = Date(),
            updatedAt = Date()
        )
        
        firebaseService.addAppointment(appointment) { success, appointmentId, error ->
            context.runOnUiThread {
                binding.progressBar.visibility = android.view.View.GONE
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = true
                
                if (success && appointmentId != null) {
                    Toast.makeText(context, "تم إضافة الموعد بنجاح", Toast.LENGTH_SHORT).show()
                    val savedAppointment = appointment.copy(id = appointmentId)
                    onAppointmentAdded(savedAppointment)
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "خطأ في إضافة الموعد: ${error ?: "خطأ غير معروف"}", Toast.LENGTH_LONG).show()
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

