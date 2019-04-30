package by.bogdan.bsuir.bsuirgraduationbackend.events

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.BasicDocument
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent
import org.springframework.stereotype.Component
import java.util.*

@Component
@Slf4j
class MongoPrePersistEventListener :
        AbstractMongoEventListener<BasicDocument>() {

    override fun onBeforeSave(event: BeforeSaveEvent<BasicDocument>) {
        val document = event.document!!
        log.info("Pre persist document: $document")
        val source = event.source
        val date = Date()
        if (source.created == null) {
            document["created"] = date
        }
        document["updated"] = date
        document["deleted"] = false
    }


    companion object {
        val log = LoggerFactory.getLogger(MongoPrePersistEventListener::class.java)!!
    }

}