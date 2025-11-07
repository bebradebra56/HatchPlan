package com.hatchi.planing.soft.erjpg.domain.model

import com.google.gson.annotations.SerializedName


private const val HATCH_PLAN_A = "com.hatchi.planing.soft"
private const val HATCH_PLAN_B = "hatchplan-f0057"
data class HatchPlanParam (
    @SerializedName("af_id")
    val hatchPlanAfId: String,
    @SerializedName("bundle_id")
    val hatchPlanBundleId: String = HATCH_PLAN_A,
    @SerializedName("os")
    val hatchPlanOs: String = "Android",
    @SerializedName("store_id")
    val hatchPlanStoreId: String = HATCH_PLAN_A,
    @SerializedName("locale")
    val hatchPlanLocale: String,
    @SerializedName("push_token")
    val hatchPlanPushToken: String,
    @SerializedName("firebase_project_id")
    val hatchPlanFirebaseProjectId: String = HATCH_PLAN_B,

    )