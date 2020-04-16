package com.hefny.hady.blogpost.ui.main.blog

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.hefny.hady.blogpost.R
import com.hefny.hady.blogpost.models.BlogPost
import com.hefny.hady.blogpost.ui.main.blog.state.BlogStateEvent
import com.hefny.hady.blogpost.ui.main.blog.viewmodel.isAuthorOfBlogPost
import com.hefny.hady.blogpost.ui.main.blog.viewmodel.setIsAuthorOfBlogPost
import com.hefny.hady.blogpost.util.DateUtils
import kotlinx.android.synthetic.main.fragment_view_blog.*

class ViewBlogFragment : BaseBlogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
        appbarManagement.expandAppBar()
        checkIfAuthorOfBlogPost()
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState.data?.let { data ->
                data.data?.let { event ->
                    event.getContentIfNotHandled()?.let { viewState ->
                        viewModel.setIsAuthorOfBlogPost(
                            viewState.viewBlogFields.isAuthorOfBlogPost
                        )
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
        findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
    }
}