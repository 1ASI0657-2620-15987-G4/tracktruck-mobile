package com.cargoexpress.app

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.data.remote.driver.DriverDto
import com.cargoexpress.app.core.data.remote.trip.TripDto
import com.cargoexpress.app.core.data.remote.user.ClientDto
import com.cargoexpress.app.core.data.remote.vehicle.VehicleDto
import com.cargoexpress.app.core.data.repository.ClientRepository
import com.cargoexpress.app.core.data.repository.DriverRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.data.repository.VehicleRepository
import com.cargoexpress.app.core.presentation.trips.trip.registerTrip.RegisterTripScreen
import com.cargoexpress.app.core.presentation.trips.trip.registerTrip.RegisterTripViewModel
import com.cargoexpress.app.testsupport.FakeClientService
import com.cargoexpress.app.testsupport.FakeDriverService
import com.cargoexpress.app.testsupport.FakeExpenseService
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
 * US01 - Registro de un nuevo viaje.
 * Como empresario de gestión logística, quiero registrar los datos de un nuevo viaje
 * para tener un registro guardado y mostrar transparencia a mis clientes.
 *
 * RegisterTripScreen now passes its `viewModel` explicitly into DriverModal/VehicleModal
 * (mirroring TripEditScreen's EditDriverModal/EditVehicleModal), so the same fake-backed
 * instance we build here is guaranteed to be the one those pickers use too.
 */
@RequiresApi(Build.VERSION_CODES.O)
@RunWith(AndroidJUnit4::class)
class US01_RegisterTripScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: NavHostController

    @Before
    fun setUp() {
        resetAppSessionState()
        Constants.TOKEN = "fake-token"
        Constants.ENTREPRENEUR_ID = 1
    }

    @Test
    fun registrarViaje_conDatosValidos_muestraConfirmacionYVuelveAlaListaDeViajes() {
        val driverService = FakeDriverService(
            getDriversByEntrepreneur = { _, _ ->
                successResponse(listOf(DriverDto(id = 5, name = "Juan Perez", dni = "10203040", license = "L-1", contactNumber = "999888777", state = "AVAILABLE", entrepreneurId = 1)))
            }
        )
        val vehicleService = FakeVehicleService(
            getVehiclesByEntrepreneur = { _, _ ->
                successResponse(listOf(VehicleDto(id = 7, name = "Volvo FH16", model = "FH16", plate = "ABC-123", tractorPlate = "XYZ-987", maxLoad = 1000.0, volume = 50.0, state = "AVAILABLE", entrepreneurId = 1)))
            }
        )
        val clientService = FakeClientService(
            getClientByDni = { dni, _ ->
                successResponse(ClientDto(id = 9, name = "Carlos Ramirez", dni = dni, birthDate = "1990-01-01T00:00:00", userId = 2))
            }
        )
        val tripService = FakeTripService(
            addTrip = { _, dto ->
                successResponse(TripDto(id = 100, name = dto.name, state = "AWAITING", type = dto.type, weight = dto.weight, loadLocation = dto.loadLocation, loadDate = dto.loadDate, unloadLocation = dto.unloadLocation, unloadDate = dto.unloadDate, driverId = dto.driverId, vehicleId = dto.vehicleId, clientId = dto.clientId, entrepreneurId = dto.entrepreneurId))
            }
        )

        val viewModel = RegisterTripViewModel(
            tripRepository = TripRepository(tripService, FakeExpenseService()),
            driverRepository = DriverRepository(driverService),
            vehicleRepository = VehicleRepository(vehicleService),
            clientRepository = ClientRepository(clientService)
        )

        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = "register_trip") {
                composable("register_trip") {
                    RegisterTripScreen(viewModel = viewModel, navController = navController) {}
                }
                composable("trips") {}
            }
        }

        composeTestRule.onNodeWithText("Nombre del Viaje").performTextInput("Viaje Lima-Arequipa")

        composeTestRule.onNodeWithText("Tipo de Carga").performClick()
        composeTestRule.onNodeWithText("ESTANDAR").performClick()

        composeTestRule.onNodeWithText("Peso (kg)").performTextInput("500")

        composeTestRule.onNodeWithText("Seleccionar Conductor").performClick()
        composeTestRule.waitUntilTextDisplayed("Juan Perez")
        composeTestRule.onNodeWithText("Juan Perez").performClick()

        composeTestRule.onNodeWithText("Seleccionar Vehículo").performClick()
        composeTestRule.waitUntilTextDisplayed("Volvo FH16")
        composeTestRule.onNodeWithText("Volvo FH16").performClick()

        composeTestRule.onNodeWithText("DNI del Cliente").performTextInput("12345678")
        composeTestRule.onNodeWithContentDescription("Verificar DNI").performClick()
        composeTestRule.waitUntilTextDisplayed("Cliente: Carlos Ramirez")
        composeTestRule.onNodeWithText("Cliente: Carlos Ramirez").assertIsDisplayed()

        composeTestRule.onNodeWithText("Ubicación de Carga").performScrollTo().performTextInput("Lima")
        composeTestRule.onNodeWithText("Ubicación de Descarga").performScrollTo().performTextInput("Arequipa")

        composeTestRule.onNodeWithText("Registrar Viaje").assertIsNotEnabled()

        composeTestRule.onNodeWithTag("loadDateField").performScrollTo().performClick()
        composeTestRule.waitUntilTextDisplayed("Aceptar")
        composeTestRule.onNodeWithText("Aceptar").performClick()
        composeTestRule.waitForIdle()
        onView(withId(android.R.id.button1)).perform(click())
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("unloadDateField").performScrollTo().performClick()
        composeTestRule.waitUntilTextDisplayed("Aceptar")
        composeTestRule.onNodeWithText("Aceptar").performClick()
        composeTestRule.waitForIdle()
        onView(withId(android.R.id.button1)).perform(click())
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Registrar Viaje").assertIsEnabled()
        composeTestRule.onNodeWithText("Registrar Viaje").performClick()

        composeTestRule.waitUntilTextDisplayed("Viaje registrado correctamente")
        composeTestRule.onNodeWithText("Aceptar").performClick()

        composeTestRule.waitForIdle()
        assert(navController.currentDestination?.route == "trips") {
            "Expected navigation to 'trips' after successful registration, was ${navController.currentDestination?.route}"
        }
    }

    @Test
    fun registrarViaje_camposObligatoriosVacios_botonPermaneceDeshabilitado() {
        val viewModel = RegisterTripViewModel(
            tripRepository = TripRepository(FakeTripService(), FakeExpenseService()),
            driverRepository = DriverRepository(FakeDriverService()),
            vehicleRepository = VehicleRepository(FakeVehicleService()),
            clientRepository = ClientRepository(FakeClientService())
        )

        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = "register_trip") {
                composable("register_trip") {
                    RegisterTripScreen(viewModel = viewModel, navController = navController) {}
                }
            }
        }

        composeTestRule.onNodeWithText("Registrar Viaje").assertIsNotEnabled()
    }
}
