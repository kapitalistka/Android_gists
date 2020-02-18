package com.android.module.phonebook.net.dto.response

import com.bpcbt.android.pdk.presenter.dto.MainResponse

class FilterByPhoneResponse : MainResponse() {

    var version: Long? = null
    var customers: List<String>? = null
    var none_customers: List<String>? = null

}