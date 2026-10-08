package com.cargoexpress.app.testsupport

import com.cargoexpress.app.core.data.remote.login.LoginRequestDto
import com.cargoexpress.app.core.data.remote.login.LoginResponseDto
import com.cargoexpress.app.core.data.remote.login.LoginService
import com.cargoexpress.app.core.data.remote.register.RegisterClientRequestDto
import com.cargoexpress.app.core.data.remote.register.RegisterEntrepreneurRequestDto
import com.cargoexpress.app.core.data.remote.register.RegisterResponseDto
import com.cargoexpress.app.core.data.remote.register.RegisterService
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/** Outcome of a fake [retrofit2.Call]: either a synchronous HTTP response or a network failure. */
sealed class CallOutcome<T> {
    data class Success<T>(val response: Response<T>) : CallOutcome<T>()
    data class NetworkFailure<T>(val throwable: Throwable) : CallOutcome<T>()
}

/**
 * LoginRepository/RegisterRepository use [Call.enqueue] (not suspend functions), so their
 * services can't be faked with plain suspend lambdas. This fake invokes the callback
 * synchronously with a pre-baked [CallOutcome].
 */
class FakeCall<T>(private val outcome: CallOutcome<T>) : Call<T> {
    override fun enqueue(callback: Callback<T>) {
        when (outcome) {
            is CallOutcome.Success -> callback.onResponse(this, outcome.response)
            is CallOutcome.NetworkFailure -> callback.onFailure(this, outcome.throwable)
        }
    }

    override fun execute(): Response<T> = when (outcome) {
        is CallOutcome.Success -> outcome.response
        is CallOutcome.NetworkFailure -> throw outcome.throwable
    }

    override fun clone(): Call<T> = FakeCall(outcome)
    override fun isExecuted(): Boolean = false
    override fun isCanceled(): Boolean = false
    override fun cancel() {}
    override fun request(): Request = Request.Builder().url("http://localhost/").build()
    override fun timeout(): Timeout = Timeout.NONE
}

class FakeLoginService(
    var signIn: (LoginRequestDto) -> CallOutcome<LoginResponseDto> = {
        CallOutcome.Success(successResponse(LoginResponseDto(id = 1, username = it.username, token = "fake-token")))
    }
) : LoginService {
    override fun signIn(request: LoginRequestDto): Call<LoginResponseDto> = FakeCall(signIn.invoke(request))
}

class FakeRegisterService(
    var signUpClient: (RegisterClientRequestDto) -> CallOutcome<RegisterResponseDto> = {
        CallOutcome.Success(successResponse(RegisterResponseDto(message = "Cuenta creada")))
    },
    var signUpEntrepreneur: (RegisterEntrepreneurRequestDto) -> CallOutcome<RegisterResponseDto> = {
        CallOutcome.Success(successResponse(RegisterResponseDto(message = "Cuenta creada")))
    }
) : RegisterService {
    override fun signUpClient(request: RegisterClientRequestDto): Call<RegisterResponseDto> =
        FakeCall(signUpClient.invoke(request))

    override fun signUpEntrepreneur(request: RegisterEntrepreneurRequestDto): Call<RegisterResponseDto> =
        FakeCall(signUpEntrepreneur.invoke(request))
}
