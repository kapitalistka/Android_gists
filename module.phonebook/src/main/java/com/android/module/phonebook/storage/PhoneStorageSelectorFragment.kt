package com.android.module.phonebook.storage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.module.phonebook.R
import ru.bpc.mobilebanksdk.commonview.CommonMBFragment
import ru.bpc.mobilebanksdk.helpers.ExtraKeys
import ru.bpc.mobilebanksdk.modulity.facilities.MoneyStorageEntry
import ru.bpc.mobilebanksdk.modulity.facilities.PaymentSourceItem
import ru.bpc.mobilebanksdk.modulity.facilities.PaymentSourceItemAbs
import ru.bpc.mobilebanksdk.utils.argument
import ru.bpc.mobilebanksdk.utils.put

class PhoneStorageSelectorFragment : CommonMBFragment() {

    companion object {

        fun newInstance(moneyStorageEntry: MoneyStorageEntry): PhoneStorageSelectorFragment {
            return PhoneStorageSelectorFragment().apply {
                arguments = Bundle()
                        .put(ExtraKeys.MONEY_STORAGE_ENTRY, moneyStorageEntry)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_phone_storage_selector, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewEnterPhone = view.findViewById<View>(R.id.view_enter_phone)

        val moneyStorageEntry = argument(ExtraKeys.MONEY_STORAGE_ENTRY) as MoneyStorageEntry
        viewEnterPhone.setOnClickListener { returnResult(StorageByPhoneNumberPaymentSourceItem(moneyStorageEntry)) }
    }

    private fun returnResult(item: PaymentSourceItem) {
        setResult(Activity.RESULT_OK, Intent()
                .putExtra(PaymentSourceItemAbs.EXTRA_KEY, item))
        closeActivity()
    }
}