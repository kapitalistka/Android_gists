package com.android.module.phonebook.ui.phonebook

import android.net.Uri
import com.android.module.phonebook.dto.Contact
import com.bpcbt.android.pdk.presenter.interfaces.ActivityResultRequester
import com.bpcbt.android.pdk.presenter.interfaces.CommonPresenter
import com.bpcbt.android.pdk.presenter.interfaces.CommonView


interface PhoneBookContract {

    interface View : CommonView, ActivityResultRequester {
        fun displayContacts(contacts: List<Contact>)
    }

    interface Presenter : CommonPresenter {
        fun contactClick(uri: Uri)
        fun search(term: String)
    }

}