package com.hatchi.planing.soft.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hatchi.planing.soft.data.entity.IncubationStage
import com.hatchi.planing.soft.data.model.BatchStatus
import com.hatchi.planing.soft.data.model.Species
import com.hatchi.planing.soft.data.model.TaskStatus
import com.hatchi.planing.soft.data.model.TaskType
import java.util.Date

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time

    @TypeConverter
    fun toDate(timestamp: Long?): Date? = timestamp?.let { Date(it) }

    @TypeConverter
    fun fromSpecies(species: Species): String = species.name

    @TypeConverter
    fun toSpecies(value: String): Species = Species.valueOf(value)

    @TypeConverter
    fun fromBatchStatus(status: BatchStatus): String = status.name

    @TypeConverter
    fun toBatchStatus(value: String): BatchStatus = BatchStatus.valueOf(value)

    @TypeConverter
    fun fromTaskType(type: TaskType): String = type.name

    @TypeConverter
    fun toTaskType(value: String): TaskType = TaskType.valueOf(value)

    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): String = status.name

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus = TaskStatus.valueOf(value)

    @TypeConverter
    fun fromIncubationStageList(stages: List<IncubationStage>): String {
        return gson.toJson(stages)
    }

    @TypeConverter
    fun toIncubationStageList(value: String): List<IncubationStage> {
        val listType = object : TypeToken<List<IncubationStage>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromIntList(list: List<Int>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toIntList(value: String): List<Int> {
        val listType = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(value, listType)
    }
}

