package by.bogdan.bsuir.bsuirgraduationbackend.controller

import by.bogdan.bsuir.bsuirgraduationbackend.security.ProtectedResource
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


/**
 * @author bogdanshishkin1998@gmail.com
 * @since 4/26/2019
 */
@ProtectedResource
@RestController
@RequestMapping("/api/project")
class ProjectController {
}