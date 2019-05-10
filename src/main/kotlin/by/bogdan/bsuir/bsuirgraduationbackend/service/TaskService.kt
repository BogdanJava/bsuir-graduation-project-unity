package by.bogdan.bsuir.bsuirgraduationbackend.service

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TaskDocument
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TaskUpdateDTO
import by.bogdan.bsuir.bsuirgraduationbackend.repository.TaskRepository
import by.bogdan.bsuir.bsuirgraduationbackend.utils.CustomReflectionUtils
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class TaskService(mongoRepository: TaskRepository,
                  mongoTemplate: ReactiveMongoTemplate,
                  objectCopyService: ObjectCopyService,
                  reflectionUtils: CustomReflectionUtils) : CrudService<TaskDocument, UUID, TaskUpdateDTO>(
        clazz = TaskDocument::class.java,
        mongoRepository = mongoRepository,
        mongoTemplate = mongoTemplate,
        objectCopyService = objectCopyService,
        reflectionUtils = reflectionUtils)