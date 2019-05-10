package by.bogdan.bsuir.bsuirgraduationbackend.datamodel

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document(collection = "projects")
data class ProjectDocument(@Id @Field("id") var id: UUID?,
                           var name: String?,
                           var assignedPersonsIds: MutableList<UUID>?,
                           var description: String?) : BasicDocument()

data class ProjectUpdateDTO(var name: String?,
                            var description: String?)