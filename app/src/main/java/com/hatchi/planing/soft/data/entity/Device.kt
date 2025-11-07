package com.hatchi.planing.soft.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class Device(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String, // "incubator"
    val capacity: Int,
    val location: String = "",
    val bleId: String? = null,
    val wifiAddress: String? = null,
    val tempCalibration: Float = 0f,
    val humidityCalibration: Float = 0f,
    val isActive: Boolean = true
)

