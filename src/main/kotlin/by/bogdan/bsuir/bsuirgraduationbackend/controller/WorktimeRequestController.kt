package by.bogdan.bsuir.bsuirgraduationbackend.controller

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.Role
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.WorktimeRequest
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.WorktimeRequestUpdateDTO
import by.bogdan.bsuir.bsuirgraduationbackend.exceptions.AuthenticationException
import by.bogdan.bsuir.bsuirgraduationbackend.repository.WorktimeRequestRepository
import by.bogdan.bsuir.bsuirgraduationbackend.security.AuthenticationService
import by.bogdan.bsuir.bsuirgraduationbackend.security.annotations.ProtectedResource
import by.bogdan.bsuir.bsuirgraduationbackend.security.annotations.RestrictedAccess
import by.bogdan.bsuir.bsuirgraduationbackend.service.WorktimeRequestService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
@ProtectedResource
@RequestMapping("/api/worktime")
class WorktimeRequestController(val worktimeRequestService: WorktimeRequestService,
                                objectMapper: ObjectMapper,
                                val worktimeRequestRepository: WorktimeRequestRepository,
                                val authenticationService: AuthenticationService) :
        AbstractController<WorktimeRequest, UUID, WorktimeRequestUpdateDTO>(worktimeRequestService, objectMapper) {

    @GetMapping("/filter")
    override fun getByFilter(@RequestParam("filter") filterRaw: String,
                             @RequestParam(value = "projection", required = false)
                             projectionRaw: String?): Flux<WorktimeRequest> {
        return this._getByFilter(filterRaw, projectionRaw)
    }

    @PostMapping
    fun createRequest(@RequestBody timeRequest: WorktimeRequest) = worktimeRequestService.create(timeRequest)

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") timeRequestId: UUID) = worktimeRequestService.findById(timeRequestId)

    @GetMapping("/approver/{id}")
    fun getByApproverId(@PathVariable("id") approverId: UUID,
                        @RequestParam(required = false) showApproved: Boolean = false): Flux<WorktimeRequest> {
        return if (showApproved) {
            worktimeRequestRepository.findByApproverId(approverId)
        } else {
            // return only unapproved requests
            worktimeRequestRepository.findByApproverIdAndApproved(approverId, false)
        }
    }

    @GetMapping("/approver/{id}/count")
    fun getByApproverIdCount(@PathVariable("id") approverId: UUID,
                             @RequestParam(required = false) showApproved: Boolean = false): Mono<Long> {
        return if (showApproved) {
            worktimeRequestRepository.countByApproverId(approverId)
        } else {
            worktimeRequestRepository.countByApproverIdAndApproved(approverId, false)
        }
    }

    @RestrictedAccess(Role.ADMIN, Role.MODERATOR)
    @PutMapping("/{id}")
    fun approveRequest(@PathVariable("id") requestId: UUID,
                       @RequestHeader("Authorization") authHeader: String): Mono<WorktimeRequest> {
        return worktimeRequestService.findById(requestId).flatMap { request ->
            val currentUserId = authenticationService.getClaimsFromAuthorizationHeader(authHeader)["id"]
            if (request.approverId != currentUserId) {
                throw AuthenticationException("Current user id and approver id don't match")
            } else {
                request.approved = true
                worktimeRequestRepository.save(request)
            }
        }
    }

}