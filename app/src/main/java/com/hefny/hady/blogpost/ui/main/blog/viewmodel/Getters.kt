package com.hefny.hady.blogpost.ui.main.blog.viewmodel

fun BlogViewModel.getSearchQuery(): String {
    getCurrentViewStateOrNew().let {
        return it.blogFields.searchQuery
    }
}

fun BlogViewModel.getPage(): Int {
    getCurrentViewStateOrNew().let {
        return it.blogFields.page
    }
}

fun BlogViewModel.getIsQueryExhausted(): Boolean {
    getCurrentViewStateOrNew().let {
        return it.blogFields.isQueryExhausted
    }
}

fun BlogViewModel.getIsQueryInProgress(): Boolean {
    getCurrentViewStateOrNew().let {
        return it.blogFields.isQueryInProgress
    }
}

fun BlogViewModel.getFilter(): String {
    getCurrentViewStateOrNew().let {
        return it.blogFields.filter
    }
}

fun BlogViewModel.getOrder(): String {
    getCurrentViewStateOrNew().let {
        return it.blogFields.order
    }
}

fun BlogViewModel.isAuthorOfBlogPost(): Boolean {
    getCurrentViewStateOrNew().let {
        return it.viewBlogFields.isAuthorOfBlogPost
    }
}

fun BlogViewModel.getSlug(): String {
    getCurrentViewStateOrNew().let {
        it.viewBlogFields.blogPost?.let { blogPost ->
            return blogPost.slug
        }
    }
    return ""
}
