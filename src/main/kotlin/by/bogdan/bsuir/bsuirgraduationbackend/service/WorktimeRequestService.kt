package by.bogdan.bsuir.bsuirgraduationbackend.service

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.RequestStatus
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.WorktimeRequest
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.WorktimeRequestUpdateDTO
import by.bogdan.bsuir.bsuirgraduationbackend.exceptions.ResourceNotFoundException
import by.bogdan.bsuir.bsuirgraduationbackend.repository.ProjectRepository
import by.bogdan.bsuir.bsuirgraduationbackend.repository.UserRepository
import by.bogdan.bsuir.bsuirgraduationbackend.repository.WorktimeRequestRepository
import by.bogdan.bsuir.bsuirgraduationbackend.utils.CustomReflectionUtils
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.text.DateFormat
import java.util.*

@Service
class WorktimeRequestService(val worktimeRequestRepository: WorktimeRequestRepository,
                             mongoTemplate: ReactiveMongoTemplate,
                             objectCopyService: ObjectCopyService,
                             val userRepository: UserRepository,
                             val projectRepository: ProjectRepository,
                             reflectionUtils: CustomReflectionUtils,
                             @Qualifier("isoDateFormat") dateFormat: DateFormat) :
        CrudService<WorktimeRequest, UUID, WorktimeRequestUpdateDTO>(worktimeRequestRepository, mongoTemplate,
                objectCopyService, WorktimeRequest::class.java, reflectionUtils, dateFormat) {
    override fun create(document: WorktimeRequest): Mono<WorktimeRequest> {
        val userExistsMono = userRepository.existsById(document.approverId!!)
        val projectExistsMono = projectRepository.existsById(document.projectId!!)
        val results = Flux.merge(userExistsMono, projectExistsMono)
        return results.all { result -> result == true }
                .flatMap { checkResult ->
                    if (checkResult) {
                        document.status = RequestStatus.PENDING
                        worktimeRequestRepository.save(document)
                    } else {
                        throw ResourceNotFoundException("No user found for id ${document.approverId} or " +
                                "no project exists for id ${document.projectId}")
                    }
                }
    }
}