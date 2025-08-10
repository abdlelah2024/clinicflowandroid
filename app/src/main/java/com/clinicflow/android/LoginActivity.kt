package com.clinicflow.android

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.clinicflow.android.databinding.ActivityLoginBinding
import com.clinicflow.android.services.FirebaseService
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseService: FirebaseService
    private lateinit var auth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize Firebase
        firebaseService = FirebaseService()
        auth = FirebaseAuth.getInstance()
        
        // Check if user is already logged in
        if (auth.currentUser != null) {
            navigateToMain()
            return
        }
        
        setupUI()
    }
    
    private fun setupUI() {
        // Set up login button
        binding.btnLogin.setOnClickListener {
            performLogin()
        }
        
        // Set up demo login button
        binding.btnDemoLogin.setOnClickListener {
            performDemoLogin()
        }
        
        // Set up forgot password
        binding.textViewForgotPassword.setOnClickListener {
            // TODO: Implement forgot password functionality
            Toast.makeText(this, "ميزة استعادة كلمة المرور قيد التطوير", Toast.LENGTH_SHORT).show()
        }
        
        // Set up create account
        binding.textViewCreateAccount.setOnClickListener {
            // TODO: Implement create account functionality
            Toast.makeText(this, "ميزة إنشاء حساب جديد قيد التطوير", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun performLogin() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()
        
        if (!validateInput(email, password)) {
            return
        }
        
        showLoading(true)
        
        firebaseService.signInWithEmailAndPassword(email, password) { success, error ->
            runOnUiThread {
                showLoading(false)
                
                if (success) {
                    Toast.makeText(this, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    val errorMessage = when {
                        error?.contains("user-not-found") == true -> "المستخدم غير موجود"
                        error?.contains("wrong-password") == true -> "كلمة المرور غير صحيحة"
                        error?.contains("invalid-email") == true -> "البريد الإلكتروني غير صحيح"
                        error?.contains("user-disabled") == true -> "تم تعطيل هذا الحساب"
                        error?.contains("too-many-requests") == true -> "تم تجاوز عدد المحاولات المسموح"
                        else -> "خطأ في تسجيل الدخول: ${error ?: "خطأ غير معروف"}"
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun performDemoLogin() {
        // Demo credentials for testing
        val demoEmail = "demo@clinicflow.com"
        val demoPassword = "demo123456"
        
        showLoading(true)
        
        firebaseService.signInWithEmailAndPassword(demoEmail, demoPassword) { success, error ->
            runOnUiThread {
                showLoading(false)
                
                if (success) {
                    Toast.makeText(this, "تم تسجيل الدخول بالحساب التجريبي", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    Toast.makeText(this, "الحساب التجريبي غير متوفر حالياً", Toast.LENGTH_LONG).show()
                    
                    // Fallback: Fill demo credentials for manual login
                    binding.editTextEmail.setText(demoEmail)
                    binding.editTextPassword.setText(demoPassword)
                    Toast.makeText(this, "تم تعبئة بيانات الحساب التجريبي", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true
        
        // Validate email
        if (email.isEmpty()) {
            binding.textInputLayoutEmail.error = "البريد الإلكتروني مطلوب"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textInputLayoutEmail.error = "البريد الإلكتروني غير صحيح"
            isValid = false
        } else {
            binding.textInputLayoutEmail.error = null
        }
        
        // Validate password
        if (password.isEmpty()) {
            binding.textInputLayoutPassword.error = "كلمة المرور مطلوبة"
            isValid = false
        } else if (password.length < 6) {
            binding.textInputLayoutPassword.error = "كلمة المرور يجب أن تكون 6 أحرف على الأقل"
            isValid = false
        } else {
            binding.textInputLayoutPassword.error = null
        }
        
        return isValid
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !show
        binding.btnDemoLogin.isEnabled = !show
        binding.editTextEmail.isEnabled = !show
        binding.editTextPassword.isEnabled = !show
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

