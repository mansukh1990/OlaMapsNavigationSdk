package com.example.olanavigationnew.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.olanavigationnew.model.OlaSearchAutoCompleteResponse
import com.example.olanavigationnew.remote.AutoCompleteRetrofitClient
import com.example.olanavigationnew.remote.OlaRouteRetrofitClient
import com.example.olanavigationnew.repositoryimpl.OlaSearchAutoCompleteRepositoryImpl
import com.example.olanavigationnew.util.DataConstants.API_KEY
import com.example.olanavigationnew.util.NetworkResult
import com.mapbox.mapboxsdk.geometry.LatLng
import com.ola.maps.navigation.v5.model.route.RouteInfoData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OlaSearchAutoCompleteViewModel : ViewModel() {
    private val olaSearchAutoCompleteRepository = OlaSearchAutoCompleteRepositoryImpl(
        AutoCompleteRetrofitClient.apiService, OlaRouteRetrofitClient.apiService
    )

    private var searchJob: Job? = null
    private val debounceDelay = 300L // 300 milliseconds debounce time
    private val mutableSearchLocation = MutableLiveData<OlaSearchAutoCompleteResponse>()
    val mSearchedLocationLiveData get() = mutableSearchLocation

    fun callSearchAutoCompleteApi(
        location: String,
        radius: Int,
        strictBounds: Boolean,
        apiKey: String = API_KEY,
        input: String
    ) {
        searchJob?.cancel() // Cancel any ongoing debounce job
        searchJob = viewModelScope.launch {
            delay(debounceDelay) // Wait for the debounce period
            val result = olaSearchAutoCompleteRepository.getSearchAutoComplete(
                location,
                radius,
                strictBounds,
                apiKey,
                input
            )
            when (result) {
                is NetworkResult.Error -> {
                    Log.d("SEAR", result.errorMsg)
                }

                is NetworkResult.Loading -> {
                    // Handle loading state if needed
                }

                is NetworkResult.Success -> {
                    mutableSearchLocation.postValue(result.data)
                }
            }
        }
    }

    fun getRouteInfo(
        originLatitudeLongitude: LatLng,
        destinationLatitudeLongitude: LatLng,
        onSuccess: (RouteInfoData) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = olaSearchAutoCompleteRepository.getRouteInfo(
                originLatLng = originLatitudeLongitude,
                destinationLatLng = destinationLatitudeLongitude
            )

            if (result is NetworkResult.Success) {
                withContext(Dispatchers.Main) {
                    onSuccess(result.data)
                }

            } else if (result is NetworkResult.Error) {
                Log.d("ola", "ERROR: ${result.errorMsg}")
            }
        }
    }

}