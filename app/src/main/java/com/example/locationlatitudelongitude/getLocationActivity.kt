package com.example.locationlatitudelongitude

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.locationlatitudelongitude.adapter.LocationAdapter
import com.example.locationlatitudelongitude.room.LocationDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale

class getLocationActivity : AppCompatActivity() {
    lateinit var rv:RecyclerView
    lateinit var database: LocationDatabase
    var locationList = ArrayList<dbLocation>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_location)
        rv = findViewById(R.id.recycler)
        database = Room.databaseBuilder(
            applicationContext,
            LocationDatabase::class.java, "LocationDB"
        ).build()

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
    }