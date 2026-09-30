package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medical_logs")
data class MedicalLogEntity(
    @PrimaryKey
    val id: String,
    val animalTag: String,
    val administeredBy: String,
    val treatmentName: String,
    val batchNumber: String,
    val dosage: String,
    val date: Long = System.currentTimeMillis(),
    val boosterDate: Long? = null,
    val notes: String = ""
)
