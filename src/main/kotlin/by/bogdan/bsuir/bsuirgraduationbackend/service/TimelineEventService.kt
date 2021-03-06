package by.bogdan.bsuir.bsuirgraduationbackend.service

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TimelineEvent
import by.bogdan.bsuir.bsuirgraduationbackend.events.NotificationEvent
import by.bogdan.bsuir.bsuirgraduationbackend.repository.TimelineEventRepository
import by.bogdan.bsuir.bsuirgraduationbackend.utils.CustomReflectionUtils
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.text.DateFormat
import java.util.*

@Service
class TimelineEventService(
        timelineEventRepository: TimelineEventRepository,
        reflectionUtils: CustomReflectionUtils,
        objectCopyService: ObjectCopyService,
        mongoTemplate: ReactiveMongoTemplate,
        val applicationEventPublisher: ApplicationEventPublisher,
        @Qualifier("isoDateFormat") dateFormat: DateFormat) :
        CrudService<TimelineEvent, UUID, TimelineEvent>(
                clazz = TimelineEvent::class.java,
                reflectionUtils = reflectionUtils,
                objectCopyService = objectCopyService,
                mongoTemplate = mongoTemplate,
                mongoRepository = timelineEventRepository,
                dateFormat = dateFormat) {

    override fun create(document: TimelineEvent): Mono<TimelineEvent> {
        return super.create(document).map { d ->
            applicationEventPublisher.publishEvent(NotificationEvent(d))
            d
        }
    }
}