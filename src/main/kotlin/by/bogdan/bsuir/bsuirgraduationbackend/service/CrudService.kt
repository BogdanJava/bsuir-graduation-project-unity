package by.bogdan.bsuir.bsuirgraduationbackend.service

import by.bogdan.bsuir.bsuirgraduationbackend.controller.Operator
import by.bogdan.bsuir.bsuirgraduationbackend.controller.ValueContainer
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.BasicDocument
import by.bogdan.bsuir.bsuirgraduationbackend.exceptions.ResourceNotFoundException
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.inValues
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

abstract class CrudService<T : BasicDocument, ID, UpdateDTO>(
        private val mongoRepository: ReactiveMongoRepository<T, ID>,
        private val mongoTemplate: ReactiveMongoTemplate,
        private val objectCopyService: ObjectCopyService,
        private val clazz: Class<T>) {
    open fun create(document: T): Mono<T> = mongoRepository.insert(document)
    open fun findById(id: ID): Mono<T> = mongoRepository.findById(id)
            .switchIfEmpty(Mono.error(ResourceNotFoundException("Entity wasn't found for id: $id")))

    open fun update(id: ID, updates: UpdateDTO): Mono<T> {
        return mongoRepository.findById(id).flatMap { found ->
            objectCopyService.patchTarget(found, updates as Any)
            mongoRepository.save(found)
        }
    }

    fun getByFilter(dataFilter: Map<String, ValueContainer>, projectionFields: List<String>): Flux<T> {
        val query = Query()
        dataFilter.forEach { path, operatorValuePair ->
            val value = operatorValuePair.value
            val criteria = Criteria(path)
            when (operatorValuePair.operator) {
                Operator.EQ -> criteria.isEqualTo(value)
                Operator.IN -> criteria.inValues(*((value as List<*>).toTypedArray()))
                Operator.LT -> criteria.lt(value)
                Operator.GT -> criteria.gt(value)
                Operator.GTE -> criteria.gte(value)
                Operator.LTE -> criteria.lte(value)
            }
            query.addCriteria(criteria)
        }
        val fields = query.fields()
        projectionFields.forEach { f -> fields.include(f) }
        return mongoTemplate.query(clazz).matching(query).all()
    }
}