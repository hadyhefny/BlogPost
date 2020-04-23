package com.hefny.hady.blogpost.ui.main.blog.state

import com.hefny.hady.blogpost.util.StateEvent
import okhttp3.MultipartBody

sealed class BlogStateEvent : StateEvent {
    data class BlogSearchEvent(
        val clearLayoutManagerState: Boolean = true
    ) : BlogStateEvent() {
        override fun errorInfo(): String {
            return "error searching for blog posts"
        }

        override fun toString(): String {
            return "BlogSearchEvent"
        }
    }

    class RestoreBlogListFromCache : BlogStateEvent() {
        override fun errorInfo(): String {
            return "failed to restore blog list from cache"
        }

        override fun toString(): String {
            return "RestoreBlogListFromCache"
        }
    }

    class CheckAuthorOfBlogPostEvent : BlogStateEvent() {
        override fun errorInfo(): String {
            return "error checking author of blog post"
        }

        override fun toString(): String {
            return "CheckAuthorOfBlogPostEvent"
        }
    }

    class DeleteBlogPostEvent : BlogStateEvent() {
        override fun errorInfo(): String {
            return "error deleting blog post"
        }

        override fun toString(): String {
            return "DeleteBlogPostEvent"
        }
    }

    data class UpdateBlogPostEvent(
        val title: String,
        val body: String,
        val image: MultipartBody.Part?
    ) : BlogStateEvent() {
        override fun errorInfo(): String {
            return "error  updating blog post"
        }

        override fun toString(): String {
            return "UpdateBlogPostEvent"
        }
    }

    class None : BlogStateEvent() {
        override fun errorInfo(): String {
            return "none"
        }

        override fun toString(): String {
            return "None"
        }
    }
}