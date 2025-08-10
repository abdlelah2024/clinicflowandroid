package com.clinicflow.android.models

data class SearchResult(
    val type: Type,
    val title: String,
    val subtitle: String,
    val patient: Patient? = null,
    val appointment: Appointment? = null,
    val appointmentId: String? = null
) {
    enum class Type {
        PATIENT,
        APPOINTMENT
    }
    
    companion object {
        fun fromPatient(patient: Patient): SearchResult {
            return SearchResult(
                type = Type.PATIENT,
                title = patient.getDisplayName(),
                subtitle = "هاتف: ${patient.getFormattedPhone()}",
                patient = patient
            )
        }
        
        fun fromAppointment(appointment: Appointment): SearchResult {
            return SearchResult(
                type = Type.APPOINTMENT,
                title = "موعد - ${appointment.patientName}",
                subtitle = "${appointment.getFormattedDateTime()} - ${appointment.type.displayName}",
                appointment = appointment,
                appointmentId = appointment.id
            )
        }
    }
}

