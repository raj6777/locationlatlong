package com.example.locationlatitudelongitude

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.HandlerThread
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.locationlatitudelongitude.room.LocationDatabase
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime


class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        val database = LocationDatabase.getInstance(applicationContext)
        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 900000).build()
            val handlerThread = HandlerThread("MyWorkerThread")
            handlerThread.start()
            val looper = handlerThread.looper

            val mLocationCallback: LocationCallback = object : LocationCallback() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    Log.e("location data", "location workmanager")
                    Log.e("date time", LocalDateTime.now().toString())
                    if (locationResult.locations.isNotEmpty()) {
                        val location: Location? = locationResult.lastLocation

                        Log.e("location", "${location?.latitude}, ${location?.longitude}")

                        GlobalScope.launch {
                            // Insert the location data into the database.
                            database.locationdao().insertLocation(
                                dbLocation(
                                    0,
                                    location?.latitude.toString(),
                                    location?.longitude.toString(),
                                    LocalDateTime.now().toString()
                                )
                            )
                        }
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, looper)

        }

        return Result.success()
    }
}