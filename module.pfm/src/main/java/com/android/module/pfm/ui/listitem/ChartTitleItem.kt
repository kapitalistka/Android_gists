package com.android.module.pfm.ui.listitem

import com.android.module.pfm.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.list_item_chart_title.view.*

class ChartTitleItem(private val title: String) : Item() {

    override fun getId() = title.hashCode().toLong()

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.itemTitle.text = title
    }

    override fun getLayout() = R.layout.list_item_chart_title
}