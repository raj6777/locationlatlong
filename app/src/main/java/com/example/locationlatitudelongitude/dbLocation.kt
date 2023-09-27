package com.example.locationlatitudelongitude

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Current_Location")
data class dbLocation(
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    var lat:String,
    var longi:String,
    var date:String
)
