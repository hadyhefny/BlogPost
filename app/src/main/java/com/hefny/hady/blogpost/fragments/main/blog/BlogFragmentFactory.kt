package com.hefny.hady.blogpost.fragments.main.blog

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.hefny.hady.blogpost.di.main.MainScope
import com.hefny.hady.blogpost.ui.main.blog.BlogFragment
import com.hefny.hady.blogpost.ui.main.blog.UpdateBlogFragment
import com.hefny.hady.blogpost.ui.main.blog.ViewBlogFragment
import javax.inject.Inject

@MainScope
class BlogFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String) =
        when (className) {
            BlogFragment::class.java.name -> {
                BlogFragment(viewModelFactory, requestManager)
            }
            UpdateBlogFragment::class.java.name -> {
                UpdateBlogFragment(viewModelFactory, requestManager)
            }
            ViewBlogFragment::class.java.name -> {
                ViewBlogFragment(viewModelFactory, requestManager)
            }
            else -> {
                BlogFragment(viewModelFactory, requestManager)
            }
        }
}