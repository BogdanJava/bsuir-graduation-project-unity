package by.bogdan.bsuir.bsuirgraduationbackend.controller

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.*
import by.bogdan.bsuir.bsuirgraduationbackend.repository.UserRepository
import by.bogdan.bsuir.bsuirgraduationbackend.security.ProtectedResource
import by.bogdan.bsuir.bsuirgraduationbackend.security.RoleSensitive
import by.bogdan.bsuir.bsuirgraduationbackend.service.ObjectCopyService
import by.bogdan.bsuir.bsuirgraduationbackend.service.UserService
import com.fasterxml.jackson.core.type.TypeReference
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
                     private val objectMapper: ObjectMapper,
                     private val objectCopyService: ObjectCopyService) {

    @RoleSensitive(Role.ADMIN)
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
    fun getByUsername(@RequestParam username: String) = userRepository.findByUsername(username)

    @GetMapping("/filter")
    fun getByFilter(@RequestParam("filter") raw: String,
                    @RequestParam("projection") projectionRaw: String): Flux<UserDocument> {
        val filter = objectMapper.readValue(raw, DataFilter::class.java)
        val projection = objectMapper.readValue<List<String>>(projectionRaw, object : TypeReference<List<String>>() {})
        return userService.getByFilter(filter.filter, projection)
    }

    companion object {
        val log = LoggerFactory.getLogger(UserController::class.java)!!
    }
}