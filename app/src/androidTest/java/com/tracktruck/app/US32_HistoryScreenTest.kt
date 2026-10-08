package com.tracktruck.app

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tracktruck.app.core.common.Constants
import com.tracktruck.app.core.common.Routes
import com.tracktruck.app.core.data.remote.auditlog.AuditLogDto
import com.tracktruck.app.core.data.repository.AuditLogRepository
import com.tracktruck.app.core.presentation.history.HistoryDetailScreen
import com.tracktruck.app.core.presentation.history.HistoryScreen
import com.tracktruck.app.testsupport.FakeAuditLogService
import com.tracktruck.app.testsupport.resetAppSessionState
import com.tracktruck.app.testsupport.successResponse
import com.tracktruck.app.testsupport.waitUntilTextDisplayed
import com.tracktruck.app.testsupport.waitUntilTextGone
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * US32 - Auditoría de cambios.
 * Como empresario de gestión logística, quiero visualizar el registro de auditoría de mis
 * viajes, vehículos, conductores, alertas y gastos para tener un registro trazable de todos los
 * cambios realizados y garantizar la transparencia con mis clientes.
 */
@RunWith(AndroidJUnit4::class)
class US32_HistoryScreenTest {

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
    fun empresario_visualizaFiltraYAccedeAlDetalleDeUnRegistroDeAuditoria() {
        val logs = listOf(
            AuditLogDto(
                id = "log-1", entityType = "VEHICLE", action = "CREATE", timestamp = "2026-07-01T10:00:00",
                modifiedFields = mapOf("Name" to "Volvo FH16", "Plate" to "ABC-123"), entrepreneurId = 1
            ),
            AuditLogDto(
                id = "log-2", entityType = "DRIVER", action = "UPDATE", timestamp = "2026-07-02T11:30:00",
                modifiedFields = mapOf(
                    "before" to mapOf("ContactNumber" to "999111222"),
                    "after" to mapOf("ContactNumber" to "999888777", "Name" to "Juan Perez")
                ),
                entrepreneurId = 1
            ),
            AuditLogDto(
                id = "log-3", entityType = "EXPENSE", action = "DELETE", timestamp = "2026-07-03T09:00:00",
                modifiedFields = mapOf("FuelDescription" to "Combustible viaje"), entrepreneurId = 1
            )
        )
        val auditLogService = FakeAuditLogService(
            getAuditLogsByEntrepreneur = { _, _ -> successResponse(logs) }
        )
        val auditLogRepository = AuditLogRepository(auditLogService)

        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = Routes.History.routes) {
                composable(Routes.History.routes) {
                    HistoryScreen(auditLogRepository = auditLogRepository, navController = navController)
                }
                composable("history_detail") {
                    HistoryDetailScreen(navController = navController)
                }
            }
        }

        composeTestRule.waitUntilTextDisplayed("Se ha creado un nuevo vehiculo.")
        composeTestRule.onNodeWithText("CONDUCTOR Juan Perez ha sido actualizado.").assertIsDisplayed()
        composeTestRule.onNodeWithText("GASTO Combustible viaje ha sido eliminado. (Desactivado)").assertIsDisplayed()

        // DELETE logs never expose a "Ver más" detail button, only the CREATE and UPDATE logs do.
        composeTestRule.onAllNodesWithText("Ver más").assertCountEquals(2)

        composeTestRule.onNode(hasText("ACTUALIZAR") and hasClickAction()).performClick()
        composeTestRule.waitUntilTextGone("Se ha creado un nuevo vehiculo.")
        composeTestRule.onNodeWithText("CONDUCTOR Juan Perez ha sido actualizado.").assertIsDisplayed()

        composeTestRule.onNodeWithText("Ver más").performClick()

        composeTestRule.waitUntilTextDisplayed("Cambios realizados")
        composeTestRule.onNodeWithText("Número de contacto").assertIsDisplayed()
        composeTestRule.onNodeWithText("999111222").assertIsDisplayed()
        composeTestRule.onNodeWithText("999888777").assertIsDisplayed()
    }
}
