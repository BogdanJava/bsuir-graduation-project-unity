package by.bogdan.bsuir.bsuirgraduationbackend.controller

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.CreateUserDTO
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.Role
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.UpdateUserDTO
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.UserDocument
import by.bogdan.bsuir.bsuirgraduationbackend.repository.UserRepository
import by.bogdan.bsuir.bsuirgraduationbackend.security.annotations.ProtectedResource
import by.bogdan.bsuir.bsuirgraduationbackend.security.annotations.RestrictedAccess
import by.bogdan.bsuir.bsuirgraduationbackend.service.ObjectCopyService
import by.bogdan.bsuir.bsuirgraduationbackend.service.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@ProtectedResource
@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService,
                     private val userRepository: UserRepository,
                     objectMapper: ObjectMapper,
                     private val objectCopyService: ObjectCopyService) :
        AbstractController<UserDocument, UUID, UpdateUserDTO>(userService, objectMapper) {

    @GetMapping("/filter")
    override fun getByFilter(@RequestParam("filter") filterRaw: String,
                             @RequestParam("projection") projectionRaw: String): Flux<UserDocument> {
        return this._getByFilter(filterRaw, projectionRaw);
    }

    @RestrictedAccess(Role.ADMIN)
    @PostMapping
    fun create(@RequestBody user: CreateUserDTO,
               @RequestHeader("Authorization") authHeader: String): Mono<UserDocument> {
        log.info("Creating user: $user")
        val userDocument = UserDocument()
        objectCopyService.noOverwriteTargetCopy(user, userDocument)
        return userService.create(userDocument)
    }

    @PutMapping("/{id}")
    fun update(@RequestBody updates: UpdateUserDTO,
               @PathVariable("id") userId: UUID): Mono<UserDocument> {
        return userService.update(userId, updates)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID) = userService.findById(id)

    @GetMapping
    fun getByUsername(@RequestParam username: String): Mono<UserDocument> {
        return userRepository.findByUsername(username)
    }

    companion object {
        val log = LoggerFactory.getLogger(UserController::class.java)!!
    }
}