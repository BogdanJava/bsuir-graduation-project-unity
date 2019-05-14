package by.bogdan.bsuir.bsuirgraduationbackend.controller

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TimelineEvent
import by.bogdan.bsuir.bsuirgraduationbackend.security.annotations.ProtectedResource
import by.bogdan.bsuir.bsuirgraduationbackend.service.TimelineEventService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.util.*

@RequestMapping(path = ["/api/timeline-events", "/api/timeline-event"])
@ProtectedResource
@RestController
class TimelineEventController(timelineEventService: TimelineEventService,
                              objectMapper: ObjectMapper) :
        AbstractController<TimelineEvent, UUID, TimelineEvent>(timelineEventService, objectMapper) {

    @GetMapping("/filter")
    override fun getByFilter(@RequestParam("filter") filterRaw: String,
                             @RequestParam("projection") projectionRaw: String?,
                             @RequestParam("pageSize") itemsPerPage: Int,
                             @RequestParam("pageNumber") pageNumber: Int): Flux<TimelineEvent> {
        return this._getByFilter(filterRaw, projectionRaw, itemsPerPage, pageNumber)
    }

}