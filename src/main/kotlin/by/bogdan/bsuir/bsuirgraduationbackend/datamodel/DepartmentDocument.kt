package by.bogdan.bsuir.bsuirgraduationbackend.datamodel

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document(collection = "departments")
data class DepartmentDocument(
        @Id @Field("id") var id: UUID?,
        var name: String?,
        var manager: UserDocument?,
        var description: String?) : BasicDocument()
