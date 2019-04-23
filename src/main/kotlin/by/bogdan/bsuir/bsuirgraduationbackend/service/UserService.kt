package by.bogdan.bsuir.bsuirgraduationbackend.service

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.UpdateUserDTO
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.UserDocument
import by.bogdan.bsuir.bsuirgraduationbackend.repository.UserRepository
import by.bogdan.bsuir.bsuirgraduationbackend.security.AuthenticationService
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class UserService(val userRepository: UserRepository,
                  val authService: AuthenticationService,
                  objectCopyService: ObjectCopyService,
                  mongoTemplate: ReactiveMongoTemplate) :
        CrudService<UserDocument, UUID, UpdateUserDTO>(userRepository, mongoTemplate, objectCopyService, UserDocument::class.java) {
    override fun create(document: UserDocument): Mono<UserDocument> {
        val password = document.password
        document.password = authService.encode(password)
        document.id = UUID.randomUUID()
        return userRepository.save(document)
    }
}