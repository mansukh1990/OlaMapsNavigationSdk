package com.example.olanavigationnew.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.olanavigationnew.remote.RetrofitClient
import com.example.olanavigationnew.repositoryimpl.OlaMapOlaMapRepositoryImpl
import com.example.olanavigationnew.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsViewModel : ViewModel() {

    private val repository = OlaMapOlaMapRepositoryImpl(RetrofitClient.apiService)

    // creating bool variable since viewModel.getAccessToken() can be called multiple times
    private var isApiCalled = false

    private var accessToken = ""
    fun getAccessToken(clientId: String, clientSecret: String, onSuccess: () -> Unit) {
        if (isApiCalled) return

        isApiCalled = true

        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getAccessToken(clientId, clientSecret)

            if (result is NetworkResult.Success) {
                Log.d("ola", "Access Token: ${result.data.accessToken}")
                accessToken = result.data.accessToken

                withContext(Dispatchers.Main) {
                    onSuccess()
                }

            } else if (result is NetworkResult.Error) {
                Log.d("ola", "ERROR: ${result.errorMsg}")
            }
        }
    }
}