package com.hefny.hady.blogpost.ui.main.blog.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.hefny.hady.blogpost.persistence.BlogQueryUtils
import com.hefny.hady.blogpost.repository.main.BlogRepository
import com.hefny.hady.blogpost.session.SessionManager
import com.hefny.hady.blogpost.ui.BaseViewModel
import com.hefny.hady.blogpost.ui.DataState
import com.hefny.hady.blogpost.ui.Loading
import com.hefny.hady.blogpost.ui.main.blog.state.BlogStateEvent
import com.hefny.hady.blogpost.ui.main.blog.state.BlogViewState
import com.hefny.hady.blogpost.util.AbsentLiveData
import com.hefny.hady.blogpost.util.PreferenceKeys
import javax.inject.Inject

class BlogViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val blogRepository: BlogRepository,
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
) : BaseViewModel<BlogStateEvent, BlogViewState>() {
    init {
        setBLogFilter(
            sharedPreferences.getString(
                PreferenceKeys.BLOG_FILTER,
                BlogQueryUtils.BLOG_FILTER_DATE_UPDATED
            )
        )
        setBLogOrder(
            sharedPreferences.getString(
                PreferenceKeys.BLOG_ORDER,
                BlogQueryUtils.BLOG_ORDER_ASC
            )!!
        )
        Log.d(
            TAG, "BlogViewModel: ${sharedPreferences.getString(
                PreferenceKeys.BLOG_ORDER,
                BlogQueryUtils.BLOG_ORDER_ASC
            )!! + sharedPreferences.getString(
                PreferenceKeys.BLOG_FILTER,
                BlogQueryUtils.BLOG_FILTER_DATE_UPDATED
            )}"
        )
    }

    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        return when (stateEvent) {
            is BlogStateEvent.BlogSearchEvent -> {
                sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.searchBlogPosts(
                        authToken = authToken,
                        query = getSearchQuery(),
                        filterAndOrder = getOrder() + getFilter(),
                        page = getPage()
                    )
                } ?: AbsentLiveData.create()
            }
            is BlogStateEvent.CheckAuthorOfBlogPostEvent -> {
                sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.isAuthorOfBlogPost(
                        authToken = authToken,
                        slug = getSlug()
                    )
                } ?: AbsentLiveData.create()
            }

            is BlogStateEvent.DeleteBlogPostEvent -> {
                sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.deleteBlogPost(
                        authToken,
                        getBlogPost()
                    )
                } ?: AbsentLiveData.create()
            }

            is BlogStateEvent.None -> {
                object : LiveData<DataState<BlogViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        value = DataState(
                            null,
                            Loading(false),
                            null
                        )
                    }
                }
            }
        }
    }

    override fun initNewViewState(): BlogViewState {
        return BlogViewState()
    }

    fun saveFilterOptions(filter: String, order: String) {
        editor.putString(PreferenceKeys.BLOG_FILTER, filter)
        editor.apply()

        editor.putString(PreferenceKeys.BLOG_ORDER, order)
        editor.apply()
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