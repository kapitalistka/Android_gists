package com.android.module.phonebook.ui.phonebook


class PhoneBookSimplePickPresenter(view: PhoneBookContract.View) : BasePhoneBookPresenter(view) {

    class Creator : PhoneBookPresenterCreator {
        override fun create(view: PhoneBookContract.View): PhoneBookContract.Presenter =
                PhoneBookSimplePickPresenter(view)
    }

    override fun isNumberChecked(number: String): Boolean = false

}