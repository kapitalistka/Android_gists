package com.android.module.phonebook.data.db.dto

import androidx.annotation.IntDef
import com.orm.SugarRecord
import com.orm.dsl.Column
import com.orm.dsl.Table
import com.orm.dsl.Unique

@Table(name = "CLIENT_CONTACT")
class ClientContact : SugarRecord() {

    companion object {
        const val STATUS_CLIENT = 0
        const val STATUS_NON_CLIENT = 1

        @IntDef(STATUS_CLIENT, STATUS_NON_CLIENT)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Status
    }

    @Unique
    @Column(name = "PHONE_NUMBER")
    var phoneNumber: String = ""
    @Status
    @Column(name = "STATUS")
    var status: Int = -1

}