package com.hefny.hady.blogpost.api.main.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hefny.hady.blogpost.models.BlogPost

data class BlogListSearchResponse(
    @SerializedName("results")
    @Expose
    var results: List<BlogSearchResponse>,

    @SerializedName("detail")
    @Expose
    var detail: String
) {
    fun toList(): List<BlogPost> {
        val blogPostList: ArrayList<BlogPost> = ArrayList()
        for (blogPostResponse in results) {
            blogPostList.add(
                blogPostResponse.toBlogPost()
            )
        }
        return blogPostList
    }
}