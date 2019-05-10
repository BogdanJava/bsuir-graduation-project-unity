package by.bogdan.bsuir.bsuirgraduationbackend.controller

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.BasicDocument
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.DataFilter
import by.bogdan.bsuir.bsuirgraduationbackend.service.CrudService
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.util.StringUtils
import reactor.core.publisher.Flux

abstract class AbstractController<T : BasicDocument, ID, UT>(
        private val entityService: CrudService<T, ID, UT>,
        private val objectMapper: ObjectMapper) {

    protected fun _getByFilter(filterRaw: String,
                               projectionRaw: String?): Flux<T> {
        val filter = objectMapper.readValue(filterRaw, DataFilter::class.java)
        val projection = if (!StringUtils.isEmpty(projectionRaw)) {
            objectMapper.readValue<List<String>>(projectionRaw, object : TypeReference<List<String>>() {})
        } else emptyList()
        return entityService.getByFilter(filter.filter, projection)
    }

    abstract fun getByFilter(filterRaw: String, projectionRaw: String?): Flux<T>

}
