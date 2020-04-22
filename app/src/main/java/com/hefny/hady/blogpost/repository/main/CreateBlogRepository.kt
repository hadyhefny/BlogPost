package com.hefny.hady.blogpost.repository.main

import androidx.lifecycle.LiveData
import com.hefny.hady.blogpost.api.main.OpenApiMainService
import com.hefny.hady.blogpost.api.main.responses.BlogCreateUpdateResponse
import com.hefny.hady.blogpost.di.main.MainScope
import com.hefny.hady.blogpost.models.AuthToken
import com.hefny.hady.blogpost.models.BlogPost
import com.hefny.hady.blogpost.persistence.BlogPostDao
import com.hefny.hady.blogpost.repository.JobManager
import com.hefny.hady.blogpost.repository.NetworkBoundResource
import com.hefny.hady.blogpost.session.SessionManager
import com.hefny.hady.blogpost.ui.DataState
import com.hefny.hady.blogpost.ui.Response
import com.hefny.hady.blogpost.ui.ResponseType
import com.hefny.hady.blogpost.ui.main.create_blog.state.CreateBlogViewState
import com.hefny.hady.blogpost.util.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@MainScope
class CreateBlogRepository
@Inject
constructor(
    private val openApiMainService: OpenApiMainService,
    private val blogPostDao: BlogPostDao,
    private val sessionManager: SessionManager
) : JobManager("CreateBlogRepository") {
    private val TAG = "AppDebug"

    fun createNewBlogPost(
        authToken: AuthToken,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?
    ): LiveData<DataState<CreateBlogViewState>> {
        return object :
            NetworkBoundResource<BlogCreateUpdateResponse, BlogPost, CreateBlogViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // not used in this case
            override suspend fun createCacheRequestAndReturn() {
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BlogCreateUpdateResponse>) {
                // If they don't have a paid membership account it will still return a 200
                // Need to account for that
                if (response.body.response != SuccessHandling.RESPONSE_MUST_BECOME_CODINGWITHMITCH_MEMBER) {
                    response.body.let {
                        val blogPost = BlogPost(
                            it.pk,
                            it.title,
                            it.slug,
                            it.body,
                            it.image,
                            DateUtils.convertServerStringDateToLong(it.date_updated),
                            it.username
                        )
                        updateLocalDb(blogPost)
                    }
                }
                withContext(Main) {
                    onCompleteJob(
                        DataState.data(
                            null,
                            Response(response.body.response, ResponseType.Dialog())
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogCreateUpdateResponse>> {
                return openApiMainService.createBlogPost(
                    "Token ${authToken.token}",
                    title,
                    body,
                    image
                )
            }

            // not used in this case
            override fun loadFromCache(): LiveData<CreateBlogViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: BlogPost?) {
                cacheObject?.let { blogPost ->
                    blogPostDao.insert(blogPost)
                }
            }

            override fun setJob(job: Job) {
                addJob("createNewBlogPost", job)
            }
        }.asLiveData()
    }
}