package com.hefny.hady.animalfeed.api.main

import androidx.lifecycle.LiveData
import com.hefny.hady.animalfeed.models.AccountProperties
import com.hefny.hady.animalfeed.util.GenericApiResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface OpenApiMainService {
    @GET("account/properties")
    fun getAccountProperties(
        @Header("Authorization") authorization: String
    ): LiveData<GenericApiResponse<AccountProperties>>
}