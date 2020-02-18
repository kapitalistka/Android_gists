package com.android.module.pfm.net.dto

import com.android.pdk.presenter.dto.MainResponse
import java.math.BigDecimal

class CardCategoriesResponse : MainResponse() {
    var amount: BigDecimal? = null
    var currency: String? = null
    var emptyCategoriesColor: String? = null
    var data: List<CategoryChartResponse> = emptyList()
}