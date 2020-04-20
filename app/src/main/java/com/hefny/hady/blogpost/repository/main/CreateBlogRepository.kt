package com.hefny.hady.blogpost.repository.main

import com.hefny.hady.blogpost.api.main.OpenApiMainService
import com.hefny.hady.blogpost.persistence.BlogPostDao
import com.hefny.hady.blogpost.repository.JobManager
import com.hefny.hady.blogpost.session.SessionManager
import javax.inject.Inject

class CreateBlogRepository
@Inject
constructor(
    private val openApiMainService: OpenApiMainService,
    private val blogPostDao: BlogPostDao,
    private val sessionManager: SessionManager
) : JobManager("CreateBlogRepository") {

}