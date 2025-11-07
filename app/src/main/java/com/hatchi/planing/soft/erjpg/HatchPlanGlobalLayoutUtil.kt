package com.hatchi.planing.soft.erjpg

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.hatchi.planing.soft.erjpg.presentation.app.HatchPlanApplication

class HatchPlanGlobalLayoutUtil {

    private var hatchPlanMChildOfContent: View? = null
    private var hatchPlanUsableHeightPrevious = 0

    fun hatchPlanAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        hatchPlanMChildOfContent = content.getChildAt(0)

        hatchPlanMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val hatchPlanUsableHeightNow = hatchPlanComputeUsableHeight()
        if (hatchPlanUsableHeightNow != hatchPlanUsableHeightPrevious) {
            val hatchPlanUsableHeightSansKeyboard = hatchPlanMChildOfContent?.rootView?.height ?: 0
            val hatchPlanHeightDifference = hatchPlanUsableHeightSansKeyboard - hatchPlanUsableHeightNow

            if (hatchPlanHeightDifference > (hatchPlanUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(HatchPlanApplication.hatchPlanInputMode)
            } else {
                activity.window.setSoftInputMode(HatchPlanApplication.hatchPlanInputMode)
            }
//            mChildOfContent?.requestLayout()
            hatchPlanUsableHeightPrevious = hatchPlanUsableHeightNow
        }
    }

    private fun hatchPlanComputeUsableHeight(): Int {
        val r = Rect()
        hatchPlanMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}