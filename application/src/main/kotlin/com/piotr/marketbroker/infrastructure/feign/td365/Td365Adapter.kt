package com.piotr.marketbroker.infrastructure.feign.td365

import com.piotr.marketbroker.common.logger
import feign.FeignException
import org.springframework.stereotype.Component
import java.util.UUID
/*
@Component
class Td365Adapter(
    private val td365Client: Td365Client,
    private val mapper: EmployeeProjectionShortResponseMapper
) : RestApi {

    val log by logger()

    override fun getShortEmployee(employerId: UUID, employeeId: UUID): EmployeeProjectionShort? {

        val responseEntity = td365Client.getShortEmployee(employeeId, employerId)

        check(responseEntity.statusCode.is2xxSuccessful) {
            "Failed to get Employee SHORT scope for employeeId $employeeId, employerId $employerId" }
        return responseEntity.body?.let { EmployeeProjectionShortResponseMapper.toEmployeeProjectionShort(it) }
    }

    override fun getFullEmployee(employerId: UUID, employeeId: UUID): EmployeeProjectionFull? {

        val responseEntity = td365Client.getFullEmployee(employeeId, employerId)

        check(responseEntity.statusCode.is2xxSuccessful) {
            "Failed to get Employee FULL scope for employeeId $employeeId, employerId $employerId" }
        return responseEntity.body?.let { mapper.toEmployeeProjectionFull(it) }
    }

    override fun createEmployee(request: EmployeeCreateCommand): UUID {

        log.trace("Creating employee: employer=${request.employerId}, email=${request.personalInfo.email}")
        val response = td365Client.createEmployee(request.employerId, CreateEmployeeRequest(
            personalInfo = V2PersonalInfo(
                names = Names(
                    firstName = request.personalInfo.names.firstName,
                    lastName = request.personalInfo.names.lastName
                ),
                email = request.personalInfo.email,
                privateEmail = request.personalInfo.privateEmail
            ),
            externalIds = listOf(RequestExternalID(request.externalId, "HUBSPOT"))
        ))
        check(response.statusCode.is2xxSuccessful) {
            "Failed to create Employee: email=${request.personalInfo.email}, externalId[HUBSPOT]=${request.externalId}" }
        return response.body!!.employeeId
    }

    override fun updateEmployee(employeeId: UUID, employerId: UUID, request: PatchEmployeeProjection): UUID {

        val patchEmployeeProjectionRequest = PatchEmployeeProjectionRequest(
            payload = request.payload.map { PatchDocument(PatchDocument.Op.valueOf(it.op.name), it.path, it.value.toString()) },
            force = request.force
        )
        log.trace("Updating employee $employeeId, employer $employerId, patch $patchEmployeeProjectionRequest")
        val response = td365Client.updateEmployee(employeeId, employerId, patchEmployeeProjectionRequest)

        check(response.statusCode.is2xxSuccessful) { "Failed to update Employee employeeId: $employeeId" }
        return response.body!!.id
    }

    override fun resolveEmployeeIdByEmail(employerId: UUID, email: String): UUID? {

        log.trace("Resolving employeeId by email $email, employer $employerId")
        try {
            val response = td365Client.resolveEmployeeByEmail(employerId = employerId, number = email)
            return response.body?.id
        } catch (ex: FeignException) {
            if (ex.status()==404) return null
            throw ex
        }
    }

    override fun updateEmployeeHsExternalId(employeeId: UUID, employerId: UUID, hsExternalId: String) {
        val updateRequest = UpdateExternalIdValueRequest(
            externalIds = mapOf("HUBSPOT" to hsExternalId),
        )
        val responseEntity = td365Client.updateEmployeeExternalId(employeeId, employerId, employerId, updateRequest)

        check(responseEntity.statusCode.is2xxSuccessful) {
            "Failed to updated Employee HS ExternalId: employeeId=$employeeId, employerId=$employerId" }
    }

}


 */