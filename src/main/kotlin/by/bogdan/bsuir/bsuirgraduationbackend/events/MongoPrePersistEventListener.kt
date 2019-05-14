package by.bogdan.bsuir.bsuirgraduationbackend.events

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.BasicDocument
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TimelineEvent
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TimelineEventType
import by.bogdan.bsuir.bsuirgraduationbackend.service.TimelineEventService
import by.bogdan.bsuir.bsuirgraduationbackend.utils.CustomReflectionUtils
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent
import org.springframework.stereotype.Component
import java.util.*

@Component
class MongoPrePersistEventListener(val reflectionUtils: CustomReflectionUtils,
                                   val timelineEventService: TimelineEventService) :
        AbstractMongoEventListener<BasicDocument>() {

    override fun onBeforeConvert(event: BeforeConvertEvent<BasicDocument>) {
        val source = event.source
        if (isIdNull(source)) {
            val generatedId = UUID.randomUUID()
            log.info("Id is null, generating a new one: [$generatedId]")
            reflectionUtils.setField("id", source, generatedId)
        }
    }

    override fun onBeforeSave(event: BeforeSaveEvent<BasicDocument>) {
        val document = event.document!!
        log.info("Pre persist document: $document")
        val source = event.source
        val date = Date()
        if (source.created == null) {
            timelineEventService.create(TimelineEvent.create(
                    source::class.java,
                    "",
                    TimelineEventType.CREATE,
                    source)).subscribe { createdEvent ->
                log.info("Event created: $createdEvent")
            }
            document["created"] = date
        }
        timelineEventService.create(TimelineEvent.create(
                source::class.java,
                "",
                TimelineEventType.UPDATE,
                source)).subscribe { createdEvent ->
            log.info("Event created: $createdEvent")
        }
        document["updated"] = date
        document["deleted"] = false
    }

    private fun isIdNull(source: BasicDocument): Boolean {
        return reflectionUtils.readField("id", source) == null
    }

    companion object {
        val log = LoggerFactory.getLogger(MongoPrePersistEventListener::class.java)!!
    }

}