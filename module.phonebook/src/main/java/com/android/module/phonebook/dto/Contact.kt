package com.android.module.phonebook.dto

import android.net.Uri

data class Contact(val uri: Uri,
                   val name: String,
                   val phoneNumberRaw: String,
                   val phoneNumber: String,
                   val avatar: Uri?,
                   val marked: Boolean)