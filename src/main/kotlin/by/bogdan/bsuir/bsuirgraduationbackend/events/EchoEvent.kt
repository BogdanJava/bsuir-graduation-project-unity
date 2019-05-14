package by.bogdan.bsuir.bsuirgraduationbackend.events

import org.springframework.context.ApplicationEvent

class EchoEvent(msg: String) : ApplicationEvent(msg)