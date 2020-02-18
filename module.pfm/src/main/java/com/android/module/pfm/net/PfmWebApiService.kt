package com.android.module.pfm.net

import com.android.module.pfm.net.dto.CardCategoriesRequest
import com.android.module.pfm.net.dto.CardCategoriesResponse
import com.android.module.pfm.net.dto.CategoryTransactionsRequest
import com.android.module.pfm.net.dto.CategoryTransactionsResponse
import com.android.pdk.presenter.dto.CommonResponse
import ru.bpc.mobilebanksdk.dto.request.CommonRequest
import ru.bpc.mobilebanksdk.net.rest.WebApiService
import ru.bpc.mobilebanksdk.net.rest.WebRequest
import java.text.SimpleDateFormat
import java.util.*

class PfmWebApiService : PfmApiService {

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
    }

    override fun executeCardCategories(request: CardCategoriesRequest): CardCategoriesResponse {
        val networkRequest = WebRequest<CardCategoriesResponse, CommonRequest>(CardCategoriesResponse::class.java)
        networkRequest.setPath(PfmApiPath.PFM_CARD_CATEGORIES)
        networkRequest.addParameter("cardId", request.cardId)
        request.from?.let { networkRequest.addParameter("from", DATE_FORMAT.format(it)) }
        request.to?.let { networkRequest.addParameter("to", DATE_FORMAT.format(it)) }
        networkRequest.addParameter("locale", getLocale())
        return networkRequest.loadDataFromNetwork()
    }

    override fun executeCategoryTransactions(categoryTransactionsRequest: CategoryTransactionsRequest): CategoryTransactionsResponse {
        val request = WebRequest<CategoryTransactionsResponse, CommonRequest>(CategoryTransactionsResponse::class.java)
        request.setPath(PfmApiPath.PFM_TRANSACTIONS)
        request.addParameter("cardId", categoryTransactionsRequest.cardId)
        categoryTransactionsRequest.from?.let { request.addParameter("from", DATE_FORMAT.format(it)) }
        categoryTransactionsRequest.to?.let { request.addParameter("to", DATE_FORMAT.format(it)) }
        request.addParameter("category", categoryTransactionsRequest.category)
        return request.loadDataFromNetwork()
    }

    private fun getLocale(): String {
        return WebApiService.getLocale()
    }

    private fun <T : CommonResponse> WebRequest<T, CommonRequest>.addParameters(parameters: Map<String, String>?, paramPrefix: String) {
        if (parameters != null)
            for ((key, value) in parameters) {
                this.addParameter(paramPrefix + key, value)
            }
    }
}