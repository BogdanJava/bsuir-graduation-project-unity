package by.bogdan.bsuir.bsuirgraduationbackend.service

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.Role
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.UpdateUserDTO
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.UserDocument
import by.bogdan.bsuir.bsuirgraduationbackend.repository.ProjectRepository
import by.bogdan.bsuir.bsuirgraduationbackend.repository.UserRepository
import by.bogdan.bsuir.bsuirgraduationbackend.security.AuthenticationService
import by.bogdan.bsuir.bsuirgraduationbackend.utils.CustomReflectionUtils
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.text.DateFormat
import java.util.*

@Service
class UserService(val userRepository: UserRepository,
                  val authService: AuthenticationService,
                  val projectRepository: ProjectRepository,
                  objectCopyService: ObjectCopyService,
                  mongoTemplate: ReactiveMongoTemplate,
                  reflectionUtils: CustomReflectionUtils,
                  @Qualifier("isoDateFormat") dateFormat: DateFormat) :
        CrudService<UserDocument, UUID, UpdateUserDTO>(userRepository, mongoTemplate, objectCopyService,
                UserDocument::class.java, reflectionUtils, dateFormat) {
    override fun create(document: UserDocument): Mono<UserDocument> {
        val password = document.password
        document.password = authService.encode(password)
        document.id = UUID.randomUUID()
        if (document.roles.isNullOrEmpty()) {
            document.roles = mutableListOf(Role.USER)
        }
        var projectIds = document.projectIds
        return projectRepository.findByName(defaultProjectName).flatMap { idleProject ->
            if (projectIds == null) {
                projectIds = mutableListOf(idleProject.id!!)
            } else {
                projectIds!!.add(idleProject.id!!)
            }
            userRepository.save(document)
        }
    }

    companion object {
        val defaultProjectName = "Idle"
    }
}