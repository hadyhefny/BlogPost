package com.hefny.hady.blogpost.ui.main.blog.viewmodel

import android.util.Log
import com.hefny.hady.blogpost.ui.main.blog.state.BlogStateEvent
import com.hefny.hady.blogpost.ui.main.blog.state.BlogViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.resetPage() {
    val update = getCurrentViewStateOrNew()
    update.blogFields.page = 1
    setViewState(update)
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.refreshFromCache() {
    if (!isJobAlreadyActive(BlogStateEvent.BlogSearchEvent())) {
        setQueryExhausted(false)
        setStateEvent(BlogStateEvent.BlogSearchEvent(false))
    }
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.loadFirstPage() {
    if (!isJobAlreadyActive(BlogStateEvent.BlogSearchEvent())) {
        setQueryExhausted(false)
        resetPage()
        setStateEvent(BlogStateEvent.BlogSearchEvent())
        Log.e(TAG, "BlogViewModel: loadFirstPage: ${viewState.value!!.blogFields.searchQuery}")
    }
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
private fun BlogViewModel.incrementPageNumber() {
    val update = getCurrentViewStateOrNew()
    val page = update.copy().blogFields.page ?: 1
    update.blogFields.page = page.plus(1)
    setViewState(update)
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.nextPage() {
    if (!isJobAlreadyActive(BlogStateEvent.BlogSearchEvent())
        && !viewState.value!!.blogFields.isQueryExhausted!!
    ) {
        Log.d(TAG, "BlogViewModel: Attempting to load next page...")
        incrementPageNumber()
        setStateEvent(BlogStateEvent.BlogSearchEvent())
    }
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.handleIncomingBlogListData(viewState: BlogViewState) {
    viewState.blogFields.let { blogFields ->
        blogFields.blogList?.let { setBlogListData(it) }
    }
}