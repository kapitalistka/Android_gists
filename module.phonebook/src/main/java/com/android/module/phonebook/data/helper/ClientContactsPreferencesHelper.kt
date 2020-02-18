package com.android.module.phonebook.data.helper

import android.content.Context
import android.content.SharedPreferences
import ru.bpc.mobilebanksdk.BaseBankApplication


object ClientContactsPreferencesHelper {
    private const val PREFERENCES = "client_contacts"

    private const val KEY_FILTER_CLIENTS_BY_PHONE_VERSION = "filter_clients_by_phone_version"

    private val sp: SharedPreferences by lazy {
        BaseBankApplication.getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    }

    fun setFilterClientsByPhoneVersion(version: Long) {
        sp.edit().putLong(KEY_FILTER_CLIENTS_BY_PHONE_VERSION, version).apply()
    }

    fun getFilterClientsByPhoneVersion() = sp.getLong(KEY_FILTER_CLIENTS_BY_PHONE_VERSION, 1L)
}