package com.android.module.pfm.ui.category.adapter

import android.util.Log
import ru.bpc.mobilebanksdk.BankFlavor
import ru.bpc.mobilebanksdk.BaseBankApplication
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TransactionsDataTimeHelper {
    companion object {
        private const val TIME_FULL_FORMAT = "HH:mm:ss"
        private const val TIME_FORMAT = "HH:mm"
        private const val DATE_FORMAT = "LLLL dd, yyyy"
        private const val DATE_FORMAT_DAY = "dd.MM.yyyy"
        private const val DATE_FORMAT_FULL = "$DATE_FORMAT $TIME_FORMAT"

        enum class FormatMode(val format: String) {
            FULL(DATE_FORMAT_FULL), BRIEF(DATE_FORMAT), DAY(DATE_FORMAT_DAY), TIME_FULL(TIME_FULL_FORMAT)
        }

        fun getFormattedDate(timestamp: String, formatMode: FormatMode): String {
            var otString = timestamp
            try {
                val operationDate = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).parse(timestamp)
                otString = getFormattedDate(operationDate, formatMode)
            } catch (ex: ParseException) {
                Log.d("DataTimeHelper", "Unable to parse timestamp: $timestamp")
            }
            return otString
        }

        fun getFormattedDate(operationDate: Date, formatMode: FormatMode): String = when (BankFlavor.current()) {
            BankFlavor.DINERS -> SimpleDateFormat(formatMode.format, BaseBankApplication.getLocale()).format(operationDate)
            BankFlavor.BOA -> DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(operationDate)
            BankFlavor.SGB -> {
                val dateFormat = SimpleDateFormat(formatMode.format, BaseBankApplication.getLocale())
                dateFormat.format(operationDate)
            }
            else -> DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(operationDate)
        }.capitalize()
    }
}