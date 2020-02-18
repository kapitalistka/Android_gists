package com.android.module.pfm.ui.filter

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.android.module.pfm.R
import com.android.module.pfm.dto.DateFilterMode
import com.android.module.pfm.dto.DateRange
import com.android.module.pfm.dto.FilterValues
import com.android.module.pfm.ui.category.adapter.TransactionsDataTimeHelper
import kotlinx.android.synthetic.main.activity_pfm_filters.*
import ru.bpc.mobilebanksdk.DI.capabilities.SystemSettings
import ru.bpc.mobilebanksdk.activity.common.NavigationDrawerActivity
import ru.bpc.mobilebanksdk.fragments.dialogs.DatePickerDialogFragment
import ru.bpc.mobilebanksdk.ui.views.ContinueButton.BUTTON_DISABLE
import ru.bpc.mobilebanksdk.ui.views.ContinueButton.BUTTON_ENABLE
import ru.bpc.mobilebanksdk.ui.views.OnFiltersChangeListener
import java.util.*
import java.util.concurrent.TimeUnit

class PfmFiltersActivity : NavigationDrawerActivity(), PfmFilterContract.View {

    companion object {
        private const val FILTER_ITEM_DAY: Long = 1L
        private const val FILTER_ITEM_WEEK: Long = 2L
        private const val FILTER_ITEM_MONTH: Long = 4L
        private const val FILTER_ITEM_CUSTOM: Long = 8L

        private val dateFilterIdToModeMap = mapOf(
                FILTER_ITEM_DAY to DateFilterMode.DAY,
                FILTER_ITEM_WEEK to DateFilterMode.WEEK,
                FILTER_ITEM_MONTH to DateFilterMode.MONTH,
                FILTER_ITEM_CUSTOM to DateFilterMode.CUSTOM
        )

        private const val EXTRA_FILTER_VALUES = "filter_values"
        private const val EXTRA_FILTER_VALUES_DEFAULT = "filter_values_default"

        fun createBundleForStart(filterValues: FilterValues,
                                 defaultFilterValues: FilterValues) = Bundle().apply {
            putSerializable(EXTRA_FILTER_VALUES, filterValues)
            putSerializable(EXTRA_FILTER_VALUES_DEFAULT, defaultFilterValues)
        }

        fun retrieveResult(data: Bundle) = data.getSerializable(EXTRA_FILTER_VALUES) as FilterValues
    }

    private lateinit var presenter: PfmFilterContract.Presenter
    private var dateFilterRange: DateRange? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pfm_filters)

        presenter = PfmFilterPresenter(
                this,
                intent?.extras?.getSerializable(EXTRA_FILTER_VALUES_DEFAULT) as FilterValues
        )
        acceptToLifecycle(presenter)

        initToolbar()
        initFilterView()
        initDatePicker()
        initApplyButton()

        (intent.extras?.getSerializable(EXTRA_FILTER_VALUES) as? FilterValues)?.apply {
            presenter.selectDateFilterMode(this.dateFilterMode)
            presenter.selectCustomDateRange(this.dateFilterRange)
        }
    }

    private fun initToolbar() {
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_24dp)
        clearFiltersMenuItem.setOnClickListener { clearFilters() }
    }

    private fun initFilterView() {
        filterView.items = listOf(
                Pair(FILTER_ITEM_DAY, getString(R.string.filter_date_day)),
                Pair(FILTER_ITEM_WEEK, getString(R.string.filter_date_week)),
                Pair(FILTER_ITEM_MONTH, getString(R.string.filter_date_month)),
                Pair(FILTER_ITEM_CUSTOM, getString(R.string.filter_date_custom))
        )
        filterView.onFiltersChangeListener = object : OnFiltersChangeListener {
            override fun onFiltersChange(filters: Long) {
                dateFilterIdToModeMap[filters]?.apply {
                    presenter.selectDateFilterMode(this)
                }
            }
        }
    }

    private fun initDatePicker() {
        dateCustomFilterFrom.setOnClickListener {
            showDatePickerDialog {
                presenter.selectCustomDateRange(DateRange(it, dateFilterRange?.to))
            }
        }
        dateCustomFilterTo.setOnClickListener {
            showDatePickerDialog {
                presenter.selectCustomDateRange(DateRange(dateFilterRange?.from, it))
            }
        }
    }

    private fun initApplyButton() {
        applyButton.setOnClickListener { presenter.apply() }
    }

    private fun clearFilters() = presenter.clear()

    override fun displayDateFilterMode(dateFilterMode: DateFilterMode) {
        dateFilterIdToModeMap.filterValues { it == dateFilterMode }.keys.firstOrNull()?.apply {
            if(filterView.filterValue != this) {
                filterView.filterValue = this
            }
        }
    }

    override fun resultData(filterValues: FilterValues) = Intent().apply {
        putExtras(Bundle().apply { putSerializable(EXTRA_FILTER_VALUES, filterValues) })
    }

    override fun displayDateFilterRange(dateRange: DateRange?) {
        dateFilterRange = dateRange
        dateCustomFilterFrom.text = dateRange?.from?.let { TransactionsDataTimeHelper.getFormattedDate(it, TransactionsDataTimeHelper.Companion.FormatMode.BRIEF) } ?: getString(R.string.hint_select_start_date)
        dateCustomFilterTo.text = dateRange?.to?.let { TransactionsDataTimeHelper.getFormattedDate(it, TransactionsDataTimeHelper.Companion.FormatMode.BRIEF) } ?: getString(R.string.hint_select_end_date)
    }

    override fun changeDatePickerVisibility(visible: Boolean) {
        datePicker.visibility = if(visible) View.VISIBLE else View.GONE
    }

    override fun setFiltersValid(valid: Boolean) {
        applyButton.setButtonState(if(valid) BUTTON_ENABLE else BUTTON_DISABLE)
    }

    private fun showDatePickerDialog(selectListener: (Date) -> Unit) {
        val to = System.currentTimeMillis()
        val from = to - TimeUnit.DAYS.toMillis(SystemSettings.Pfm.filterDateRange().toLong())
        val datePickerDialogFragment = DatePickerDialogFragment.create(from, to)
        datePickerDialogFragment.setListener { view, year, monthOfYear, dayOfMonth ->
            val selectedDateCalendar = Calendar.getInstance()
            selectedDateCalendar.set(year, monthOfYear, dayOfMonth)

            if (selectedDateCalendar.timeInMillis < view.minDate) {
                showErrorMessage(R.string.wrong_date)
            } else {
                selectListener(selectedDateCalendar.time)
            }
        }
        datePickerDialogFragment.show(supportFragmentManager, null)
    }
}