package com.hatchi.planing.soft.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.hatchi.planing.soft.data.converter.Converters
import com.hatchi.planing.soft.data.model.BatchStatus
import com.hatchi.planing.soft.data.model.Species
import java.util.Date

@Entity(
    tableName = "batches",
    foreignKeys = [
        ForeignKey(
            entity = Preset::class,
            parentColumns = ["id"],
            childColumns = ["presetId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Device::class,
            parentColumns = ["id"],
            childColumns = ["deviceId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
@TypeConverters(Converters::class)
data class Batch(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val species: Species,
    val presetId: Long? = null,
    val deviceId: Long? = null,
    val startDate: Date,
    val totalEggs: Int,
    val expectedHatchDate: Date,
    val status: BatchStatus = BatchStatus.ACTIVE,
    val notes: String = "",
    val imageUri: String? = null,
    val hatchedCount: Int = 0,
    val actualHatchDate: Date? = null
)

