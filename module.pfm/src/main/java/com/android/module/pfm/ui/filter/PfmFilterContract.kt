package com.android.module.pfm.ui.filter

import android.content.Intent
import com.android.module.pfm.dto.DateFilterMode
import com.android.module.pfm.dto.DateRange
import com.android.module.pfm.dto.FilterValues
import com.android.pdk.presenter.interfaces.CommonPresenter
import com.android.pdk.presenter.interfaces.CommonView

interface PfmFilterContract {

    interface View : CommonView {
        fun displayDateFilterMode(dateFilterMode: DateFilterMode)
        fun displayDateFilterRange(dateRange: DateRange?)
        fun resultData(filterValues: FilterValues) : Intent
        fun changeDatePickerVisibility(visible: Boolean)
        fun setFiltersValid(valid: Boolean)
    }

    interface Presenter : CommonPresenter {
        fun clear()
        fun selectDateFilterMode(dateFilterMode: DateFilterMode)
        fun selectCustomDateRange(dateRange: DateRange?)
        fun apply()
    }

}