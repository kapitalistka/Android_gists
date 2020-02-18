package com.android.module.pfm.ui.listitem

import com.android.module.pfm.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

class ProgressItem : Item() {

    override fun getId() = this.javaClass.simpleName.hashCode().toLong()

    override fun bind(viewHolder: ViewHolder, position: Int) {}

    override fun getLayout() = R.layout.list_item_progress
}