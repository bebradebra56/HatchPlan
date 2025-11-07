package com.hatchi.planing.soft.erjpg.domain.usecases

import android.util.Log
import com.hatchi.planing.soft.erjpg.data.repo.HatchPlanRepository
import com.hatchi.planing.soft.erjpg.data.utils.HatchPlanPushToken
import com.hatchi.planing.soft.erjpg.data.utils.HatchPlanSystemService
import com.hatchi.planing.soft.erjpg.domain.model.HatchPlanEntity
import com.hatchi.planing.soft.erjpg.domain.model.HatchPlanParam
import com.hatchi.planing.soft.erjpg.presentation.app.HatchPlanApplication

class HatchPlanGetAllUseCase(
    private val hatchPlanRepository: HatchPlanRepository,
    private val hatchPlanSystemService: HatchPlanSystemService,
    private val hatchPlanPushToken: HatchPlanPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : HatchPlanEntity?{
        val params = HatchPlanParam(
            hatchPlanLocale = hatchPlanSystemService.hatchPlanGetLocale(),
            hatchPlanPushToken = hatchPlanPushToken.hatchPlanGetToken(),
            hatchPlanAfId = hatchPlanSystemService.hatchPlanGetAppsflyerId()
        )
        Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "Params for request: $params")
        return hatchPlanRepository.hatchPlanGetClient(params, conversion)
    }



}