package com.hefny.hady.blogpost.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.hefny.hady.blogpost.api.GenericResponse
import com.hefny.hady.blogpost.api.main.OpenApiMainService
import com.hefny.hady.blogpost.api.main.responses.BlogListSearchResponse
import com.hefny.hady.blogpost.models.AuthToken
import com.hefny.hady.blogpost.models.BlogPost
import com.hefny.hady.blogpost.persistence.BlogPostDao
import com.hefny.hady.blogpost.persistence.returnOrderedBlogQuery
import com.hefny.hady.blogpost.repository.JobManager
import com.hefny.hady.blogpost.repository.NetworkBoundResource
import com.hefny.hady.blogpost.session.SessionManager
import com.hefny.hady.blogpost.ui.DataState
import com.hefny.hady.blogpost.ui.main.blog.state.BlogViewState
import com.hefny.hady.blogpost.util.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BlogRepository
@Inject
constructor(
    private val openApiMainService: OpenApiMainService,
    private val blogPostDao: BlogPostDao,
    private val sessionManager: SessionManager
) : JobManager("BlogRepository") {
    private val TAG = "AppDebug"

    fun searchBlogPosts(
        authToken: AuthToken,
        query: String,
        filterAndOrder: String,
        page: Int
    ): LiveData<DataState<BlogViewState>> {
        return object : NetworkBoundResource<BlogListSearchResponse, List<BlogPost>, BlogViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            false,
            true
        ) {
            override suspend fun createCacheRequestAndReturn() {
                withContext(Main) {
                    // finish by viewing the cache
                    result.addSource(loadFromCache()) { viewState ->
                        viewState.blogFields.isQueryInProgress = false
                        if (page * Constants.PAGINATION_PAGE_SIZE > viewState.blogFields.blogList.size) {
                            viewState.blogFields.isQueryExhausted = true
                        }
                        onCompleteJob(DataState.data(viewState))
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BlogListSearchResponse>) {
                val blogPosList = ArrayList<BlogPost>()
                for (blogPost in response.body.results) {
                    blogPosList.add(
                        BlogPost(
                            blogPost.pk,
                            blogPost.title,
                            blogPost.slug,
                            blogPost.body,
                            blogPost.image,
                            DateUtils.convertServerStringDateToLong(blogPost.date_updated),
                            blogPost.username
                        )
                    )
                }
                updateLocalDb(blogPosList)
                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogListSearchResponse>> {
                Log.d(TAG, "BlogRepository, createCall: $filterAndOrder")
                return openApiMainService.getAllBlogPosts(
                    "Token ${authToken.token}",
                    query,
                    filterAndOrder,
                    page
                )
            }

            override fun loadFromCache(): LiveData<BlogViewState> {
                return Transformations.switchMap(
                    blogPostDao.returnOrderedBlogQuery(
                        query = query,
                        filterAndOrder = filterAndOrder,
                        page = page
                    )
                ) {
                    object : LiveData<BlogViewState>() {
                        override fun onActive() {
                            super.onActive()
                            value = BlogViewState(
                                BlogViewState.BlogFields(
                                    blogList = it,
                                    isQueryInProgress = true
                                )
                            )
                        }
                    }
                }
            }

            override suspend fun updateLocalDb(cacheObject: List<BlogPost>?) {
                if (cacheObject != null) {
                    withContext(IO) {
                        for (blogPost in cacheObject) {
                            try {
                                // launch each insert as a separate job to be executed in parallel
                                launch {
                                    Log.d(TAG, "updateLocalDb: inserting blog: $blogPost")
                                    blogPostDao.insert(blogPost)
                                }
                            } catch (e: Exception) {
                                Log.e(
                                    TAG, "updateLocalDb: error updating cache " +
                                            "in blog post with slug: ${blogPost.slug}"
                                )
                            }
                        }
                    }
                }
            }

            override fun setJob(job: Job) {
                addJob("searchBlogPosts", job)
            }
        }.asLiveData()
    }

    fun isAuthorOfBlogPost(
        authToken: AuthToken,
        slug: String
    ): LiveData<DataState<BlogViewState>> {
        return object : NetworkBoundResource<GenericResponse, Any, BlogViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ) {
            // not used in this case
            override suspend fun createCacheRequestAndReturn() {
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                withContext(Main) {
                    Log.d(TAG, "handleApiSuccessResponse: ${response.body}")
                    var isAuthor = false
                    if (response.body.response == SuccessHandling.RESPONSE_HAS_PERMISSION_TO_EDIT) {
                        isAuthor = true
                    }
                    onCompleteJob(
                        DataState.data(
                            data = BlogViewState(
                                viewBlogFields = BlogViewState.ViewBlogFields(
                                    isAuthorOfBlogPost = isAuthor
                                )
                            ),
                            response = null
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService
                    .isAuthorOfBlogPost(
                        "Token ${authToken.token}",
                        slug
                    )
            }

            // not used in this case
            override fun loadFromCache(): LiveData<BlogViewState> {
                return AbsentLiveData.create()
            }

            // not used in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {
            }

            override fun setJob(job: Job) {
                addJob("isAuthorOfBlogPost", job)
            }

        }.asLiveData()
    }
}