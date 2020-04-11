package com.hefny.hady.blogpost.api.main.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BlogListSearchResponse(
    @SerializedName("results")
    @Expose
    var results: List<BlogSearchResponse>,

    @SerializedName("detail")
    @Expose
    var detail: String
)