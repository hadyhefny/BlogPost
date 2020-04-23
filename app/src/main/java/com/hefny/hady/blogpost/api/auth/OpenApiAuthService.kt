package com.hefny.hady.blogpost.api.auth

import com.hefny.hady.blogpost.api.auth.network_reponses.LoginResponse
import com.hefny.hady.blogpost.api.auth.network_reponses.RegistrationResponse
import com.hefny.hady.blogpost.di.auth.AuthScope
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

@AuthScope
interface OpenApiAuthService {
    @POST("account/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") email: String,
        @Field("password") password: String
    ): LoginResponse

    @POST("account/register")
    @FormUrlEncoded
    suspend fun register(
        @Field("email") email: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("password2") password2: String
    ): RegistrationResponse
}