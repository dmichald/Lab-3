package com.example.lab_3.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log

class NetworkReceiver : BroadcastReceiver() {
    companion object {
        val TAG = NetworkReceiver.javaClass.canonicalName
    }

    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.d(
            TAG,
            "TEST TES TES"
        ) // Receiver jest wywoływany. Aplikacja przestaje działać, ze wzglęu na brak uprawnień.
        val connectivityManager = p0?.getSystemService(Context.CONNECTIVITY_SERVICE) as
                ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo;
        Log.d(TAG, "Is connected: ${networkInfo?.isConnected}")
        Log.d(TAG, "Type: ${networkInfo?.type} ${ConnectivityManager.TYPE_WIFI}")
    }
}