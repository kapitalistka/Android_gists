package com.android.module.pfm.ui.listitem

import android.view.View
import android.widget.TextView
import com.android.module.pfm.R
import com.android.module.pfm.dto.CardCategoriesChart
import com.android.module.pfm.dto.ChartCategory
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.list_item_pie_chart.view.*
import ru.bpc.mobilebanksdk.helpers.currency.CurrencyFormatFactory
import ru.bpc.piechart.ui.view.PieChartView
import ru.bpc.piechart.ui.view.PieSegment
import java.math.BigDecimal


class PieChartItem(private val selectListener: ((ChartCategory, Boolean) -> Unit),
                   private val transactionClickListener: (ChartCategory) -> Unit) : Item() {
    private var currentChart: CardCategoriesChart? = null
    private var currentChartCategory: ChartCategory? = null

    private var pieChart: PieChartView? = null
    private var selectedCategoryText: TextView? = null
    private var selectedCategoryAmountText: TextView? = null
    private var categoryTransactionsBtn: View? = null

    override fun getId() = this.javaClass.simpleName.hashCode().toLong()

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            this@PieChartItem.pieChart = pieChart
            this@PieChartItem.selectedCategoryText = selectedCategoryText
            this@PieChartItem.selectedCategoryAmountText = selectedCategoryAmountText
            this@PieChartItem.categoryTransactionsBtn = categoryTransactionsBtn

            pieChart.clickListener = { id: String, selected: Boolean ->
                getChartCategory(id)?.let { selectListener(it, selected) }
            }
            categoryTransactionsBtn?.setOnClickListener {
                currentChartCategory?.let { transactionClickListener(it) }
            }
        }

        updateCategoriesChart(false)
    }

    override fun unbind(holder: ViewHolder) {
        super.unbind(holder)

        pieChart = null
        selectedCategoryText = null
        selectedCategoryAmountText = null
        categoryTransactionsBtn = null
    }

    fun displayCategoriesChart(cardCategoriesChart: CardCategoriesChart?) {
        currentChart = cardCategoriesChart
        updateCategoriesChart(true)
    }

    fun displayChartCategory(chartCategory: ChartCategory?) {
        displayChartCategory(chartCategory, true)
    }

    private fun displayChartCategory(chartCategory: ChartCategory?, animated: Boolean) {
        currentChartCategory = getChartCategory(chartCategory?.category)
        updateChartCategory(animated)
    }

    private fun updateCategoriesChart(animated: Boolean) {
        pieChart?.resetSegments()
        currentChart?.let { chart ->
            chart.categories.forEach { category ->
                pieChart?.add(PieSegment(
                        category.category,
                        category.percent.toFloat(),
                        category.color
                ))
            }
            chart.emptyCategoriesColor.let { pieChart?.fillColor(it) }
        }

        displayChartCategory(currentChartCategory, animated)
    }

    private fun updateChartCategory(animated: Boolean) {
        val category = currentChartCategory?.category ?: selectedCategoryText?.context?.getString(R.string.title_all_categories)
        selectedCategoryText?.text = category
        currentChartCategory?.let {
            selectedCategoryAmountText?.text = CurrencyFormatFactory.getInstance().format(it.amount, it.currency)
        } ?: currentChart?.let {
            selectedCategoryAmountText?.text = CurrencyFormatFactory.getInstance().format(it.amount, it.currency)
        } ?: run {
            selectedCategoryAmountText?.text = CurrencyFormatFactory.getInstance().format(BigDecimal.ZERO, "")
        }

        categoryTransactionsBtn?.visibility = if (currentChartCategory != null) View.VISIBLE else View.INVISIBLE

        if (pieChart?.segmentSelected(currentChartCategory?.category) != true) {
            pieChart?.selectSegment(currentChartCategory?.category, animated)
        }
    }

    private fun getChartCategory(id: String?) = currentChart?.categories?.find { it.category == id }

    override fun getLayout() = R.layout.list_item_pie_chart

    fun deselect() {
        pieChart?.deselectAll(false)
    }

}