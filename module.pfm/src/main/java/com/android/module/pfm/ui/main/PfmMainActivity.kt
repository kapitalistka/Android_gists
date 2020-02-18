package com.android.module.pfm.ui.main

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.android.module.pfm.R
import com.android.module.pfm.dto.CardCategoriesChart
import com.android.module.pfm.dto.ChartCategory
import com.android.module.pfm.dto.DateRange
import com.android.module.pfm.ui.listitem.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_pfm_main.*
import kotlinx.android.synthetic.main.activity_pfm_main.view.*
import kotlinx.android.synthetic.main.list_item_menu_card.view.*
import ru.bpc.mobilebanksdk.DI.capabilities.SystemSettings
import ru.bpc.mobilebanksdk.activity.common.NavigationDrawerActivity
import ru.bpc.mobilebanksdk.dto.item.Card
import ru.bpc.mobilebanksdk.modulity.ModulesManager
import ru.bpc.mobilebanksdk.ui.views.menu.DialogMenuManager
import ru.bpc.module.cards.di.CardBriefInformationInflater
import ru.bpc.module.cards.di.CardBriefInformationInflaterUser
import ru.bpc.module.cards.util.CardFormatUtils

class PfmMainActivity : NavigationDrawerActivity(), PfmMainContract.View {

    private lateinit var menuManager: DialogMenuManager
    private lateinit var cardsMenuAdapter: CardsMenuAdapter
    private lateinit var presenter: PfmMainContract.Presenter

    private val filterItem = ChartFilterItem(clearListener = { presenter.clearDateFilters() })
    private val pieChartItem = PieChartItem(
            selectListener = { chartCategory: ChartCategory, selected: Boolean ->
                if (selected) presenter.categorySelect(chartCategory) else presenter.categoryDeselect(chartCategory)
            },
            transactionClickListener = {
                presenter.categoryDetailsClick(it)
            })
    private val chartHeaderSection = Section().apply {
        update(listOf(filterItem, pieChartItem))
    }
    private val chartSection = Section()
    private val contentSection = Section().apply {
        update(listOf(chartHeaderSection, chartSection))
    }
    private val loadingSection = Section().apply {
        update(listOf(ProgressItem()))
    }
    private val chartAdapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pfm_main)

        initToolbar()
        initCardsMenu()
        initRecycler()

        presenter = PfmMainPresenter(this)
        acceptToLifecycle(presenter)
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle("")
        toolbar?.toolbar_title?.text = title
    }

    private fun initToolbar() {
        title = ""
        toolbar?.toolbar_title?.setOnClickListener { menuManager.toggle() }
    }

    private fun initCardsMenu() {
        ModulesManager.getModule<CardBriefInformationInflaterUser>(CardBriefInformationInflaterUser::class.java)?.let { inflaterModule ->
            cardsMenuAdapter = CardsMenuAdapter(
                    inflaterModule.createInflater(this),
                    selectListener = { presenter.selectCard(it); menuManager.dismiss() }
            )
            menuManager = DialogMenuManager(content, cardsMenuAdapter)
        }
    }

    private fun initRecycler() {
        setChartData(emptyList())
        recycler.adapter = chartAdapter
    }

    override fun onBackPressed() {
        if (menuManager.handleBackClick().not()) super.onBackPressed()
    }

    override fun setCardsMenuItems(cards: List<Card>) {
        cardsMenuAdapter.cards = cards
    }

    override fun displayCard(card: Card) {
        title = CardFormatUtils.formatBriefNumber(card, this)
        cardsMenuAdapter.selectedCard = card
        pieChartItem.deselect()
    }

    override fun displayDateFilter(dateFilter: DateRange, clearable: Boolean) {
        filterItem.displayDateFilter(dateFilter, clearable)
    }

    override fun displayCategoriesChart(cardCategoriesChart: CardCategoriesChart?) {
        cardCategoriesChart?.let { chart ->
            pieChartItem.displayCategoriesChart(cardCategoriesChart)

            if (chart.categories.isNotEmpty()) {
                val displayPercents = SystemSettings.Pfm.displayPercent()
                setChartData(chart.categories.map { chartCategory ->
                    ChartCategoryItem(
                            chartCategory,
                            displayPercents,
                            clickListener = { presenter.categoryDetailsClick(it) })
                })
            } else {
                setChartTitle(getString(R.string.item_title_no_data))
            }
        } ?: run { updateContent(loadingSection) }
    }

    private fun setChartTitle(title: String) {
        chartSection.update(listOf(ChartTitleItem(title)))
        updateContent(contentSection)
    }

    private fun setChartData(data: List<Item>) {
        chartSection.update(data)
        updateContent(contentSection)
    }

    private fun updateContent(section: Section) {
        chartAdapter.update(listOf(section))
    }

    override fun displayChartCategory(chartCategory: ChartCategory?) {
        pieChartItem.displayChartCategory(chartCategory)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_pfm_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_filter) {
            presenter.openFilters()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

private class CardsMenuAdapter(private val cardInflater: CardBriefInformationInflater,
                               private val selectListener: (Card) -> Unit) : RecyclerView.Adapter<CardViewHolder>() {
    var cards: List<Card> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var selectedCard: Card? = null
        set(value) {
            field = value
            notifyItemChanged(cards.indexOf(value))
        }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) = CardViewHolder(
            LayoutInflater.from(p0.context).inflate(R.layout.list_item_menu_card, p0, false)
    )

    override fun getItemCount() = cards.size

    override fun onBindViewHolder(p0: CardViewHolder, p1: Int) {
        val card = cards[p1]
        p0.itemView.cardItemContent.removeAllViews()
        p0.itemView.cardItemContent.addView(cardInflater.inflate(card, null))
        p0.itemView.setOnClickListener { selectListener(card) }
    }

}

private class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)