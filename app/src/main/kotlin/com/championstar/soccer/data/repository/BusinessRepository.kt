package com.championstar.soccer.data.repository

import androidx.lifecycle.LiveData
import com.championstar.soccer.data.dao.BusinessDao
import com.championstar.soccer.data.entities.BusinessEntity

class BusinessRepository(private val businessDao: BusinessDao) {
    val allBusinesses: LiveData<List<BusinessEntity>> = businessDao.getAllBusinesses()

    suspend fun getAllBusinessesSync(): List<BusinessEntity> = businessDao.getAllBusinessesSync()

    suspend fun update(business: BusinessEntity) {
        businessDao.update(business)
    }

    suspend fun getCount(): Int = businessDao.getCount()
}