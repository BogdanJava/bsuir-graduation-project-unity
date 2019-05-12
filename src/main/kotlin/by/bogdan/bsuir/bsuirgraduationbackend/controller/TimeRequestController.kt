package by.bogdan.bsuir.bsuirgraduationbackend.controller

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.RequestStatus
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.Role
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TimeRequest
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TimeRequestUpdateDTO
import by.bogdan.bsuir.bsuirgraduationbackend.exceptions.AuthenticationException
import by.bogdan.bsuir.bsuirgraduationbackend.exceptions.BadPayloadException
import by.bogdan.bsuir.bsuirgraduationbackend.repository.TimeRequestRepository
import by.bogdan.bsuir.bsuirgraduationbackend.security.AuthenticationService
import by.bogdan.bsuir.bsuirgraduationbackend.security.annotations.ProtectedResource
import by.bogdan.bsuir.bsuirgraduationbackend.security.annotations.RestrictedAccess
import by.bogdan.bsuir.bsuirgraduationbackend.service.TimeRequestService
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
                            objectMapper: ObjectMapper) :
        AbstractController<TimeRequest, UUID, TimeRequestUpdateDTO>(timeRequestService, objectMapper) {

    @GetMapping("/filter")
    override fun getByFilter(@RequestParam("filter") filterRaw: String,
                             @RequestParam("projection") projectionRaw: String?,
                             @RequestParam("pageSize") itemsPerPage: Int,
                             @RequestParam("pageNumber") pageNumber: Int): Flux<TimeRequest> {
        return this._getByFilter(filterRaw, projectionRaw, itemsPerPage, pageNumber)
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
            timeRequestRepository.findByApproverIdAndStatus(approverId, RequestStatus.PENDING)
        }
    }

    @GetMapping("/approver/{id}/count")
    fun getByApproverIdCount(@PathVariable("id") approverId: UUID,
                             @RequestParam(required = false) showApproved: Boolean = false): Mono<Long> {
        return if (showApproved) {
            timeRequestRepository.countByApproverId(approverId)
        } else {
            timeRequestRepository.countByApproverIdAndStatus(approverId, RequestStatus.PENDING)
        }
    }

    @RestrictedAccess(Role.ADMIN, Role.MODERATOR)
    @PutMapping("/{id}")
    fun approveRequest(@RequestHeader("Authorization") authHeader: String,
                       @PathVariable("id") requestId: UUID,
                       @RequestBody params: Map<String, Any>): Mono<TimeRequest> {
        val approved: Boolean = params["approved"]!! as Boolean
        return timeRequestService.findById(requestId).flatMap { request ->
            val currentUserId = authenticationService.getClaimsFromAuthorizationHeader(authHeader)["id"] as String
            if (request.approverId!! != UUID.fromString(currentUserId)) {
                throw AuthenticationException("Current user id and approver id don't match")
            } else if (request.status != RequestStatus.PENDING) {
                throw BadPayloadException("Request [$requestId] has been already processed")
            } else {
                request.status = if (approved) RequestStatus.APPROVED else RequestStatus.DECLINED
                timeRequestRepository.save(request)
            }
        }
    }

}