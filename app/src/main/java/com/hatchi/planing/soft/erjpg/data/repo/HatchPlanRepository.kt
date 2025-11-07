package com.hatchi.planing.soft.erjpg.data.repo

import android.util.Log
import com.hatchi.planing.soft.erjpg.domain.model.HatchPlanEntity
import com.hatchi.planing.soft.erjpg.domain.model.HatchPlanParam
import com.hatchi.planing.soft.erjpg.presentation.app.HatchPlanApplication.Companion.HATCH_PLAN_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface HatchPlanApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun hatchPlanGetClient(
        @Body jsonString: JsonObject,
    ): Call<HatchPlanEntity>
}


private const val HATCH_PLAN_MAIN = "https://hattchplan.com/"
class HatchPlanRepository {

    suspend fun hatchPlanGetClient(
        hatchPlanParam: HatchPlanParam,
        hatchPlanConversion: MutableMap<String, Any>?
    ): HatchPlanEntity? {
        val gson = Gson()
        val api = hatchPlanGetApi(HATCH_PLAN_MAIN, null)

        val hatchPlanJsonObject = gson.toJsonTree(hatchPlanParam).asJsonObject
        hatchPlanConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            hatchPlanJsonObject.add(key, element)
        }
        return try {
            val hatchPlanRequest: Call<HatchPlanEntity> = api.hatchPlanGetClient(
                jsonString = hatchPlanJsonObject,
            )
            val hatchPlanResult = hatchPlanRequest.awaitResponse()
            Log.d(HATCH_PLAN_MAIN_TAG, "Retrofit: Result code: ${hatchPlanResult.code()}")
            if (hatchPlanResult.code() == 200) {
                Log.d(HATCH_PLAN_MAIN_TAG, "Retrofit: Get request success")
                Log.d(HATCH_PLAN_MAIN_TAG, "Retrofit: Code = ${hatchPlanResult.code()}")
                Log.d(HATCH_PLAN_MAIN_TAG, "Retrofit: ${hatchPlanResult.body()}")
                hatchPlanResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(HATCH_PLAN_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(HATCH_PLAN_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun hatchPlanGetApi(url: String, client: OkHttpClient?) : HatchPlanApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
