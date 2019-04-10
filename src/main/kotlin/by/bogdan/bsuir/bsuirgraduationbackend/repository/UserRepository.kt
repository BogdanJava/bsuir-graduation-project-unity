package by.bogdan.bsuir.bsuirgraduationbackend.repository

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.UserDocument
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono
import java.util.*

interface UserRepository: ReactiveMongoRepository<UserDocument, UUID> {
    fun findByUsername(username: String) : Mono<UserDocument>
}