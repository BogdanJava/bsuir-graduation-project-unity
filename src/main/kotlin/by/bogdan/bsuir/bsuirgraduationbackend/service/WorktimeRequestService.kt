package by.bogdan.bsuir.bsuirgraduationbackend.service

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.WorktimeRequest
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.WorktimeRequestUpdateDTO
import by.bogdan.bsuir.bsuirgraduationbackend.exceptions.ResourceNotFoundException
import by.bogdan.bsuir.bsuirgraduationbackend.repository.UserRepository
import by.bogdan.bsuir.bsuirgraduationbackend.repository.WorktimeRequestRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class WorktimeRequestService(val worktimeRequestRepository: WorktimeRequestRepository,
                             mongoTemplate: ReactiveMongoTemplate,
                             objectCopyService: ObjectCopyService,
                             val userRepository: UserRepository) :
        CrudService<WorktimeRequest, UUID, WorktimeRequestUpdateDTO>(worktimeRequestRepository, mongoTemplate, objectCopyService, WorktimeRequest::class.java) {
    override fun create(document: WorktimeRequest): Mono<WorktimeRequest> {
//        return userRepository.existsById(document.approverId!!).flatMap { exists ->
//            if (exists) {
//                document.approved = false
//                document.id = UUID.randomUUID()
//                worktimeRequestRepository.save(document)
//            } else {
//                throw ResourceNotFoundException("No user found for id ${document.approverId}")
//            }
//        }
        val userExistsMono = userRepository.existsById(document.approverId!!)
        return Mono.empty()
    }
}