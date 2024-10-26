package com.ashfaque.demopoi.roomdb

import androidx.room.TypeConverter
import java.util.Date

class Converters {

    @TypeConverter
    fun fromDateToLong(value: Date): Long {
        return value.time
    }

    @TypeConverter
    fun formLongToDate(value: Long): Date {
        return Date(value)
    }
}