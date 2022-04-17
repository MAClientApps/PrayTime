package com.code.apppraytime.repository

object SalatRepository {

    private val salat = listOf(
        "FAJR",
        "DHUHR",
        "ASR",
        "MAGHRIB",
        "ISHA"
    )

    val salatNames: List<String> get() = salat
}