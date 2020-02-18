package com.android.module.pfm.ui.listitem

import android.view.View
import android.widget.TextView
import com.android.module.pfm.R
import com.android.module.pfm.dto.DateRange
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.list_item_chart_filters.view.*
import ru.bpc.mobilebanksdk.BaseBankApplication
import java.text.SimpleDateFormat

class ChartFilterItem(private val clearListener: () -> Unit) : Item()  {

    private var dateFilter: DateRange? = null
    private var dateFilterClearable: Boolean = false

    private var dateFilterDefaultText: TextView? = null
    private var dateFilterClearableText: TextView? = null
    private var dateFilterClearableView: View? = null

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", BaseBankApplication.getLocale())

    override fun getId() = this.javaClass.simpleName.hashCode().toLong()

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            this@ChartFilterItem.dateFilterDefaultText = dateFilterDefaultText
            this@ChartFilterItem.dateFilterClearableText = dateFilterClearableText
            this@ChartFilterItem.dateFilterClearableView = dateFilterClearableView

            dateFilterClearableButton.setOnClickListener { clearListener() }
        }

        updateDateFilter()
    }

    override fun unbind(holder: ViewHolder) {
        super.unbind(holder)

        dateFilterDefaultText = null
        dateFilterClearableText = null
        dateFilterClearableView = null
    }

    private fun updateDateFilter() {
        val dateFilterSet = listOf(dateFilter?.from, dateFilter?.to).any { it == null }.not()

        dateFilterDefaultText?.let { dateFilterDefaultText ->
            dateFilterDefaultText.context.apply {
                dateFilterDefaultText.text = if(dateFilterSet) getString(
                        R.string.title_date_filter,
                        dateFormat.format(dateFilter!!.from),
                        dateFormat.format(dateFilter!!.to)
                ) else getString(R.string.filter_date_all_time)
            }
        }
        dateFilterClearableText?.let {  dateFilterClearableText ->
            dateFilterClearableText.context.apply {
                dateFilterClearableText.text = if(dateFilterSet) getString(
                        R.string.title_date_filter_short,
                        dateFormat.format(dateFilter!!.from),
                        dateFormat.format(dateFilter!!.to)
                ) else getString(R.string.filter_date_all_time)
            }
        }

        dateFilterDefaultText?.visibility = if(dateFilterClearable) View.INVISIBLE else View.VISIBLE
        dateFilterClearableView?.visibility = if(dateFilterClearable) View.VISIBLE else View.INVISIBLE
    }

    fun displayDateFilter(dateFilter: DateRange, clearable: Boolean) {
        this.dateFilter = dateFilter
        this.dateFilterClearable = clearable
        updateDateFilter()
    }

    override fun getLayout() = R.layout.list_item_chart_filters


}