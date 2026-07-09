package com.cargoexpress.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.data.remote.ongoingtrip.OngoingTripDto
import com.cargoexpress.app.core.data.remote.trip.TripDto
import com.cargoexpress.app.core.data.repository.DriverRepository
import com.cargoexpress.app.core.data.repository.OngoingTripRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.data.repository.VehicleRepository
import com.cargoexpress.app.core.presentation.trips.gps.GpsScreen
import com.cargoexpress.app.testsupport.FakeDriverService
import com.cargoexpress.app.testsupport.FakeExpenseService
import com.cargoexpress.app.testsupport.FakeOngoingTripService
import com.cargoexpress.app.testsupport.FakeTripService
import com.cargoexpress.app.testsupport.FakeVehicleService
import com.cargoexpress.app.testsupport.resetAppSessionState
import com.cargoexpress.app.testsupport.successResponse
import com.cargoexpress.app.testsupport.waitUntilTextDisplayed
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * US28 - Seguimiento activo de viajes para cliente.
 * Como cliente de empresa logística, quiero consultar el estado y una ubicación referencial de
 * mi envío mediante la plataforma, para obtener información sobre el progreso de mi mercancía
 * durante el transporte.
 */
@RunWith(AndroidJUnit4::class)
class US28_GpsClientScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: NavHostController
    private val tripId = 66

    @Before
    fun setUp() {
        resetAppSessionState()
        Constants.TOKEN = "fake-token"
        Constants.USER_ROLE = "CLIENT"
    }

    private fun trip(state: String) = TripDto(
        id = tripId, name = "Envio Cliente", state = state, type = "ESTANDAR", weight = 150.0,
        loadLocation = "Lima", loadDate = "2026-08-01T08:00:00", unloadLocation = "Cusco", unloadDate = "2026-08-02T08:00:00",
        driverId = 5, vehicleId = 7, clientId = 3, entrepreneurId = 1
    )

    @Test
    fun cliente_sinViajeActivo_noVeControlesDeEmpresario() {
        val tripRepository = TripRepository(FakeTripService(getTrip = { id, _ -> successResponse(trip("AWAITING").copy(id = id)) }), FakeExpenseService())
        val ongoingTripRepository = OngoingTripRepository(FakeOngoingTripService(getOngoingTrips = { successResponse(emptyList()) }))

        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = "gps") {
                composable("gps") {
                    GpsScreen(
                        tripId = tripId,
                        tripRepository = tripRepository,
                        navController = navController,
                        ongoingTripRepository = ongoingTripRepository,
                        vehicleRepository = VehicleRepository(FakeVehicleService()),
                        driverRepository = DriverRepository(FakeDriverService())
                    )
                }
                composable("trips") {}
                composable("home") {}
                composable("alert/{tripId}") {}
            }
        }

        composeTestRule.waitUntilTextDisplayed("Sin viaje activo.")
        composeTestRule.onNodeWithText("Iniciar Viaje").assertDoesNotExist()
    }

    @Test
    fun cliente_consultaUbicacionYEstadoDeSuEnvioEnCurso() {
        val ongoingTrip = OngoingTripDto(id = 1, latitude = -12.01f, longitude = -77.02f, speed = 45, distance = 8000, tripId = tripId)
        val tripRepository = TripRepository(FakeTripService(getTrip = { id, _ -> successResponse(trip("PROGRESS").copy(id = id)) }), FakeExpenseService())
        val ongoingTripRepository = OngoingTripRepository(FakeOngoingTripService(getOngoingTrips = { successResponse(listOf(ongoingTrip)) }))

        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = "gps") {
                composable("gps") {
                    GpsScreen(
                        tripId = tripId,
                        tripRepository = tripRepository,
                        navController = navController,
                        ongoingTripRepository = ongoingTripRepository,
                        vehicleRepository = VehicleRepository(FakeVehicleService()),
                        driverRepository = DriverRepository(FakeDriverService())
                    )
                }
                composable("trips") {}
                composable("home") {}
                composable("alert/{tripId}") {}
            }
        }

        composeTestRule.waitUntilTextDisplayed("Envio Cliente")
        composeTestRule.waitUntilTextDisplayed("Latitud")
        composeTestRule.onNodeWithText("Velocidad").assertIsDisplayed()
        composeTestRule.onNodeWithText("Distancia").assertIsDisplayed()

        composeTestRule.onNodeWithText("Finalizar").assertDoesNotExist()
        composeTestRule.onNodeWithText("Cancelar").assertDoesNotExist()
    }
}
