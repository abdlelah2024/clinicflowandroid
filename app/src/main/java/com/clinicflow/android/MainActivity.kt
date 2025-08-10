package com.clinicflow.android

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.clinicflow.android.adapters.QuickSearchAdapter
import com.clinicflow.android.databinding.ActivityMainBinding
import com.clinicflow.android.models.Patient
import com.clinicflow.android.models.SearchResult
import com.clinicflow.android.services.FirebaseService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseService: FirebaseService
    private lateinit var quickSearchAdapter: QuickSearchAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        firebaseService = FirebaseService()
        
        // Check if user is logged in
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        setupUI()
        setupQuickSearch()
    }
    
    private fun setupUI() {
        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "ClinicFlow"
        
        // Setup logout button
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        
        // Setup navigation buttons
        binding.btnPatients.setOnClickListener {
            // Navigate to patients list
            Toast.makeText(this, "قائمة المرضى", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnAppointments.setOnClickListener {
            // Navigate to appointments list
            Toast.makeText(this, "قائمة المواعيد", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnUsers.setOnClickListener {
            // Navigate to users management
            Toast.makeText(this, "إدارة المستخدمين", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupQuickSearch() {
        // Setup RecyclerView for search results
        quickSearchAdapter = QuickSearchAdapter { searchResult ->
            handleSearchResultClick(searchResult)
        }
        
        binding.recyclerViewSearchResults.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = quickSearchAdapter
        }
        
        // Setup search functionality
        binding.editTextQuickSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.length >= 2) {
                    performQuickSearch(query)
                } else {
                    hideSearchResults()
                }
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
        
        // Setup quick add buttons
        binding.btnQuickAddPatient.setOnClickListener {
            val searchQuery = binding.editTextQuickSearch.text.toString().trim()
            showQuickAddPatientDialog(searchQuery)
        }
        
        binding.btnQuickAddAppointment.setOnClickListener {
            val searchQuery = binding.editTextQuickSearch.text.toString().trim()
            showQuickAddAppointmentDialog(searchQuery)
        }
    }
    
    private fun performQuickSearch(query: String) {
        binding.progressBarSearch.visibility = View.VISIBLE
        
        firebaseService.searchPatients(query) { results ->
            runOnUiThread {
                binding.progressBarSearch.visibility = View.GONE
                
                if (results.isNotEmpty()) {
                    showSearchResults(results)
                } else {
                    showNoResultsFound(query)
                }
            }
        }
    }
    
    private fun showSearchResults(results: List<SearchResult>) {
        binding.recyclerViewSearchResults.visibility = View.VISIBLE
        binding.layoutNoResults.visibility = View.GONE
        binding.layoutQuickActions.visibility = View.GONE
        
        quickSearchAdapter.updateResults(results)
    }
    
    private fun showNoResultsFound(query: String) {
        binding.recyclerViewSearchResults.visibility = View.GONE
        binding.layoutNoResults.visibility = View.VISIBLE
        binding.layoutQuickActions.visibility = View.VISIBLE
        
        binding.textViewNoResults.text = "لم يتم العثور على نتائج لـ: $query"
    }
    
    private fun hideSearchResults() {
        binding.recyclerViewSearchResults.visibility = View.GONE
        binding.layoutNoResults.visibility = View.GONE
        binding.layoutQuickActions.visibility = View.GONE
    }
    
    private fun handleSearchResultClick(searchResult: SearchResult) {
        when (searchResult.type) {
            SearchResult.Type.PATIENT -> {
                // Show patient details and quick appointment options
                showPatientQuickActions(searchResult.patient!!)
            }
            SearchResult.Type.APPOINTMENT -> {
                // Show appointment details
                showAppointmentDetails(searchResult.appointmentId!!)
            }
        }
    }
    
    private fun showPatientQuickActions(patient: Patient) {
        val dialog = QuickActionsDialog(this, patient) { action ->
            when (action) {
                QuickActionsDialog.Action.ADD_APPOINTMENT -> {
                    showQuickAddAppointmentDialog(patient = patient)
                }
                QuickActionsDialog.Action.VIEW_HISTORY -> {
                    // Navigate to patient history
                    Toast.makeText(this, "عرض تاريخ المريض: ${patient.name}", Toast.LENGTH_SHORT).show()
                }
                QuickActionsDialog.Action.EDIT_PATIENT -> {
                    // Navigate to edit patient
                    Toast.makeText(this, "تعديل بيانات المريض: ${patient.name}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }
    
    private fun showQuickAddPatientDialog(searchQuery: String = "") {
        val dialog = QuickAddPatientDialog(this, searchQuery) { patient ->
            // Patient added successfully, now add appointment
            showQuickAddAppointmentDialog(patient = patient, isNewPatient = true)
        }
        dialog.show()
    }
    
    private fun showQuickAddAppointmentDialog(searchQuery: String = "", patient: Patient? = null, isNewPatient: Boolean = false) {
        val dialog = QuickAddAppointmentDialog(this, patient, searchQuery) { appointment ->
            val message = if (isNewPatient) {
                "تم إضافة المريض والموعد بنجاح"
            } else {
                "تم إضافة الموعد بنجاح"
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            
            // Clear search
            binding.editTextQuickSearch.text.clear()
            hideSearchResults()
        }
        dialog.show()
    }
    
    private fun showAppointmentDetails(appointmentId: String) {
        // Show appointment details
        Toast.makeText(this, "عرض تفاصيل الموعد: $appointmentId", Toast.LENGTH_SHORT).show()
    }
}

