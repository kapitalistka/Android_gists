package com.android.module.pfm.net.dto

import java.io.Serializable
import java.math.BigDecimal

class CategoryChartResponse : Serializable {
    var amount: BigDecimal? = null
    var currency: String? = null
    var category: String? = null
    var color: String? = null
    var percent: Int? = null
}