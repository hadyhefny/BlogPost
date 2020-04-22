package com.hefny.hady.blogpost.ui.main.create_blog

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.hefny.hady.blogpost.R
import com.hefny.hady.blogpost.di.main.MainScope
import com.hefny.hady.blogpost.ui.*
import com.hefny.hady.blogpost.ui.main.create_blog.state.CREATE_BLOG_VIEW_STATE_BUNDLE_KEY
import com.hefny.hady.blogpost.ui.main.create_blog.state.CreateBlogStateEvent
import com.hefny.hady.blogpost.ui.main.create_blog.state.CreateBlogViewState
import com.hefny.hady.blogpost.util.Constants
import com.hefny.hady.blogpost.util.ErrorHandling
import com.hefny.hady.blogpost.util.SuccessHandling
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_create_blog.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@MainScope
class CreateBlogFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : BaseCreateBlogFragment(R.layout.fragment_create_blog) {

    val viewModel: CreateBlogViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // restore state after process death
        savedInstanceState?.let { inState ->
            (inState[CREATE_BLOG_VIEW_STATE_BUNDLE_KEY] as CreateBlogViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(CREATE_BLOG_VIEW_STATE_BUNDLE_KEY, viewModel.viewState.value)
        super.onSaveInstanceState(outState)
    }

    override fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        blog_image.setOnClickListener {
            if (storagePermissionInterface.isStoragePermissionGranted()) {
                pickFromGallery()
            }
        }
        update_textview.setOnClickListener {
            if (storagePermissionInterface.isStoragePermissionGranted()) {
                pickFromGallery()
            }
        }
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            if (dataState != null) {
                stateChangeListener.onDataStateChange(dataState)
                dataState.data?.let { data ->
                    data.response?.let { event ->
                        event.peekContent().let { response ->
                            response.message?.let { message ->
                                if (message == SuccessHandling.SUCCESS_BLOG_CREATED) {
                                    viewModel.clearNewBlogFields()
                                }
                            }
                        }
                    }
                }
            }
        })
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.blogFields.let { newBlogFields ->
                setBlogProperties(
                    newBlogFields.newBlogTitle,
                    newBlogFields.newBlogBody,
                    newBlogFields.newImageUri
                )
            }
        })
    }

    private fun setBlogProperties(
        title: String? = "",
        body: String? = "",
        imageUri: Uri?
    ) {
        imageUri?.let {
            requestManager
                .load(imageUri)
                .into(blog_image)
        } ?: setDefaultImage()
        blog_title.setText(title)
        blog_body.setText(body)
    }

    private fun setDefaultImage() {
        requestManager
            .load(R.drawable.default_image)
            .into(blog_image)
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, Constants.GALLERY_REQUEST_CODE)
    }

    private fun launchImageCrop(uri: Uri?) {
        context?.let {
            CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(it, this)
        }
    }

    private fun showErrorDialog(errorMessage: String) {
        stateChangeListener.onDataStateChange(
            DataState(
                Event(StateError(Response(errorMessage, ResponseType.Dialog()))),
                Loading(false),
                Data(Event.dataEvent(null), null)
            )
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.GALLERY_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        launchImageCrop(uri)
                    } ?: showErrorDialog(ErrorHandling.ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    Log.d(TAG, "Crop: CROP_IMAGE_ACTIVITY_REQUEST_CODE")
                    val result = CropImage.getActivityResult(data)
                    val resultUri = result.uri
                    Log.d(TAG, "Crop: CROP_IMAGE_ACTIVITY_REQUEST_CODE: uri: $resultUri ")
                    viewModel.setNewBlogFields(
                        null,
                        null,
                        resultUri
                    )
                }
                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                    showErrorDialog(ErrorHandling.ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.setNewBlogFields(
            blog_title.text.toString(),
            blog_body.text.toString(),
            null
        )
    }

    private fun publishNewBlog() {
        var mulitpartBody: MultipartBody.Part? = null
        viewModel.getNewImageUri()?.let { imageUri ->
            imageUri.path?.let { filePath ->
                val imageFile = File(filePath)
                Log.d(TAG, "CreateBlogFragment: imageFile: $imageFile")
                val requestBody = RequestBody.create(
                    MediaType.parse("image/*"),
                    imageFile
                )
                mulitpartBody = MultipartBody.Part.createFormData(
                    "image",
                    imageFile.name,
                    requestBody
                )
            }
        }

        mulitpartBody?.let {
            viewModel.setStateEvent(
                CreateBlogStateEvent.CreateNewBlogEvent(
                    blog_title.text.toString(),
                    blog_body.text.toString(),
                    it
                )
            )
            keyboardManagement.hideSoftKeyboard()
        } ?: showErrorDialog(ErrorHandling.ERROR_MUST_SELECT_IMAGE)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.publish_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.publish -> {
                val callback = object : AreYouSureCallback {
                    override fun proceed() {
                        publishNewBlog()
                    }

                    override fun cancel() {
                        // ignore
                    }
                }
                uiCommunicationListener.onUIMessageReceived(
                    UIMessage(
                        getString(R.string.are_you_sure_publish),
                        UIMessageType.AreYouSureDialog(callback)
                    )
                )
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}