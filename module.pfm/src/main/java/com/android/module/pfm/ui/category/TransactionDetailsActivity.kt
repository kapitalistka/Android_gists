package com.android.module.pfm.ui.category

import android.os.Bundle
import com.android.module.pfm.R
import com.android.module.pfm.net.dto.TransactionInformation
import com.android.module.pfm.ui.category.fragment.TransactionDetailsFragment
import ru.bpc.mobilebanksdk.commonview.CommonMBActivity
import ru.bpc.mobilebanksdk.dto.item.Card

class TransactionDetailsActivity : CommonMBActivity() {

    companion object {
        private const val EXTRA_KEY_TRANSACTION = "category_transaction_information_extra"
        private const val EXTRA_KEY_CARD = "category_transaction_card_extra"

        fun createBundleForStart(transaction: TransactionInformation, card: Card) = Bundle().apply {
            putSerializable(EXTRA_KEY_TRANSACTION, transaction)
            putSerializable(EXTRA_KEY_CARD, card)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_for_fragment_no_toolbar)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, TransactionDetailsFragment.newInstance(intent?.extras?.getSerializable(EXTRA_KEY_TRANSACTION) as TransactionInformation, intent?.extras?.getSerializable(EXTRA_KEY_CARD) as Card))
                .commit()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit)
    }
}
