package com.android.module.phonebook.ui.phonebook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.module.phonebook.R
import com.android.module.phonebook.data.adapter.ContactsAdapter
import com.android.module.phonebook.dto.Contact
import com.bpcbt.android.pdk.presenter.interfaces.PresenterCreator
import ru.bpc.mobilebanksdk.activity.common.NavigationDrawerActivity
import ru.bpc.mobilebanksdk.helpers.ExtraKeys



class PhoneBookActivity : NavigationDrawerActivity(), PhoneBookContract.View {

    private var searchView: SearchView? = null

    private lateinit var presenter: PhoneBookContract.Presenter
    private val adapter = ContactsAdapter(contactSelectedListener = { presenter.contactClick(it.uri) })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_recycler)

        initRecycler()

        presenter = (intent.getSerializableExtra(ExtraKeys.PRESENTER_CREATOR) as PhoneBookPresenterCreator).create(this)
        acceptToLifecycle(presenter)
    }

    private fun initRecycler() {
        val recycler = findViewById<RecyclerView>(R.id.recycler)

        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_phone_book, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = (searchItem.actionView as? SearchView)?.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    presenter.search(p0 ?: "")
                    return true
                }

            })
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun displayContacts(contacts: List<Contact>) {
        adapter.contacts = contacts
    }

    companion object {
        fun getStarterIntent(context: Context,
                             creator: PresenterCreator<PhoneBookContract.Presenter, PhoneBookContract.View>) = Intent().apply {
            setClass(context, PhoneBookActivity::class.java)
            putExtra(ExtraKeys.PRESENTER_CREATOR, creator)
        }
    }

}