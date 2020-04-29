package com.hefny.hady.blogpost.ui.main.blog.viewmodel

import android.content.SharedPreferences
import android.util.Log
import com.hefny.hady.blogpost.di.main.MainScope
import com.hefny.hady.blogpost.persistence.BlogQueryUtils
import com.hefny.hady.blogpost.repository.main.BlogRepositoryImpl
import com.hefny.hady.blogpost.session.SessionManager
import com.hefny.hady.blogpost.ui.BaseViewModel
import com.hefny.hady.blogpost.ui.main.blog.state.BlogStateEvent
import com.hefny.hady.blogpost.ui.main.blog.state.BlogViewState
import com.hefny.hady.blogpost.util.*
import com.hefny.hady.blogpost.util.ErrorHandling.Companion.INVALID_STATE_EVENT
import com.hefny.hady.blogpost.util.PreferenceKeys.Companion.BLOG_FILTER
import com.hefny.hady.blogpost.util.PreferenceKeys.Companion.BLOG_ORDER
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@MainScope
class BlogViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val blogRepository: BlogRepositoryImpl,
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
) : BaseViewModel<BlogViewState>() {

    init {
        setBlogFilter(
            sharedPreferences.getString(
                BLOG_FILTER,
                BlogQueryUtils.BLOG_FILTER_DATE_UPDATED
            )
        )
        setBlogOrder(
            sharedPreferences.getString(
                BLOG_ORDER,
                BlogQueryUtils.BLOG_ORDER_DESC
            )
        )
    }

    override fun handleNewData(data: BlogViewState) {

        data.blogFields.let { blogFields ->

            blogFields.blogList?.let { blogList ->
                handleIncomingBlogListData(data)
            }

            blogFields.isQueryExhausted?.let { isQueryExhausted ->
                setQueryExhausted(isQueryExhausted)
            }

        }

        data.viewBlogFields.let { viewBlogFields ->

            viewBlogFields.blogPost?.let { blogPost ->
                setBlogPost(blogPost)
            }

            viewBlogFields.isAuthorOfBlogPost?.let { isAuthor ->
                setIsAuthorOfBlogPost(isAuthor)
            }
        }

        data.updatedBlogFields.let { updatedBlogFields ->

            updatedBlogFields.updatedImageUri?.let { uri ->
                setUpdatedUri(uri)
            }

            updatedBlogFields.updatedBlogTitle?.let { title ->
                setUpdatedTitle(title)
            }

            updatedBlogFields.updatedBlogBody?.let { body ->
                setUpdatedBody(body)
            }
        }
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        Log.d(TAG, "setStateEvent: CALLED")
        if (!isJobAlreadyActive(stateEvent)) {
            Log.d(TAG, "setStateEvent: ${!isJobAlreadyActive(stateEvent)}, CALLED")
            sessionManager.cachedToken.value?.let { authToken ->
                val job: Flow<DataState<BlogViewState>> = when (stateEvent) {

                    is BlogStateEvent.BlogSearchEvent -> {
                        if (stateEvent.clearLayoutManagerState) {
                            clearLayoutManagerState()
                        }
                        blogRepository.searchBlogPosts(
                            stateEvent = stateEvent,
                            authToken = authToken,
                            query = getSearchQuery(),
                            filterAndOrder = getOrder() + getFilter(),
                            page = getPage()
                        )
                    }

                    is BlogStateEvent.CheckAuthorOfBlogPostEvent -> {
                        blogRepository.isAuthorOfBlogPost(
                            stateEvent = stateEvent,
                            authToken = authToken,
                            slug = getSlug()
                        )
                    }
                    is BlogStateEvent.DeleteBlogPostEvent -> {
                        blogRepository.deleteBlogPost(
                            stateEvent = stateEvent,
                            authToken = authToken,
                            blogPost = getBlogPost()
                        )
                    }
                    is BlogStateEvent.UpdateBlogPostEvent -> {
                        Log.d(TAG, "setStateEvent: UpdateBlogPost CALLED")
                        val title = RequestBody.create(
                            MediaType.parse("text/plain"),
                            stateEvent.title
                        )
                        val body = RequestBody.create(
                            MediaType.parse("text/plain"),
                            stateEvent.body
                        )
                        blogRepository.updateBlogPost(
                            stateEvent = stateEvent,
                            authToken = authToken,
                            slug = getSlug(),
                            title = title,
                            body = body,
                            image = stateEvent.image
                        )
                    }
                    else -> {
                        flow {
                            emit(
                                DataState.error<BlogViewState>(
                                    response = Response(
                                        message = INVALID_STATE_EVENT,
                                        uiComponentType = UIComponentType.None(),
                                        messageType = MessageType.Error()
                                    ),
                                    stateEvent = stateEvent
                                )
                            )
                        }
                    }
                }
                launchJob(stateEvent, job)
            }
        }
    }

    override fun initNewViewState(): BlogViewState {
        return BlogViewState()
    }

    fun saveFilterOptions(filter: String, order: String) {
        editor.putString(BLOG_FILTER, filter)
        editor.apply()
        editor.putString(BLOG_ORDER, order)
        editor.apply()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}