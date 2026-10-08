package com.tracktruck.app.core.presentation.trips.expense.registerExpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracktruck.app.core.data.repository.ExpenseRepository
import com.tracktruck.app.core.domain.Expense
import com.tracktruck.app.core.common.Constants
import com.tracktruck.app.core.common.Resource
import kotlinx.coroutines.launch

class RegisterExpenseViewModel(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    var fuelAmount: Double = 0.0
    var fuelDescription: String = ""
    var viaticsAmount: Double = 0.0
    var viaticsDescription: String = ""
    var tollsAmount: Double = 0.0
    var tollsDescription: String = ""

    fun registerExpense(onResult: (Resource<Expense>) -> Unit) {
        viewModelScope.launch {
            val expense = Expense(
                id = 0,
                fuelAmount = fuelAmount,
                fuelDescription = fuelDescription,
                viaticsAmount = viaticsAmount,
                viaticsDescription = viaticsDescription,
                tollsAmount = tollsAmount,
                tollsDescription = tollsDescription,
                state = true,
                tripId = Constants.TRIP_ID
            )
            val result = expenseRepository.addExpense(expense)
            onResult(result)
        }
    }
}