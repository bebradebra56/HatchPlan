package com.hatchi.planing.soft.erjpg.presentation.di

import com.hatchi.planing.soft.erjpg.data.repo.HatchPlanRepository
import com.hatchi.planing.soft.erjpg.data.shar.HatchPlanSharedPreference
import com.hatchi.planing.soft.erjpg.data.utils.HatchPlanPushToken
import com.hatchi.planing.soft.erjpg.data.utils.HatchPlanSystemService
import com.hatchi.planing.soft.erjpg.domain.usecases.HatchPlanGetAllUseCase
import com.hatchi.planing.soft.erjpg.presentation.pushhandler.HatchPlanPushHandler
import com.hatchi.planing.soft.erjpg.presentation.ui.load.HatchPlanLoadViewModel
import com.hatchi.planing.soft.erjpg.presentation.ui.view.HatchPlanViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val hatchPlanModule = module {
    factory {
        HatchPlanPushHandler()
    }
    single {
        HatchPlanRepository()
    }
    single {
        HatchPlanSharedPreference(get())
    }
    factory {
        HatchPlanPushToken()
    }
    factory {
        HatchPlanSystemService(get())
    }
    factory {
        HatchPlanGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        HatchPlanViFun(get())
    }
    viewModel {
        HatchPlanLoadViewModel(get(), get(), get())
    }
}