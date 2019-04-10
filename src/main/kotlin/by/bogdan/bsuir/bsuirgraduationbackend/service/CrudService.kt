package by.bogdan.bsuir.bsuirgraduationbackend.service

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.BasicDocument
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.UserDocument
import reactor.core.publisher.Mono
import java.util.*

interface CrudService<T : BasicDocument, ID> {
    fun create(document: T): Mono<UserDocument>
    fun findById(id: ID): Mono<T>
}