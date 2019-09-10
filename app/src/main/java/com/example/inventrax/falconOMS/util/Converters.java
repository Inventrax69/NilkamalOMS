package com.example.inventrax.falconOMS.util;

import android.arch.persistence.room.TypeConverter;

import java.sql.Date;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }


    @TypeConverter
    public static Date stringTimeToDate(String value) {
        java.sql.Date ts = java.sql.Date.valueOf(value);
        return ts;
    }
}