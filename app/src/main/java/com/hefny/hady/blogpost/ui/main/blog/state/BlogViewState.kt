package com.hefny.hady.blogpost.ui.main.blog.state

import android.net.Uri
import android.os.Parcelable
import com.hefny.hady.blogpost.models.BlogPost
import com.hefny.hady.blogpost.persistence.BlogQueryUtils
import kotlinx.android.parcel.Parcelize

const val BLOG_VIEW_STATE_BUNDLE_KEY = "com.hefny.hady.blogpost.ui.main.blog.state.BlogViewState"

@Parcelize
data class BlogViewState(
    // BlogFragment vars
    var blogFields: BlogFields = BlogFields(),

    // ViewBlogFragment vars
    var viewBlogFields: ViewBlogFields = ViewBlogFields(),

    // UpdateBlogFragment
    var updateBlogFields: UpdateBlogFields = UpdateBlogFields()
) : Parcelable {
    @Parcelize
    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList(),
        var searchQuery: String = "",
        var page: Int = 1,
        var isQueryInProgress: Boolean = false,
        var isQueryExhausted: Boolean = false,
        var filter: String = BlogQueryUtils.ORDER_BY_ASC_DATE_UPDATED,
        var order: String = BlogQueryUtils.BLOG_ORDER_ASC
    ) : Parcelable

    @Parcelize
    data class ViewBlogFields(
        var blogPost: BlogPost? = null,
        var isAuthorOfBlogPost: Boolean = false
    ) : Parcelable

    @Parcelize
    data class UpdateBlogFields(
        var updateBlogTitle: String? = null,
        var updatedBlogBody: String? = null,
        var updateBlogImage: Uri? = null
    ) : Parcelable
}