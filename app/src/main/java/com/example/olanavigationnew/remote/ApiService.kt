package com.example.olanavigationnew.remote

import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import com.example.olanavigationnew.model.OlaMapAccessTokenResponse
import com.example.olanavigationnew.util.DataConstants.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

object RetrofitClient {
    private val builder = OkHttpClient.Builder().addInterceptor(OkHttpProfilerInterceptor())
    private val client = builder.build()
    private val retrofitBuilder = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(client)
    .addConverterFactory(MoshiConverterFactory.create())
    .build()
    val apiService: ApiServiceWithoutToken = retrofitBuilder.create(ApiServiceWithoutToken::class.java)
}

interface ApiServiceWithoutToken {
    @FormUrlEncoded
    @POST("token")
    suspend fun getAccessToken(
        @Header("Content-Type") contentType: String = "application/x-www-form-urlencoded",
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String = "client_credentials",
        @Field("scope") scope: String = "openid",
    ): OlaMapAccessTokenResponse
}

