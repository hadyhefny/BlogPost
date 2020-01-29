package com.hefny.hady.animalfeed.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hefny.hady.animalfeed.models.AccountProperties
import com.hefny.hady.animalfeed.models.AuthToken

@Database(entities = [AccountProperties::class, AuthToken::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getAccountPropertiesDao(): AccountPropertiesDao
    abstract fun getAuthTokenDao(): AuthTokenDao

    object RoomInstance {
        fun getRoomInstance(context: Context) = Room.databaseBuilder(
            context.applicationContext, AppDatabase::class.java,
            DATABASE_NAME
        )
            .build()
    }

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}