package by.bogdan.bsuir.bsuirgraduationbackend.controller

import by.bogdan.bsuir.bsuirgraduationbackend.exceptions.ResourceNotFoundException
import by.bogdan.bsuir.bsuirgraduationbackend.repository.UserRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/public")
class PublicApiController(val userRepository: UserRepository) {

    @GetMapping("/users")
    fun getPublicUserInfo(@RequestParam username: String) =
            userRepository.findByUsername(username)
                    .map { user ->
                        hashMapOf(Pair("username", user.username), Pair("photoUrl", user.photoUrl))
                    }.switchIfEmpty(Mono.error(ResourceNotFoundException("No user found for username $username")))
}