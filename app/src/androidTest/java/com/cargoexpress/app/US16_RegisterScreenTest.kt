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
import com.cargoexpress.app.core.data.remote.register.RegisterResponseDto
import com.cargoexpress.app.core.data.repository.RegisterRepository
import com.cargoexpress.app.core.presentation.auth.register.RegisterScreen
import com.cargoexpress.app.core.presentation.auth.register.RegisterViewModel
import com.cargoexpress.app.testsupport.CallOutcome
import com.cargoexpress.app.testsupport.FakeRegisterService
import com.cargoexpress.app.testsupport.resetAppSessionState
import com.cargoexpress.app.testsupport.successResponse
import com.cargoexpress.app.testsupport.waitUntilTextDisplayed
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * US16 - Registro de usuario.
 * Como usuario quiero poder registrarme en la aplicación seleccionando mi rol (empresario o
 * cliente), para tener acceso autorizado y personalizado según mi tipo de cuenta.
 */
@RunWith(AndroidJUnit4::class)
class US16_RegisterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: NavHostController
    private var registeredUsername: String? = null
    private var registeredPassword: String? = null

    @Before
    fun setUp() {
        resetAppSessionState()
        registeredUsername = null
        registeredPassword = null
    }

    @Test
    fun registrarUsuario_comoCliente_creaLaCuentaCorrectamente() {
        val registerService = FakeRegisterService(
            signUpClient = { CallOutcome.Success(successResponse(RegisterResponseDto("Cuenta creada"))) }
        )
        val viewModel = RegisterViewModel(RegisterRepository(registerService))

        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = "register") {
                composable("register") {
                    RegisterScreen(
                        navController = navController,
                        viewModel = viewModel,
                        onRegisterSuccess = { username, password ->
                            registeredUsername = username
                            registeredPassword = password
                        }
                    )
                }
                composable("Login") {}
                composable("TermsAndConditions") {}
            }
        }

        composeTestRule.onNodeWithText("Cliente").assertIsDisplayed()

        composeTestRule.onNodeWithText("Correo electrónico").performTextInput("cliente@test.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("Passw0rd!")
        composeTestRule.onNodeWithText("Nombre completo").performTextInput("Cliente De Prueba")
        composeTestRule.onNodeWithText("Celular").performTextInput("987654321")
        composeTestRule.onNodeWithText("DNI").performTextInput("12345678")

        composeTestRule.onNodeWithText("Fecha de nacimiento", useUnmergedTree = true).performClick()
        composeTestRule.waitUntilTextDisplayed("Aceptar")
        composeTestRule.onNodeWithText("Aceptar").performClick()

        composeTestRule.onNodeWithText("Crear Cuenta").performClick()

        composeTestRule.waitUntilTextDisplayed("¡Cuenta creada exitosamente!")
        composeTestRule.onNodeWithText("Aceptar").performClick()

        composeTestRule.waitForIdle()
        assertEquals("cliente@test.com", registeredUsername)
        assertEquals("Passw0rd!", registeredPassword)
    }

    @Test
    fun registrarUsuario_seleccionaRolEmpresario_muestraCamposDeEmpresa() {
        val viewModel = RegisterViewModel(RegisterRepository(FakeRegisterService()))

        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = "register") {
                composable("register") {
                    RegisterScreen(navController = navController, viewModel = viewModel, onRegisterSuccess = { _, _ -> })
                }
            }
        }

        composeTestRule.onNodeWithText("Empresario").performClick()

        composeTestRule.onNodeWithText("Nombre de la empresa").assertIsDisplayed()
        composeTestRule.onNodeWithText("RUC").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dirección").assertIsDisplayed()
    }
}
