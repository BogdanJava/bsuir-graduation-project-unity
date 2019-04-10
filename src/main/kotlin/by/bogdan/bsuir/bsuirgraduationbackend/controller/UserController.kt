package by.bogdan.bsuir.bsuirgraduationbackend.controller

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.UserDocument
import by.bogdan.bsuir.bsuirgraduationbackend.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/users")
class UserController(val userService: UserService) {

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun create(@RequestBody user: UserDocument): Mono<UserDocument> {
        log.info("Creating user: $user")
        return userService.create(user)
    }

    companion object {
        val log = LoggerFactory.getLogger(UserController::class.java)!!
    }
}