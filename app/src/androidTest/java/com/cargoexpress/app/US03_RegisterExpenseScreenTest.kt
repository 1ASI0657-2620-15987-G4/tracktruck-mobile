package com.cargoexpress.app

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.data.remote.expense.ExpenseDto
import com.cargoexpress.app.core.data.repository.ExpenseRepository
import com.cargoexpress.app.core.domain.Expense
import com.cargoexpress.app.core.presentation.trips.expense.registerExpense.RegisterExpenseScreen
import com.cargoexpress.app.core.presentation.trips.expense.registerExpense.RegisterExpenseViewModel
import com.cargoexpress.app.testsupport.FakeExpenseService
import com.cargoexpress.app.testsupport.resetAppSessionState
import com.cargoexpress.app.testsupport.successResponse
import com.cargoexpress.app.testsupport.waitUntilTextDisplayed
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * US03 - Registro de gastos de viaje.
 * Como empresario de gestión logística, quiero poder registrar los gastos realizados durante
 * los viajes para mantener un registro preciso y mantener informados a mis clientes sobre los
 * costos asociados a sus servicios.
 */
@RequiresApi(Build.VERSION_CODES.O)
@RunWith(AndroidJUnit4::class)
class US03_RegisterExpenseScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: NavHostController
    private var registeredExpense: Expense? = null

    @Before
    fun setUp() {
        resetAppSessionState()
        Constants.TOKEN = "fake-token"
        Constants.TRIP_ID = 55
        registeredExpense = null
    }

    @Test
    fun registrarGastos_conDatosValidos_muestraConfirmacion() {
        val expenseService = FakeExpenseService(
            addExpense = { _, dto ->
                successResponse(
                    ExpenseDto(
                        id = 200,
                        fuelAmount = dto.fuelAmount,
                        fuelDescription = dto.fuelDescription,
                        viaticsAmount = dto.viaticsAmount,
                        viaticsDescription = dto.viaticsDescription,
                        tollsAmount = dto.tollsAmount,
                        tollsDescription = dto.tollsDescription,
                        state = true,
                        tripId = dto.tripId
                    )
                )
            }
        )
        val viewModel = RegisterExpenseViewModel(ExpenseRepository(expenseService))

        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = "register_expense") {
                composable("register_expense") {
                    RegisterExpenseScreen(
                        navController = navController,
                        viewModel = viewModel,
                        onExpenseRegistered = { registeredExpense = it }
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Registrar Gasto").assertIsNotEnabled()

        composeTestRule.onNodeWithText("Monto Combustible (USD)").performTextInput("50")
        composeTestRule.onAllNodesWithText("Descripción")[0].performTextInput("Gasolina Lima-Cusco")

        composeTestRule.onNodeWithText("Monto Viáticos (USD)").performTextInput("30")
        composeTestRule.onAllNodesWithText("Descripción")[1].performTextInput("Almuerzo y hospedaje")

        composeTestRule.onNodeWithText("Monto Peajes (USD)").performTextInput("15")
        composeTestRule.onAllNodesWithText("Descripción")[2].performTextInput("Peaje Autopista Sur")

        composeTestRule.onNodeWithText("Registrar Gasto").assertIsEnabled()
        composeTestRule.onNodeWithText("Registrar Gasto").performClick()

        composeTestRule.waitUntilTextDisplayed("Gasto registrado correctamente")
        composeTestRule.onNodeWithText("Aceptar").performClick()

        composeTestRule.waitForIdle()
        assertEquals(50.0, registeredExpense?.fuelAmount)
        assertEquals(30.0, registeredExpense?.viaticsAmount)
        assertEquals(15.0, registeredExpense?.tollsAmount)
        assertEquals(55, registeredExpense?.tripId)
    }

    @Test
    fun registrarGastos_sinCompletarFormulario_botonPermaneceDeshabilitado() {
        val viewModel = RegisterExpenseViewModel(ExpenseRepository(FakeExpenseService()))

        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = "register_expense") {
                composable("register_expense") {
                    RegisterExpenseScreen(
                        navController = navController,
                        viewModel = viewModel,
                        onExpenseRegistered = { registeredExpense = it }
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Monto Combustible (USD)").performTextInput("50")
        composeTestRule.onNodeWithText("Registrar Gasto").assertIsNotEnabled()
    }
}
