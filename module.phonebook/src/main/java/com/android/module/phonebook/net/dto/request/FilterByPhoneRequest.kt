package com.android.module.phonebook.net.dto.request

import ru.bpc.mobilebanksdk.dto.request.CommonRequest

class FilterByPhoneRequest : CommonRequest() {

    var version: Long? = null
    var save: List<String>? = null
    var delete: List<String>? = null
    var locale: String? = null

}