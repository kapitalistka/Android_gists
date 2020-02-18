package com.android.module.pfm.net

import com.android.module.pfm.net.dto.CardCategoriesRequest
import com.android.module.pfm.net.dto.CardCategoriesResponse
import com.android.module.pfm.net.dto.CategoryTransactionsRequest
import com.android.module.pfm.net.dto.CategoryTransactionsResponse
import fake.FakeCreatorPfm
import ru.bpc.mobilebanksdk.net.WebService
import ru.bpc.mobilebanksdk.net.WebUtils.createResponseResultErrorExceptionActionN
import ru.bpc.mobilebanksdk.net.WebUtils.getSingleTransformer
import ru.bpc.mobilebanksdk.net.WebUtils.single
import ru.bpc.mobilebanksdk.settings.ApplicationPropertiesHelper
import rx.Observable
import rx.Single

object PfmWebService {
    private var apiService: PfmApiService? = null
    private var lastIsFakeMode: Boolean = false

    private fun getApiService(): PfmApiService {
        if (apiService == null || lastIsFakeMode != ApplicationPropertiesHelper.isFakeMode()) {
            lastIsFakeMode = ApplicationPropertiesHelper.isFakeMode()
            apiService = if (lastIsFakeMode) FakeCreatorPfm.getFakeService() else PfmWebApiService()
        }
        return apiService!!
    }

    fun getCardCategoriesRequest(request: CardCategoriesRequest): Single<CardCategoriesResponse> =
            single { getApiService().executeCardCategories(request) }
                    .doOnEach(createResponseResultErrorExceptionActionN())
                    .compose(getSingleTransformer())

    @JvmStatic
    fun getCategoryTransactionsRequest(request: CategoryTransactionsRequest): Observable<CategoryTransactionsResponse> {
        return Observable.create(Observable.OnSubscribe<CategoryTransactionsResponse> { subscriber ->
            try {
                subscriber.onNext(getApiService().executeCategoryTransactions(request))
                subscriber.onCompleted()
            } catch (e: Throwable) {
                subscriber.onError(e)
            }
        }).compose(WebService.getDefaultTransformer(null))
    }
}