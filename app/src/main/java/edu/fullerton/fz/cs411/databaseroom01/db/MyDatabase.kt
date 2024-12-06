package edu.fullerton.fz.cs411.databaseroom01.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        Person::class
    ],
    version = 1
)
@TypeConverters(MyTypeConverters::class)
abstract class MyDatabase: RoomDatabase() {
    abstract fun myDao(): MyDataAccessObject
}
