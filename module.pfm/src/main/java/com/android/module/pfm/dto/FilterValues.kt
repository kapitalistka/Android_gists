package com.android.module.pfm.dto

import java.io.Serializable

class FilterValues(
        val dateFilterMode: DateFilterMode,
        val dateFilterRange: DateRange?
) : Serializable