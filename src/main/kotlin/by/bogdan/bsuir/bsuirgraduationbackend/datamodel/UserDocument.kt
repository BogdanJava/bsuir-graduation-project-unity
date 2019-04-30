package by.bogdan.bsuir.bsuirgraduationbackend.datamodel

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document(collection = "users")
data class UserDocument(
        @Id @Field("id") var id: UUID? = null,
        var username: String = "",
        @JsonIgnore var password: String = "",
        var photoUrl: String? = null,
        var roles: MutableList<Role> = mutableListOf(),
        var realName: String? = null,
        var birthday: Long? = null,
        var address: String? = null,
        var department: DepartmentDocument? = null,
        var projectIds: MutableList<UUID>? = null) : BasicDocument()

enum class Role {
    ADMIN, USER, MODERATOR
}

data class CreateUserDTO(var username: String,
                         var password: String)

data class UpdateUserDTO(var realName: String?,
                         var department: DepartmentDocument?,
                         var address: String?,
                         var birthday: Long?,
                         var photoUrl: String?) {
    constructor() : this(null, null, null, 0, null)
}