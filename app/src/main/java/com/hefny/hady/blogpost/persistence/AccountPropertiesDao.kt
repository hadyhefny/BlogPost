package com.hefny.hady.blogpost.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hefny.hady.blogpost.models.AccountProperties

@Dao
interface AccountPropertiesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAndReplace(accountProperties: AccountProperties): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAndIgnore(accountProperties: AccountProperties): Long

    @Query("SELECT * FROM account_properties WHERE pk = :pk")
    fun searchByPk(pk: Int): LiveData<AccountProperties>

    @Query("SELECT * FROM account_properties WHERE email = :email")
    fun searchByEmail(email: String): AccountProperties?

    @Query("UPDATE account_properties SET email = :email, username = :username WHERE pk = :pk ")
    fun updateAccountProperties(email: String, username: String, pk: Int)
}