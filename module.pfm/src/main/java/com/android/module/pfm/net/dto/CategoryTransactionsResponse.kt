package com.android.module.pfm.net.dto

import com.android.pdk.presenter.dto.CommonResponse
import java.math.BigDecimal

class CategoryTransactionsResponse : CommonResponse() {
    var amount: BigDecimal? = null
    var currency: String? = null
    var category: String? = null
    var data: ArrayList<TransactionInformation> = ArrayList()
}