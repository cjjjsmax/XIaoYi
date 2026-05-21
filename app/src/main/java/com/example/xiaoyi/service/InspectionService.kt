package com.example.xiaoyi.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.xiaoyi.api.QualityInspectionRequest
import com.example.xiaoyi.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InspectionService : Service() {
    private val TAG = "InspectionService"
    inner class LocalBinder : Binder(){
        fun getService() : InspectionService = this@InspectionService
    }
    private val binder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"服务被创建了")
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG,"Service被绑定了")
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val productId = intent?.getLongExtra("productId",-1L) ?: -1L
        if (productId != -1L){
            Log.d(TAG,"开始后台质检，商品ID：$productId")
            Thread{
                performInspection(productId)
            }.start()
        }
        return START_NOT_STICKY
    }

    private fun performInspection(productId: Long){
        try {
            Log.d(TAG,"正在质检商品: $productId")
            val request = QualityInspectionRequest(productId = productId)
            RetrofitClient.qualityInspectionApi.generateProductReport(request).enqueue(object :
                Callback<Map<String , Any>>{
                override fun onResponse(
                    call: Call<Map<String, Any>?>,
                    response: Response<Map<String, Any>?>
                ) {
                    if (response.isSuccessful){
                        val data = response.body()
                        val code = data?.get("code") as? Int ?: -1
                        if (code == 200){
                            Log.d(TAG,"质检成功！ 商品ID： $productId")
                            Log.d(TAG,"质检结果: $data")
                        }else{
                            val message = data?.get("message") as? String ?: "质检失败"
                            Log.e(TAG,"质检失败: $message")
                        }
                    }else{
                        Log.e(TAG,"质检请求失败,状态码:${response.code()}")
                    }
                    stopSelf()
                }

                override fun onFailure(call: Call<Map<String, Any>?>, t: Throwable) {
                    Log.e(TAG,"网络错误: ${t.message}")
                    t.printStackTrace()
                    stopSelf()
                }
            })
        }catch (e : Exception){
            Log.e(TAG,"质检失败: ${e.message}")
            e.printStackTrace()
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"销毁服务")
    }
}