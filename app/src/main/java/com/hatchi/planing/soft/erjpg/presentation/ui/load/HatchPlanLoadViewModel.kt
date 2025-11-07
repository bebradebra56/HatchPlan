package com.hatchi.planing.soft.erjpg.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hatchi.planing.soft.erjpg.data.shar.HatchPlanSharedPreference
import com.hatchi.planing.soft.erjpg.data.utils.HatchPlanSystemService
import com.hatchi.planing.soft.erjpg.domain.usecases.HatchPlanGetAllUseCase
import com.hatchi.planing.soft.erjpg.presentation.app.HatchPlanAppsFlyerState
import com.hatchi.planing.soft.erjpg.presentation.app.HatchPlanApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HatchPlanLoadViewModel(
    private val hatchPlanGetAllUseCase: HatchPlanGetAllUseCase,
    private val hatchPlanSharedPreference: HatchPlanSharedPreference,
    private val hatchPlanSystemService: HatchPlanSystemService
) : ViewModel() {

    private val _hatchPlanHomeScreenState: MutableStateFlow<HatchPlanHomeScreenState> =
        MutableStateFlow(HatchPlanHomeScreenState.HatchPlanLoading)
    val hatchPlanHomeScreenState = _hatchPlanHomeScreenState.asStateFlow()

    private var hatchPlanGetApps = false


    init {
        viewModelScope.launch {
            when (hatchPlanSharedPreference.hatchPlanAppState) {
                0 -> {
                    if (hatchPlanSystemService.hatchPlanIsOnline()) {
                        HatchPlanApplication.hatchPlanConversionFlow.collect {
                            when(it) {
                                HatchPlanAppsFlyerState.HatchPlanDefault -> {}
                                HatchPlanAppsFlyerState.HatchPlanError -> {
                                    hatchPlanSharedPreference.hatchPlanAppState = 2
                                    _hatchPlanHomeScreenState.value =
                                        HatchPlanHomeScreenState.HatchPlanError
                                    hatchPlanGetApps = true
                                }
                                is HatchPlanAppsFlyerState.HatchPlanSuccess -> {
                                    if (!hatchPlanGetApps) {
                                        hatchPlanGetData(it.hatchPlanData)
                                        hatchPlanGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _hatchPlanHomeScreenState.value =
                            HatchPlanHomeScreenState.HatchPlanNotInternet
                    }
                }
                1 -> {
                    if (hatchPlanSystemService.hatchPlanIsOnline()) {
                        if (HatchPlanApplication.HATCH_PLAN_FB_LI != null) {
                            _hatchPlanHomeScreenState.value =
                                HatchPlanHomeScreenState.HatchPlanSuccess(
                                    HatchPlanApplication.HATCH_PLAN_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > hatchPlanSharedPreference.hatchPlanExpired) {
                            Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "Current time more then expired, repeat request")
                            HatchPlanApplication.hatchPlanConversionFlow.collect {
                                when(it) {
                                    HatchPlanAppsFlyerState.HatchPlanDefault -> {}
                                    HatchPlanAppsFlyerState.HatchPlanError -> {
                                        _hatchPlanHomeScreenState.value =
                                            HatchPlanHomeScreenState.HatchPlanSuccess(
                                                hatchPlanSharedPreference.hatchPlanSavedUrl
                                            )
                                        hatchPlanGetApps = true
                                    }
                                    is HatchPlanAppsFlyerState.HatchPlanSuccess -> {
                                        if (!hatchPlanGetApps) {
                                            hatchPlanGetData(it.hatchPlanData)
                                            hatchPlanGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "Current time less then expired, use saved url")
                            _hatchPlanHomeScreenState.value =
                                HatchPlanHomeScreenState.HatchPlanSuccess(
                                    hatchPlanSharedPreference.hatchPlanSavedUrl
                                )
                        }
                    } else {
                        _hatchPlanHomeScreenState.value =
                            HatchPlanHomeScreenState.HatchPlanNotInternet
                    }
                }
                2 -> {
                    _hatchPlanHomeScreenState.value =
                        HatchPlanHomeScreenState.HatchPlanError
                }
            }
        }
    }


    private suspend fun hatchPlanGetData(conversation: MutableMap<String, Any>?) {
        val hatchPlanData = hatchPlanGetAllUseCase.invoke(conversation)
        if (hatchPlanSharedPreference.hatchPlanAppState == 0) {
            if (hatchPlanData == null) {
                hatchPlanSharedPreference.hatchPlanAppState = 2
                _hatchPlanHomeScreenState.value =
                    HatchPlanHomeScreenState.HatchPlanError
            } else {
                hatchPlanSharedPreference.hatchPlanAppState = 1
                hatchPlanSharedPreference.apply {
                    hatchPlanExpired = hatchPlanData.hatchPlanExpires
                    hatchPlanSavedUrl = hatchPlanData.hatchPlanUrl
                }
                _hatchPlanHomeScreenState.value =
                    HatchPlanHomeScreenState.HatchPlanSuccess(hatchPlanData.hatchPlanUrl)
            }
        } else  {
            if (hatchPlanData == null) {
                _hatchPlanHomeScreenState.value =
                    HatchPlanHomeScreenState.HatchPlanSuccess(hatchPlanSharedPreference.hatchPlanSavedUrl)
            } else {
                hatchPlanSharedPreference.apply {
                    hatchPlanExpired = hatchPlanData.hatchPlanExpires
                    hatchPlanSavedUrl = hatchPlanData.hatchPlanUrl
                }
                _hatchPlanHomeScreenState.value =
                    HatchPlanHomeScreenState.HatchPlanSuccess(hatchPlanData.hatchPlanUrl)
            }
        }
    }


    sealed class HatchPlanHomeScreenState {
        data object HatchPlanLoading : HatchPlanHomeScreenState()
        data object HatchPlanError : HatchPlanHomeScreenState()
        data class HatchPlanSuccess(val data: String) : HatchPlanHomeScreenState()
        data object HatchPlanNotInternet: HatchPlanHomeScreenState()
    }
}