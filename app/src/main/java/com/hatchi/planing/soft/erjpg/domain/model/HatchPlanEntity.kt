package com.hatchi.planing.soft.erjpg.domain.model

import com.google.gson.annotations.SerializedName


data class HatchPlanEntity (
    @SerializedName("ok")
    val hatchPlanOk: String,
    @SerializedName("url")
    val hatchPlanUrl: String,
    @SerializedName("expires")
    val hatchPlanExpires: Long,
)