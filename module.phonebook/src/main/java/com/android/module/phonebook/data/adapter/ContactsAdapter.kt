package com.android.module.phonebook.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.module.phonebook.R
import com.android.module.phonebook.dto.Contact
import kotlinx.android.synthetic.main.list_item_contact.view.*


class ContactsAdapter(private val contactSelectedListener: (Contact) -> Unit) : RecyclerView.Adapter<ContactViewHolder>() {

    var contacts: List<Contact> = emptyList()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ContactViewHolder {
        return ContactViewHolder(
                LayoutInflater.from(p0.context).inflate(R.layout.list_item_contact, p0, false),
                contactSelectedListener
        )
    }

    override fun getItemCount(): Int = contacts.size

    override fun onBindViewHolder(p0: ContactViewHolder, p1: Int) {
        p0.bind(contacts[p1])
    }
}

class ContactViewHolder(view: View,
                        private val contactSelectedListener: (Contact) -> Unit) : RecyclerView.ViewHolder(view) {

    fun bind(contact: Contact) {
        itemView.contactNameTv.text = contact.name
        itemView.contactPhoneTv.text = contact.phoneNumber
        itemView.contactMarkIv.visibility = if(contact.marked) View.VISIBLE else View.GONE
        contact.avatar?.let {
            itemView.contactPhotoIv.setImageURI(it)
        } ?: run {
            itemView.contactPhotoIv.setImageResource(R.drawable.ic_contact_default)
        }

        itemView.setOnClickListener { contactSelectedListener(contact) }
    }

}