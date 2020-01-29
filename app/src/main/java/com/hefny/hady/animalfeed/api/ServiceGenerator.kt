package com.hefny.hady.animalfeed.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceGenerator {
    fun getRetrofitInstance(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

const val BASE_URL = "https://jsonplaceholder.typicode.com/"