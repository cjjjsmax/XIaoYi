package com.example.xiaoyi.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
class NetworkReceiver : BroadcastReceiver() {
    interface NetworkStatusListener{
        fun onNetworkConnected()
        fun onNetworkDisconnected()
    }
    companion object{
        private var listener : NetworkStatusListener? = null
        fun setListener(listener: NetworkStatusListener){
            this.listener = listener
        }
        fun removeListener(){
            this.listener = null
        }
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null){
            return
        }
        val isConnected = isNetworkConnected(context)

        if (isConnected){
            listener?.onNetworkConnected()
        }else{
            listener?.onNetworkDisconnected()
        }
    }
    private fun isNetworkConnected(context: Context): Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        }else{
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }
}