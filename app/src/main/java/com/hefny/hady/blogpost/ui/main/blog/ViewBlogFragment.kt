package com.hefny.hady.blogpost.ui.main.blog

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.hefny.hady.blogpost.R
import com.hefny.hady.blogpost.di.main.MainScope
import com.hefny.hady.blogpost.models.BlogPost
import com.hefny.hady.blogpost.ui.AreYouSureCallback
import com.hefny.hady.blogpost.ui.UIMessage
import com.hefny.hady.blogpost.ui.UIMessageType
import com.hefny.hady.blogpost.ui.main.blog.state.BLOG_VIEW_STATE_BUNDLE_KEY
import com.hefny.hady.blogpost.ui.main.blog.state.BlogStateEvent
import com.hefny.hady.blogpost.ui.main.blog.state.BlogViewState
import com.hefny.hady.blogpost.ui.main.blog.viewmodel.*
import com.hefny.hady.blogpost.util.DateUtils
import com.hefny.hady.blogpost.util.SuccessHandling
import kotlinx.android.synthetic.main.fragment_view_blog.*
import javax.inject.Inject

@MainScope
class ViewBlogFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : BaseBlogFragment(R.layout.fragment_view_blog) {

    val viewModel: BlogViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // restore state after process death
        savedInstanceState?.let { inState ->
            (inState[BLOG_VIEW_STATE_BUNDLE_KEY] as BlogViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val viewState = viewModel.viewState.value
        viewState?.blogFields?.blogList = ArrayList()
        outState.putParcelable(BLOG_VIEW_STATE_BUNDLE_KEY, viewState)
        super.onSaveInstanceState(outState)
    }

    override fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
        appbarManagement.expandAppBar(R.id.app_bar)
        checkIfAuthorOfBlogPost()
        delete_button.setOnClickListener {
            confirmDeleteRequest()
        }
    }

    private fun confirmDeleteRequest() {
        val callback = object : AreYouSureCallback {
            override fun proceed() {
                deleteBlogPost()
            }

            override fun cancel() {
                // ignore
            }
        }
        uiCommunicationListener.onUIMessageReceived(
            UIMessage(
                getString(R.string.are_you_sure_delete),
                UIMessageType.AreYouSureDialog(callback)
            )
        )
    }

    private fun deleteBlogPost() {
        viewModel.setStateEvent(BlogStateEvent.DeleteBlogPostEvent())
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            if (dataState != null) {
                stateChangeListener.onDataStateChange(dataState)
                dataState.data?.let { data ->
                    data.data?.getContentIfNotHandled()?.let { viewState ->
                        viewModel.setIsAuthorOfBlogPost(
                            viewState.viewBlogFields.isAuthorOfBlogPost
                        )
                    }
                    data.response?.peekContent()?.let { response ->
                        if (response.message == SuccessHandling.SUCCESS_BLOG_DELETED) {
                            viewModel.removeDeletedBlogPost()
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.viewBlogFields.blogPost?.let { blogPost ->
                setBlogProperties(blogPost)
            }
            if (viewState.viewBlogFields.isAuthorOfBlogPost) {
                adaptViewToAuthorMode()
            }
        })
    }

    private fun adaptViewToAuthorMode() {
        activity?.invalidateOptionsMenu()
        delete_button.visibility = View.VISIBLE
    }

    private fun checkIfAuthorOfBlogPost() {
        viewModel.setIsAuthorOfBlogPost(false) // reset
        viewModel.setStateEvent(BlogStateEvent.CheckAuthorOfBlogPostEvent())
    }

    private fun setBlogProperties(blogPost: BlogPost) {
        requestManager
            .load(blogPost.image)
            .into(blog_image)
        blog_title.setText(blogPost.title)
        blog_author.setText(blogPost.username)
        blog_body.setText(blogPost.body)
        blog_update_date.setText(
            DateUtils.convertLongToStringDate(blogPost.date_updated)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (viewModel.isAuthorOfBlogPost()) {
            inflater.inflate(R.menu.edit_view_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (viewModel.isAuthorOfBlogPost()) {
            when (item.itemId) {
                R.id.edit -> {
                    navUpdateBlogFragment()
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navUpdateBlogFragment() {
        try {
            viewModel.setUpdateBlogPostFields(
                viewModel.getBlogPost().title,
                viewModel.getBlogPost().body,
                viewModel.getBlogPost().image.toUri()
            )
            findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message}")
        }
    }
}