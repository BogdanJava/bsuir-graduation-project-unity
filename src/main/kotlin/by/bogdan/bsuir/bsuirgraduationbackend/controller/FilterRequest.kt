package by.bogdan.bsuir.bsuirgraduationbackend.controller

import reactor.core.publisher.Flux

interface FilterRequest<T> {
    fun getByFilter(filterRaw: String, projectionRaw: String): Flux<T>
}
