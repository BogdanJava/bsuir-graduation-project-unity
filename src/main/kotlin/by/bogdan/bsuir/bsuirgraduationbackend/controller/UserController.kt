package by.bogdan.bsuir.bsuirgraduationbackend.controller

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.Role
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.UserDocument
import by.bogdan.bsuir.bsuirgraduationbackend.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/users")
class UserController(val userService: UserService) {

    @PostMapping
    fun create(@RequestBody user: CreateUserDTO): Mono<UserDocument> {
        log.info("Creating user: $user")
        return userService.create(user.toUserDocument())
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID) = userService.findById(id)

    companion object {
        val log = LoggerFactory.getLogger(UserController::class.java)!!
    }
}

data class CreateUserDTO(var username: String,
                         var password: String) {
    fun toUserDocument() = UserDocument(null, username, password, null, Role.USER)
}