package com.tracktruck.app.core.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tracktruck.app.core.data.repository.TripRepository

class StatisticsViewModelFactory(
    private val tripRepository: TripRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatisticsViewModel(tripRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
