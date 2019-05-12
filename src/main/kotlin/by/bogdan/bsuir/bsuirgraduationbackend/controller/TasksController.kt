package by.bogdan.bsuir.bsuirgraduationbackend.controller

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TaskDocument
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TaskStatus
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TaskUpdateDTO
import by.bogdan.bsuir.bsuirgraduationbackend.repository.TaskRepository
import by.bogdan.bsuir.bsuirgraduationbackend.security.annotations.ProtectedResource
import by.bogdan.bsuir.bsuirgraduationbackend.service.TaskService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@ProtectedResource
@RestController
@RequestMapping("/api/tasks")
class TasksController(val taskService: TaskService,
                      val taskRepository: TaskRepository,
                      objectMapper: ObjectMapper) :
        AbstractController<TaskDocument, UUID, TaskUpdateDTO>(taskService, objectMapper) {

    @GetMapping("/filter")
    override fun getByFilter(@RequestParam("filter") filterRaw: String,
                             @RequestParam("projection") projectionRaw: String?,
                             @RequestParam("pageSize") itemsPerPage: Int,
                             @RequestParam("pageNumber") pageNumber: Int): Flux<TaskDocument> {
        return this._getByFilter(filterRaw, projectionRaw, itemsPerPage, pageNumber)
    }

    @GetMapping("/count/{assigneeId}")
    fun pendingTasksCount(@PathVariable assigneeId: UUID,
                          @RequestParam status: TaskStatus): Mono<Int> {
        return taskRepository.countByAssigneeIdAndStatus(assigneeId, status)
    }

    @PostMapping
    fun createTask(task: TaskDocument): Mono<TaskDocument> = taskService.create(task)
}