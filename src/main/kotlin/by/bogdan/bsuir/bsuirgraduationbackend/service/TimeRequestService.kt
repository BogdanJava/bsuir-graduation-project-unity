package by.bogdan.bsuir.bsuirgraduationbackend.service

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TimeRequest
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TimeRequestUpdateDTO
import by.bogdan.bsuir.bsuirgraduationbackend.repository.TimeRequestRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class TimeRequestService(private val timeRequestRepository: TimeRequestRepository,
                         mongoTemplate: ReactiveMongoTemplate) :
        CrudService<TimeRequest, UUID, TimeRequestUpdateDTO>(timeRequestRepository, mongoTemplate, TimeRequest::class.java) {
    override fun create(document: TimeRequest) = timeRequestRepository.save(document)

    override fun findById(id: UUID): Mono<TimeRequest> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(id: UUID, updates: TimeRequestUpdateDTO): Mono<TimeRequest> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}