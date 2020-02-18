package com.android.module.pfm.ui.category.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.module.pfm.R
import com.android.module.pfm.net.dto.TransactionInformation
import ru.bpc.mobilebanksdk.helpers.currency.CurrencyFormatFactory
import ru.bpc.mobilebanksdk.utils.listeners.OnRecyclerItemClickListener
import ru.bpc.mobilebanksdk.utils.listeners.OnRecyclerItemViewClickListener

class TransactionsAdapter(private val transactions: List<TransactionInformation>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listener: OnRecyclerItemClickListener<TransactionInformation>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_operation, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        fillViews(holder as ViewHolder, transactions[position], listener)
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    fun setOnRecyclerItemClickListener(listener: OnRecyclerItemClickListener<TransactionInformation>) {
        this.listener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var icon: ImageView
        internal var summ: TextView
        internal var description: TextView
        internal var time: TextView

        init {
            icon = itemView.findViewById(R.id.item_icon)
            summ = itemView.findViewById(R.id.operation_summ)
            description = itemView.findViewById(R.id.operation_description)
            time = itemView.findViewById(R.id.operation_time)
        }
    }

    companion object {
        fun fillViews(holder: ViewHolder, transaction: TransactionInformation, listener: OnRecyclerItemClickListener<TransactionInformation>?) {
            holder.icon.setImageResource(R.drawable.icon_statements_out) //all transactions are debit
            holder.description.text = transaction.description
            holder.summ.text = CurrencyFormatFactory.getInstance().format(transaction.primaryAmount.amount?.abs(), transaction.primaryAmount.currency)
            holder.time.text = TransactionsDataTimeHelper.getFormattedDate(transaction.timestamp!!, TransactionsDataTimeHelper.Companion.FormatMode.FULL)

            if (listener != null) {
                holder.itemView.setOnClickListener(OnRecyclerItemViewClickListener(listener, transaction))
            }
        }
    }
}
