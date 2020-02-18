package com.android.module.pfm.ui.main

import com.android.module.pfm.dto.CardCategoriesChart
import com.android.module.pfm.dto.ChartCategory
import com.android.module.pfm.dto.DateRange
import com.android.pdk.presenter.interfaces.ActivityResultRequester
import com.android.pdk.presenter.interfaces.CommonPresenter
import com.android.pdk.presenter.interfaces.CommonView
import ru.bpc.mobilebanksdk.dto.item.Card

interface PfmMainContract {

    interface View : CommonView, ActivityResultRequester {
        fun setCardsMenuItems(cards: List<Card>)
        fun displayCard(card: Card)
        fun displayDateFilter(dateFilter: DateRange, clearable: Boolean)
        fun displayCategoriesChart(cardCategoriesChart: CardCategoriesChart?)
        fun displayChartCategory(chartCategory: ChartCategory?)
    }

    interface Presenter : CommonPresenter {
        fun selectCard(card: Card)
        fun openFilters()
        fun categorySelect(chartCategory: ChartCategory)
        fun categoryDeselect(chartCategory: ChartCategory)
        fun categoryDetailsClick(chartCategory: ChartCategory)
        fun clearDateFilters()
    }

}