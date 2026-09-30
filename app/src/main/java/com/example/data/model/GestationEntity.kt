package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gestations")
data class GestationEntity(
    @PrimaryKey
    val id: String,
    val eweTag: String,
    val ramTag: String? = null,
    val serviceDate: Long,
    val expectedDeliveryDate: Long,
    val actualDeliveryDate: Long? = null,
    val offspringCount: Int = 0,
    val status: String = "in_progress", // "in_progress", "delivered", "aborted"
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
