package com.hectorllb.neonFichaje.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_config")
data class UserConfigEntity(
    @PrimaryKey
    val id: Int = 1,
    val weeklyTargetHours: Double = 40.0, // Default 40h
    val flexibleSchedule: Boolean = true
)
