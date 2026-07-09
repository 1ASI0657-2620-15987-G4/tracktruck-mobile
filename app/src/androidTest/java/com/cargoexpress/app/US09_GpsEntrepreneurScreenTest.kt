package com.cargoexpress.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
 * US09 - Seguimiento activo del viaje para empresario.
 * Como empresario de gestión logística, quiero visualizar el estado y una ubicación referencial
 * del viaje en curso mediante la plataforma, para identificar posibles imprevistos y tomar
 * acciones correctivas de manera oportuna.
 */
@RunWith(AndroidJUnit4::class)
class US09_GpsEntrepreneurScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: NavHostController
    private val tripId = 77

    @Before
    fun setUp() {
        resetAppSessionState()
        Constants.TOKEN = "fake-token"
        Constants.USER_ROLE = "ENTREPRENEUR"
    }

    @Test
    fun empresario_iniciaViajeYLuegoLoFinaliza() {
        var tripState = "AWAITING"
        val storedOngoingTrips = mutableListOf<OngoingTripDto>()

        val tripService = FakeTripService(
            getTrip = { id, _ ->
                successResponse(
                    TripDto(
                        id = id, name = "Viaje Callao", state = tripState, type = "ESTANDAR", weight = 300.0,
                        loadLocation = "Lima", loadDate = "2026-08-01T08:00:00", unloadLocation = "Callao", unloadDate = "2026-08-01T12:00:00",
                        driverId = 5, vehicleId = 7, clientId = 9, entrepreneurId = 1
                    )
                )
            },
            updateTripState = { id, _, dto ->
                tripState = dto.state
                successResponse(dto)
            }
        )
        val ongoingTripService = FakeOngoingTripService(
            getOngoingTrips = { successResponse(storedOngoingTrips.toList()) },
            createOngoingTrip = { _, dto ->
                val created = OngoingTripDto(id = 1, latitude = dto.latitude, longitude = dto.longitude, speed = dto.speed, distance = dto.distance, tripId = dto.tripId)
                storedOngoingTrips.add(created)
                successResponse(created)
            }
        )

        val tripRepository = TripRepository(tripService, FakeExpenseService())
        val ongoingTripRepository = OngoingTripRepository(ongoingTripService)
        val vehicleRepository = VehicleRepository(FakeVehicleService())
        val driverRepository = DriverRepository(FakeDriverService())

        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = "gps") {
                composable("gps") {
                    GpsScreen(
                        tripId = tripId,
                        tripRepository = tripRepository,
                        navController = navController,
                        ongoingTripRepository = ongoingTripRepository,
                        vehicleRepository = vehicleRepository,
                        driverRepository = driverRepository
                    )
                }
                composable("trips") {}
                composable("home") {}
                composable("alert/{tripId}") {}
            }
        }

        composeTestRule.waitUntilTextDisplayed("No hay viaje activo. Inicia el viaje para rastrear.")
        composeTestRule.onNodeWithText("Iniciar Viaje").assertIsDisplayed()

        composeTestRule.onNodeWithText("Iniciar Viaje").performClick()

        composeTestRule.waitUntilTextDisplayed("Latitud")
        composeTestRule.onNodeWithText("Velocidad").assertIsDisplayed()
        composeTestRule.onNodeWithText("Finalizar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancelar").assertIsDisplayed()

        composeTestRule.onNodeWithText("Finalizar").performClick()
        composeTestRule.waitUntilTextDisplayed("¿Estás seguro de que deseas finalizar el viaje? Esta acción no se puede deshacer.")
        composeTestRule.onNodeWithText("Sí, finalizar").performClick()

        composeTestRule.waitUntilTextDisplayed("El viaje ha finalizado correctamente.")
        composeTestRule.onNodeWithText("Aceptar").performClick()

        composeTestRule.waitForIdle()
        assert(navController.currentDestination?.route == "trips") {
            "Expected navigation to 'trips' after finishing the trip, was ${navController.currentDestination?.route}"
        }
    }
}
