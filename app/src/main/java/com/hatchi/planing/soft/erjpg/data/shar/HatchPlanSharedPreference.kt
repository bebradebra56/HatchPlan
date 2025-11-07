package com.hatchi.planing.soft.erjpg.data.shar

import android.content.Context
import androidx.core.content.edit

class HatchPlanSharedPreference(context: Context) {
    private val hatchPlanPrefs = context.getSharedPreferences("hatchPlanSharedPrefsAb", Context.MODE_PRIVATE)

    var hatchPlanSavedUrl: String
        get() = hatchPlanPrefs.getString(HATCH_PLAN_SAVED_URL, "") ?: ""
        set(value) = hatchPlanPrefs.edit { putString(HATCH_PLAN_SAVED_URL, value) }

    var hatchPlanExpired : Long
        get() = hatchPlanPrefs.getLong(HATCH_PLAN_EXPIRED, 0L)
        set(value) = hatchPlanPrefs.edit { putLong(HATCH_PLAN_EXPIRED, value) }

    var hatchPlanAppState: Int
        get() = hatchPlanPrefs.getInt(HATCH_PLAN_APPLICATION_STATE, 0)
        set(value) = hatchPlanPrefs.edit { putInt(HATCH_PLAN_APPLICATION_STATE, value) }

    var hatchPlanNotificationRequest: Long
        get() = hatchPlanPrefs.getLong(HATCH_PLAN_NOTIFICAITON_REQUEST, 0L)
        set(value) = hatchPlanPrefs.edit { putLong(HATCH_PLAN_NOTIFICAITON_REQUEST, value) }

    var hatchPlanNotificationRequestedBefore: Boolean
        get() = hatchPlanPrefs.getBoolean(HATCH_PLAN_NOTIFICATION_REQUEST_BEFORE, false)
        set(value) = hatchPlanPrefs.edit { putBoolean(
            HATCH_PLAN_NOTIFICATION_REQUEST_BEFORE, value) }

    companion object {
        private const val HATCH_PLAN_SAVED_URL = "hatchPlanSavedUrl"
        private const val HATCH_PLAN_EXPIRED = "hatchPlanExpired"
        private const val HATCH_PLAN_APPLICATION_STATE = "hatchPlanApplicationState"
        private const val HATCH_PLAN_NOTIFICAITON_REQUEST = "hatchPlanNotificationRequest"
        private const val HATCH_PLAN_NOTIFICATION_REQUEST_BEFORE = "hatchPlanNotificationRequestedBefore"
    }
}