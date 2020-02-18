package com.android.module.pfm.net.dto

import ru.bpc.mobilebanksdk.dto.request.CommonRequest
import java.util.*

class CardCategoriesRequest : CommonRequest() {
    var cardId: String? = null
    var from: Date? = null
    var to: Date? = null
}