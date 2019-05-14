package by.bogdan.bsuir.bsuirgraduationbackend.datamodel

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document(collection = "timelineEvents")
class TimelineEvent {
    lateinit var className: String
    lateinit var description: String
    lateinit var type: TimelineEventType
    lateinit var obj: Any
    lateinit var created: Date

    @Field("id")
    @Id
    lateinit var id: UUID;

    companion object {
        fun create(clazz: Class<*>, description: String, type: TimelineEventType, obj: Any): TimelineEvent {
            val timelineEvent = TimelineEvent()
            timelineEvent.className = clazz.name
            timelineEvent.description = description
            timelineEvent.type = type
            timelineEvent.obj = obj
            timelineEvent.id = UUID.randomUUID()
            timelineEvent.created = Date()
            return timelineEvent
        }
    }

    override fun toString(): String {
        return "{type: $type, class: $className, description: $description}"
    }
}

enum class TimelineEventType {
    CREATE, UPDATE, DELETE, APPROVE, DECLINE, MESSAGE, HANDSHAKE
}