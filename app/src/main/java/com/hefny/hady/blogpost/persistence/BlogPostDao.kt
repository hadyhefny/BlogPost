package com.hefny.hady.blogpost.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hefny.hady.blogpost.models.BlogPost

@Dao
interface BlogPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(blogPost: BlogPost): Long

    @Query("SELECT * FROM blog_post")
    fun searchBlogPosts(): LiveData<List<BlogPost>>
}