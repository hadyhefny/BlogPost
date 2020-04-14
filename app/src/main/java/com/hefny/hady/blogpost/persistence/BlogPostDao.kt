package com.hefny.hady.blogpost.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hefny.hady.blogpost.models.BlogPost
import com.hefny.hady.blogpost.util.Constants.Companion.PAGINATION_PAGE_SIZE

@Dao
interface BlogPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(blogPost: BlogPost): Long

    @Query(
        """
        SELECT * FROM blog_post
         WHERE title LIKE '%' || :query || '%' 
         OR body LIKE '%' || :query || '%'
         OR username LIKE '%' || :query || '&'
         LIMIT (:page * :pageSize)
    """
    )
    fun searchBlogPosts(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): LiveData<List<BlogPost>>
}