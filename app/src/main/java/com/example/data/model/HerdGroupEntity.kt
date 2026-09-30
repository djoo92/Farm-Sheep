package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "herd_groups")
data class HerdGroupEntity(
    @PrimaryKey
    val id: String, // e.g. "grp_ancient", "grp_nouveaux_c1", "grp_nouveaux_c2"
    val name: String,
    val description: String,
    val originType: String, // "legacy_heritage", "acquisition"
    val unitPurchaseCostDzd: Double,
    val initialHeadCount: Int,
    val createdAt: Long = System.currentTimeMillis()
)
