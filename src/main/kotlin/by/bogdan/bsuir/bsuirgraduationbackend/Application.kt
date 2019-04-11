package by.bogdan.bsuir.bsuirgraduationbackend

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.Role
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.UserDocument
import by.bogdan.bsuir.bsuirgraduationbackend.repository.UserRepository
import by.bogdan.bsuir.bsuirgraduationbackend.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.core.publisher.Mono

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    val context = runApplication<Application>(*args)
    val userRepository = context.getBean(UserRepository::class.java)
    val userService = context.getBean(UserService::class.java)
    val superUser = userRepository.findByUsername("bogdanjava").switchIfEmpty(Mono.just(
            UserDocument(null, "bogdanjava", "12345", null, null)))
    superUser.subscribe { user ->
        if (user.role == null) {
            // create a new superuser
            user.role = Role.ADMIN
            user.photoUrl = context.environment.getProperty("images.bogdanjava-url")
            userService.create(user).subscribe { created ->
                log.info("Superuser created: $created")
            }
        }
    }
}

val log = LoggerFactory.getLogger(Application::class.java)!!
