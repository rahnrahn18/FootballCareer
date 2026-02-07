package com.championstar.soccer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.championstar.soccer.data.converters.Converters
import com.championstar.soccer.data.dao.BusinessDao
import com.championstar.soccer.data.dao.PlayerDao
import com.championstar.soccer.data.dao.StoryEventDao
import com.championstar.soccer.data.entities.BusinessEntity
import com.championstar.soccer.data.entities.PlayerEntity
import com.championstar.soccer.data.entities.StoryEventEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [PlayerEntity::class, BusinessEntity::class, StoryEventEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao
    abstract fun businessDao(): BusinessDao
    abstract fun storyEventDao(): StoryEventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "soccer_career_database"
                )
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.businessDao())
                }
            }
        }

        suspend fun populateDatabase(businessDao: BusinessDao) {
            // Check if businesses exist, if not insert 50
            if (businessDao.getCount() == 0) {
                val businesses = generateBusinesses()
                businessDao.insertAll(businesses)
            }
        }

        fun generateBusinesses(): List<BusinessEntity> {
            val list = mutableListOf<BusinessEntity>()
            // Generate 50 businesses with varying complexity
            val types = listOf("Retail", "Tech", "Sports", "Real Estate", "Media")
            for (i in 1..50) {
                val type = types.random()
                val cost = i * 10000L // Example cost scaling
                val income = cost / 20 // 5% return
                list.add(BusinessEntity(
                    name = "$type Enterprise $i",
                    type = type,
                    baseCost = cost,
                    baseIncome = income,
                    level = 0
                ))
            }
            return list
        }
    }
}