package com.android.module.pfm.ui.category.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.module.pfm.R
import com.android.module.pfm.dto.Category
import com.android.module.pfm.net.PfmWebService
import com.android.module.pfm.net.dto.CategoryTransactionsRequest
import com.android.module.pfm.net.dto.CategoryTransactionsResponse
import com.android.module.pfm.net.dto.TransactionInformation
import com.android.module.pfm.ui.category.TransactionDetailsActivity
import com.android.module.pfm.ui.category.adapter.TransactionsAdapter
import ru.bpc.mobilebanksdk.commonview.CommonMBFragment
import ru.bpc.mobilebanksdk.dto.item.Card
import ru.bpc.mobilebanksdk.helpers.currency.CurrencyFormatFactory
import ru.bpc.mobilebanksdk.utils.listeners.OnRecyclerItemClickListener


class TransactionsListFragment : CommonMBFragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var subTitle: Toolbar
    private lateinit var textEmpty: TextView
    private lateinit var refreshLayout: SwipeRefreshLayout

    val request = CategoryTransactionsRequest()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transactions_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var category: Category? = null
        val arguments = arguments
        if (arguments != null)
            category = arguments.getSerializable(EXTRA_KEY_CATEGORY_TRANSACTIONS) as Category

        if (category == null) {
            fragmentManager!!.popBackStack()
            return
        }

        bindView(view)

        initSwipeRefreshLayout()

        request.cardId = category.cardId
        request.from = category.from
        request.to = category.to
        request.category = category.category
        loadData()
    }

    private fun loadData() {
        addSubscription(PfmWebService.getCategoryTransactionsRequest(request).subscribe(
                { categoryTransactionsResponse ->
                    refreshLayout.isRefreshing = false
                    onSuccess(categoryTransactionsResponse)
                },
                { throwable ->
                    refreshLayout.isRefreshing = false
                    errorHandler.handle(throwable)
                }
        ))
    }

    private fun initSwipeRefreshLayout() {
        refreshLayout.setColorSchemeResources(R.color.swipe_refresh_color_scheme1,
                R.color.swipe_refresh_color_scheme2,
                R.color.swipe_refresh_color_scheme3,
                R.color.swipe_refresh_color_scheme4)
        refreshLayout.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        refreshLayout.isRefreshing = true
        loadData()
    }

    private fun bindView(view: View) {
        recyclerView = view.findViewById<View>(R.id.recycler) as RecyclerView
        subTitle = view.findViewById(R.id.sub_title)
        textEmpty = view.findViewById(R.id.text_empty)
        refreshLayout = view.findViewById(R.id.refresh_layout)
    }

    private fun onSuccess(response: CategoryTransactionsResponse) {
        response.category?.let { setTitle(response.category) }
        response.amount?.let { subTitle.title = CurrencyFormatFactory.getInstance().format(it, response.currency) }

        if (response.data.isEmpty()) {
            textEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            val adapter = TransactionsAdapter(response.data)
            adapter.setOnRecyclerItemClickListener(OnRecyclerItemClickListener { _, item -> openApplicationCategories(item) })
            recyclerView.adapter = adapter
            val linearLayoutManager = LinearLayoutManager(context)
            recyclerView.layoutManager = linearLayoutManager
        }
    }

    private fun openApplicationCategories(transactionInformation: TransactionInformation) {
        startActivity(
                TransactionDetailsActivity::class.java,
                TransactionDetailsActivity.createBundleForStart(
                        transactionInformation,
                        arguments?.getSerializable(EXTRA_KEY_CARD) as Card
                )
        )
        activity?.overridePendingTransition(R.anim.enter, R.anim.exit)
    }

    companion object {
        fun newInstance(category: Category, card: Card): TransactionsListFragment {
            val args = Bundle()
            putUnderForStart(args, category, card)
            val fragment = TransactionsListFragment()
            fragment.arguments = args
            return fragment
        }

        private fun putUnderForStart(args: Bundle, category: Category, card: Card) {
            args.putSerializable(EXTRA_KEY_CATEGORY_TRANSACTIONS, category)
            args.putSerializable(EXTRA_KEY_CARD, card)
        }

        private const val EXTRA_KEY_CATEGORY_TRANSACTIONS = "category_transactions_extra"
        private const val EXTRA_KEY_CARD = "card_extra"
    }
}
