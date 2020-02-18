package com.android.module.phonebook.net

import com.android.module.phonebook.net.dto.request.FilterByPhoneRequest
import com.android.module.phonebook.net.dto.response.FilterByPhoneResponse

internal interface  ClientContactsApiService {

    @Throws(Exception::class)
    fun filterByPhone(filterByPhoneRequest: FilterByPhoneRequest): FilterByPhoneResponse
}