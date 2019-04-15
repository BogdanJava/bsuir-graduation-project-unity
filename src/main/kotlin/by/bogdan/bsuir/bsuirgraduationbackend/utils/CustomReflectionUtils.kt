package by.bogdan.bsuir.bsuirgraduationbackend.utils

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.ReflectionUtils
import java.lang.reflect.Field

@Component
class CustomReflectionUtils {

    fun readField(field: Field, source: Any): Any? {
        val foundField = ReflectionUtils.findField(source::class.java, field.name)
        return if (foundField != null) {
            foundField.isAccessible = true
            ReflectionUtils.getField(foundField, source)
        } else {
            log.warn("Missing field \"${field.name}\" in class \"${source::class.java}\"")
            null
        }
    }

    fun setField(field: Field, source: Any, value: Any?) {
        val foundField = ReflectionUtils.findField(source::class.java, field.name)
        if (foundField != null) {
            foundField.isAccessible = true
            ReflectionUtils.setField(field, source, value)
        } else {
            log.warn("Missing field \"${field.name}\" in class \"${source::class.java}\"")
        }
    }

    fun setField(field: String, source: Any, value: Any) {
        val foundField = ReflectionUtils.findField(source::class.java, field)
        if (foundField != null) {
            this.setField(foundField, source, value)
        } else {
            log.warn("Missing field \"$field\" in class \"${source::class.java}\"")
        }
    }

    fun readField(field: String, source: Any): Any? {
        val foundField = ReflectionUtils.findField(source::class.java, field)
        return if (foundField != null) {
            this.readField(foundField, source)
        } else {
            log.warn("Missing field \"$field\" in class \"${source::class.java}\"")
            null
        }
    }

    companion object {
        val log = LoggerFactory.getLogger(CustomReflectionUtils::class.java)!!
    }
}