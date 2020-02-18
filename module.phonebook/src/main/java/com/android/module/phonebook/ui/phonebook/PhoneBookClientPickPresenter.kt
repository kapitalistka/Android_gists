package com.android.module.phonebook.ui.phonebook

import com.android.module.phonebook.data.db.ClientContactsStorage
import com.android.module.phonebook.data.db.dto.ClientContact
import com.android.module.phonebook.data.helper.ClientContactsPreferencesHelper
import com.android.module.phonebook.net.ClientContactsWebService
import com.android.module.phonebook.net.dto.request.FilterByPhoneRequest
import ru.bpc.mobilebanksdk.utils.addSubscription


class PhoneBookClientPickPresenter(view: PhoneBookContract.View) : BasePhoneBookPresenter(view) {

    private var clientNumbers : Set<String> = hashSetOf()
    private val clientContactsStorage = ClientContactsStorage()

    override fun onViewCreated() {
        updateClients()
        super.onViewCreated()
    }

    override fun contactsInitialLoadingCompleted() {
        super.contactsInitialLoadingCompleted()
        if(contacts.isNullOrEmpty().not()) loadClientsList()
    }

    @Suppress("MoveLambdaOutsideParentheses")
    private fun loadClientsList() {
        val contactsPhones = contacts?.map { it.phoneNumberRaw }?.toHashSet() ?: emptySet<String>()
        val possibleClientsPhones = clientContactsStorage.get().map { it.phoneNumber }.toHashSet()
        val request = FilterByPhoneRequest()
        request.save = contactsPhones.filter { possibleClientsPhones.contains(it).not() }
        request.delete = possibleClientsPhones.filter { contactsPhones.contains(it).not() }
        val version = ClientContactsPreferencesHelper.getFilterClientsByPhoneVersion()
        request.version = Math.max(version, 1L)

        ClientContactsWebService.getFilterByPhoneRequest(request)
                .subscribe({
                    ClientContactsPreferencesHelper.setFilterClientsByPhoneVersion(it.version ?: version.plus(1L))
                    clientContactsStorage.delete(request.delete ?: emptyList())
                    clientContactsStorage.save(it.customers?.map { ClientContact().apply { phoneNumber = it; status = ClientContact.STATUS_CLIENT } } ?: emptyList())
                    clientContactsStorage.save(it.none_customers?.map { ClientContact().apply { phoneNumber = it; status = ClientContact.STATUS_NON_CLIENT } } ?: emptyList())
                    updateClients()
                }, {})
                .addSubscription(this)
    }

    private fun updateClients() {
        updateClientsList()
        refreshContacts()
    }

    private fun updateClientsList() {
        clientNumbers = clientContactsStorage.getClients()
                .map { it.phoneNumber }
                .toHashSet()
    }

    override fun isNumberChecked(number: String) = clientNumbers.contains(number)

    class Creator : PhoneBookPresenterCreator {
        override fun create(view: PhoneBookContract.View): PhoneBookContract.Presenter =
                PhoneBookClientPickPresenter(view)
    }

}