package by.bogdan.bsuir.bsuirgraduationbackend.events

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TimelineEvent
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.context.ApplicationEvent

class NotificationEvent(@JsonIgnore val payload: TimelineEvent) : ApplicationEvent(payload)