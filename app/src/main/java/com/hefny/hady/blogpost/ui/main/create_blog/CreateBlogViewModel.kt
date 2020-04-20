package com.hefny.hady.blogpost.ui.main.create_blog

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.hefny.hady.blogpost.repository.main.CreateBlogRepository
import com.hefny.hady.blogpost.session.SessionManager
import com.hefny.hady.blogpost.ui.BaseViewModel
import com.hefny.hady.blogpost.ui.DataState
import com.hefny.hady.blogpost.ui.Loading
import com.hefny.hady.blogpost.ui.main.create_blog.state.CreateBlogStateEvent
import com.hefny.hady.blogpost.ui.main.create_blog.state.CreateBlogViewState
import com.hefny.hady.blogpost.util.AbsentLiveData
import javax.inject.Inject

class CreateBlogViewModel
@Inject
constructor(
    private val createBlogRepository: CreateBlogRepository,
    private val sessionManager: SessionManager
) : BaseViewModel<CreateBlogStateEvent, CreateBlogViewState>() {
    override fun handleStateEvent(stateEvent: CreateBlogStateEvent): LiveData<DataState<CreateBlogViewState>> {
        when (stateEvent) {
            is CreateBlogStateEvent.CreateNewBlogEvent -> {
                return AbsentLiveData.create()
            }
            is CreateBlogStateEvent.None -> {
                return liveData {
                    emit(
                        DataState(null, Loading(false), null)
                    )
                }
            }
        }
    }

    override fun initNewViewState(): CreateBlogViewState {
        return CreateBlogViewState()
    }

    fun setNewBlogFields(
        title: String?,
        body: String?,
        imageUri: Uri?
    ) {
        val update = getCurrentViewStateOrNew()
        val newBlogFields = update.blogFields
        title?.let { newBlogFields.newBlogTitle = it }
        body?.let { newBlogFields.newBlogBody = it }
        imageUri?.let { newBlogFields.newImageUri = it }
        update.blogFields = newBlogFields
        setViewState(update)
    }

    fun clearNewBlogFields() {
        val update = getCurrentViewStateOrNew()
        update.blogFields = CreateBlogViewState.NewBlogFields()
        setViewState(update)
    }

    fun cancelActiveJobs() {
        createBlogRepository.cancelActiveJobs()
        handlePendingData()
    }

    private fun handlePendingData() {
        setStateEvent(CreateBlogStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}