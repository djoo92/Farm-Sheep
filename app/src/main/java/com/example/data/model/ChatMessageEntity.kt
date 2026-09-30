package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val channelId: String = "ops_general",
    val senderName: String,
    val senderRole: String, // "Super Admin", "Veterinarian", "Field Worker"
    val messageText: String,
    val mediaUri: String? = null,
    val watermarkText: String? = null,
    val tagId: String? = null,
    val gpsLocation: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
