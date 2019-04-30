package by.bogdan.bsuir.bsuirgraduationbackend.service

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.ProjectDocument
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.ProjectUpdateDTO
import by.bogdan.bsuir.bsuirgraduationbackend.repository.ProjectRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class ProjectService(val mongoRepository: ProjectRepository,
                     mongoTemplate: ReactiveMongoTemplate,
                     objectCopyService: ObjectCopyService) : CrudService<ProjectDocument, UUID, ProjectUpdateDTO>(
        clazz = ProjectDocument::class.java,
        mongoRepository = mongoRepository,
        mongoTemplate = mongoTemplate,
        objectCopyService = objectCopyService) {
    override fun create(document: ProjectDocument): Mono<ProjectDocument> = mongoRepository.save(document)
}