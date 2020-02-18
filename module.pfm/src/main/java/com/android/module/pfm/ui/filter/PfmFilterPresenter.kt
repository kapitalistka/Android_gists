package com.android.module.pfm.ui.filter

import android.app.Activity
import com.android.module.pfm.dto.DateFilterMode
import com.android.module.pfm.dto.DateRange
import com.android.module.pfm.dto.FilterValues
import com.android.pdk.presenter.interfaces.CommonPresenterAbs
import java.util.concurrent.TimeUnit

class PfmFilterPresenter(private val view: PfmFilterContract.View,
                         private val defaultFilter: FilterValues) : CommonPresenterAbs(), PfmFilterContract.Presenter {

    private var dateFilterMode: DateFilterMode = defaultFilter.dateFilterMode
    private var dateRange: DateRange? = defaultFilter.dateFilterRange

    override fun onViewCreated() {
        super.onViewCreated()
        resetFilters()
    }

    override fun clear() {
        resetFilters()
        apply()
    }

    private fun resetFilters() {
        selectCustomDateRange(defaultFilter.dateFilterRange)
        selectDateFilterMode(defaultFilter.dateFilterMode)
    }

    override fun selectDateFilterMode(dateFilterMode: DateFilterMode) {
        this.dateFilterMode = dateFilterMode
        view.displayDateFilterMode(dateFilterMode)
        view.changeDatePickerVisibility(dateFilterMode == DateFilterMode.CUSTOM)
        selectCustomDateRange(DateFilterMode.dateRange(dateFilterMode, dateRange))
        updateFilterValid()
    }

    override fun selectCustomDateRange(dateRange: DateRange?) {
        this.dateRange = dateRange
        view.displayDateFilterRange(dateRange)
        updateFilterValid()
    }

    private fun updateFilterValid() {
        view.setFiltersValid(
                (dateFilterMode != DateFilterMode.CUSTOM) ||
                        listOf(dateRange, dateRange?.from, dateRange?.to).any { it == null }.not() &&
                        with(TimeUnit.DAYS.toMillis(1)) {
                            val fromDay = dateRange?.from?.time?.div(this) ?: 0L
                            val toDay = dateRange?.to?.time?.div(this) ?: 0L
                            fromDay <= toDay
                        }
        )
    }

    override fun apply() {
        view.setResult(Activity.RESULT_OK, view.resultData(FilterValues(dateFilterMode, dateRange)))
        view.close()
    }
}