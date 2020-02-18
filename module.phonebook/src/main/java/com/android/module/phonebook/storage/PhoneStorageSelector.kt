package com.android.module.phonebook.storage

import androidx.fragment.app.Fragment
import com.android.module.phonebook.R
import com.bpcbt.inputs.dto.StorageSelector
import com.bpcbt.inputs.dto.StorageSelectorTypes
import ru.bpc.mobilebanksdk.modulity.facilities.MoneyStorage
import ru.bpc.mobilebanksdk.modulity.facilities.MoneyStorageEntry
import ru.bpc.mobilebanksdk.modulity.facilities.PaymentSourceItem
import ru.bpc.mobilebanksdk.modulity.facilities.PreDefinedMoneyStorages
import ru.bpc.mobilebanksdk.modulity.module.PaymentSourceItemSelectionListener
import ru.bpc.mobilebanksdk.modulity.module.StorageSelectorUser
import rx.Observable

class PhoneStorageSelector : StorageSelectorUser {

    override fun getTypes(): List<StorageSelectorTypes> = listOf(StorageSelectorTypes.PHONE)

    override fun getPaymentSourceItems(moneyStorageEntry: MoneyStorageEntry, isNotActiveAllowed: Boolean): Observable<PaymentSourceItem>? {
        if (!moneyStorageEntry.isByPhoneNumberAllowed) return null
        return Observable.just(StorageByPhoneNumberPaymentSourceItem(null) as PaymentSourceItem)
    }

    override fun getPaymentSourceItems(preDefinedMoneyStorages: PreDefinedMoneyStorages<*>, moneyStorageEntry: MoneyStorageEntry?): Observable<PaymentSourceItem>? = null

    override fun getPaymentSourceItem(moneyStorage: MoneyStorage): Observable<PaymentSourceItem>? {
        if (!types.contains(moneyStorage.type))
            return null
        return Observable.just(StorageByPhoneNumberPaymentSourceItem(null))
    }

    override fun getSelectionListFragment(moneyStorageEntry: MoneyStorageEntry, isNotActiveAllowed: Boolean, style: StorageSelector.Style?): Fragment =
            PhoneStorageSelectorFragment.newInstance(moneyStorageEntry)

    override fun getDefaultPaymentListFragment(moneyStorageEntry: MoneyStorageEntry, isNotActiveAllowed: Boolean, style: StorageSelector.Style?, selectListener: PaymentSourceItemSelectionListener?): Fragment =
            Fragment()

    override fun getSelectionListFragment(preDefinedMoneyStorages: PreDefinedMoneyStorages<*>, moneyStorageEntry: MoneyStorageEntry?): Fragment? =
            null

    override fun getPresetPaymentSourceItem(moneyStorageEntry: MoneyStorageEntry): Observable<PaymentSourceItem> {
        if (!moneyStorageEntry.isByPhoneNumberAllowed)
            return Observable.just(null)
        return Observable.just(StorageByPhoneNumberPaymentSourceItem(moneyStorageEntry))
    }

    override fun getFragmentTitleResID(): Int = R.string.phone
}