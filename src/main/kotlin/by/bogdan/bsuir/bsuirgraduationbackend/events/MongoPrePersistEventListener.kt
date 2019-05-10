package by.bogdan.bsuir.bsuirgraduationbackend.events

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.BasicDocument
import by.bogdan.bsuir.bsuirgraduationbackend.utils.CustomReflectionUtils
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent
import org.springframework.stereotype.Component
import java.util.*

@Component
@Slf4j
class MongoPrePersistEventListener(val reflectionUtils: CustomReflectionUtils) :
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
            document["created"] = date
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