package com.example.olanavigationnew.util

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()

    data class Error(val errorMsg: String) : NetworkResult<Nothing>()

    data object Loading : NetworkResult<Nothing>()
}