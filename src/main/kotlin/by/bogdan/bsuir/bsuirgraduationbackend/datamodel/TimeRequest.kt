package by.bogdan.bsuir.bsuirgraduationbackend.datamodel

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document(collection = "time_requests")
data class TimeRequest(@Id @Field("id") var id: UUID? = null,
                       var userId: UUID?,
                       var type: TimeRequestType,
                       var startDate: Date?,
                       var endDate: Date?,
                       var description: String?,
                       var approverId: UUID?,
                       var status: RequestStatus?) : BasicDocument()

enum class TimeRequestType {
    BUSINESS_LEAVE,
    VACATION,
    UNPAID,
    TIME_SHIFT,
    ILLNESS
}

enum class RequestStatus {
    APPROVED,
    DECLINED,
    PENDING
}

data class TimeRequestUpdateDTO(var approved: Boolean,
                                var description: String?)