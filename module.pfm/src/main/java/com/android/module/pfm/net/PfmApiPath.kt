package com.android.module.pfm.net

import org.springframework.http.HttpMethod
import ru.bpc.mobilebanksdk.net.rest.ApiPath

enum class PfmApiPath(var _path: String, var _httpMethod: HttpMethod) : ApiPath {
    PFM_CARD_CATEGORIES("pfm/chart", HttpMethod.GET),
    PFM_TRANSACTIONS("pfm/transactions", HttpMethod.GET);

    override fun getHttpMethod(): HttpMethod {
        return _httpMethod
    }

    override fun getPath(): String {
        return _path
    }
}