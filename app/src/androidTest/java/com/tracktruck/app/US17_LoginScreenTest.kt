package com.tracktruck.app

import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tracktruck.app.core.common.Constants
import com.tracktruck.app.core.common.Routes
import com.tracktruck.app.core.data.remote.login.LoginResponseDto
import com.tracktruck.app.core.data.remote.user.ClientDto
import com.tracktruck.app.core.data.remote.user.UserDto
import com.tracktruck.app.core.data.remote.user.UserRoleDto
import com.tracktruck.app.core.data.repository.ClientRepository
import com.tracktruck.app.core.data.repository.EntrepreneurRepository
import com.tracktruck.app.core.data.repository.LoginRepository
import com.tracktruck.app.core.data.repository.UserRepository
import com.tracktruck.app.core.presentation.auth.login.LoginScreen
import com.tracktruck.app.core.presentation.auth.login.LoginViewModel
import com.tracktruck.app.testsupport.CallOutcome
import com.tracktruck.app.testsupport.FakeClientService
import com.tracktruck.app.testsupport.FakeEntrepreneurService
import com.tracktruck.app.testsupport.FakeLoginService
import com.tracktruck.app.testsupport.FakeUserService
import com.tracktruck.app.testsupport.resetAppSessionState
import com.tracktruck.app.testsupport.successResponse
import com.tracktruck.app.testsupport.waitUntilTextDisplayed
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * US17 - Inicio de sesión.
 * Como usuario quiero acceder a mi cuenta registrada para acceder a las funciones de la
 * aplicación.
 */
@RunWith(AndroidJUnit4::class)
class US17_LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: NavHostController

    @Before
    fun setUp() {
        resetAppSessionState()
    }

    @Test
    fun iniciarSesion_conCredencialesValidas_navegaAlHomeYGuardaLaSesion() {
        val loginService = FakeLoginService(
            signIn = { req -> CallOutcome.Success(successResponse(LoginResponseDto(id = 1, username = req.username, token = "fake-session-token"))) }
        )
        val userService = FakeUserService(
            getUser = { id, _ -> successResponse(UserDto(id = id, username = "cliente@test.com", phone = "987654321", state = true, modifiedAt = "2026-01-01T00:00:00")) },
            getUserRole = { _, _ -> successResponse(UserRoleDto(role = false)) }
        )
        val clientService = FakeClientService(
            getAllClients = { successResponse(listOf(ClientDto(id = 3, name = "Cliente Uno", dni = "12345678", birthDate = "1990-01-01T00:00:00", userId = 1))) }
        )

        composeTestRule.setContent {
            navController = rememberNavController()
            val viewModel = remember {
                LoginViewModel(
                    navController,
                    LoginRepository(loginService),
                    UserRepository(userService),
                    EntrepreneurRepository(FakeEntrepreneurService()),
                    ClientRepository(clientService)
                )
            }
            NavHost(navController = navController, startDestination = Routes.Login.routes) {
                composable(Routes.Login.routes) { LoginScreen(viewModel = viewModel, navController = navController) }
                composable(Routes.Home.routes) {}
            }
        }

        composeTestRule.onNodeWithText("Correo electrónico").performTextInput("cliente@test.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("Secret123!")
        composeTestRule.onNodeWithText("Iniciar Sesión").performClick()

        composeTestRule.waitUntil(8_000) { navController.currentDestination?.route == Routes.Home.routes }

        assertEquals("CLIENT", Constants.USER_ROLE)
        assertEquals(3, Constants.CLIENT_ID)
        assertEquals("fake-session-token", Constants.TOKEN)
    }

    @Test
    fun iniciarSesion_conCredencialesInvalidas_muestraMensajeDeError() {
        val loginService = FakeLoginService(
            signIn = { CallOutcome.NetworkFailure(Exception("Credenciales inválidas")) }
        )

        composeTestRule.setContent {
            navController = rememberNavController()
            val viewModel = remember {
                LoginViewModel(
                    navController,
                    LoginRepository(loginService),
                    UserRepository(FakeUserService()),
                    EntrepreneurRepository(FakeEntrepreneurService()),
                    ClientRepository(FakeClientService())
                )
            }
            NavHost(navController = navController, startDestination = Routes.Login.routes) {
                composable(Routes.Login.routes) { LoginScreen(viewModel = viewModel, navController = navController) }
                composable(Routes.Home.routes) {}
            }
        }

        composeTestRule.onNodeWithText("Correo electrónico").performTextInput("cliente@test.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("WrongPass1!")
        composeTestRule.onNodeWithText("Iniciar Sesión").performClick()

        composeTestRule.waitUntilTextDisplayed("Correo y/o contraseña incorrectos")
    }
}
