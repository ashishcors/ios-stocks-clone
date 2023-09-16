package com.example.dummystocks.data.local.converter

import androidx.room.TypeConverter

object ListTypeConverters {

  @TypeConverter
  fun fromStringList(list: List<String>?): String? {
    return list?.joinToString(",")
  }

  @TypeConverter
  fun toStringList(data: String?): List<String>? {
    return data?.split(",")?.map { it.trim() }
  }

  @TypeConverter
  fun fromDoubleList(list: List<Double>?): String? {
    return list?.joinToString(",")
  }

  @TypeConverter
  fun toDoubleList(data: String?): List<Double>? {
    return data?.split(",")?.map { it.trim().toDoubleOrNull() ?: 0.0 }
  }
}


