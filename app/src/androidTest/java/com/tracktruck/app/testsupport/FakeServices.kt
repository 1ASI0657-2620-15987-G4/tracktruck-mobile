package com.tracktruck.app.testsupport

import com.tracktruck.app.core.data.remote.alert.AlertDto
import com.tracktruck.app.core.data.remote.alert.AlertPostDto
import com.tracktruck.app.core.data.remote.alert.AlertService
import com.tracktruck.app.core.data.remote.auditlog.AuditLogDto
import com.tracktruck.app.core.data.remote.auditlog.AuditLogService
import com.tracktruck.app.core.data.remote.driver.DriverDto
import com.tracktruck.app.core.data.remote.driver.DriverPostDto
import com.tracktruck.app.core.data.remote.driver.DriverService
import com.tracktruck.app.core.data.remote.driver.DriverStateUpdateDto
import com.tracktruck.app.core.data.remote.driver.DriverUpdateDto
import com.tracktruck.app.core.data.remote.expense.ExpenseDto
import com.tracktruck.app.core.data.remote.expense.ExpensePostDto
import com.tracktruck.app.core.data.remote.expense.ExpenseService
import com.tracktruck.app.core.data.remote.expense.ExpenseStateUpdateDto
import com.tracktruck.app.core.data.remote.ongoingtrip.OngoingTripDto
import com.tracktruck.app.core.data.remote.ongoingtrip.OngoingTripDtoPost
import com.tracktruck.app.core.data.remote.ongoingtrip.OngoingTripService
import com.tracktruck.app.core.data.remote.ongoingtrip.OngoingTripUpdateDto
import com.tracktruck.app.core.data.remote.trip.TripDetailsUpdateDto
import com.tracktruck.app.core.data.remote.trip.TripDto
import com.tracktruck.app.core.data.remote.trip.TripPostDto
import com.tracktruck.app.core.data.remote.trip.TripScheduleUpdateDto
import com.tracktruck.app.core.data.remote.trip.TripService
import com.tracktruck.app.core.data.remote.trip.TripStateUpdateDto
import com.tracktruck.app.core.data.remote.user.ClientDto
import com.tracktruck.app.core.data.remote.user.ClientService
import com.tracktruck.app.core.data.remote.user.EntrepreneurDto
import com.tracktruck.app.core.data.remote.user.EntrepreneurService
import com.tracktruck.app.core.data.remote.user.UserDto
import com.tracktruck.app.core.data.remote.user.UserRoleDto
import com.tracktruck.app.core.data.remote.user.UserService
import com.tracktruck.app.core.data.remote.user.UserStateUpdateDto
import com.tracktruck.app.core.data.remote.vehicle.VehicleDto
import com.tracktruck.app.core.data.remote.vehicle.VehicleNameUpdateDto
import com.tracktruck.app.core.data.remote.vehicle.VehiclePostDto
import com.tracktruck.app.core.data.remote.vehicle.VehicleService
import com.tracktruck.app.core.data.remote.vehicle.VehicleStateUpdateDto
import retrofit2.Response

class FakeTripService(
    var getTripsByEntrepreneur: suspend (Int, String) -> Response<List<TripDto>> = { _, _ -> successResponse(emptyList()) },
    var getTripsByClient: suspend (Int, String) -> Response<List<TripDto>> = { _, _ -> successResponse(emptyList()) },
    var getTrip: suspend (Int, String) -> Response<TripDto> = { _, _ -> errorResponse(404, "No encontrado") },
    var addTrip: suspend (String, TripPostDto) -> Response<TripDto> = { _, _ -> errorResponse(400, "No stubbed") },
    var updateTripDetails: suspend (Int, String, TripDetailsUpdateDto) -> Response<TripDetailsUpdateDto> = { _, _, dto -> successResponse(dto) },
    var updateTripSchedule: suspend (Int, String, TripScheduleUpdateDto) -> Response<TripScheduleUpdateDto> = { _, _, dto -> successResponse(dto) },
    var updateTripState: suspend (Int, String, TripStateUpdateDto) -> Response<TripStateUpdateDto> = { _, _, dto -> successResponse(dto) },
    var getAlertsByTripId: suspend (Int, String) -> Response<List<AlertDto>> = { _, _ -> successResponse(emptyList()) },
    var getOngoingTripByTripId: suspend (Int, String) -> Response<OngoingTripDto> = { _, _ -> errorResponse(404, "No encontrado") },
    var getExpenseByTripId: suspend (Int, String) -> Response<ExpenseDto> = { _, _ -> errorResponse(404, "No encontrado") }
) : TripService {
    override suspend fun getTripsByEntrepreneur(entrepreneurId: Int, token: String) = getTripsByEntrepreneur.invoke(entrepreneurId, token)
    override suspend fun getTripsByClient(clientId: Int, token: String) = getTripsByClient.invoke(clientId, token)
    override suspend fun getTrip(id: Int, token: String) = getTrip.invoke(id, token)
    override suspend fun addTrip(token: String, trip: TripPostDto) = addTrip.invoke(token, trip)
    override suspend fun updateTripDetails(id: Int, token: String, trip: TripDetailsUpdateDto) = updateTripDetails.invoke(id, token, trip)
    override suspend fun updateTripSchedule(id: Int, token: String, schedule: TripScheduleUpdateDto) = updateTripSchedule.invoke(id, token, schedule)
    override suspend fun updateTripState(id: Int, token: String, state: TripStateUpdateDto) = updateTripState.invoke(id, token, state)
    override suspend fun getAlertsByTripId(tripId: Int, token: String) = getAlertsByTripId.invoke(tripId, token)
    override suspend fun getOngoingTripByTripId(tripId: Int, token: String) = getOngoingTripByTripId.invoke(tripId, token)
    override suspend fun getExpenseByTripId(tripId: Int, token: String) = getExpenseByTripId.invoke(tripId, token)
}

class FakeDriverService(
    var getDriverById: suspend (Int, String) -> Response<DriverDto> = { _, _ -> errorResponse(404, "No encontrado") },
    var getDriversByEntrepreneur: suspend (Int, String) -> Response<List<DriverDto>> = { _, _ -> successResponse(emptyList()) },
    var addDriver: suspend (String, DriverPostDto) -> Response<DriverDto> = { _, _ -> errorResponse(400, "No stubbed") },
    var updateDriver: suspend (Int, String, DriverUpdateDto) -> Response<DriverUpdateDto> = { _, _, dto -> successResponse(dto) },
    var updateDriverState: suspend (Int, String, DriverStateUpdateDto) -> Response<DriverStateUpdateDto> = { _, _, dto -> successResponse(dto) }
) : DriverService {
    override suspend fun getDriverById(driverId: Int, token: String) = getDriverById.invoke(driverId, token)
    override suspend fun getDriversByEntrepreneur(entrepreneurId: Int, token: String) = getDriversByEntrepreneur.invoke(entrepreneurId, token)
    override suspend fun addDriver(token: String, driver: DriverPostDto) = addDriver.invoke(token, driver)
    override suspend fun updateDriver(driverId: Int, token: String, update: DriverUpdateDto) = updateDriver.invoke(driverId, token, update)
    override suspend fun updateDriverState(driverId: Int, token: String, update: DriverStateUpdateDto) = updateDriverState.invoke(driverId, token, update)
}

class FakeVehicleService(
    var getVehicleById: suspend (Int, String) -> Response<VehicleDto> = { _, _ -> errorResponse(404, "No encontrado") },
    var getVehiclesByEntrepreneur: suspend (Int, String) -> Response<List<VehicleDto>> = { _, _ -> successResponse(emptyList()) },
    var addVehicle: suspend (String, VehiclePostDto) -> Response<VehicleDto> = { _, _ -> errorResponse(400, "No stubbed") },
    var updateVehicleName: suspend (Int, String, VehicleNameUpdateDto) -> Response<VehicleNameUpdateDto> = { _, _, dto -> successResponse(dto) },
    var updateVehicleState: suspend (Int, String, VehicleStateUpdateDto) -> Response<VehicleStateUpdateDto> = { _, _, dto -> successResponse(dto) }
) : VehicleService {
    override suspend fun getVehicleById(vehicleId: Int, token: String) = getVehicleById.invoke(vehicleId, token)
    override suspend fun getVehiclesByEntrepreneur(entrepreneurId: Int, token: String) = getVehiclesByEntrepreneur.invoke(entrepreneurId, token)
    override suspend fun addVehicle(token: String, vehicle: VehiclePostDto) = addVehicle.invoke(token, vehicle)
    override suspend fun updateVehicleName(vehicleId: Int, token: String, update: VehicleNameUpdateDto) = updateVehicleName.invoke(vehicleId, token, update)
    override suspend fun updateVehicleState(vehicleId: Int, token: String, update: VehicleStateUpdateDto) = updateVehicleState.invoke(vehicleId, token, update)
}

class FakeClientService(
    var getAllClients: suspend (String) -> Response<List<ClientDto>> = { _ -> successResponse(emptyList()) },
    var getClient: suspend (Int, String) -> Response<ClientDto> = { _, _ -> errorResponse(404, "No encontrado") },
    var getClientByDni: suspend (String, String) -> Response<ClientDto> = { _, _ -> errorResponse(404, "Cliente no encontrado") },
    var getTripsByClientId: suspend (Int, String) -> Response<List<TripDto>> = { _, _ -> successResponse(emptyList()) }
) : ClientService {
    override suspend fun getAllClients(token: String) = getAllClients.invoke(token)
    override suspend fun getClient(id: Int, token: String) = getClient.invoke(id, token)
    override suspend fun getClientByDni(dni: String, token: String) = getClientByDni.invoke(dni, token)
    override suspend fun getTripsByClientId(clientId: Int, token: String) = getTripsByClientId.invoke(clientId, token)
}

class FakeEntrepreneurService(
    var getAllEntrepreneurs: suspend (String) -> Response<List<EntrepreneurDto>> = { _ -> successResponse(emptyList()) },
    var getEntrepreneurById: suspend (Int, String) -> Response<EntrepreneurDto> = { _, _ -> errorResponse(404, "No encontrado") },
    var getTripsByEntrepreneurId: suspend (Int, String) -> Response<List<TripDto>> = { _, _ -> successResponse(emptyList()) },
    var getClientsByEntrepreneurId: suspend (Int, String) -> Response<List<ClientDto>> = { _, _ -> successResponse(emptyList()) },
    var getVehiclesByEntrepreneurId: suspend (Int, String) -> Response<List<VehicleDto>> = { _, _ -> successResponse(emptyList()) },
    var getDriversByEntrepreneurId: suspend (Int, String) -> Response<List<DriverDto>> = { _, _ -> successResponse(emptyList()) }
) : EntrepreneurService {
    override suspend fun getAllEntrepreneurs(token: String) = getAllEntrepreneurs.invoke(token)
    override suspend fun getEntrepreneurById(id: Int, token: String) = getEntrepreneurById.invoke(id, token)
    override suspend fun getTripsByEntrepreneurId(id: Int, token: String) = getTripsByEntrepreneurId.invoke(id, token)
    override suspend fun getClientsByEntrepreneurId(id: Int, token: String) = getClientsByEntrepreneurId.invoke(id, token)
    override suspend fun getVehiclesByEntrepreneurId(id: Int, token: String) = getVehiclesByEntrepreneurId.invoke(id, token)
    override suspend fun getDriversByEntrepreneurId(id: Int, token: String) = getDriversByEntrepreneurId.invoke(id, token)
}

class FakeExpenseService(
    var getExpenses: suspend (String) -> Response<List<ExpenseDto>> = { _ -> successResponse(emptyList()) },
    var getExpense: suspend (Int, String) -> Response<ExpenseDto> = { _, _ -> errorResponse(404, "No encontrado") },
    var addExpense: suspend (String, ExpensePostDto) -> Response<ExpenseDto> = { _, _ -> errorResponse(400, "No stubbed") },
    var updateExpense: suspend (Int, String, ExpensePostDto) -> Response<ExpenseDto> = { _, _, _ -> errorResponse(400, "No stubbed") },
    var updateExpenseState: suspend (Int, String, ExpenseStateUpdateDto) -> Response<ExpenseStateUpdateDto> = { _, _, dto -> successResponse(dto) }
) : ExpenseService {
    override suspend fun getExpenses(token: String) = getExpenses.invoke(token)
    override suspend fun getExpense(expenseId: Int, token: String) = getExpense.invoke(expenseId, token)
    override suspend fun addExpense(token: String, expense: ExpensePostDto) = addExpense.invoke(token, expense)
    override suspend fun updateExpense(expenseId: Int, token: String, expense: ExpensePostDto) = updateExpense.invoke(expenseId, token, expense)
    override suspend fun updateExpenseState(expenseId: Int, token: String, update: ExpenseStateUpdateDto) = updateExpenseState.invoke(expenseId, token, update)
}

class FakeOngoingTripService(
    var getOngoingTrips: suspend (String) -> Response<List<OngoingTripDto>> = { _ -> successResponse(emptyList()) },
    var getOngoingTripById: suspend (Int, String) -> Response<OngoingTripDto> = { _, _ -> errorResponse(404, "No encontrado") },
    var createOngoingTrip: suspend (String, OngoingTripDtoPost) -> Response<OngoingTripDto> = { _, dto ->
        successResponse(OngoingTripDto(latitude = dto.latitude, longitude = dto.longitude, speed = dto.speed, distance = dto.distance, tripId = dto.tripId))
    },
    var updateOngoingTrip: suspend (Int, String, OngoingTripUpdateDto) -> Response<OngoingTripDto> = { _, _, dto ->
        successResponse(OngoingTripDto(latitude = dto.latitude, longitude = dto.longitude, speed = dto.speed, distance = dto.distance, tripId = dto.tripId))
    }
) : OngoingTripService {
    override suspend fun getOngoingTrips(token: String) = getOngoingTrips.invoke(token)
    override suspend fun getOngoingTripById(id: Int, token: String) = getOngoingTripById.invoke(id, token)
    override suspend fun createOngoingTrip(token: String, ongoingTrip: OngoingTripDtoPost) = createOngoingTrip.invoke(token, ongoingTrip)
    override suspend fun updateOngoingTrip(id: Int, token: String, update: OngoingTripUpdateDto) = updateOngoingTrip.invoke(id, token, update)
}

class FakeAlertService(
    var getAlerts: suspend (String) -> Response<List<AlertDto>> = { _ -> successResponse(emptyList()) },
    var getAlertById: suspend (Int, String) -> Response<AlertDto> = { _, _ -> errorResponse(404, "No encontrado") },
    var getAlertsByTripId: suspend (Int, String) -> Response<List<AlertDto>> = { _, _ -> successResponse(emptyList()) },
    var createAlert: suspend (String, AlertPostDto) -> Response<AlertDto> = { _, dto ->
        successResponse(AlertDto(id = 1, title = dto.title, type = dto.type, description = dto.description, date = dto.date, tripId = dto.tripId))
    }
) : AlertService {
    override suspend fun getAlerts(token: String) = getAlerts.invoke(token)
    override suspend fun getAlertById(alertId: Int, token: String) = getAlertById.invoke(alertId, token)
    override suspend fun getAlertsByTripId(tripId: Int, token: String) = getAlertsByTripId.invoke(tripId, token)
    override suspend fun createAlert(token: String, alert: AlertPostDto) = createAlert.invoke(token, alert)
}

class FakeAuditLogService(
    var getAuditLogs: suspend (String) -> Response<List<AuditLogDto>> = { _ -> successResponse(emptyList()) },
    var getAuditLogById: suspend (String, String) -> Response<AuditLogDto> = { _, _ -> errorResponse(404, "No encontrado") },
    var getAuditLogsByEntrepreneur: suspend (Int, String) -> Response<List<AuditLogDto>> = { _, _ -> successResponse(emptyList()) },
    var getAlertAuditLogs: suspend (Int, String) -> Response<List<AuditLogDto>> = { _, _ -> successResponse(emptyList()) },
    var getExpenseAuditLogs: suspend (Int, String) -> Response<List<AuditLogDto>> = { _, _ -> successResponse(emptyList()) },
    var getDriverAuditLogs: suspend (Int, String) -> Response<List<AuditLogDto>> = { _, _ -> successResponse(emptyList()) },
    var getTripAuditLogs: suspend (Int, String) -> Response<List<AuditLogDto>> = { _, _ -> successResponse(emptyList()) },
    var getVehicleAuditLogs: suspend (Int, String) -> Response<List<AuditLogDto>> = { _, _ -> successResponse(emptyList()) }
) : AuditLogService {
    override suspend fun getAuditLogs(token: String) = getAuditLogs.invoke(token)
    override suspend fun getAuditLogById(auditLogId: String, token: String) = getAuditLogById.invoke(auditLogId, token)
    override suspend fun getAuditLogsByEntrepreneur(entrepreneurId: Int, token: String) = getAuditLogsByEntrepreneur.invoke(entrepreneurId, token)
    override suspend fun getAlertAuditLogs(entrepreneurId: Int, token: String) = getAlertAuditLogs.invoke(entrepreneurId, token)
    override suspend fun getExpenseAuditLogs(entrepreneurId: Int, token: String) = getExpenseAuditLogs.invoke(entrepreneurId, token)
    override suspend fun getDriverAuditLogs(entrepreneurId: Int, token: String) = getDriverAuditLogs.invoke(entrepreneurId, token)
    override suspend fun getTripAuditLogs(entrepreneurId: Int, token: String) = getTripAuditLogs.invoke(entrepreneurId, token)
    override suspend fun getVehicleAuditLogs(entrepreneurId: Int, token: String) = getVehicleAuditLogs.invoke(entrepreneurId, token)
}

class FakeUserService(
    var getUser: suspend (Int, String) -> Response<UserDto> = { _, _ -> errorResponse(404, "No encontrado") },
    var getUserRole: suspend (Int, String) -> Response<UserRoleDto> = { _, _ -> errorResponse(404, "No encontrado") },
    var updateUserState: suspend (Int, String, UserStateUpdateDto) -> Response<UserDto> = { _, _, _ -> errorResponse(400, "No stubbed") }
) : UserService {
    override suspend fun getUser(userId: Int, token: String) = getUser.invoke(userId, token)
    override suspend fun getUserRole(userId: Int, token: String) = getUserRole.invoke(userId, token)
    override suspend fun updateUserState(userId: Int, token: String, update: UserStateUpdateDto) = updateUserState.invoke(userId, token, update)
}
