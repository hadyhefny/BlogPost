package com.hefny.hady.blogpost.ui.main.blog.state

import com.hefny.hady.blogpost.models.BlogPost

data class BlogViewState(
    // BlogFragment vars
    var blogFields: BlogFields = BlogFields()
) {
    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList(),
        var searchQuery: String = ""
    )
}