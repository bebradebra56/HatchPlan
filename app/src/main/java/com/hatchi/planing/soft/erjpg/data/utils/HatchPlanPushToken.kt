package com.hatchi.planing.soft.erjpg.data.utils

import android.util.Log
import com.hatchi.planing.soft.erjpg.presentation.app.HatchPlanApplication
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HatchPlanPushToken {

    suspend fun hatchPlanGetToken(): String = suspendCoroutine { continuation ->
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (!it.isSuccessful) {
                    continuation.resume(it.result)
                    Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "Token error: ${it.exception}")
                } else {
                    continuation.resume(it.result)
                }
            }
        } catch (e: Exception) {
            Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "FirebaseMessagingPushToken = null")
            continuation.resume("")
        }
    }


}