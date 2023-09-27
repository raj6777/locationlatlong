package com.example.locationlatitudelongitude

import LocationForegroundService
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.room.Room
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.locationlatitudelongitude.room.LocationDatabase
import com.google.android.gms.location.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    /* lateinit var fusedLocationClient: FusedLocationProviderClient*/
  /*  lateinit var database: LocationDatabase*/
    private lateinit var tvGpsLocation: TextView
    private val locationPermissionCode = 2
    private val locationRequestInterval = 600000// 10 minutes in millisecondse

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Location App"

        val serviceIntent = Intent(this, LocationForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)

        val button: Button = findViewById(R.id.getLocation)
        val button1: Button = findViewById(R.id.getLocationList)
        tvGpsLocation = findViewById(R.id.textView)
        /*  fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)*/

       /* database = Room.databaseBuilder(
            applicationContext,
            LocationDatabase::class.java, "LocationDB"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()

        database = Room.databaseBuilder(
            applicationContext,
            LocationDatabase::class.java, "LocationDB"
        ).allowMainThreadQueries().build()*/
        button.setOnClickListener {
            scheduleRepeatingWorkManager()
        }
        button1.setOnClickListener {
            val intent = Intent(this@MainActivity, getLocationActivity::class.java)
            startActivity(intent)
        }
      /*  GlobalScope.launch {
            database.locationdao().deleteAllData()
        }*/
    }
    private fun scheduleRepeatingWorkManager() {
        val serviceIntent = Intent(this, LocationForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        val repeatInterval = 15
        val repeatIntervalTimeUnit = TimeUnit.MINUTES

        val workRequest = PeriodicWorkRequest.Builder(
            MyWorker::class.java,
            repeatInterval.toLong(),
            repeatIntervalTimeUnit
        ).build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }
  /*  private fun scheduleJob() {
    val jobInfo = JobInfo.Builder(123, ComponentName(this,LocationJobService::class.java))
    val job =   jobInfo.setRequiresCharging(false)
        .setMinimumLatency(3)
        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build()

        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        val result = jobScheduler.schedule(job)

        if (result == JobScheduler.RESULT_SUCCESS) {
            Toast.makeText(this, "data inserted successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "somthing went wrong", Toast.LENGTH_SHORT).show()
        }
    }*/






    /* private fun scheduleAlarmManager() {
         val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
         val intent = Intent(this, LocationUpdateReceiver::class.java)
         val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
         val intervalMillis = 1 * 60 * 1000

         val startTimeMillis = System.currentTimeMillis()

         alarmManager.setRepeating(
             AlarmManager.RTC_WAKEUP,
             startTimeMillis,
             intervalMillis.toLong(),
             pendingIntent
         )
     }*/

/*    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(locationRequestInterval.toLong())

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
                      *//*  Log.e("location list",locationList.toString())*//*
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null)
        }
    }*/

  /*  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }*/

}

