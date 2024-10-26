package com.piotr.marketbroker.infrastructure.feign.td365


import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

interface RestApi {

    @PostMapping("/v2/employees")
    fun createEmployee(
        @RequestParam employerId: UUID,
        @RequestBody request: String
    ): ResponseEntity<String>


    @PatchMapping("/v2/employees/{employeeId}")
    fun updateEmployee(
        @PathVariable employeeId: UUID,
        @RequestParam employerId: UUID,
        @RequestBody request: String
    ): ResponseEntity<String>

    @GetMapping("/v3/employees/{employeeId}")
    fun getFullEmployee(
        @PathVariable employeeId: UUID,
        @RequestParam employerId: UUID,
        @RequestHeader("Content-Scope") contentScope: String = "FULL"
    ): ResponseEntity<String?>


}
