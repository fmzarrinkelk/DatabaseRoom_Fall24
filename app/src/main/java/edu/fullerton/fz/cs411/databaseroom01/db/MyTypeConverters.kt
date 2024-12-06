package edu.fullerton.fz.cs411.databaseroom01.db

import androidx.room.TypeConverter
import java.util.UUID

class MyTypeConverters {

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid.toString()
    }

}