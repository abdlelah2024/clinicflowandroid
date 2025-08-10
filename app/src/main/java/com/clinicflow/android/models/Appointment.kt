package com.clinicflow.android.models

import java.util.Date

data class Appointment(
    val id: String = "",
    val patientId: String = "",
    val patientName: String = "",
    val patientPhone: String = "",
    val doctorId: String = "",
    val doctorName: String = "",
    val appointmentDate: Date = Date(),
    val appointmentTime: String = "",
    val duration: Int = 30, // in minutes
    val type: AppointmentType = AppointmentType.CONSULTATION,
    val status: AppointmentStatus = AppointmentStatus.SCHEDULED,
    val notes: String = "",
    val symptoms: String = "",
    val diagnosis: String = "",
    val treatment: String = "",
    val prescription: String = "",
    val followUpDate: Date? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isActive: Boolean = true
) {
    
    enum class AppointmentType(val displayName: String) {
        CONSULTATION("استشارة"),
        FOLLOW_UP("متابعة"),
        EMERGENCY("طوارئ"),
        CHECKUP("فحص دوري"),
        PROCEDURE("إجراء طبي"),
        VACCINATION("تطعيم")
    }
    
    enum class AppointmentStatus(val displayName: String, val colorRes: Int) {
        SCHEDULED("مجدول", android.R.color.holo_blue_light),
        CONFIRMED("مؤكد", android.R.color.holo_green_light),
        IN_PROGRESS("جاري", android.R.color.holo_orange_light),
        COMPLETED("مكتمل", android.R.color.holo_green_dark),
        CANCELLED("ملغي", android.R.color.holo_red_light),
        NO_SHOW("لم يحضر", android.R.color.darker_gray),
        RESCHEDULED("معاد جدولته", android.R.color.holo_purple)
    }
    
    fun getFormattedDateTime(): String {
        val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("ar"))
        return "${dateFormat.format(appointmentDate)} - $appointmentTime"
    }
    
    fun getFormattedDate(): String {
        val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("ar"))
        return dateFormat.format(appointmentDate)
    }
    
    fun getFormattedTime(): String {
        return appointmentTime
    }
    
    fun getDurationText(): String {
        return "$duration دقيقة"
    }
    
    fun isToday(): Boolean {
        val today = Date()
        val appointmentCal = java.util.Calendar.getInstance().apply { time = appointmentDate }
        val todayCal = java.util.Calendar.getInstance().apply { time = today }
        
        return appointmentCal.get(java.util.Calendar.YEAR) == todayCal.get(java.util.Calendar.YEAR) &&
                appointmentCal.get(java.util.Calendar.DAY_OF_YEAR) == todayCal.get(java.util.Calendar.DAY_OF_YEAR)
    }
    
    fun isPast(): Boolean {
        return appointmentDate.before(Date())
    }
    
    fun canBeModified(): Boolean {
        return status in listOf(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED) && !isPast()
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "patientId" to patientId,
            "patientName" to patientName,
            "patientPhone" to patientPhone,
            "doctorId" to doctorId,
            "doctorName" to doctorName,
            "appointmentDate" to appointmentDate,
            "appointmentTime" to appointmentTime,
            "duration" to duration,
            "type" to type.name,
            "status" to status.name,
            "notes" to notes,
            "symptoms" to symptoms,
            "diagnosis" to diagnosis,
            "treatment" to treatment,
            "prescription" to prescription,
            "followUpDate" to (followUpDate ?: Date()),
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "isActive" to isActive
        )
    }
    
    companion object {
        fun fromMap(map: Map<String, Any>, id: String = ""): Appointment {
            return Appointment(
                id = id.ifEmpty { map["id"] as? String ?: "" },
                patientId = map["patientId"] as? String ?: "",
                patientName = map["patientName"] as? String ?: "",
                patientPhone = map["patientPhone"] as? String ?: "",
                doctorId = map["doctorId"] as? String ?: "",
                doctorName = map["doctorName"] as? String ?: "",
                appointmentDate = map["appointmentDate"] as? Date ?: Date(),
                appointmentTime = map["appointmentTime"] as? String ?: "",
                duration = (map["duration"] as? Long)?.toInt() ?: 30,
                type = try {
                    AppointmentType.valueOf(map["type"] as? String ?: "CONSULTATION")
                } catch (e: Exception) {
                    AppointmentType.CONSULTATION
                },
                status = try {
                    AppointmentStatus.valueOf(map["status"] as? String ?: "SCHEDULED")
                } catch (e: Exception) {
                    AppointmentStatus.SCHEDULED
                },
                notes = map["notes"] as? String ?: "",
                symptoms = map["symptoms"] as? String ?: "",
                diagnosis = map["diagnosis"] as? String ?: "",
                treatment = map["treatment"] as? String ?: "",
                prescription = map["prescription"] as? String ?: "",
                followUpDate = map["followUpDate"] as? Date,
                createdAt = map["createdAt"] as? Date ?: Date(),
                updatedAt = map["updatedAt"] as? Date ?: Date(),
                isActive = map["isActive"] as? Boolean ?: true
            )
        }
        
        fun getAvailableTimeSlots(): List<String> {
            return listOf(
                "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
                "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",
                "14:00", "14:30", "15:00", "15:30", "16:00", "16:30",
                "17:00", "17:30", "18:00", "18:30", "19:00", "19:30",
                "20:00", "20:30", "21:00", "21:30", "22:00"
            )
        }
    }
}

