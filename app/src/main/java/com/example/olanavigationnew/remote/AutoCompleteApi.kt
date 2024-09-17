package com.example.olanavigationnew.remote

import com.example.olanavigationnew.model.OlaSearchAutoCompleteResponse
import com.example.olanavigationnew.util.DataConstants.MAP_BASE_URL
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AutoCompleteApi {

    @GET("places/v1/autocomplete")
    suspend fun getAutoCompleteSearchResult(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("strictbounds") strictBounds: Boolean,
        @Query("input") search: String,
        @Query("api_key") apiKey: String
    ): OlaSearchAutoCompleteResponse

}


object AutoCompleteRetrofitClient {
    private val builder = OkHttpClient.Builder().addInterceptor(OkHttpProfilerInterceptor())
    private val client = builder.build()
    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl(MAP_BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
    val apiService: AutoCompleteApi = retrofitBuilder.create(AutoCompleteApi::class.java)
}