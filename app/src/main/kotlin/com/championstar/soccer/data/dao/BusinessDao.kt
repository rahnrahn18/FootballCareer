package com.championstar.soccer.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.championstar.soccer.data.entities.BusinessEntity

@Dao
interface BusinessDao {
    @Query("SELECT * FROM business_table ORDER BY baseCost ASC")
    fun getAllBusinesses(): LiveData<List<BusinessEntity>>

    @Query("SELECT * FROM business_table ORDER BY baseCost ASC")
    suspend fun getAllBusinessesSync(): List<BusinessEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(businesses: List<BusinessEntity>)

    @Update
    suspend fun update(business: BusinessEntity)

    @Query("SELECT COUNT(*) FROM business_table")
    suspend fun getCount(): Int
}