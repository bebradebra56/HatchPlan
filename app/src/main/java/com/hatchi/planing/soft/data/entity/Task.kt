package com.hatchi.planing.soft.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.hatchi.planing.soft.data.converter.Converters
import com.hatchi.planing.soft.data.model.TaskStatus
import com.hatchi.planing.soft.data.model.TaskType
import java.util.Date

@Entity(
    tableName = "tasks",
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
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val batchId: Long,
    val type: TaskType,
    val dueDate: Date,
    val status: TaskStatus = TaskStatus.PENDING,
    val completedAt: Date? = null,
    val notes: String = ""
)

