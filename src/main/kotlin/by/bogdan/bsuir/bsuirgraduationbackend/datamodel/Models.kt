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
        var role: Role? = Role.USER,
        var realName: String? = null,
        var birthday: Long? = null,
        var address: String? = null,
        var department: DepartmentDocument? = null) : BasicDocument()


@Document(collection = "departments")
data class DepartmentDocument(
        @Id @Field("id") var id: UUID?,
        var name: String?,
        var manager: UserDocument?,
        var description: String?) : BasicDocument()

enum class Role {
    ADMIN, USER
}