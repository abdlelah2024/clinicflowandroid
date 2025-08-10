package com.clinicflow.android.models

import java.util.Date

data class Patient(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val dateOfBirth: Date? = null,
    val gender: String = "",
    val address: String = "",
    val medicalHistory: String = "",
    val allergies: String = "",
    val emergencyContact: String = "",
    val emergencyContactPhone: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isActive: Boolean = true
) {
    fun getDisplayName(): String {
        return if (name.isNotEmpty()) name else "مريض غير محدد"
    }
    
    fun getFormattedPhone(): String {
        return if (phone.isNotEmpty()) {
            // Format phone number for display
            when {
                phone.startsWith("966") -> "+966 ${phone.substring(3)}"
                phone.startsWith("05") -> phone
                else -> phone
            }
        } else {
            "غير محدد"
        }
    }
    
    fun getAge(): Int? {
        return dateOfBirth?.let { dob ->
            val now = Date()
            val diffInMillis = now.time - dob.time
            val ageInYears = diffInMillis / (365.25 * 24 * 60 * 60 * 1000)
            ageInYears.toInt()
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "phone" to phone,
            "email" to email,
            "dateOfBirth" to (dateOfBirth ?: Date()),
            "gender" to gender,
            "address" to address,
            "medicalHistory" to medicalHistory,
            "allergies" to allergies,
            "emergencyContact" to emergencyContact,
            "emergencyContactPhone" to emergencyContactPhone,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "isActive" to isActive
        )
    }
    
    companion object {
        fun fromMap(map: Map<String, Any>, id: String = ""): Patient {
            return Patient(
                id = id.ifEmpty { map["id"] as? String ?: "" },
                name = map["name"] as? String ?: "",
                phone = map["phone"] as? String ?: "",
                email = map["email"] as? String ?: "",
                dateOfBirth = map["dateOfBirth"] as? Date,
                gender = map["gender"] as? String ?: "",
                address = map["address"] as? String ?: "",
                medicalHistory = map["medicalHistory"] as? String ?: "",
                allergies = map["allergies"] as? String ?: "",
                emergencyContact = map["emergencyContact"] as? String ?: "",
                emergencyContactPhone = map["emergencyContactPhone"] as? String ?: "",
                createdAt = map["createdAt"] as? Date ?: Date(),
                updatedAt = map["updatedAt"] as? Date ?: Date(),
                isActive = map["isActive"] as? Boolean ?: true
            )
        }
    }
}

