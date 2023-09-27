package com.example.locationlatitudelongitude.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.locationlatitudelongitude.dbLocation

@Dao
interface LocationDao {
    @Insert
    suspend fun insertLocation(dbLocation: dbLocation)

    @Query("SELECT * FROM Current_Location")
    suspend fun getDataOfLocation():List<dbLocation>

    @Query("DELETE FROM Current_Location")
    suspend fun deleteAllData()
}