package com.hefny.hady.blogpost.ui.main.create_blog.state

import com.hefny.hady.blogpost.util.StateEvent
import okhttp3.MultipartBody

sealed class CreateBlogStateEvent : StateEvent {
    data class CreateNewBlogEvent(
        val title: String,
        val body: String,
        val image: MultipartBody.Part
    ) : CreateBlogStateEvent() {
        override fun errorInfo(): String {
            return "error creating blog post"
        }

        override fun toString(): String {
            return "CreateNewBlogEvent"
        }
    }

    class None : CreateBlogStateEvent() {
        override fun errorInfo(): String {
            return "none"
        }

        override fun toString(): String {
            return "None"
        }
    }
}