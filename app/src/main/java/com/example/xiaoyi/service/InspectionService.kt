package com.example.xiaoyi.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.xiaoyi.api.QualityInspectionRequest
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.model.Result
import retrofit2.Call
import retrofit2.Callback
class InspectionService : Service() {
    inner class LocalBinder : Binder(){
        fun getService() : InspectionService = this@InspectionService
    }
    private val binder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val productId = intent?.getLongExtra("productId",-1L) ?: -1L
        if (productId != -1L){
            Thread{
                performInspection(productId)
            }.start()
        } else {
            stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun performInspection(productId: Long){
        try {
            val request = QualityInspectionRequest(productId = productId)
            RetrofitClient.qualityInspectionApi.generateProductReport(request).enqueue(object :
                Callback<Result<Map<String, Any>>>{
                override fun onResponse(
                    call: Call<Result<Map<String, Any>>>,
                    response: Response<Result<Map<String, Any>>>
                ) {
                    stopSelf()
                }

                override fun onFailure(call: Call<Result<Map<String, Any>>>, t: Throwable) {
                    stopSelf()
                }
            })
        }catch (e : Exception){
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}