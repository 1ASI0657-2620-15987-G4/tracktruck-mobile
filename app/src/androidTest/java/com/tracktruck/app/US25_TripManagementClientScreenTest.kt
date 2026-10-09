package com.tracktruck.app

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tracktruck.app.core.common.Constants
import com.tracktruck.app.core.data.remote.trip.TripDto
import com.tracktruck.app.core.data.repository.OngoingTripRepository
import com.tracktruck.app.core.data.repository.TripRepository
import com.tracktruck.app.core.presentation.trips.trip.listTrip.TripManagementScreen
import com.tracktruck.app.testsupport.FakeExpenseService
import com.tracktruck.app.testsupport.FakeOngoingTripService
import com.tracktruck.app.testsupport.FakeTripService
import com.tracktruck.app.testsupport.resetAppSessionState
import com.tracktruck.app.testsupport.successResponse
import com.tracktruck.app.testsupport.waitUntilTextDisplayed
import com.tracktruck.app.testsupport.waitUntilTextGone
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * US25 - Visualización de viajes del cliente.
 * Como cliente de empresa logística, quiero visualizar todos mis envíos registrados para hacer
 * seguimiento de mis despachos y acceder al detalle de cada uno.
 */
@RunWith(AndroidJUnit4::class)
class US25_TripManagementClientScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: NavHostController

    @Before
    fun setUp() {
        resetAppSessionState()
        Constants.TOKEN = "fake-token"
        Constants.USER_ROLE = "CLIENT"
        Constants.CLIENT_ID = 3
    }

    @Test
    fun cliente_visualizaSusEnviosYAccedeAlDetalle() {
        val tripService = FakeTripService(
            getTripsByClient = { clientId, _ ->
                successResponse(
                    listOf(
                        TripDto(id = 1, name = "Envio A", state = "AWAITING", type = "ESTANDAR", weight = 100.0, loadLocation = "Lima", loadDate = "2026-08-01T08:00:00", unloadLocation = "Arequipa", unloadDate = "2026-08-02T08:00:00", driverId = 5, vehicleId = 7, clientId = clientId, entrepreneurId = 1),
                        TripDto(id = 2, name = "Envio B", state = "PROGRESS", type = "FRAGIL", weight = 50.0, loadLocation = "Lima", loadDate = "2026-08-03T08:00:00", unloadLocation = "Trujillo", unloadDate = "2026-08-04T08:00:00", driverId = 6, vehicleId = 8, clientId = clientId, entrepreneurId = 1)
                    )
                )
            }
        )

        val tripRepository = TripRepository(tripService, FakeExpenseService())
        val ongoingTripRepository = OngoingTripRepository(FakeOngoingTripService())

        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = "trips") {
                composable("trips") {
                    TripManagementScreen(tripRepository = tripRepository, ongoingTripRepository = ongoingTripRepository, navController = navController)
                }
                composable("trip_details/{tripId}") {}
                composable("gps/{tripId}") {}
            }
        }

        composeTestRule.waitUntilTextDisplayed("Envio A")
        composeTestRule.onNodeWithText("Envio B").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Agregar viaje").assertDoesNotExist()

        composeTestRule.onNodeWithText("EN PROGRESO").performClick()
        composeTestRule.waitUntilTextDisplayed("Envio B")
        composeTestRule.waitUntilTextGone("Envio A")

        composeTestRule.onNodeWithText("GPS").performClick()
        composeTestRule.waitForIdle()
        assert(navController.currentDestination?.route == "gps/{tripId}") {
            "Expected navigation to the GPS screen, was ${navController.currentDestination?.route}"
        }
    }
}
