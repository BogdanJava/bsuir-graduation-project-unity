package by.bogdan.bsuir.bsuirgraduationbackend.service

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TaskDocument
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TaskUpdateDTO
import by.bogdan.bsuir.bsuirgraduationbackend.repository.TaskRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class TaskService(mongoRepository: TaskRepository,
                  mongoTemplate: ReactiveMongoTemplate,
                  objectCopyService: ObjectCopyService) : CrudService<TaskDocument, UUID, TaskUpdateDTO>(
        clazz = TaskDocument::class.java,
        mongoRepository = mongoRepository,
        mongoTemplate = mongoTemplate,
        objectCopyService = objectCopyService)