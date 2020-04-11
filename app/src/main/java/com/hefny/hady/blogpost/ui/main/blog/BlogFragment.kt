package com.hefny.hady.blogpost.ui.main.blog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.hefny.hady.blogpost.R
import com.hefny.hady.blogpost.ui.main.blog.state.BlogStateEvent
import kotlinx.android.synthetic.main.fragment_blog.*

class BlogFragment : BaseBlogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goViewBlogFragment.setOnClickListener {
            findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
        }
        subscribeObservers()
        executeSearch()
    }

    private fun executeSearch() {
        viewModel.setQuery("")
        viewModel.setStateEvent(BlogStateEvent.BlogSearchEvent())
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            if (dataState != null) {
                dataState.data?.let { data ->
                    data.data?.let { event ->
                        event.getContentIfNotHandled()?.let {
                            Log.d(TAG, "BlogFragment, dataState: $it")
                            viewModel.setBlogList(it.blogFields.blogList)
                        }
                    }
                }
            }
            viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
                Log.d(TAG, "BlogFragment, viewState: $viewState")
            })
        })
    }
}