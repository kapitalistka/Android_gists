package com.android.module.phonebook.ui.phonebook

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.provider.ContactsContract
import com.android.module.phonebook.data.helper.ContactsHelper
import com.android.module.phonebook.dto.Contact
import com.bpcbt.android.pdk.presenter.interfaces.ActivityResultReceiver
import com.bpcbt.android.pdk.presenter.interfaces.CommonPresenterAbs
import ru.bpc.mobilebanksdk.BaseBankApplication
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


abstract class BasePhoneBookPresenter(private val view: PhoneBookContract.View) : CommonPresenterAbs(), PhoneBookContract.Presenter {

    private var contactsSubscription: Subscription? = null

    private var searchTerm: String = ""
        set(value) {
            field = value
            searchHandler.removeCallbacks(searchRunnable)
            searchHandler.postDelayed(searchRunnable, 500)
        }

    private val searchHandler = Handler()
    private val searchRunnable = { if(contacts.isNullOrEmpty().not()) refreshContacts() }

    protected var contacts: List<Contact>? = null
    set(value) {
        field = value?.sortedBy { it.name }
        updateContactsInView()
    }

    private val contactsHelper = ContactsHelper.create(BaseBankApplication.getContext())

    override fun onViewCreated() {
        super.onViewCreated()
        refreshContacts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cancelContactsFetch()
        searchHandler.removeCallbacks(searchRunnable)
    }

    protected fun refreshContacts() {
        cancelContactsFetch()
        contacts ?: view.showProgress { view.close() }
        contactsSubscription = loadContactsObservable()
                .doOnTerminate { view.hideProgress() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val initialLoading = contacts == null
                    contacts = it
                    if(it.isEmpty()) {
                        if(initialLoading) contactsRetrieveError()
                    } else {
                        if(initialLoading) contactsInitialLoadingCompleted()
                    }
                }, {
                    contacts ?: contactsRetrieveError()
                }).also { addSubscription(it) }
    }

    private fun cancelContactsFetch() {
        contactsSubscription?.unsubscribe()
    }

    private fun loadContactsObservable() : Observable<List<Contact>> = Observable.fromCallable {
        retrieveContacts()
    }

    private fun retrieveContacts() = contactsHelper.searchContacts(
            searchTerm = searchTerm,
            isNumberChecked = { isNumberChecked(it) }
    )

    private fun contactsRetrieveError() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        view.startActivityForResult(object: ActivityResultReceiver {
            override fun receive(requestCode: Int, resultCode: Int, data: Intent?) {
                pickContact(resultCode, data?.data)
            }
        }, intent, 0)
    }

    override fun contactClick(uri: Uri) {
        pickContact(Activity.RESULT_OK, uri)
    }

    private fun pickContact(resultCode: Int, uri: Uri?) {
        view.setResult(resultCode, Intent().apply { data = uri })
        view.close()
    }

    override fun search(term: String) {
        this.searchTerm = term
    }

    private fun updateContactsInView() {
        // TODO move search to ContactsHelper database request
//        view.displayContacts(contacts ?: emptyList())
        view.displayContacts(contacts?.filter { contact ->
            arrayOf(contact.name, contact.phoneNumberRaw).any { it.contains(searchTerm, true) }
        } ?: emptyList())
    }

    protected open fun contactsInitialLoadingCompleted() {}

    protected abstract fun isNumberChecked(number: String): Boolean

}