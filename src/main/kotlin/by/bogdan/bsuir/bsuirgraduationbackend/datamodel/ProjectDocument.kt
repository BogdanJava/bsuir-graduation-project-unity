package by.bogdan.bsuir.bsuirgraduationbackend.datamodel

import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "projects")
data class ProjectDocument(var id: UUID?,
                           var name: String?,
                           @Transient var assignedPersonsIds: Array<UUID>,
                           var description: String?,
                           var customer: String?) : BasicDocument() {
}