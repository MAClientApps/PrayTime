package com.code.apppraytime.interfaces

interface LocationInterface {
    suspend fun located(city: String, country: String)
}