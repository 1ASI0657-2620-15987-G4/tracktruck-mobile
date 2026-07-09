package com.cargoexpress.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.data.remote.alert.AlertDto
import com.cargoexpress.app.core.data.remote.trip.TripDto
import com.cargoexpress.app.core.data.repository.AlertRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.presentation.trips.alert.AlertScreen
import com.cargoexpress.app.testsupport.FakeAlertService
import com.cargoexpress.app.testsupport.FakeExpenseService
import com.cargoexpress.app.testsupport.FakeTripService
import com.cargoexpress.app.testsupport.resetAppSessionState
import com.cargoexpress.app.testsupport.successResponse
import com.cargoexpress.app.testsupport.waitUntilTextDisplayed
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * US11 - Registro y visualización de alertas de viaje.
 * Como empresario de gestión logística quiero recibir una alerta sobre cualquier evento
 * importante que pueda afectar la entrega para minimizar cualquier impacto en mi operación.
 */
@RunWith(AndroidJUnit4::class)
class US11_AlertScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: NavHostController
    private val tripId = 88

    private fun inProgressTrip() = TripDto(
        id = tripId, name = "Viaje Callao", state = "PROGRESS", type = "ESTANDAR", weight = 300.0,
        loadLocation = "Lima", loadDate = "2026-08-01T08:00:00", unloadLocation = "Callao", unloadDate = "2026-08-01T12:00:00",
        driverId = 5, vehicleId = 7, clientId = 9, entrepreneurId = 1
    )

    @Before
    fun setUp() {
        resetAppSessionState()
        Constants.TOKEN = "fake-token"
        Constants.USER_ROLE = "ENTREPRENEUR"
    }

    @Test
    fun empresario_registraNuevaAlertaDuranteElViaje() {
        val storedAlerts = mutableListOf<AlertDto>()
        val tripService = FakeTripService(getTrip = { id, _ -> successResponse(inProgressTrip().copy(id = id)) })
        val alertService = FakeAlertService(
            getAlertsByTripId = { _, _ -> successResponse(storedAlerts.toList()) },
            createAlert = { _, dto ->
                val created = AlertDto(id = storedAlerts.size + 1, title = dto.title, type = dto.type, description = dto.description, date = dto.date, tripId = dto.tripId)
                storedAlerts.add(created)
                successResponse(created)
            }
        )

        val tripRepository = TripRepository(tripService, FakeExpenseService())
        val alertRepository = AlertRepository(alertService)

        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = "alert") {
                composable("alert") {
                    AlertScreen(tripId = tripId, tripRepository = tripRepository, navController = navController, alertRepository = alertRepository)
                }
                composable("gps/{tripId}") {}
            }
        }

        composeTestRule.waitUntilTextDisplayed("No se encontraron alertas\nNada que reportar capitan")
        composeTestRule.onNodeWithText("Usa el botón + para crear una alerta").assertIsDisplayed()

        composeTestRule.onNodeWithText("Nueva Alerta", useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithText("Título").performTextInput("Retraso en la ruta")
        composeTestRule.onNodeWithText("Tipo").performClick()
        composeTestRule.onNodeWithText("INCIDENTE").performClick()
        composeTestRule.onNodeWithText("Descripción").performTextInput("Congestión vehicular en la Panamericana")

        composeTestRule.onNodeWithText("Crear Alerta").performClick()

        composeTestRule.waitUntilTextDisplayed("Alerta registrada correctamente")
        composeTestRule.onNodeWithText("Aceptar").performClick()

        composeTestRule.waitUntilTextDisplayed("Retraso en la ruta")
        composeTestRule.onNodeWithText("Congestión vehicular en la Panamericana").assertIsDisplayed()
    }

    @Test
    fun empresario_visualizaAlertasExistentesDelViaje() {
        val tripService = FakeTripService(getTrip = { id, _ -> successResponse(inProgressTrip().copy(id = id)) })
        val alertService = FakeAlertService(
            getAlertsByTripId = { _, _ ->
                successResponse(
                    listOf(
                        AlertDto(id = 1, title = "Motor sobrecalentado", type = "MANTENIMIENTO", description = "Se detectó sobrecalentamiento del motor", date = "2026-08-01T09:30:00", tripId = tripId)
                    )
                )
            }
        )

        val tripRepository = TripRepository(tripService, FakeExpenseService())
        val alertRepository = AlertRepository(alertService)

        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = "alert") {
                composable("alert") {
                    AlertScreen(tripId = tripId, tripRepository = tripRepository, navController = navController, alertRepository = alertRepository)
                }
                composable("gps/{tripId}") {}
            }
        }

        composeTestRule.waitUntilTextDisplayed("Motor sobrecalentado")
        composeTestRule.onNodeWithText("MANTENIMIENTO").assertIsDisplayed()
        composeTestRule.onNodeWithText("Se detectó sobrecalentamiento del motor").assertIsDisplayed()
    }
}
