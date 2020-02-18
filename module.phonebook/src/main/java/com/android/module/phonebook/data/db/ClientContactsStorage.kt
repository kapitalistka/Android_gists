package com.android.module.phonebook.data.db

import com.android.module.phonebook.data.db.dto.ClientContact
import com.orm.SugarRecord
import com.orm.SugarTransactionHelper

class ClientContactsStorage {

    fun get(): List<ClientContact> = mutableListOf<ClientContact>().apply {
        SugarTransactionHelper.doInTransaction {
            addAll(SugarRecord.listAll(ClientContact::class.java))
        }
    }

    fun getClients(): List<ClientContact> = mutableListOf<ClientContact>().apply {
        SugarTransactionHelper.doInTransaction {
            addAll(SugarRecord.find(ClientContact::class.java, "status = ?", ClientContact.STATUS_CLIENT.toString()))
        }
    }

    fun save(contacts: List<ClientContact>) {
        SugarRecord.saveInTx(contacts)
    }

    fun delete(phones: List<String>) {
        SugarTransactionHelper.doInTransaction {
            phones.forEach {
                SugarRecord.deleteAll(
                        ClientContact::class.java,
                        " phone_number IN (?)",
                        it
                )
            }
        }
    }

}