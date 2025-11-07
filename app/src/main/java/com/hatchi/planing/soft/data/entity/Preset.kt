package com.hatchi.planing.soft.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.hatchi.planing.soft.data.converter.Converters
import com.hatchi.planing.soft.data.model.Species

@Entity(tableName = "presets")
@TypeConverters(Converters::class)
data class Preset(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val species: Species,
    val totalDays: Int,
    val turnsPerDay: Int,
    val stopTurnDay: Int,
    val stages: List<IncubationStage>,
    val candleDays: List<Int>,
    val requiresCooling: Boolean = false,
    val requiresSpraying: Boolean = false,
    val coolingStartDay: Int? = null,
    val notes: String = ""
)

data class IncubationStage(
    val dayStart: Int,
    val dayEnd: Int,
    val tempMin: Float,
    val tempMax: Float,
    val humidityMin: Float,
    val humidityMax: Float,
    val notes: String = ""
)

