package com.hefny.hady.animalfeed.api

import com.hefny.hady.animalfeed.modelsTest.User
import retrofit2.http.GET

interface FirebaseAuthService {
    @GET("users")
    suspend fun getUsers(): List<User>
}