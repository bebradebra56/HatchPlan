package com.hatchi.planing.soft.util

import com.hatchi.planing.soft.data.model.TaskType

object TaskUtils {
    fun getTaskDisplayName(taskType: TaskType): String = when (taskType) {
        TaskType.TURN -> "Turn Eggs"
        TaskType.CANDLE -> "Candle Eggs"
        TaskType.STOP_TURN -> "Stop Turning"
        TaskType.HATCH -> "Hatching"
        TaskType.VENTILATION -> "Ventilation"
        TaskType.ADD_WATER -> "Add Water"
        TaskType.COOL -> "Cooling"
        TaskType.SPRAY -> "Spray Eggs"
    }

    fun getTaskIcon(taskType: TaskType): String = when (taskType) {
        TaskType.TURN -> "🔄"
        TaskType.CANDLE -> "🔦"
        TaskType.STOP_TURN -> "🛑"
        TaskType.HATCH -> "🐣"
        TaskType.VENTILATION -> "💨"
        TaskType.ADD_WATER -> "💧"
        TaskType.COOL -> "❄️"
        TaskType.SPRAY -> "💦"
    }
}

