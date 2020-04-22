package com.hefny.hady.blogpost.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.hefny.hady.blogpost.api.GenericResponse
import com.hefny.hady.blogpost.api.main.OpenApiMainService
import com.hefny.hady.blogpost.api.main.responses.BlogCreateUpdateResponse
import com.hefny.hady.blogpost.api.main.responses.BlogListSearchResponse
import com.hefny.hady.blogpost.di.main.MainScope
import com.hefny.hady.blogpost.models.AuthToken
import com.hefny.hady.blogpost.models.BlogPost
import com.hefny.hady.blogpost.persistence.BlogPostDao
import com.hefny.hady.blogpost.persistence.returnOrderedBlogQuery
import com.hefny.hady.blogpost.repository.JobManager
import com.hefny.hady.blogpost.repository.NetworkBoundResource
import com.hefny.hady.blogpost.session.SessionManager
import com.hefny.hady.blogpost.ui.DataState
import com.hefny.hady.blogpost.ui.Response
import com.hefny.hady.blogpost.ui.ResponseType
import com.hefny.hady.blogpost.ui.main.blog.state.BlogViewState
import com.hefny.hady.blogpost.util.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@MainScope
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

    fun restoreBlogListFromCache(
        query: String,
        filterAndOrder: String,
        page: Int
    ): LiveData<DataState<BlogViewState>> {
        return object : NetworkBoundResource<BlogListSearchResponse, List<BlogPost>, BlogViewState>(
            sessionManager.isConnectedToTheInternet(),
            false,
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

            // not used in this case
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BlogListSearchResponse>) {
            }

            // not used in this case
            override fun createCall(): LiveData<GenericApiResponse<BlogListSearchResponse>> {
                return AbsentLiveData.create()
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

            // not used in this case
            override suspend fun updateLocalDb(cacheObject: List<BlogPost>?) {
            }

            override fun setJob(job: Job) {
                addJob("restoreBlogListFromCache", job)
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

    fun deleteBlogPost(
        authToken: AuthToken,
        blogPost: BlogPost
    ): LiveData<DataState<BlogViewState>> {
        return object : NetworkBoundResource<GenericResponse, BlogPost, BlogViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ) {
            // not used in this case
            override suspend fun createCacheRequestAndReturn() {
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                if (response.body.response == SuccessHandling.SUCCESS_BLOG_DELETED) {
                    updateLocalDb(blogPost)
                } else {
                    onCompleteJob(
                        DataState.error(
                            Response(
                                message = ErrorHandling.ERROR_UNKNOWN,
                                responseType = ResponseType.Dialog()
                            )
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.deleteBlogPost(
                    "Token ${authToken.token}",
                    blogPost.slug
                )
            }

            // not used in this case
            override fun loadFromCache(): LiveData<BlogViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: BlogPost?) {
                cacheObject?.let { blogPost ->
                    blogPostDao.deleteBlogPost(blogPost)
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(
                                SuccessHandling.SUCCESS_BLOG_DELETED, ResponseType.Toast()
                            )
                        )
                    )
                }
            }

            override fun setJob(job: Job) {
                addJob("deleteBlogPost", job)
            }

        }.asLiveData()
    }

    fun updateBlogFields(
        authToken: AuthToken,
        slug: String,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?
    ): LiveData<DataState<BlogViewState>> {
        return object : NetworkBoundResource<BlogCreateUpdateResponse, BlogPost, BlogViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ) {
            // not used in this case
            override suspend fun createCacheRequestAndReturn() {
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BlogCreateUpdateResponse>) {
                val updatedBlogPost = BlogPost(
                    response.body.pk,
                    response.body.title,
                    response.body.slug,
                    response.body.body,
                    response.body.image,
                    DateUtils.convertServerStringDateToLong(response.body.date_updated),
                    response.body.username
                )
                updateLocalDb(updatedBlogPost)
                withContext(Main) {
                    onCompleteJob(
                        DataState.data(
                            data = BlogViewState(
                                viewBlogFields = BlogViewState.ViewBlogFields(updatedBlogPost)
                            ),
                            response = Response(
                                message = response.body.response,
                                responseType = ResponseType.Toast()
                            )
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogCreateUpdateResponse>> {
                return openApiMainService.updateBlogPost(
                    "Token ${authToken.token}",
                    slug,
                    title,
                    body,
                    image
                )
            }

            // not used in this case
            override fun loadFromCache(): LiveData<BlogViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: BlogPost?) {
                cacheObject?.let { blogPost ->
                    blogPostDao.updateBlogPost(
                        blogPost.pk,
                        blogPost.title,
                        blogPost.body,
                        blogPost.image
                    )
                }
            }

            override fun setJob(job: Job) {
                addJob("updateBlogFields", job)
            }
        }.asLiveData()
    }
}