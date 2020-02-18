package com.android.module.pfm.dto

import java.math.BigDecimal

data class ChartCategory(
        val amount: BigDecimal,
        val currency: String,
        val category: String,
        val color: Int,
        val percent: Int
)