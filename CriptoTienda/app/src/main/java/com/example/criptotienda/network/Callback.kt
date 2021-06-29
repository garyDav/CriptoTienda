package com.example.criptotienda.network

interface Callback<T> {
    fun onSuccess(result: T?)

    fun onFailed(exception: Exception)
}