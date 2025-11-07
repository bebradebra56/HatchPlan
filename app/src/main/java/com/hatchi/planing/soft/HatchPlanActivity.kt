package com.hatchi.planing.soft

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.hatchi.planing.soft.erjpg.HatchPlanGlobalLayoutUtil
import com.hatchi.planing.soft.erjpg.presentation.app.HatchPlanApplication
import com.hatchi.planing.soft.erjpg.presentation.pushhandler.HatchPlanPushHandler
import com.hatchi.planing.soft.erjpg.hatchPlanSetupSystemBars
import org.koin.android.ext.android.inject

class HatchPlanActivity : AppCompatActivity() {

    private val hatchPlanPushHandler by inject<HatchPlanPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hatchPlanSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_hatch_plan)

        val hatchPlanRootView = findViewById<View>(android.R.id.content)
        HatchPlanGlobalLayoutUtil().hatchPlanAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(hatchPlanRootView) { hatchPlanView, hatchPlanInsets ->
            val hatchPlanSystemBars = hatchPlanInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val hatchPlanDisplayCutout = hatchPlanInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val hatchPlanIme = hatchPlanInsets.getInsets(WindowInsetsCompat.Type.ime())


            val hatchPlanTopPadding = maxOf(hatchPlanSystemBars.top, hatchPlanDisplayCutout.top)
            val hatchPlanLeftPadding = maxOf(hatchPlanSystemBars.left, hatchPlanDisplayCutout.left)
            val hatchPlanRightPadding = maxOf(hatchPlanSystemBars.right, hatchPlanDisplayCutout.right)
            window.setSoftInputMode(HatchPlanApplication.hatchPlanInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "ADJUST PUN")
                val hatchPlanBottomInset = maxOf(hatchPlanSystemBars.bottom, hatchPlanDisplayCutout.bottom)

                hatchPlanView.setPadding(hatchPlanLeftPadding, hatchPlanTopPadding, hatchPlanRightPadding, 0)

                hatchPlanView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = hatchPlanBottomInset
                }
            } else {
                Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "ADJUST RESIZE")

                val hatchPlanBottomInset = maxOf(hatchPlanSystemBars.bottom, hatchPlanDisplayCutout.bottom, hatchPlanIme.bottom)

                hatchPlanView.setPadding(hatchPlanLeftPadding, hatchPlanTopPadding, hatchPlanRightPadding, 0)

                hatchPlanView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = hatchPlanBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "Activity onCreate()")
        hatchPlanPushHandler.hatchPlanHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hatchPlanSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        hatchPlanSetupSystemBars()
    }
}