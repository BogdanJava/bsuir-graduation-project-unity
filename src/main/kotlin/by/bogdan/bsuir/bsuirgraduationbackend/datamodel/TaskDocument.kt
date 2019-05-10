package by.bogdan.bsuir.bsuirgraduationbackend.datamodel

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "tasks")
data class TaskDocument(
        var assigneeId: UUID?,
        var description: String?,
        var deadline: Date?,
        var status: TaskStatus?) : BasicDocument()

enum class TaskStatus {
    IN_WORK,
    COMPLETED,
    OPEN
}

data class TaskUpdateDTO(
        var assigneeId: UUID?,
        var description: String?,
        var deadline: Date?,
        var status: TaskStatus?)