package com.hefny.hady.blogpost.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.hefny.hady.blogpost.api.main.OpenApiMainService
import com.hefny.hady.blogpost.api.main.responses.BlogListSearchResponse
import com.hefny.hady.blogpost.models.AuthToken
import com.hefny.hady.blogpost.models.BlogPost
import com.hefny.hady.blogpost.persistence.BlogPostDao
import com.hefny.hady.blogpost.repository.JobManager
import com.hefny.hady.blogpost.repository.NetworkBoundResource
import com.hefny.hady.blogpost.session.SessionManager
import com.hefny.hady.blogpost.ui.DataState
import com.hefny.hady.blogpost.ui.main.blog.state.BlogViewState
import com.hefny.hady.blogpost.util.ApiSuccessResponse
import com.hefny.hady.blogpost.util.DateUtils
import com.hefny.hady.blogpost.util.GenericApiResponse
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
        query: String
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
                return openApiMainService.getAllBlogPosts(
                    "Token ${authToken.token}",
                    query
                )
            }

            override fun loadFromCache(): LiveData<BlogViewState> {
                return Transformations.switchMap(blogPostDao.searchBlogPosts()) {
                    object : LiveData<BlogViewState>() {
                        override fun onActive() {
                            super.onActive()
                            value = BlogViewState(BlogViewState.BlogFields(it))
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
}