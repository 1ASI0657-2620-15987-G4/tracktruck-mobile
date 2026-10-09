package com.tracktruck.app.testsupport

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import com.tracktruck.app.core.common.Constants
import com.tracktruck.app.core.common.SelectedLogHolder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

fun <T> successResponse(body: T): Response<T> = Response.success(body)

fun <T> errorResponse(code: Int = 400, message: String = "Error de prueba"): Response<T> {
    val json = "{\"message\":\"$message\"}"
    val body = json.toResponseBody("application/json".toMediaTypeOrNull())
    return Response.error(code, body)
}

/**
 * Constants/SelectedLogHolder are process-wide singletons the app mutates on login/navigation.
 * Reset them before every test so scenarios don't leak into each other.
 */
fun resetAppSessionState() {
    Constants.TOKEN = ""
    Constants.USER_ID = 0
    Constants.USER_NAME = ""
    Constants.PROFILE_NAME = ""
    Constants.USER_PHONE = ""
    Constants.USER_ROLE = ""
    Constants.ENTREPRENEUR_ID = 0
    Constants.CLIENT_ID = 0
    Constants.TRIP_ID = 0
    SelectedLogHolder.log = null
}

/**
 * Repository calls in this app hop to Dispatchers.IO for real network work; the fakes complete
 * almost instantly but still asynchronously relative to the test thread. Compose's own
 * waitForIdle() only tracks main-thread/recomposition work, so polling for the expected text is
 * the reliable way to wait for a fake-backed async load/action to finish.
 */
fun ComposeTestRule.waitUntilTextDisplayed(text: String, timeoutMillis: Long = 8_000) {
    waitUntil(timeoutMillis) {
        onAllNodesWithText(text, substring = false, useUnmergedTree = true)
            .fetchSemanticsNodes().isNotEmpty()
    }
}

fun ComposeTestRule.waitUntilTextGone(text: String, timeoutMillis: Long = 8_000) {
    waitUntil(timeoutMillis) {
        onAllNodesWithText(text, substring = false, useUnmergedTree = true)
            .fetchSemanticsNodes().isEmpty()
    }
}
