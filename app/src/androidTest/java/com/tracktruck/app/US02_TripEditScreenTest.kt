package com.tracktruck.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tracktruck.app.core.common.Constants
import com.tracktruck.app.core.data.remote.driver.DriverDto
import com.tracktruck.app.core.data.remote.trip.TripDto
import com.tracktruck.app.core.data.remote.user.ClientDto
import com.tracktruck.app.core.data.remote.vehicle.VehicleDto
import com.tracktruck.app.core.data.repository.ClientRepository
import com.tracktruck.app.core.data.repository.DriverRepository
import com.tracktruck.app.core.data.repository.ExpenseRepository
import com.tracktruck.app.core.data.repository.TripRepository
import com.tracktruck.app.core.data.repository.VehicleRepository
import com.tracktruck.app.core.presentation.trips.trip.editTrip.TripEditScreen
import com.tracktruck.app.testsupport.FakeClientService
import com.tracktruck.app.testsupport.FakeDriverService
import com.tracktruck.app.testsupport.FakeExpenseService
import com.tracktruck.app.testsupport.FakeTripService
import com.tracktruck.app.testsupport.FakeVehicleService
import com.tracktruck.app.testsupport.resetAppSessionState
import com.tracktruck.app.testsupport.successResponse
import com.tracktruck.app.testsupport.waitUntilTextDisplayed
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * US02 - Modificación de datos de un viaje.
 * Como empresario de gestión logística, quiero modificar los datos de un viaje
 * para corregir datos erróneos o actualizar su información.
 */
@RunWith(AndroidJUnit4::class)
class US02_TripEditScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: NavHostController

    @Before
    fun setUp() {
        resetAppSessionState()
        Constants.TOKEN = "fake-token"
        Constants.ENTREPRENEUR_ID = 1
    }

    private fun awaitingTripDto(id: Int) = TripDto(
        id = id, name = "Viaje Original", state = "AWAITING", type = "ESTANDAR", weight = 200.0,
        loadLocation = "Lima", loadDate = "2026-08-01T08:00:00", unloadLocation = "Cusco", unloadDate = "2026-08-02T08:00:00",
        driverId = 5, vehicleId = 7, clientId = 9, entrepreneurId = 1
    )

    @Test
    fun editarViaje_corrigeDatosYActualizaCorrectamente() {
        val tripService = FakeTripService(
            getTrip = { id, _ -> successResponse(awaitingTripDto(id)) },
            updateTripDetails = { _, _, dto -> successResponse(dto) }
        )
        val driverService = FakeDriverService(
            getDriversByEntrepreneur = { _, _ ->
                successResponse(
                    listOf(
                        DriverDto(id = 5, name = "Juan Perez", dni = "10203040", license = "L-1", contactNumber = "999888777", state = "AVAILABLE", entrepreneurId = 1),
                        DriverDto(id = 6, name = "Maria Lopez", dni = "40302010", license = "L-2", contactNumber = "999888666", state = "AVAILABLE", entrepreneurId = 1)
                    )
                )
            }
        )
        val vehicleService = FakeVehicleService(
            getVehiclesByEntrepreneur = { _, _ ->
                successResponse(
                    listOf(
                        VehicleDto(id = 7, name = "Volvo FH16", model = "FH16", plate = "ABC-123", tractorPlate = "XYZ-987", maxLoad = 1000.0, volume = 50.0, state = "AVAILABLE", entrepreneurId = 1),
                        VehicleDto(id = 8, name = "Volvo FH12", model = "FH12", plate = "DEF-456", tractorPlate = "UVW-654", maxLoad = 800.0, volume = 40.0, state = "AVAILABLE", entrepreneurId = 1)
                    )
                )
            }
        )
        val clientService = FakeClientService(
            getClient = { id, _ -> successResponse(ClientDto(id = id, name = "Carlos Ramirez", dni = "12345678", birthDate = "1990-01-01T00:00:00", userId = 2)) },
            getClientByDni = { dni, _ -> successResponse(ClientDto(id = 11, name = "Ana Torres", dni = dni, birthDate = "1992-05-05T00:00:00", userId = 3)) }
        )

        val tripRepository = TripRepository(tripService, FakeExpenseService())
        val driverRepository = DriverRepository(driverService)
        val vehicleRepository = VehicleRepository(vehicleService)
        val clientRepository = ClientRepository(clientService)

        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = "edit_trip") {
                composable("edit_trip") {
                    TripEditScreen(
                        tripId = 42,
                        tripRepository = tripRepository,
                        driverRepository = driverRepository,
                        vehicleRepository = vehicleRepository,
                        clientRepository = clientRepository,
                        navController = navController
                    )
                }
            }
        }

        composeTestRule.waitUntilTextDisplayed("Viaje Original")
        composeTestRule.onNodeWithText("Viaje Original").performTextReplacement("Viaje Corregido")

        composeTestRule.onNodeWithText("Juan Perez").performClick()
        composeTestRule.waitUntilTextDisplayed("Maria Lopez")
        composeTestRule.onNodeWithText("Maria Lopez").performClick()

        composeTestRule.onNodeWithText("FH16").performClick()
        composeTestRule.waitUntilTextDisplayed("FH12")
        composeTestRule.onNodeWithText("FH12").performClick()

        composeTestRule.onNodeWithText("12345678").performTextReplacement("87654321")
        composeTestRule.onNodeWithContentDescription("Verificar DNI").performClick()
        composeTestRule.waitUntilTextDisplayed("Cliente: Ana Torres")

        composeTestRule.onNodeWithText("Actualizar Detalles").assertIsEnabled()
        composeTestRule.onNodeWithText("Actualizar Detalles").performClick()

        composeTestRule.waitUntilTextDisplayed("Detalles actualizados correctamente")
        composeTestRule.onNodeWithText("Aceptar").performClick()
    }

    @Test
    fun editarViaje_enEstadoFinalizado_bloqueaEdicion() {
        val finishedTrip = awaitingTripDto(43).copy(state = "FINISHED")
        val tripService = FakeTripService(getTrip = { id, _ -> successResponse(finishedTrip.copy(id = id)) })
        val tripRepository = TripRepository(tripService, FakeExpenseService())
        val driverRepository = DriverRepository(FakeDriverService())
        val vehicleRepository = VehicleRepository(FakeVehicleService())
        val clientRepository = ClientRepository(FakeClientService())

        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = "edit_trip") {
                composable("edit_trip") {
                    TripEditScreen(
                        tripId = 43,
                        tripRepository = tripRepository,
                        driverRepository = driverRepository,
                        vehicleRepository = vehicleRepository,
                        clientRepository = clientRepository,
                        navController = navController
                    )
                }
            }
        }

        composeTestRule.waitUntilTextDisplayed("Este viaje no puede editarse en estado FINALIZADO")
        composeTestRule.onNodeWithText("Actualizar Detalles").assertIsNotEnabled()
    }
}
