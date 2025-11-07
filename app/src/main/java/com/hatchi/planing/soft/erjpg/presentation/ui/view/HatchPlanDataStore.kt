package com.hatchi.planing.soft.erjpg.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class HatchPlanDataStore : ViewModel(){
    val hatchPlanViList: MutableList<HatchPlanVi> = mutableListOf()
    var hatchPlanIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var hatchPlanContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var hatchPlanView: HatchPlanVi

}