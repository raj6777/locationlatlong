package com.example.locationlatitudelongitude

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.locationlatitudelongitude.room.adapter.RetriveDataAdapter


class RetriveActivity : AppCompatActivity() {
    lateinit var imagelist:ArrayList<String>
    lateinit var rv:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrive)
        imagelist = intent.getSerializableExtra("key") as ArrayList<String>
        Log.e("retrive activity",imagelist.toString())
        rv=findViewById(R.id.rvRetrive)
        setrecyclerview()
    }

    fun setrecyclerview(){
        val adapter = RetriveDataAdapter(imagelist)
        rv.layoutManager = LinearLayoutManager(this@RetriveActivity)
        rv.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}