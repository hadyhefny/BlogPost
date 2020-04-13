package com.hefny.hady.blogpost.ui.main.blog.state

sealed class BlogStateEvent {
    class BlogSearchEvent : BlogStateEvent()
    class CheckAuthorOfBlogPostEvent : BlogStateEvent()
    class None : BlogStateEvent()
}