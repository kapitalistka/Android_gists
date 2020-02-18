package com.android.module.pfm.dto

enum class DateFilterMode {
    DAY, WEEK, MONTH, CUSTOM;

    companion object {
        fun dateRange(mode: DateFilterMode, customDateRange: DateRange?) = when(mode) {
            DAY -> DateRange.day()
            WEEK -> DateRange.week()
            MONTH -> DateRange.month()
            else -> customDateRange
        }
    }
}