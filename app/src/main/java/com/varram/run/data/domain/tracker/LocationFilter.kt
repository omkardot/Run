package com.varram.run.data.domain.tracker

import com.varram.run.data.model.LocationData

class LocationFilter {

    companion object {
        private const val MAX_ACCURACY_METERS = 30f
        private const val MAX_SPEED_MPS = 12f
    }

    private var previousLocation: LocationData? = null

    fun accept(location: LocationData): Boolean {

        // 1. Accuracy check
        if (location.accuracy > MAX_ACCURACY_METERS) {
            return false
        }

        // First valid point
        val previous = previousLocation

        if (previous == null) {
            previousLocation = location
            return true
        }

        // 2. Speed sanity check
        if (location.speed > MAX_SPEED_MPS) {
            return false
        }

        previousLocation = location

        return true
    }

    fun reset() {
        previousLocation = null
    }
}