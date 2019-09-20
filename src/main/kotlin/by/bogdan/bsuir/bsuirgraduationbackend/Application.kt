package by.bogdan.bsuir.bsuirgraduationbackend

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.ProjectDocument
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.Role
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.UserDocument
import by.bogdan.bsuir.bsuirgraduationbackend.service.UserService
import by.bogdan.bsuir.bsuirgraduationbackend.repository.ProjectRepository
import by.bogdan.bsuir.bsuirgraduationbackend.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.core.publisher.Mono
import java.util.*

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    val context = runApplication<Application>(*args)
    val userRepository = context.getBean(UserRepository::class.java)
    val userService = context.getBean(UserService::class.java)
    val projectRepository = context.getBean(ProjectRepository::class.java)
    userRepository.findByUsername("bogdanjava").switchIfEmpty(Mono.just(
            UserDocument(null, "bogdanjava", "12345", null, mutableListOf())))
            .subscribe { user ->
                if (user.roles.isNullOrEmpty()) {
                    // create a new superuser
                    user.roles = mutableListOf(Role.ADMIN, Role.USER, Role.MODERATOR)
                    user.photoUrl = context.environment.getProperty("images.bogdanjava-url")
                    userService.create(user).subscribe { created ->
                        log.info("Superuser created: $created")
                        createDefaultProject(created, projectRepository, userRepository)
                    }
                }
                createDefaultProject(user, projectRepository, userRepository)
            }
}

fun createDefaultProject(user: UserDocument, projectRepository: ProjectRepository, userRepository: UserRepository) {
    projectRepository.findByName("Idle").switchIfEmpty(Mono.just(
            ProjectDocument(null, "Idle", mutableListOf(user.id!!), "No project", " ")))
            .subscribe { project ->
                if (project.id == null) {
                    project.id = UUID.randomUUID()
                    projectRepository.save(project).subscribe { savedProject ->
                        log.info("Idle project created: $savedProject")
                        user.projectIds = mutableListOf(project.id!!)
                        userRepository.save(user).subscribe { userWithProjects ->
                            log.info("Added projects to user: $userWithProjects")
                        }
                    }
                }
            }
}

val log = LoggerFactory.getLogger(Application::class.java)!!
