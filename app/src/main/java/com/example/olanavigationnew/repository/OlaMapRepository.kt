package com.example.olanavigationnew.repository

import com.example.olanavigationnew.model.OlaMapAccessTokenResponse
import com.example.olanavigationnew.util.NetworkResult

interface OlaMapRepository {
    suspend fun getAccessToken(
        clientId: String,
        clientSecret: String
    ): NetworkResult<OlaMapAccessTokenResponse>
}