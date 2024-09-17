package com.example.olanavigationnew.remote

import com.example.olanavigationnew.util.DataConstants.API_KEY
import com.example.olanavigationnew.util.DataConstants.MAP_BASE_URL
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import com.ola.maps.navigation.v5.model.route.RouteInfoData
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface OlaRouteApiService {

    @POST("routing/v1/directions")
    suspend fun getRouteInfo(
        @QueryMap queryMap: Map<String, String>
    ): Response<RouteInfoData>

}

object OlaRouteRetrofitClient {

    private val okhttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()

            val originalUrl = originalRequest.url

            val newUrl = originalUrl.newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .build()

            val newRequest = originalRequest.newBuilder()
                .url(newUrl)
                .build()

            chain.proceed(newRequest)
        }.build()

    private val builder = OkHttpClient.Builder().addInterceptor(OkHttpProfilerInterceptor())
    private val client = builder.build()
    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl(MAP_BASE_URL)
        .client(client)
        .client(okhttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val apiService: OlaRouteApiService = retrofitBuilder.create(OlaRouteApiService::class.java)
}