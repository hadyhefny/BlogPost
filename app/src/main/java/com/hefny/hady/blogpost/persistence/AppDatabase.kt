package com.hefny.hady.blogpost.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hefny.hady.blogpost.models.AccountProperties
import com.hefny.hady.blogpost.models.AuthToken
import com.hefny.hady.blogpost.models.BlogPost

@Database(entities = [AuthToken::class, AccountProperties::class, BlogPost::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getAuthTokenDao(): AuthTokenDao
    abstract fun getAccountPropertiesDao(): AccountPropertiesDao
    abstract fun getBlogPostDao(): BlogPostDao

    companion object {
        val DATABASE_NAME = "app_db"
    }
}