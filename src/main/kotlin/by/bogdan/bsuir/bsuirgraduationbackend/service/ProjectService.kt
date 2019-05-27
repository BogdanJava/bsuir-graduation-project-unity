package by.bogdan.bsuir.bsuirgraduationbackend.service

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.ProjectDocument
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.ProjectUpdateDTO
import by.bogdan.bsuir.bsuirgraduationbackend.repository.ProjectRepository
import by.bogdan.bsuir.bsuirgraduationbackend.utils.CustomReflectionUtils
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.text.DateFormat
import java.util.*

@Service
class ProjectService(val mongoRepository: ProjectRepository,
                     mongoTemplate: ReactiveMongoTemplate,
                     objectCopyService: ObjectCopyService,
                     reflectionUtils: CustomReflectionUtils,
                     @Qualifier("isoDateFormat") dateFormat: DateFormat) : CrudService<ProjectDocument, UUID, ProjectUpdateDTO>(
        clazz = ProjectDocument::class.java,
        mongoRepository = mongoRepository,
        mongoTemplate = mongoTemplate,
        objectCopyService = objectCopyService,
        reflectionUtils = reflectionUtils,
        dateFormat = dateFormat) {
    override fun create(document: ProjectDocument): Mono<ProjectDocument> = mongoRepository.save(document)
}