package com.hatchi.planing.soft.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    private val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)

    fun formatDate(date: Date): String = dateFormat.format(date)
    
    fun formatTime(date: Date): String = timeFormat.format(date)
    
    fun formatDateTime(date: Date): String = dateTimeFormat.format(date)

    fun getDaysBetween(start: Date, end: Date): Int {
        val diff = end.time - start.time
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }

    fun addDays(date: Date, days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_MONTH, days)
        return calendar.time
    }

    fun isToday(date: Date): Boolean {
        val today = Calendar.getInstance()
        val checkDate = Calendar.getInstance()
        checkDate.time = date
        return today.get(Calendar.YEAR) == checkDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == checkDate.get(Calendar.DAY_OF_YEAR)
    }

    fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}

