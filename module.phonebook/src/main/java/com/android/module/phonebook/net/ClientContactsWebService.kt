package com.android.module.phonebook.net

import com.android.module.phonebook.net.dto.request.FilterByPhoneRequest
import com.android.module.phonebook.net.dto.response.FilterByPhoneResponse
import fake.FakeCreatorPhonebook
import ru.bpc.mobilebanksdk.net.WebUtils.createResponseResultErrorExceptionActionN
import ru.bpc.mobilebanksdk.net.WebUtils.getSingleTransformer
import ru.bpc.mobilebanksdk.net.WebUtils.single
import ru.bpc.mobilebanksdk.settings.ApplicationPropertiesHelper
import rx.Single

object ClientContactsWebService {

    private var lastIsFakeMode: Boolean? = null
    private var apiService: ClientContactsApiService? = null

    private fun getApiService(): ClientContactsApiService {
        if (apiService == null || lastIsFakeMode != ApplicationPropertiesHelper.isFakeMode()) {
            lastIsFakeMode = ApplicationPropertiesHelper.isFakeMode()
            apiService = if (lastIsFakeMode!!) FakeCreatorPhonebook.getFakeService() else ClientContactsWebApiService()
        }
        return apiService!!
    }

    fun getFilterByPhoneRequest(request: FilterByPhoneRequest): Single<FilterByPhoneResponse> =
            single { getApiService().filterByPhone(request) }
                    .doOnEach(createResponseResultErrorExceptionActionN())
                    .compose(getSingleTransformer())

}
