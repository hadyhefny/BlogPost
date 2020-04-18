package com.hefny.hady.blogpost.ui.main.blog.state

import okhttp3.MultipartBody

sealed class BlogStateEvent {
    class BlogSearchEvent : BlogStateEvent()
    class CheckAuthorOfBlogPostEvent : BlogStateEvent()
    class DeleteBlogPostEvent : BlogStateEvent()
    data class UpdatedBlogPostEvent(
        val title: String,
        val body: String,
        val image: MultipartBody.Part?
    ) : BlogStateEvent()

    class None : BlogStateEvent()
}