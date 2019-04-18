package by.bogdan.bsuir.bsuirgraduationbackend.repository

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TimeRequest
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import java.util.*

interface TimeRequestRepository : ReactiveMongoRepository<TimeRequest, UUID>