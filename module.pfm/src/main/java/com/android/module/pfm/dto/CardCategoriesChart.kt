package com.android.module.pfm.dto

import java.math.BigDecimal

class CardCategoriesChart(
        val amount: BigDecimal,
        val currency: String,
        val emptyCategoriesColor: Int,
        val categories: List<ChartCategory>
)