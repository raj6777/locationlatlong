package com.example.locationlatitudelongitude

import LocationForegroundService
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Database
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    /* lateinit var fusedLocationClient: FusedLocationProviderClient*/
    /*  lateinit var database: LocationDatabase*/
    private lateinit var tvGpsLocation: TextView
    private var REQUEST_CHECK_SETTING=123
    private val PICK_IMAGE_REQUEST = 234
    private var filePathimage: Uri? = null
    lateinit var imageView:ImageView
    private var storageReference: StorageReference? = null
    private var database: FirebaseDatabase? = null

    private val locationPermissionCode = 2
    private val locationRequestInterval = 600000// 10 minutes in millisecondse
    @SuppressLint("HardwareIds")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Location App"

        val serviceIntent = Intent(this, LocationForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)

        val button: Button = findViewById(R.id.getLocation)
        val button1: Button = findViewById(R.id.getLocationList)
        val btnchoosefile:Button=findViewById(R.id.choosefile)
        val uploadfile:Button=findViewById(R.id.uploadfile)
        imageView=findViewById(R.id.imageView)
        tvGpsLocation = findViewById(R.id.textView)
        database=FirebaseDatabase.getInstance()

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
        btnchoosefile.setOnClickListener {
            showFileChooser()
        }
        uploadfile.setOnClickListener{
            Uploadfile()
        }
        /*  GlobalScope.launch {
              database.locationdao().deleteAllData()
          }*/
    }
    private fun scheduleRepeatingWorkManager() {
        val mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val mGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasLocationPermission = ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasLocationPermission) {
            if (mGPS) {
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
                saveLogsToTxtFile()
            }else{
                checkLocationSetting()
            }
        }else {
            locationPermissionRequest.launch(arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    /*  fun showGPSNotEnabledDialog(context: Context) {
          AlertDialog.Builder(context)
              .setTitle(context.getString(R.string.enable_gps))
              .setMessage(context.getString(R.string.required_for_this_app))
              .setCancelable(false)
              .setPositiveButton(context.getString(R.string.enable_now)) { _, _ ->
                  context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
              }
              .setNegativeButton(context.getString(R.string.dismiss)){ _, _ ->
                  AlertDialog.Builder(context).show().dismiss()
              }
              .show()
      }*/

    private fun checkLocationSetting()
    {
        val locationRequest = LocationRequest.Builder(1000L).setPriority(Priority.PRIORITY_HIGH_ACCURACY).build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(applicationContext)
            .checkLocationSettings(builder.build())

        result.addOnCompleteListener {
            try{
                val response: LocationSettingsResponse = it.getResult(ApiException::class.java)
                Toast.makeText(this@MainActivity, "GPS is On", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "checkSetting: GPS On")
            }catch(e:ApiException){

                when(e.statusCode){
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->{
                        val resolvableApiException = e as ResolvableApiException
                        resolvableApiException.startResolutionForResult(this@MainActivity, REQUEST_CHECK_SETTING)
                        Log.d(TAG, "checkSetting: RESOLUTION_REQUIRED")
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }
    private fun showFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    fun getFileExtension(uri: Uri?): String? {
        val cR = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri!!))
    }
@SuppressLint("HardwareIds")
private fun Uploadfile() {
    if (filePathimage != null) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading")
        progressDialog.show()

        // Set the folder path inside Firebase Storage
        val folderPath =
            (Settings.Secure.getString(contentResolver!!, Settings.Secure.ANDROID_ID) + System.currentTimeMillis()) + "/"

        storageReference = FirebaseStorage.getInstance().reference

        val sRef = storageReference!!.child(folderPath +
                (Settings.Secure.getString(contentResolver!!, Settings.Secure.ANDROID_ID) + System.currentTimeMillis()).toString() +
                "." + getFileExtension(filePathimage))

        val mDatabase=database?.getReference()
        sRef.putFile(filePathimage!!)
            .addOnSuccessListener { taskSnapshot ->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "File Uploaded", Toast.LENGTH_LONG).show()


                // Get the download URL of the uploaded image
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val imageUrl = downloadUrl.toString()
                    Log.e("url",imageUrl.toString())

                    // Create an instance of your data class
                    val upload = UploadedFile(imageUrl)

                    // Push the data to the Firebase Realtime Database
                    Log.e("uplodaded id",mDatabase!!.push().key.toString())
                    val uploadId: String = mDatabase!!.push().key ?: ""
                    mDatabase.child(uploadId).setValue(upload)


                }
            }
            .addOnFailureListener { exception ->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_LONG).show()
            }
            .addOnProgressListener { taskSnapshot -> // Display the upload progress
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                progressDialog.setMessage("Uploaded " + progress.toInt() + "%...")
            }
    } else {
        Toast.makeText(applicationContext, "No files are selected", Toast.LENGTH_LONG).show()
    }
}    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode)
        {
            PICK_IMAGE_REQUEST->{
                when(resultCode){
                   Activity.RESULT_OK->{
                       filePathimage = data!!.getData();
                       try {
                           val source = ImageDecoder.createSource(this.contentResolver, filePathimage!!)
                           val bitmap = ImageDecoder.decodeBitmap(source)
                           imageView.setImageBitmap(bitmap)
                       } catch (e: IOException) {
                           e.printStackTrace()
                       }
                   }
                }
            }
            REQUEST_CHECK_SETTING ->{
                when(resultCode){
                    Activity.RESULT_OK->{
                        scheduleRepeatingWorkManager()
                        Toast.makeText(this@MainActivity, "GPS is Turned on", Toast.LENGTH_SHORT).show()
                    }
                    Activity.RESULT_CANCELED ->{
                        Toast.makeText(this@MainActivity, "GPS is Required to use this app", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                checkLocationSetting()
            }
            permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
            } else -> {
        }
        }
    }
    private fun readLogcat(): String {
        val process = Runtime.getRuntime().exec("logcat -d")
        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
        val logcatStringBuilder = StringBuilder()
        var line: String?

        while (bufferedReader.readLine().also { line = it } != null) {
            logcatStringBuilder.append(line).append("\n")
        }

        return logcatStringBuilder.toString()
    }
    private fun saveLogsToTxtFile() {
        val coroutineCallLogger = CoroutineScope(Dispatchers.IO)
        coroutineCallLogger.launch {
            async {
                val fileDirectory = this@MainActivity.filesDir

                val filePath = File(fileDirectory, "New_Log_File.txt")

                if (filePath.exists()) {
                    filePath.delete()
                }
                runCatching {
                    //Creating new txt file.
                    filePath.createNewFile()
                    //Writing my logs to txt file.
                    filePath.appendText(
                        readLogcat().toString()
                    )
                }
            }
        }
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

