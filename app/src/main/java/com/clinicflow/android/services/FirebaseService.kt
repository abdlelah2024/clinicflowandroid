package com.clinicflow.android.services

import android.util.Log
import com.clinicflow.android.models.Appointment
import com.clinicflow.android.models.Patient
import com.clinicflow.android.models.SearchResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import java.util.*

class FirebaseService {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    companion object {
        private const val TAG = "FirebaseService"
        private const val COLLECTION_PATIENTS = "patients"
        private const val COLLECTION_APPOINTMENTS = "appointments"
        private const val COLLECTION_USERS = "users"
    }
    
    init {
        // Enable offline persistence
        try {
            firestore.enableNetwork()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to enable network", e)
        }
    }
    
    // Authentication Methods
    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    callback(true, null)
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    callback(false, task.exception?.message)
                }
            }
    }
    
    fun signOut() {
        auth.signOut()
    }
    
    fun getCurrentUser() = auth.currentUser
    
    // Patient Methods
    fun addPatient(patient: Patient, callback: (Boolean, String?, String?) -> Unit) {
        val patientData = patient.toMap()
        
        firestore.collection(COLLECTION_PATIENTS)
            .add(patientData)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Patient added with ID: ${documentReference.id}")
                callback(true, documentReference.id, null)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding patient", e)
                callback(false, null, e.message)
            }
    }
    
    fun updatePatient(patient: Patient, callback: (Boolean, String?) -> Unit) {
        if (patient.id.isEmpty()) {
            callback(false, "Patient ID is required for update")
            return
        }
        
        val patientData = patient.toMap().toMutableMap()
        patientData["updatedAt"] = Date()
        
        firestore.collection(COLLECTION_PATIENTS)
            .document(patient.id)
            .set(patientData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "Patient updated successfully")
                callback(true, null)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating patient", e)
                callback(false, e.message)
            }
    }
    
    fun getPatient(patientId: String, callback: (Patient?, String?) -> Unit) {
        firestore.collection(COLLECTION_PATIENTS)
            .document(patientId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    try {
                        val patient = Patient.fromMap(document.data!!, document.id)
                        callback(patient, null)
                    } catch (e: Exception) {
                        Log.w(TAG, "Error parsing patient data", e)
                        callback(null, "Error parsing patient data: ${e.message}")
                    }
                } else {
                    callback(null, "Patient not found")
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting patient", e)
                callback(null, e.message)
            }
    }
    
    fun searchPatients(query: String, callback: (List<SearchResult>) -> Unit) {
        val results = mutableListOf<SearchResult>()
        var completedQueries = 0
        val totalQueries = 2 // Search by name and phone
        
        fun checkCompletion() {
            completedQueries++
            if (completedQueries >= totalQueries) {
                // Remove duplicates and sort by relevance
                val uniqueResults = results.distinctBy { 
                    when (it.type) {
                        SearchResult.Type.PATIENT -> it.patient?.id
                        SearchResult.Type.APPOINTMENT -> it.appointmentId
                    }
                }
                callback(uniqueResults)
            }
        }
        
        // Search by name
        firestore.collection(COLLECTION_PATIENTS)
            .whereGreaterThanOrEqualTo("name", query)
            .whereLessThanOrEqualTo("name", query + "\uf8ff")
            .whereEqualTo("isActive", true)
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    try {
                        val patient = Patient.fromMap(document.data, document.id)
                        results.add(SearchResult.fromPatient(patient))
                    } catch (e: Exception) {
                        Log.w(TAG, "Error parsing patient from search", e)
                    }
                }
                checkCompletion()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error searching patients by name", e)
                checkCompletion()
            }
        
        // Search by phone
        firestore.collection(COLLECTION_PATIENTS)
            .whereGreaterThanOrEqualTo("phone", query)
            .whereLessThanOrEqualTo("phone", query + "\uf8ff")
            .whereEqualTo("isActive", true)
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    try {
                        val patient = Patient.fromMap(document.data, document.id)
                        results.add(SearchResult.fromPatient(patient))
                    } catch (e: Exception) {
                        Log.w(TAG, "Error parsing patient from phone search", e)
                    }
                }
                checkCompletion()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error searching patients by phone", e)
                checkCompletion()
            }
    }
    
    // Appointment Methods
    fun addAppointment(appointment: Appointment, callback: (Boolean, String?, String?) -> Unit) {
        val appointmentData = appointment.toMap()
        
        firestore.collection(COLLECTION_APPOINTMENTS)
            .add(appointmentData)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Appointment added with ID: ${documentReference.id}")
                callback(true, documentReference.id, null)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding appointment", e)
                callback(false, null, e.message)
            }
    }
    
    fun updateAppointment(appointment: Appointment, callback: (Boolean, String?) -> Unit) {
        if (appointment.id.isEmpty()) {
            callback(false, "Appointment ID is required for update")
            return
        }
        
        val appointmentData = appointment.toMap().toMutableMap()
        appointmentData["updatedAt"] = Date()
        
        firestore.collection(COLLECTION_APPOINTMENTS)
            .document(appointment.id)
            .set(appointmentData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "Appointment updated successfully")
                callback(true, null)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating appointment", e)
                callback(false, e.message)
            }
    }
    
    fun getAppointmentsForPatient(
        patientId: String,
        callback: (List<Appointment>, String?) -> Unit
    ) {
        firestore.collection(COLLECTION_APPOINTMENTS)
            .whereEqualTo("patientId", patientId)
            .whereEqualTo("isActive", true)
            .orderBy("appointmentDate", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val appointments = mutableListOf<Appointment>()
                for (document in documents) {
                    try {
                        val appointment = Appointment.fromMap(document.data, document.id)
                        appointments.add(appointment)
                    } catch (e: Exception) {
                        Log.w(TAG, "Error parsing appointment", e)
                    }
                }
                callback(appointments, null)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting appointments for patient", e)
                callback(emptyList(), e.message)
            }
    }
    
    fun getTodayAppointments(callback: (List<Appointment>, String?) -> Unit) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time
        
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val startOfNextDay = calendar.time
        
        firestore.collection(COLLECTION_APPOINTMENTS)
            .whereGreaterThanOrEqualTo("appointmentDate", startOfDay)
            .whereLessThan("appointmentDate", startOfNextDay)
            .whereEqualTo("isActive", true)
            .orderBy("appointmentDate")
            .orderBy("appointmentTime")
            .get()
            .addOnSuccessListener { documents ->
                val appointments = mutableListOf<Appointment>()
                for (document in documents) {
                    try {
                        val appointment = Appointment.fromMap(document.data, document.id)
                        appointments.add(appointment)
                    } catch (e: Exception) {
                        Log.w(TAG, "Error parsing today's appointment", e)
                    }
                }
                callback(appointments, null)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting today's appointments", e)
                callback(emptyList(), e.message)
            }
    }
    
    fun checkTimeSlotAvailability(
        date: Date,
        time: String,
        excludeAppointmentId: String? = null,
        callback: (Boolean, String?) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time
        
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val startOfNextDay = calendar.time
        
        var query = firestore.collection(COLLECTION_APPOINTMENTS)
            .whereGreaterThanOrEqualTo("appointmentDate", startOfDay)
            .whereLessThan("appointmentDate", startOfNextDay)
            .whereEqualTo("appointmentTime", time)
            .whereEqualTo("isActive", true)
        
        query.get()
            .addOnSuccessListener { documents ->
                val conflictingAppointments = documents.filter { document ->
                    excludeAppointmentId == null || document.id != excludeAppointmentId
                }
                
                val isAvailable = conflictingAppointments.isEmpty()
                callback(isAvailable, null)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error checking time slot availability", e)
                callback(false, e.message)
            }
    }
    
    // Utility Methods
    fun enableOfflineSupport() {
        try {
            firestore.enableNetwork()
        } catch (e: Exception) {
            Log.w(TAG, "Offline support already enabled or failed to enable", e)
        }
    }
    
    fun disableOfflineSupport() {
        try {
            firestore.disableNetwork()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to disable network", e)
        }
    }
}

