package com.android.module.phonebook.net

import com.android.module.phonebook.net.dto.request.FilterByPhoneRequest
import com.android.module.phonebook.net.dto.response.FilterByPhoneResponse
import ru.bpc.mobilebanksdk.net.rest.WebApiService
import ru.bpc.mobilebanksdk.net.rest.WebRequest


internal class ClientContactsWebApiService : ClientContactsApiService {

    override fun filterByPhone(filterByPhoneRequest: FilterByPhoneRequest): FilterByPhoneResponse {
        filterByPhoneRequest.locale = getLocale()

        val request = WebRequest<FilterByPhoneResponse, FilterByPhoneRequest>(FilterByPhoneResponse::class.java)
        request.setPath(ClientContactsApiPath.FILTER_BY_PHONE)
        request.setBody(filterByPhoneRequest)

        return request.loadDataFromNetwork()
    }

    private fun getLocale(): String {
        return WebApiService.getLocale()
    }

}
