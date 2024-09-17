package com.example.olanavigationnew.repositoryimpl

import com.example.olanavigationnew.model.OlaMapAccessTokenResponse
import com.example.olanavigationnew.remote.ApiServiceWithoutToken
import com.example.olanavigationnew.repository.OlaMapRepository
import com.example.olanavigationnew.util.NetworkResult

class OlaMapOlaMapRepositoryImpl(
    private val apiService: ApiServiceWithoutToken
) : OlaMapRepository {

    override suspend fun getAccessToken(
        clientId: String,
        clientSecret: String
    ): NetworkResult<OlaMapAccessTokenResponse> = try {
        NetworkResult.Success(apiService.getAccessToken(clientId=clientId, clientSecret = clientSecret))
    } catch (e: Exception) {
        NetworkResult.Error(errorMsg = e.message.toString())
    }
}