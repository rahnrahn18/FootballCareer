package com.championstar.soccer.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: String?): List<String>? {
        if (value == null) return null
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>?): String? {
        if (list == null) return null
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromMap(value: String?): Map<String, Int>? {
        if (value == null) return null
        val mapType = object : TypeToken<Map<String, Int>>() {}.type
        return gson.fromJson(value, mapType)
    }

    @TypeConverter
    fun mapToString(map: Map<String, Int>?): String? {
        if (map == null) return null
        return gson.toJson(map)
    }
}