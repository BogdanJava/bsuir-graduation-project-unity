package by.bogdan.bsuir.bsuirgraduationbackend.service

import by.bogdan.bsuir.bsuirgraduationbackend.controller.Operator
import by.bogdan.bsuir.bsuirgraduationbackend.controller.ValueContainer
import by.bogdan.bsuir.bsuirgraduationbackend.controller.ValueType
import by.bogdan.bsuir.bsuirgraduationbackend.exceptions.ResourceNotFoundException
import by.bogdan.bsuir.bsuirgraduationbackend.utils.CustomReflectionUtils
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.CriteriaDefinition
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.inValues
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.reflect.Field
import java.text.DateFormat
import java.util.*

abstract class CrudService<T, ID, UpdateDTO>(
        private val mongoRepository: ReactiveMongoRepository<T, ID>,
        private val mongoTemplate: ReactiveMongoTemplate,
        private val objectCopyService: ObjectCopyService,
        private val clazz: Class<T>,
        private val reflectionUtils: CustomReflectionUtils,
        private val dateFormat: DateFormat) {
    open fun create(document: T): Mono<T> = mongoRepository.insert(document)
    open fun findById(id: ID): Mono<T> = mongoRepository.findById(id)
            .switchIfEmpty(Mono.error(ResourceNotFoundException("Entity wasn't found for id: $id")))

    open fun update(id: ID, updates: UpdateDTO): Mono<T> {
        return mongoRepository.findById(id).flatMap { found ->
            objectCopyService.patchTarget(found as Any, updates as Any)
            mongoRepository.save(found)
        }
    }

    fun getByFilter(dataFilter: Map<String, ValueContainer>,
                    projectionFields: List<String>,
                    itemsPerPage: Int,
                    pageNumber: Int): Flux<T> {
        val query = Query()
        query.with(PageRequest.of(pageNumber, itemsPerPage))
        dataFilter.forEach { path, operatorValuePair ->
            query.addCriteria(getCriteria(path, operatorValuePair))
        }
        val fields = query.fields()
        if (!projectionFields.isEmpty()) {
            projectionFields.forEach { f -> fields.include(f) }
        }
        return mongoTemplate.query(clazz).matching(query).all()
    }

    private fun getCriteria(path: String, operatorValuePair: ValueContainer): CriteriaDefinition {
        var value = getValue(path, operatorValuePair.value)
        value = when (operatorValuePair.type) {
            ValueType.ANY -> value
            ValueType.DATE -> dateFormat.parse(value as String)
        }
        return when (operatorValuePair.operator) {
            Operator.EQ -> if (operatorValuePair.not) Criteria.where(path).ne(value) else Criteria.where(path).`is`(value)
            Operator.CONTAINS -> Criteria.where(path).regex(value as String)
            Operator.CONTAINS_I -> Criteria.where(path).regex(value as String, "i")
            Operator.IN -> Criteria.where(path).inValues(*((value as List<*>).toTypedArray()))
            Operator.LT -> Criteria.where(path).lt(value)
            Operator.GT -> Criteria.where(path).gt(value)
            Operator.GTE -> Criteria.where(path).gte(value)
            Operator.LTE -> Criteria.where(path).lte(value)
            Operator.AND -> Criteria().andOperator(*getCriteriaList(path, asValueContainers(value)))
            Operator.OR -> Criteria().orOperator(*getCriteriaList(path, asValueContainers(value)))
            else -> throw UnsupportedOperationException("Operation is not supported: " +
                    "${operatorValuePair.operator}")
        }
    }

    private fun asValueContainers(value: Any): Collection<ValueContainer> {
        val rawValue = value as List<Map<*, *>>
        return rawValue.map { map: Map<*, *> ->
            val valueContainer = ValueContainer()
            valueContainer.operator = Operator.valueOf(map["operator"] as String)
            valueContainer.type = if (map["type"] != null) {
                ValueType.valueOf(map["type"] as String)
            } else ValueType.ANY
            valueContainer.value = map["value"]!!
            valueContainer.not = if (map["not"] != null) map["not"] as Boolean else false
            valueContainer
        }
    }

    private fun getCriteriaList(path: String, value: Collection<ValueContainer>): Array<Criteria> {
        return value
                .map { c -> getCriteria(path, c) }
                .map { criteriaDefinition -> criteriaDefinition as Criteria }
                .toTypedArray()
    }

    private fun getValue(path: String, value: Any): Any {
        val field = reflectionUtils.findField(path, clazz)!!
        return if (value is List<*>) {
            return value.map { convertValue(field, it!!) }
        } else convertValue(field, value)
    }

    private fun convertValue(field: Field, value: Any): Any {
        return when (field.type) {
            UUID::class.java -> UUID.fromString(value as String)
            else -> value
        }
    }
}