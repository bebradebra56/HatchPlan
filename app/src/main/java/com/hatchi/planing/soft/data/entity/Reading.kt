package com.hatchi.planing.soft.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.hatchi.planing.soft.data.converter.Converters
import java.util.Date

@Entity(
    tableName = "readings",
    foreignKeys = [
        ForeignKey(
            entity = Batch::class,
            parentColumns = ["id"],
            childColumns = ["batchId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@TypeConverters(Converters::class)
data class Reading(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val batchId: Long,
    val timestamp: Date,
    val temperature: Float,
    val humidity: Float,
    val source: String = "manual", // "manual", "ble", "wifi"
    val notes: String = ""
)

