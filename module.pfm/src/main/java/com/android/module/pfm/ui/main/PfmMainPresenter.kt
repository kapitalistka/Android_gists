package com.android.module.pfm.ui.main

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import com.android.module.pfm.dto.*
import com.android.module.pfm.net.PfmWebService
import com.android.module.pfm.net.dto.CardCategoriesRequest
import com.android.module.pfm.ui.category.CategoryTransactionsActivity
import com.android.module.pfm.ui.filter.PfmFiltersActivity
import com.android.pdk.presenter.interfaces.ActivityResultReceiver
import com.android.pdk.presenter.interfaces.CommonPresenterAbs
import ru.bpc.mobilebanksdk.DI.SessionHolder
import ru.bpc.mobilebanksdk.dto.item.Card
import ru.bpc.module.cards.net.CardWebService
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.math.BigDecimal

class PfmMainPresenter(private val view: PfmMainContract.View) : CommonPresenterAbs(), PfmMainContract.Presenter {

    companion object {
        private const val PFM_FILTERS_REQUEST_CODE: Int = 3
        private val DATE_FILTER_MODE_DEFAULT = DateFilterMode.CUSTOM
        private val DATE_FILTER_DEFAULT = DateRange.currentMonth()
    }

    private var selectedCategory: ChartCategory? = null
    private var dateFilterMode: DateFilterMode = DATE_FILTER_MODE_DEFAULT
    private var dateFilter: DateRange = DATE_FILTER_DEFAULT

    private var currentCard: Card? = null

    override fun onViewCreated() {
        super.onViewCreated()

        clearViewChart()
        updateViewDateFilter()

        clearDateFilters()
        fetchCardsData()
    }

    private fun clearViewChart() {
        view.displayCategoriesChart(null)
    }

    private fun fetchCardsData() {
        view.showProgress { view.close() }
        CardWebService.getActiveCardsRequest(SessionHolder.getInstance())
                .doOnTerminate { view.hideProgress() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { cardsList ->
                            view.setCardsMenuItems(cardsList)
                            cardsList.firstOrNull()?.let { selectCard(it) }
                        },
                        { view.errorHandler.handle(it) }
                ).also { addSubscription(it) }
    }

    override fun selectCard(card: Card) {
        view.displayCard(card)
        currentCard = card
        updateCardChart()
    }

    private fun updateCardChart() {
        clearViewChart()

        currentCard?.let { card ->

            val request = CardCategoriesRequest().apply {
                cardId = card.id
                from = dateFilter.from
                to = dateFilter.to
            }

            addSubscription(PfmWebService.getCardCategoriesRequest(request).subscribe(
                    { response ->
                        if (currentCard?.id == card.id) {
                            categoriesChartFetchResult(CardCategoriesChart(
                                    response.amount ?: BigDecimal.ZERO,
                                    response.currency ?: currentCard?.currency ?: "",
                                    parseColor(response.emptyCategoriesColor),
                                    response.data
                                            .filter { (it.amount ?: BigDecimal.ZERO) > BigDecimal.ZERO }
                                            .map { category ->
                                                ChartCategory(
                                                        category.amount ?: BigDecimal.ZERO,
                                                        category.currency ?: currentCard?.currency ?: "",
                                                        category.category ?: "",
                                                        parseColor(category.color),
                                                        category.percent ?: 0
                                                )
                                            }
                            ))
                        }
                    },
                    {
                        categoriesChartFetchResult(CardCategoriesChart(
                                BigDecimal.ZERO,
                                card.currency,
                                Color.TRANSPARENT,
                                emptyList()
                        ))
                        view.errorHandler.handle(it)
                    }
            ))
        }
    }

    private fun categoriesChartFetchResult(result: CardCategoriesChart) {
        updateViewDateFilter()
        view.displayCategoriesChart(result)
    }

    private fun parseColor(color: String?) = color?.removePrefix("#")
            ?.let { try { Color.parseColor("#$it") } catch (t: Throwable) { null } }
            ?: Color.TRANSPARENT

    override fun openFilters() {
        view.startActivityForResult(
                PfmFiltersActivity::class.java,
                PFM_FILTERS_REQUEST_CODE,
                PfmFiltersActivity.createBundleForStart(
                        FilterValues(dateFilterMode, dateFilter),
                        FilterValues(DATE_FILTER_MODE_DEFAULT, DATE_FILTER_DEFAULT)
                ),
                object : ActivityResultReceiver {
                    override fun receive(requestCode: Int, resultCode: Int, data: Intent?) {
                        if (resultCode == Activity.RESULT_OK) {
                            data?.extras?.let { PfmFiltersActivity.retrieveResult(it) }?.apply {
                                setDateFilterMode(this.dateFilterMode, this.dateFilterRange)
                            }
                        }
                    }
                }
        )
    }

    override fun categorySelect(chartCategory: ChartCategory) {
        selectCategory(chartCategory)
    }

    override fun categoryDeselect(chartCategory: ChartCategory) {
        if(selectedCategory?.category == chartCategory.category) {
            selectCategory(null)
        }
    }

    private fun selectCategory(chartCategory: ChartCategory?) {
        selectedCategory = chartCategory
        view.displayChartCategory(chartCategory)
    }

    override fun clearDateFilters() {
        setDateFilterMode(DATE_FILTER_MODE_DEFAULT, DATE_FILTER_DEFAULT)
    }

    private fun setDateFilterMode(mode: DateFilterMode, dateFilter: DateRange? = null) {
        dateFilterMode = mode
        DateFilterMode.dateRange(mode, dateFilter)?.let {
            this.dateFilter = it
            updateCardChart()
        }
    }

    private fun updateViewDateFilter() {
        view.displayDateFilter(dateFilter, dateFilter != DATE_FILTER_DEFAULT)
    }

    override fun categoryDetailsClick(chartCategory: ChartCategory) {
        currentCard?.let {
            view.startActivity(
                    CategoryTransactionsActivity::class.java,
                    CategoryTransactionsActivity.createBundleForStart(Category().apply {
                        cardId = currentCard?.id
                        category = chartCategory.category
                        from = dateFilter.from
                        to = dateFilter.to
                    }, it)
            )
        }
    }
}