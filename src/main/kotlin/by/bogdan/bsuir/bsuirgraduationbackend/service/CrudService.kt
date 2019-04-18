package by.bogdan.bsuir.bsuirgraduationbackend.service

import by.bogdan.bsuir.bsuirgraduationbackend.controller.Operator
import by.bogdan.bsuir.bsuirgraduationbackend.controller.ValueContainer
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.BasicDocument
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.UserDocument
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
        private val clazz: Class<T>) {
    abstract fun create(document: T): Mono<T>
    abstract fun findById(id: ID): Mono<T>
    abstract fun update(id: ID, updates: UpdateDTO): Mono<T>

    fun getByFilter(dataFilter: Map<String, ValueContainer>): Flux<T> {
        val query = Query()
        dataFilter.forEach { path, operatorValuePair ->
            val value = operatorValuePair.value
            val criteria = Criteria(path)
            when (operatorValuePair.operator) {
                Operator.EQ -> criteria.isEqualTo(value)
                Operator.IN -> criteria.inValues(value)
                Operator.LT -> criteria.lt(value)
                Operator.GT -> criteria.gt(value)
                Operator.GTE -> criteria.gte(value)
                Operator.LTE -> criteria.lte(value)
            }
            query.addCriteria(criteria)
        }
        return mongoTemplate.query(clazz).matching(query).all()
    }
}