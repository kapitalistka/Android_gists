package com.android.module.pfm.ui.category

import android.os.Bundle
import com.android.module.pfm.R
import com.android.module.pfm.dto.Category
import com.android.module.pfm.ui.category.fragment.TransactionsListFragment
import ru.bpc.mobilebanksdk.activity.common.NavigationDrawerActivity
import ru.bpc.mobilebanksdk.dto.item.Card

class CategoryTransactionsActivity : NavigationDrawerActivity() {

    companion object {
        private const val EXTRA_CATEGORY = "extra_category"
        private const val EXTRA_CARD = "extra_card"

        fun createBundleForStart(category: Category, card: Card) = Bundle().apply {
            putSerializable(EXTRA_CATEGORY, category)
            putSerializable(EXTRA_CARD, card)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_for_fragment)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, TransactionsListFragment.newInstance(intent?.extras?.getSerializable(EXTRA_CATEGORY) as Category, intent?.extras?.getSerializable(EXTRA_CARD) as Card))
                .commit()
    }
}
