package com.hatchi.planing.soft.erjpg.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.hatchi.planing.soft.erjpg.presentation.app.HatchPlanApplication

class HatchPlanPushHandler {
    fun hatchPlanHandlePush(extras: Bundle?) {
        Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = hatchPlanBundleToMap(extras)
            Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    HatchPlanApplication.HATCH_PLAN_FB_LI = map["url"]
                    Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "Push data no!")
        }
    }

    private fun hatchPlanBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}