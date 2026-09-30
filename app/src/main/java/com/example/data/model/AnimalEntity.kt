package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey
    val tagId: String, // e.g. "LEG-PRIME-01", "C1-MOTHER-01"
    val rfid: String = "",
    val groupId: String, // "grp_ancient", "grp_nouveaux_c1", "grp_nouveaux_c2"
    val groupName: String, // "Group Ancient", "Group Nouveaux - Cohort 1", "Group Nouveaux - Cohort 2"
    val category: String, // "primiparous_ewe", "multiparous_ewe", "breeding_ram", "juvenile_ram", "nursing_lamb_male", "nursing_lamb_female", "fattening_lamb"
    val gender: String, // "male", "female"
    val birthDate: Long? = null,
    val damTag: String? = null, // Mother tag ID
    val sireTag: String? = null, // Father tag ID
    val reproductiveStatus: String = "open", // "open", "serviced", "pregnant", "lactating", "weaned", "culled", "deceased"
    val lastServiceDate: Long? = null,
    val expectedLambingDate: Long? = null,
    val currentWeightKg: Double = 45.0,
    val purchasePriceDzd: Double = 0.0,
    val currentValuationDzd: Double = 0.0,
    val isQuarantined: Boolean = false,
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
