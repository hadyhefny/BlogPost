package com.hefny.hady.blogpost.persistence

import androidx.lifecycle.LiveData
import com.hefny.hady.blogpost.models.BlogPost

class BlogQueryUtils {
    companion object {
        const val BLOG_ORDER_ASC: String = ""
        const val BLOG_ORDER_DESC: String = "-"
        const val BLOG_FILTER_USERNAME = "username"
        const val BLOG_FILTER_DATE_UPDATED = "date_updated"
        val ORDER_BY_ASC_DATE_UPDATED = BLOG_ORDER_ASC + BLOG_FILTER_DATE_UPDATED
        val ORDER_BY_DESC_DATE_UPDATED = BLOG_ORDER_DESC + BLOG_FILTER_DATE_UPDATED
        val ORDER_BY_ASC_USERNAME = BLOG_ORDER_ASC + BLOG_FILTER_USERNAME
        val ORDER_BY_DESC_USERNAME = BLOG_ORDER_DESC + BLOG_FILTER_USERNAME
    }
}

fun BlogPostDao.returnOrderedBlogQuery(
    query: String,
    filterAndOrder: String,
    page: Int
): LiveData<List<BlogPost>> {
    return when {
        filterAndOrder.contains(BlogQueryUtils.ORDER_BY_ASC_DATE_UPDATED) -> {
            searchBlogPostsOrderByDateASC(
                query = query,
                page = page
            )
        }
        filterAndOrder.contains(BlogQueryUtils.ORDER_BY_DESC_DATE_UPDATED) -> {
            searchBlogPostsOrderByDateDESC(
                query = query,
                page = page
            )
        }
        filterAndOrder.contains(BlogQueryUtils.ORDER_BY_ASC_USERNAME) -> {
            searchBlogPostsOrderByAuthorASC(
                query = query,
                page = page
            )
        }
        filterAndOrder.contains(BlogQueryUtils.ORDER_BY_DESC_USERNAME) -> {
            searchBlogPostsOrderByAuthorDESC(
                query = query,
                page = page
            )
        }
        else -> {
            searchBlogPostsOrderByDateDESC(
                query = query,
                page = page
            )
        }
    }
}