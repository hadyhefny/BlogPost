package com.hefny.hady.blogpost.ui.main.blog

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.bumptech.glide.RequestManager
import com.hefny.hady.blogpost.models.BlogPost
import com.hefny.hady.blogpost.repository.main.BlogRepository
import com.hefny.hady.blogpost.session.SessionManager
import com.hefny.hady.blogpost.ui.BaseViewModel
import com.hefny.hady.blogpost.ui.DataState
import com.hefny.hady.blogpost.ui.main.blog.state.BlogStateEvent
import com.hefny.hady.blogpost.ui.main.blog.state.BlogViewState
import com.hefny.hady.blogpost.util.AbsentLiveData
import javax.inject.Inject

class BlogViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val blogRepository: BlogRepository,
    private val sharedPreferences: SharedPreferences,
    private val requestManager: RequestManager
) : BaseViewModel<BlogStateEvent, BlogViewState>() {
    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        return when (stateEvent) {
            is BlogStateEvent.BlogSearchEvent -> {
                sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.searchBlogPosts(
                        authToken,
                        viewState.value!!.blogFields.searchQuery
                    )
                } ?: AbsentLiveData.create()
            }
            is BlogStateEvent.None -> {
                AbsentLiveData.create()
            }
        }
    }

    override fun initNewViewState(): BlogViewState {
        return BlogViewState()
    }

    fun setQuery(query: String) {
        val update = getCurrentViewStateOrNew()
        update.blogFields.searchQuery = query
        _viewState.value = update
    }

    fun setBlogList(blogList: List<BlogPost>) {
        val update = getCurrentViewStateOrNew()
        update.blogFields.blogList = blogList
        _viewState.value = update
    }

    fun cancelActiveJobs() {
        handlePendingData()
        blogRepository.cancelActiveJobs()
    }

    // hide progressBar
    private fun handlePendingData() {
        setStateEvent(BlogStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}