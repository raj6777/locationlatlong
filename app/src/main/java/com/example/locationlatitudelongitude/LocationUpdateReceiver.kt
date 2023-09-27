package com.example.locationlatitudelongitude

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
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

/*
class LocationUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val database = LocationDatabase.getInstance(context)

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {

                    Log.d("LocationUpdateReceiver", "Location received")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Log.d("LocationUpdateReceiver", "Date time: ${LocalDateTime.now()}")
                    }else{
                        Log.e("wrong", "something is wrong")
                    }

                    val latitude = location.latitude
                    val longitude = location.longitude
                    if (latitude != 0.0 && longitude != 0.0) {

                        Log.d(
                            "LocationUpdateReceiver",
                            "Latitude: $latitude, Longitude: $longitude"
                        )

                        GlobalScope.launch {
                            // Insert the location data into the database.
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                database.locationdao().insertLocation(
                                    dbLocation(
                                        0,
                                        latitude.toString(),
                                        longitude.toString(),
                                        LocalDateTime.now().toString()
                                    )
                                )
                            }
                        }
                    }
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    // Handle status changes if needed.
                }

                override fun onProviderEnabled(provider: String) {
                    // Handle provider enabled if needed.
                }

                override fun onProviderDisabled(provider: String) {
                    // Handle provider disabled if needed.
                }
            }

            // Request location updates.
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                60000L, // Minimum time interval in milliseconds (e.g., 60 seconds)
                0f, // Minimum distance change in meters (e.g., 10 meters)
                locationListener
            )
        }
    }
}
*/

class LocationUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val database = LocationDatabase.getInstance(context)
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,60000).build()
               /* .setInterval(locationRequestInterval.toLong())*/

            val mLocationCallback: LocationCallback = object : LocationCallback() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    Log.e("location data", "location")
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
    }

}