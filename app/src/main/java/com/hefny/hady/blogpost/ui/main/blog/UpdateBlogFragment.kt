package com.hefny.hady.blogpost.ui.main.blog

import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import com.hefny.hady.blogpost.R
import com.hefny.hady.blogpost.ui.main.blog.state.BlogStateEvent
import kotlinx.android.synthetic.main.fragment_update_blog.*
import okhttp3.MultipartBody

class UpdateBlogFragment : BaseBlogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState.data?.let { data ->
                data.data?.getContentIfNotHandled()?.let { viewState ->
                    // if this is not null, the blog post was updated
                    viewState.viewBlogFields.blogPost?.let {
                        // TODO("onBlogPostUpdateSuccess")
                    }
                }
            }
        })
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.updateBlogFields.let { updatedBlogFields ->
                setBlogProperties(
                    updatedBlogFields.updateBlogTitle,
                    updatedBlogFields.updatedBlogBody,
                    updatedBlogFields.updateBlogImage
                )
            }
        })
    }

    private fun setBlogProperties(
        updateBlogTitle: String?,
        updatedBlogBody: String?,
        updateBlogImage: Uri?
    ) {
        requestManager
            .load(updateBlogImage)
            .into(blog_image)
        blog_title.setText(updateBlogTitle)
        blog_body.setText(updatedBlogBody)
    }

    private fun saveChanges() {
        var multipartBody: MultipartBody.Part? = null
        viewModel.setStateEvent(
            BlogStateEvent.UpdatedBlogPostEvent(
                blog_title.text.toString(),
                blog_body.text.toString(),
                multipartBody
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.update_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                saveChanges()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}