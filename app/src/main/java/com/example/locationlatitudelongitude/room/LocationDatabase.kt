package com.example.locationlatitudelongitude.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.locationlatitudelongitude.dbLocation

@Database(entities=[dbLocation::class],version=2, exportSchema = false)
abstract class LocationDatabase:RoomDatabase() {
    abstract fun locationdao():LocationDao
    companion object {
        private var INSTANCE: LocationDatabase? = null

        fun getInstance(context: Context): LocationDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    LocationDatabase::class.java,
                    "LocationDB"
                ).build()
            }
            return INSTANCE!!
        }
    }

}