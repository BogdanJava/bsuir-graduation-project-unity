package by.bogdan.bsuir.bsuirgraduationbackend.controller

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.DataFilter
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.Role
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TimeRequest
import by.bogdan.bsuir.bsuirgraduationbackend.exceptions.AuthenticationException
import by.bogdan.bsuir.bsuirgraduationbackend.repository.TimeRequestRepository
import by.bogdan.bsuir.bsuirgraduationbackend.security.AuthenticationService
import by.bogdan.bsuir.bsuirgraduationbackend.security.ProtectedResource
import by.bogdan.bsuir.bsuirgraduationbackend.security.RoleSensitive
import by.bogdan.bsuir.bsuirgraduationbackend.service.TimeRequestService
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@ProtectedResource
@RestController
@RequestMapping("/api/time-request")
class TimeRequestController(val timeRequestService: TimeRequestService,
                            val authenticationService: AuthenticationService,
                            val timeRequestRepository: TimeRequestRepository,
                            val objectMapper: ObjectMapper) : FilterRequest<TimeRequest> {

    @GetMapping("/filter")
    override fun getByFilter(@RequestParam("filter") filterRaw: String,
                             @RequestParam("projection") projectionRaw: String): Flux<TimeRequest> {
        val filter = objectMapper.readValue(filterRaw, DataFilter::class.java)
        val projection = objectMapper.readValue<List<String>>(projectionRaw, object : TypeReference<List<String>>() {})
        return timeRequestService.getByFilter(filter.filter, projection)
    }

    @PostMapping
    fun createRequest(@RequestBody timeRequest: TimeRequest) = timeRequestService.create(timeRequest)

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") timeRequestId: UUID) = timeRequestService.findById(timeRequestId)

    @GetMapping("/approver/{id}")
    fun getByApproverId(@PathVariable("id") approverId: UUID,
                        @RequestParam(required = false) showApproved: Boolean = false): Flux<TimeRequest> {
        return if (showApproved) {
            timeRequestRepository.findByApproverId(approverId)
        } else {
            // return only unapproved requests
            timeRequestRepository.findByApproverIdAndApproved(approverId, false)
        }
    }

    @GetMapping("/approver/{id}/count")
    fun getByApproverIdCount(@PathVariable("id") approverId: UUID,
                             @RequestParam(required = false) showApproved: Boolean = false): Mono<Long> {
        return if (showApproved) {
            timeRequestRepository.countByApproverId(approverId)
        } else {
            timeRequestRepository.countByApproverIdAndApproved(approverId, false)
        }
    }

    @RoleSensitive(Role.ADMIN, Role.MODERATOR)
    @PostMapping("/{id}")
    fun approveRequest(@PathVariable("id") requestId: UUID,
                       @RequestHeader("Authorization") authHeader: String): Mono<TimeRequest> {
        return timeRequestService.findById(requestId).flatMap { request ->
            val currentUserId = authenticationService.getClaimsFromAuthorizationHeader(authHeader)["id"]
            if (request.approverId != currentUserId) {
                throw AuthenticationException("Current user id and approver id don't match")
            } else {
                request.approved = true
                timeRequestRepository.save(request)
            }
        }
    }

}