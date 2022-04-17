package com.code.apppraytime.times

import com.code.apppraytime.times.internal.QiblaUtil

class Qibla(coordinates: Coordinates?) {
    val direction: Double

    companion object {
        private val MAKKAH = Coordinates(21.4225241, 39.8261818)
    }

    init {
        direction = QiblaUtil.calculateQiblaDirection(coordinates)
    }
}