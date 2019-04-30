package by.bogdan.bsuir.bsuirgraduationbackend.datamodel

import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

open class BasicDocument(@Field var created: Date? = null,
                         @Field var updated: Date? = null,
                         @Field var deleted: Boolean? = null)