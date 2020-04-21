package com.hefny.hady.blogpost.ui.main.blog.viewmodel

import android.util.Log
import com.hefny.hady.blogpost.ui.main.blog.state.BlogStateEvent
import com.hefny.hady.blogpost.ui.main.blog.state.BlogViewState

fun BlogViewModel.resetPage() {
    val update = getCurrentViewStateOrNew()
    update.blogFields.page = 1
    setViewState(update)
}

fun BlogViewModel.refreshFromCache() {
    setQueryInProgress(true)
    setQueryExhausted(false)
    setStateEvent(BlogStateEvent.RestoreBlogListFromCache())
}

fun BlogViewModel.loadFirstPage() {
    setQueryExhausted(false)
    setQueryInProgress(true)
    resetPage()
    setStateEvent(BlogStateEvent.BlogSearchEvent())
}

fun BlogViewModel.incrementPageNumber() {
    val update = getCurrentViewStateOrNew()
    val page = update.copy().blogFields.page
    update.blogFields.page = page + 1
    setViewState(update)
}

fun BlogViewModel.loadNextPage() {
    if (!getIsQueryInProgress()
        && !getIsQueryExhausted()
    ) {
        Log.d(TAG, "BlogViewModel: Attempting to load next page...")
        incrementPageNumber()
        setQueryInProgress(true)
        setStateEvent(BlogStateEvent.BlogSearchEvent())
    }
}

fun BlogViewModel.handleIncomingBlogListData(blogViewState: BlogViewState) {
    setQueryExhausted(blogViewState.blogFields.isQueryExhausted)
    setQueryInProgress(blogViewState.blogFields.isQueryInProgress)
    setBlogListData(blogViewState.blogFields.blogList)
}