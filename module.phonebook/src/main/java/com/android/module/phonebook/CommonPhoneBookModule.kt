package com.android.module.phonebook

import android.content.Context
import android.content.Intent
import com.android.module.phonebook.ui.phonebook.PhoneBookActivity
import com.android.module.phonebook.ui.phonebook.PhoneBookClientPickPresenter
import com.android.module.phonebook.ui.phonebook.PhoneBookSimplePickPresenter
import ru.bpc.mobilebanksdk.modulity.facilities.PhoneBookIntentFactory
import ru.bpc.mobilebanksdk.modulity.module.IntentType
import ru.bpc.mobilebanksdk.modulity.module.PhoneBookUser

class CommonPhoneBookModule : PhoneBookUser {

    override fun intentFactory(intentType: IntentType): PhoneBookIntentFactory = when (intentType) {
        IntentType.SIMPLE -> PhoneBookSelectIntentFactory()
        IntentType.FILTERED -> PhoneBookFilteredSelectIntentFactory()
    }
}

private class PhoneBookSelectIntentFactory : PhoneBookIntentFactory {
    override fun create(context: Context): Intent = PhoneBookActivity.getStarterIntent(context, PhoneBookSimplePickPresenter.Creator())
}

private class PhoneBookFilteredSelectIntentFactory : PhoneBookIntentFactory {
    override fun create(context: Context): Intent = PhoneBookActivity.getStarterIntent(context, PhoneBookClientPickPresenter.Creator())
}