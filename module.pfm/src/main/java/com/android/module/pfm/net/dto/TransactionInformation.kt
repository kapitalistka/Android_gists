package com.android.module.pfm.net.dto

import com.bpcbt.outputs.dto.item.parameters.MoneyParameterObject
import java.io.Serializable
import java.math.BigDecimal

class TransactionInformation : Serializable {
    var description: String? = null
    var primaryAmount: MoneyParameterObject = MoneyParameterObject()
    var secondaryAmount: MoneyParameterObject? = null
    var timestamp: String? = null
}