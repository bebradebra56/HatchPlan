package com.hatchi.planing.soft.erjpg.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.hatchi.planing.soft.erjpg.presentation.di.hatchPlanModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface HatchPlanAppsFlyerState {
    data object HatchPlanDefault : HatchPlanAppsFlyerState
    data class HatchPlanSuccess(val hatchPlanData: MutableMap<String, Any>?) :
        HatchPlanAppsFlyerState

    data object HatchPlanError : HatchPlanAppsFlyerState
}

interface HatchPlanAppsApi {
    @Headers("Content-Type: application/json")
    @GET(HATCH_PLAN_LIN)
    fun hatchPlanGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val HATCH_PLAN_APP_DEV = "4NXq7nHuvqfrN6XPYtU84Z"
private const val HATCH_PLAN_LIN = "com.hatchi.planing.soft"

class HatchPlanApplication : Application() {
    private var hatchPlanIsResumed = false
    private var hatchPlanConversionTimeoutJob: Job? = null
    private var hatchPlanDeepLinkData: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        hatchPlanSetDebufLogger(appsflyer)
        hatchPlanMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        hatchPlanExtractDeepMap(p0.deepLink)
                        Log.d(HATCH_PLAN_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(HATCH_PLAN_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(HATCH_PLAN_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })


        appsflyer.init(
            HATCH_PLAN_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    hatchPlanConversionTimeoutJob?.cancel()
                    Log.d(HATCH_PLAN_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = hatchPlanGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.hatchPlanGetClient(
                                    devkey = HATCH_PLAN_APP_DEV,
                                    deviceId = hatchPlanGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(HATCH_PLAN_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    hatchPlanResume(HatchPlanAppsFlyerState.HatchPlanError)
                                } else {
                                    hatchPlanResume(
                                        HatchPlanAppsFlyerState.HatchPlanSuccess(resp)
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(HATCH_PLAN_MAIN_TAG, "Error: ${d.message}")
                                hatchPlanResume(HatchPlanAppsFlyerState.HatchPlanError)
                            }
                        }
                    } else {
                        hatchPlanResume(HatchPlanAppsFlyerState.HatchPlanSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    hatchPlanConversionTimeoutJob?.cancel()
                    Log.d(HATCH_PLAN_MAIN_TAG, "onConversionDataFail: $p0")
                    hatchPlanResume(HatchPlanAppsFlyerState.HatchPlanError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(HATCH_PLAN_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(HATCH_PLAN_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, HATCH_PLAN_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(HATCH_PLAN_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(HATCH_PLAN_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
        hatchPlanStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@HatchPlanApplication)
            modules(
                listOf(
                    hatchPlanModule
                )
            )
        }
    }

    private fun hatchPlanExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(HATCH_PLAN_MAIN_TAG, "Extracted DeepLink data: $map")
        hatchPlanDeepLinkData = map
    }

    private fun hatchPlanStartConversionTimeout() {
        hatchPlanConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            if (!hatchPlanIsResumed) {
                Log.d(HATCH_PLAN_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
                hatchPlanResume(HatchPlanAppsFlyerState.HatchPlanError)
            }
        }
    }

    private fun hatchPlanResume(state: HatchPlanAppsFlyerState) {
        hatchPlanConversionTimeoutJob?.cancel()
        if (state is HatchPlanAppsFlyerState.HatchPlanSuccess) {
            val convData = state.hatchPlanData ?: mutableMapOf()
            val deepData = hatchPlanDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!hatchPlanIsResumed) {
                hatchPlanIsResumed = true
                hatchPlanConversionFlow.value = HatchPlanAppsFlyerState.HatchPlanSuccess(merged)
            }
        } else {
            if (!hatchPlanIsResumed) {
                hatchPlanIsResumed = true
                hatchPlanConversionFlow.value = state
            }
        }
    }

    private fun hatchPlanGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(HATCH_PLAN_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun hatchPlanSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun hatchPlanMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun hatchPlanGetApi(url: String, client: OkHttpClient?): HatchPlanAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {
        var hatchPlanInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val hatchPlanConversionFlow: MutableStateFlow<HatchPlanAppsFlyerState> = MutableStateFlow(
            HatchPlanAppsFlyerState.HatchPlanDefault
        )
        var HATCH_PLAN_FB_LI: String? = null
        const val HATCH_PLAN_MAIN_TAG = "HatchPlanMainTag"
    }
}