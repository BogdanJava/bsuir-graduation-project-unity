package by.bogdan.bsuir.bsuirgraduationbackend.service

import by.bogdan.bsuir.bsuirgraduationbackend.controller.UpdateUserDTO
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.UserDocument
import by.bogdan.bsuir.bsuirgraduationbackend.exceptions.ResourceNotFoundException
import by.bogdan.bsuir.bsuirgraduationbackend.repository.UserRepository
import by.bogdan.bsuir.bsuirgraduationbackend.security.AuthenticationService
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class UserService(val userRepository: UserRepository,
                  val authService: AuthenticationService,
                  val objectCopyService: ObjectCopyService,
                  mongoTemplate: ReactiveMongoTemplate) :
        CrudService<UserDocument, UUID, UpdateUserDTO>(mongoTemplate, UserDocument::class.java) {
    override fun create(document: UserDocument): Mono<UserDocument> {
        val password = document.password
        document.password = authService.encode(password)
        document.id = UUID.randomUUID()
        return userRepository.save(document)
    }

    override fun findById(id: UUID): Mono<UserDocument> = userRepository.findById(id)
            .switchIfEmpty(Mono.error(ResourceNotFoundException("User wasn't found for id: $id")))

    override fun update(id: UUID, updates: UpdateUserDTO): Mono<UserDocument> {
        return userRepository.findById(id).flatMap { user ->
            objectCopyService.patchTarget(user, updates)
            userRepository.save(user)
        }
    }

}