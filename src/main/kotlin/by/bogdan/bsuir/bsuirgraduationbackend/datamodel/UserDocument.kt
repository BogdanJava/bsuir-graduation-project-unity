package by.bogdan.bsuir.bsuirgraduationbackend.datamodel

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document(collection = "users")
data class UserDocument(
        @Id @Field("id") var id: UUID?,
        var username: String,
        var password: String,
        var photoUrl: String?,
        var role: Role?) : BasicDocument()

enum class Role {
    ADMIN, USER
}