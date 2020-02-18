package com.android.module.pfm.net

import com.android.module.pfm.net.dto.CardCategoriesRequest
import com.android.module.pfm.net.dto.CardCategoriesResponse
import com.android.module.pfm.net.dto.CategoryTransactionsRequest
import com.android.module.pfm.net.dto.CategoryTransactionsResponse


interface PfmApiService {
    @Throws(Exception::class)
    fun executeCardCategories(request: CardCategoriesRequest): CardCategoriesResponse
    @Throws(Exception::class)
    fun executeCategoryTransactions(request: CategoryTransactionsRequest): CategoryTransactionsResponse
}