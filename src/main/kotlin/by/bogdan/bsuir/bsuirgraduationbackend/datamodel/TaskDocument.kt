package by.bogdan.bsuir.bsuirgraduationbackend.datamodel

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "tasks")
data class TaskDocument(
        @Id var id: UUID?,
        var assigneeId: UUID?,
        var name: String?,
        var description: String?,
        var deadline: Date?,
        var status: TaskStatus?) : BasicDocument()

enum class TaskStatus {
    IN_WORK,
    COMPLETED,
    OPEN
}

data class TaskUpdateDTO(
        var name: String?,
        var assigneeId: UUID?,
        var description: String?,
        var deadline: Date?,
        var status: TaskStatus?)