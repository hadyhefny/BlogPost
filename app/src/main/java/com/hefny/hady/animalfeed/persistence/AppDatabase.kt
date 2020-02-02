package com.hefny.hady.animalfeed.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hefny.hady.animalfeed.models.AccountProperties
import com.hefny.hady.animalfeed.models.AuthToken

@Database(entities = [AuthToken::class, AccountProperties::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getAuthTokenDao(): AuthTokenDao
    abstract fun getAccountPropertiesDao(): AccountPropertiesDao

    companion object {
        val DATABASE_NAME = "app_db"
    }
}