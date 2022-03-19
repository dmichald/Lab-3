package com.example.lab_3

import android.Manifest
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.lab_3.receiver.NetworkReceiver
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        val TAG = MainActivity.javaClass.canonicalName
        val networkReceiver = NetworkReceiver()
        var READ_CONTACTS_PERMISSION_REQUEST_CODE = 234;
        var READ_LOCATION_PERMISSION_REQUEST_CODE = 345;

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    fun onGetPostsClick(v: View) {
        val url = URL("https://jsonplaceholder.typicode.com/posts")
        GlobalScope.launch {
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET" // optional default is GET
                inputStream.bufferedReader().use {
                    it.lines().forEach { line ->
                        Log.d("ACT", line)
                    }
                }
            }
        }
    }

    fun onGetContactsClick(v: View) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS),
            READ_CONTACTS_PERMISSION_REQUEST_CODE
        )

    }

    fun onGetLocationClick(v: View) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            READ_LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_CONTACTS_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {
                readContacts()
            } else {
                Toast.makeText(this, "Brak uprawnien do czytania kontakow.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        if (requestCode == READ_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {
                readLocation()
            } else {
                Toast.makeText(this, "Brak uprawnien do lokalizacji.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun readLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                Toast.makeText(this, " ${location?.latitude} ${location?.longitude}", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Location: ${location?.latitude} ${location?.longitude}")
            }
    }

    fun readContacts() {
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        while (cursor!!.moveToNext()) {
            val contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            val displayName =
                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            Log.d(TAG, "Contact ${contactId} ${displayName}")
        }
    }

    fun onWifiStateClick(v: View) {
        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WIFI ENABLED", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "WIFI DISABLED", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        unregisterReceiver(networkReceiver)
        super.onDestroy()
    }
}