package by.bogdan.bsuir.bsuirgraduationbackend.datamodel

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "time_requests")
data class TimeRequest(var id: UUID?,
                       var userId: UUID?,
                       var type: TimeRequestType,
                       var startDate: Date?,
                       var endDate: Date?,
                       var projectId: UUID?,
                       var description: String?,
                       var approverId: UUID?,
                       var approved: Boolean = false) : BasicDocument()

enum class TimeRequestType {
    BUSINESS_LEAVE,
    VACATION,
    UNPAID,
    TIME_SHIFT,
    ILLNESS
}

data class TimeRequestUpdateDTO(var approved: Boolean,
                                var description: String?)