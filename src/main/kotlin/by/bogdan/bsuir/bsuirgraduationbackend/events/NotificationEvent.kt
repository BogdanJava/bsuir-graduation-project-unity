package by.bogdan.bsuir.bsuirgraduationbackend.events

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TimelineEvent
import org.springframework.context.ApplicationEvent

class NotificationEvent(payload: TimelineEvent) : ApplicationEvent(payload)