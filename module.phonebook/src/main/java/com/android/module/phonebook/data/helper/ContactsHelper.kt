package com.android.module.phonebook.data.helper

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import com.android.module.phonebook.dto.Contact



class ContactsHelper private constructor(private val context: Context) {
    private val digitsOnlyPattern = Regex("[^0-9]")

    fun searchContacts(searchTerm: String, isNumberChecked: (String) -> Boolean): List<Contact> {
        val contactsCursor = getContactsCursor(searchTerm)
        val phonesCursor = getPhonesCursor()
        return getContacts(contactsCursor, phonesCursor, isNumberChecked)
    }

    private fun getContactsCursor(searchTerm: String): Cursor? {
        return context.contentResolver.query(
                // TODO search 1/2 apply search term
//                if(searchTerm.isBlank()) ContactsContract.Contacts.CONTENT_URI else Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(searchTerm)),
                ContactsContract.Contacts.CONTENT_URI,
                CONTACTS_PROJECTION,
                "${ContactsContract.Contacts.HAS_PHONE_NUMBER} != 0",
                null,
                "${ContactsContract.Contacts._ID} ASC"
        )
    }

    private fun getPhonesCursor(): Cursor? {
        return context.contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                DATA_PROJECTION,
                "${ContactsContract.Data.MIMETYPE} = ?",
                arrayOf(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE),
                "${ContactsContract.Data.CONTACT_ID} ASC"
        )
    }

    private fun getContacts(contactsCursor: Cursor?, phonesCursor: Cursor?, numberChecked: (String) -> Boolean) = mutableListOf<Contact>().apply {
        // TODO search 2/2 get contacts phone numbers from batch transaction instead of cursor in order to fix search
        try {
            if (contactsCursor != null && phonesCursor != null) {

                var id = ""
                while (phonesCursor.moveToNext() && contactsCursor.isAfterLast.not()) {

                    val phoneNumber = phonesCursor.getStringByColumn(ContactsContract.Data.DATA1)
                    val phoneNumberId = phonesCursor.getStringByColumn(ContactsContract.Data._ID)
                    val contactId = phonesCursor.getStringByColumn(ContactsContract.Data.CONTACT_ID)

                    if ((id == contactId).not()) {
                        contactsCursor.moveToNext()
                        id = contactsCursor.getStringByColumn(ContactsContract.Contacts._ID) ?: ""

                        if (phoneNumber.isNullOrEmpty().not()) {
                            val imageUri = contactsCursor.getStringByColumn(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)
                            val name = contactsCursor.getStringByColumn(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY) ?: ""
                            val digitsOnlyPhoneNumber = digitsOnlyPattern.replace(phoneNumber!!, "")
                            add(Contact(
                                    Uri.withAppendedPath(ContactsContract.Data.CONTENT_URI, phoneNumberId),
                                    name,
                                    digitsOnlyPhoneNumber,
                                    phoneNumber,
                                    imageUri?.takeIf { it.isBlank().not() }?.let { Uri.parse(imageUri) },
                                    numberChecked(digitsOnlyPhoneNumber)
                            ))
                        }
                    }
                }
            }
        } finally {
            contactsCursor?.close()
            phonesCursor?.close()
        }
    }

    private fun Cursor.getStringByColumn(column: String): String? {
        return try {
            getString(getColumnIndex(column))
        } catch(e: Exception) {
            null
        }
    }

    companion object {
        private val CONTACTS_PROJECTION = arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
        )

        private val DATA_PROJECTION = arrayOf(
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Data.DATA1,
                ContactsContract.Data.MIMETYPE,
                ContactsContract.Data._ID
        )

        fun create(context: Context) = ContactsHelper(context)
    }

}