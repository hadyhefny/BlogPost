package com.hefny.hady.blogpost.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hefny.hady.blogpost.models.AuthToken

@Dao
interface AuthTokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(authToken: AuthToken): Long

    @Query("UPDATE auth_token SET token = NULL WHERE account_pk = :pk")
    fun nullifyToken(pk: Int): Int

    @Query("SELECT * FROM auth_token WHERE account_pk = :pk")
    fun searchByPk(pk: Int): AuthToken?
}