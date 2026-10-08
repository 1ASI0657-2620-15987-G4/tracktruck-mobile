package com.tracktruck.app.core.presentation.trips.trip.detailsTrip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tracktruck.app.core.data.repository.ClientRepository
import com.tracktruck.app.core.data.repository.DriverRepository
import com.tracktruck.app.core.data.repository.ExpenseRepository
import com.tracktruck.app.core.data.repository.TripRepository
import com.tracktruck.app.core.data.repository.VehicleRepository

class TripDetailViewModelFactory(
    private val tripRepository: TripRepository,
    private val expenseRepository: ExpenseRepository,
    private val driverRepository: DriverRepository,
    private val vehicleRepository: VehicleRepository,
    private val clientRepository: ClientRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TripDetailViewModel(
            tripRepository,
            expenseRepository,
            driverRepository,
            vehicleRepository,
            clientRepository
        ) as T
    }
}