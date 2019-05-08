package by.bogdan.bsuir.bsuirgraduationbackend.service

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.RequestStatus
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TimeRequest
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TimeRequestUpdateDTO
import by.bogdan.bsuir.bsuirgraduationbackend.exceptions.ResourceNotFoundException
import by.bogdan.bsuir.bsuirgraduationbackend.repository.TimeRequestRepository
import by.bogdan.bsuir.bsuirgraduationbackend.repository.UserRepository
import by.bogdan.bsuir.bsuirgraduationbackend.utils.CustomReflectionUtils
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class TimeRequestService(private val timeRequestRepository: TimeRequestRepository,
                         private val userRepository: UserRepository,
                         mongoTemplate: ReactiveMongoTemplate,
                         objectCopyService: ObjectCopyService,
                         reflectionUtils: CustomReflectionUtils) :
        CrudService<TimeRequest, UUID, TimeRequestUpdateDTO>(timeRequestRepository,
                mongoTemplate, objectCopyService, TimeRequest::class.java, reflectionUtils) {
    override fun create(document: TimeRequest): Mono<TimeRequest> {
        return userRepository.existsById(document.approverId!!).flatMap { exists ->
            if (exists) {
                document.status = RequestStatus.PENDING
                document.id = UUID.randomUUID()
                timeRequestRepository.save(document)
            } else {
                throw ResourceNotFoundException("No user found for id ${document.approverId}")
            }
        }
    }
}