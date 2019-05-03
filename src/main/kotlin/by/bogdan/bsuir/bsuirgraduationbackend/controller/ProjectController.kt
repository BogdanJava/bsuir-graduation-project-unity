package by.bogdan.bsuir.bsuirgraduationbackend.controller

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.ProjectDocument
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.ProjectUpdateDTO
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.Role
import by.bogdan.bsuir.bsuirgraduationbackend.repository.ProjectRepository
import by.bogdan.bsuir.bsuirgraduationbackend.security.annotations.ProtectedResource
import by.bogdan.bsuir.bsuirgraduationbackend.security.annotations.RestrictedAccess
import by.bogdan.bsuir.bsuirgraduationbackend.service.ProjectService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import java.util.*

/**
 * @author bogdanshishkin1998@gmail.com
 * @since 4/26/2019
 */
@ProtectedResource
@RestController
@RequestMapping(path = arrayOf("/api/project", "/api/projects"))
class ProjectController(objectMapper: ObjectMapper, val service: ProjectService, val repo: ProjectRepository)
    : AbstractController<ProjectDocument, UUID, ProjectUpdateDTO>(objectMapper = objectMapper, entityService = service) {

    @GetMapping("/filter")
    override fun getByFilter(@RequestParam("filter") filterRaw: String,
                             @RequestParam("projection") projectionRaw: String?): Flux<ProjectDocument> {
        return this._getByFilter(filterRaw, projectionRaw)
    }

    @RestrictedAccess(Role.ADMIN, Role.MODERATOR)
    @PostMapping
    fun create(@RequestBody projectDocument: ProjectDocument) = service.create(document = projectDocument)

    @GetMapping("/{userId}")
    fun getByUserId(@PathVariable userId: UUID): Flux<ProjectDocument> = repo.findByAssignedPersonsIdsIn(arrayListOf(userId))

}