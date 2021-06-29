package com.example.criptotienda.network

import java.lang.Exception

interface RealtimeDataListener<T> {
    fun onDataChange(updatedData: T)

    fun onError(exception: Exception)
}