package by.bogdan.bsuir.bsuirgraduationbackend.datamodel

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

/**
 * @author bogdanshishkin1998@gmail.com
 * @since 4/26/2019
 */

@Document(collection = "worktime_requests")
data class WorktimeRequest(@Id @Field("id") var id: UUID? = null,
                           var userId: UUID?,
                           var startDate: Date?,
                           var endDate: Date?,
                           var description: String?,
                           var projectId: UUID?,
                           var approverId: UUID?,
                           var approved: Boolean = false) : BasicDocument()