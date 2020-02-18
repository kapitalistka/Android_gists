package com.android.module.pfm.ui.category.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.StringRes
import com.android.module.pfm.R
import com.android.module.pfm.net.dto.TransactionInformation
import com.android.module.pfm.ui.category.adapter.TransactionsDataTimeHelper
import kotlinx.android.synthetic.main.fragment_transaction_details.*
import kotlinx.android.synthetic.main.list_item_requisites.view.*
import ru.bpc.mobilebanksdk.commonview.CommonMBFragment
import ru.bpc.mobilebanksdk.dto.item.Card
import ru.bpc.mobilebanksdk.helpers.currency.CurrencyFormatFactory
import ru.bpc.module.cards.util.CardFormatUtils


class TransactionDetailsFragment : CommonMBFragment() {
    private lateinit var closeBtn: ImageButton
    private lateinit var summ: TextView
    private lateinit var summSecondary: TextView
    private lateinit var description: TextView
    private lateinit var requisites: ViewGroup


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaction_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var transaction: TransactionInformation? = null
        val card = arguments?.getSerializable(EXTRA_KEY_CARD) as Card
        val arguments = arguments
        if (arguments != null)
            transaction = arguments.getSerializable(EXTRA_KEY_TRANSACTION) as TransactionInformation

        if (listOf<Any?>(card, transaction).any { it == null }) {
            fragmentManager!!.popBackStack()
            return
        }

        bindView(view)

        description.text = transaction!!.description
        summ.text = CurrencyFormatFactory.getInstance().format(transaction.primaryAmount.amount, transaction.primaryAmount.currency)
        if (transaction.secondaryAmount != null) {
            summSecondary.text = CurrencyFormatFactory.getInstance().format(transaction.secondaryAmount!!.amount, transaction.secondaryAmount!!.currency)
        } else {
            summSecondary.visibility = View.GONE
        }

        listOf(
            createParamView(requisites,
                R.string.title_param_pfm_date,
                TransactionsDataTimeHelper.getFormattedDate(transaction.timestamp!!, TransactionsDataTimeHelper.Companion.FormatMode.DAY)),
            createParamView(requisites,
                R.string.title_param_pfm_time,
                TransactionsDataTimeHelper.getFormattedDate(transaction.timestamp!!, TransactionsDataTimeHelper.Companion.FormatMode.TIME_FULL)),
            createParamView(requisites,
                R.string.title_param_pfm_card_number,
                CardFormatUtils.formatBriefNumber(card, context!!))
        )
            .forEach {
                requisites.addView(it)
            }

        closeBtn.setOnClickListener { close() }
    }

    private fun bindView(view: View) {
        summ = view.findViewById(R.id.operation_summ)
        summSecondary = operation_summ_secondary
        description = view.findViewById(R.id.operation_description)

        requisites = requisites_layout
        closeBtn = close_btn

    }

    private fun createParamView(parent: ViewGroup, @StringRes label: Int, value: String): View? {
        val view = this.layoutInflater.inflate(R.layout.list_item_requisites, parent, false)
        view.requisitesItemTitle.text = getString(label)
        view.requisitesItemValue.text = value
        return view
    }

    companion object {
        fun newInstance(transaction: TransactionInformation, card: Card): TransactionDetailsFragment {
            val args = Bundle()
            putUnderForStart(args, transaction, card)
            val fragment = TransactionDetailsFragment()
            fragment.arguments = args
            return fragment
        }

        private fun putUnderForStart(args: Bundle, transaction: TransactionInformation, card: Card) {
            args.putSerializable(EXTRA_KEY_TRANSACTION, transaction)
            args.putSerializable(EXTRA_KEY_CARD, card)
        }

        private const val EXTRA_KEY_TRANSACTION = "category_transaction_information_extra"
        private const val EXTRA_KEY_CARD = "category_transaction_card_extra"
    }
}
