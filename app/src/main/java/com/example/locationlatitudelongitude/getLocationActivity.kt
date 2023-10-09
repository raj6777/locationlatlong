package com.example.locationlatitudelongitude

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.locationlatitudelongitude.room.adapter.LocationAdapter
import com.example.locationlatitudelongitude.room.LocationDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


class getLocationActivity : AppCompatActivity() {
    lateinit var rv:RecyclerView
    lateinit var database: LocationDatabase
    var locationList = ArrayList<dbLocation>()
    lateinit var button:Button
     val REQUEST_WRITE_EXTERNAL_STORAGE = 111
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_location)
        rv = findViewById(R.id.recycler)
        button=findViewById(R.id.screenshot)
        var REQUEST_WRITE_EXTERNAL_STORAGE=111
        database = Room.databaseBuilder(
            applicationContext,
            LocationDatabase::class.java, "LocationDB"
        ).build()

        button.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                val viewToCapture: View = findViewById(R.id.root) // Replace with your actual View
                val screenshotBitmap: Bitmap? = getScreenShot(viewToCapture)

                val fileName = "screenshot.png" // Replace with your desired file name
                store(screenshotBitmap!!, fileName)
            }else{
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_EXTERNAL_STORAGE)
            }
        }

        GlobalScope.launch {
            var arrayList=database.locationdao().getDataOfLocation() as ArrayList<dbLocation>
            for (i in(0..arrayList.lastIndex).reversed()) {
                locationList.add(arrayList[i])
            }

        }
        showProductData()
    }

    fun showProductData() {
        val adapter = LocationAdapter(locationList)
        rv.layoutManager = LinearLayoutManager(this@getLocationActivity)
        rv.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    fun getScreenShot(view: View): Bitmap? {
        val screenView = view.rootView
        screenView.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(screenView.drawingCache)
        screenView.isDrawingCacheEnabled = false
        return bitmap
    }

    fun store(bm: Bitmap, fileName: String) {
        val dirPath = this@getLocationActivity.filesDir
        val file = File(dirPath, fileName)
        try {
            val fOut = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut)
            fOut.flush()
            fOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val viewToCapture: View = findViewById(R.id.root)
                val screenshotBitmap: Bitmap? = getScreenShot(viewToCapture)
                val fileName = "screenshot.png"
                store(screenshotBitmap!!, fileName)
            } else {
                Toast.makeText(this, "something is wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }
    }