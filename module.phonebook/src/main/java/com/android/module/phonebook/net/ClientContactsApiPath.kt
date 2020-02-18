package com.android.module.phonebook.net

import org.springframework.http.HttpMethod
import ru.bpc.mobilebanksdk.net.rest.ApiPath

enum class ClientContactsApiPath(private val path: String, private val httpMethod: HttpMethod) : ApiPath {

    FILTER_BY_PHONE("customers/filter/byPhone", HttpMethod.POST);

    override fun getPath(): String {
        return path
    }

    override fun getHttpMethod(): HttpMethod {
        return httpMethod
    }

}