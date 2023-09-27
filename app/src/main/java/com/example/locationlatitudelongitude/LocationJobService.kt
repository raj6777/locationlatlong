package com.example.locationlatitudelongitude

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.locationlatitudelongitude.room.LocationDatabase
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime


@SuppressLint("SpecifyJobSchedulerIdRange")
class LocationJobService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val database = LocationDatabase.getInstance(this)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,3*60*1000).build()

            val mLocationCallback: LocationCallback = object : LocationCallback() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    Log.e("location data", "location schaduler")
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
                        //*  Log.e("location list",locationList.toString())*//*
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null)

        }
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }
}