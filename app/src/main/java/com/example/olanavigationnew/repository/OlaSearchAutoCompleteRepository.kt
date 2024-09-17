package com.example.olanavigationnew.repository

import com.example.olanavigationnew.model.OlaSearchAutoCompleteResponse
import com.example.olanavigationnew.util.NetworkResult
import com.mapbox.mapboxsdk.geometry.LatLng
import com.ola.maps.navigation.v5.model.route.RouteInfoData

interface OlaSearchAutoCompleteRepository {
    suspend fun getSearchAutoComplete(
        location: String,
        radius: Int,
        strictBounds: Boolean,
        apiKey: String,
        input: String
    ): NetworkResult<OlaSearchAutoCompleteResponse>

    suspend fun getRouteInfo(
        originLatLng: LatLng,
        destinationLatLng: LatLng
    ): NetworkResult<RouteInfoData>
}