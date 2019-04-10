package by.bogdan.bsuir.bsuirgraduationbackend.datamodel

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "users")
data class UserDocument(@Id var id: UUID?,
                        var username: String,
                        var password: String,
                        var photoUrl: String?) : BasicDocument()