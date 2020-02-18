package com.android.module.pfm.ui.listitem

import android.view.View
import com.android.module.pfm.R
import com.android.module.pfm.dto.ChartCategory
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.list_item_chart_category.view.*
import ru.bpc.mobilebanksdk.helpers.currency.CurrencyFormatFactory
import ru.bpc.piechart.ui.view.PieSegment
import java.math.BigDecimal
import java.math.RoundingMode

class ChartCategoryItem(private val chartCategory: ChartCategory,
                        private val displayPercents: Boolean,
                        private val clickListener: (ChartCategory) -> Unit) : Item() {

    override fun getId() = chartCategory.hashCode().toLong()

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.setOnClickListener { clickListener(chartCategory) }
        viewHolder.itemView.categoryPieChart.resetSegments()
        viewHolder.itemView.categoryPieChart.add(PieSegment("", 1f, chartCategory.color))
        viewHolder.itemView.categoryName.text = chartCategory.category
        viewHolder.itemView.categoryAmount.text = CurrencyFormatFactory.getInstance().format(chartCategory.amount, chartCategory.currency)
        viewHolder.itemView.categoryAmountPercent.text = viewHolder.itemView.context.getString(
                R.string.template_percent,
                chartCategory.percent
        )
        viewHolder.itemView.categoryAmountPercent.visibility = if(displayPercents) View.VISIBLE else View.GONE
    }

    override fun getLayout() = R.layout.list_item_chart_category
}