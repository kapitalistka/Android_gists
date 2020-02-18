package com.android.module.pfm.dto

import java.io.Serializable
import java.util.*

data class DateRange(val from: Date?,
                     val to: Date?) : Serializable {
    companion object {

        fun day(): DateRange {
            val calendar = Calendar.getInstance()
            return DateRange(
                    calendar.time,
                    calendar.time
            )
        }

        fun week(): DateRange {
            val calendar = Calendar.getInstance()
            val to = calendar.time
            return DateRange(
                    with(calendar) { this.add(Calendar.DATE, -7); this.time },
                    to
            )
        }

        fun month(): DateRange {
            val calendar = Calendar.getInstance()
            val to = calendar.time
            return DateRange(
                    with(calendar) { this.add(Calendar.MONTH, -1); this.time },
                    to
            )
        }

        fun currentMonth(): DateRange {
            val calendar = Calendar.getInstance()
            val to = calendar.time
            return DateRange(
                    with(calendar) {
                        this.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
                        this.time
                    },
                    to
            )
        }

        fun custom(from: Date, to: Date) = DateRange(
                from,
                to
        )
    }
}