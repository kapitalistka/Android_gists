package fake

import android.util.Log
import com.android.module.phonebook.net.ClientContactsApiService
import com.android.module.phonebook.net.dto.request.FilterByPhoneRequest
import com.android.module.phonebook.net.dto.response.FilterByPhoneResponse
import fake.FakeApiService.delay


internal class ClientsContactsFakeApiService : ClientContactsApiService {

    @Suppress("PrivatePropertyName")
    private val TAG = this::class.java.simpleName

    override fun filterByPhone(filterByPhoneRequest: FilterByPhoneRequest): FilterByPhoneResponse {
        Log.d(TAG, "filterByPhone")
        val response = FilterByPhoneResponse().apply {
            version = filterByPhoneRequest.version?.plus(1L)
            if ((version ?: 0) % 2L == 0L) {
                customers = listOf("33333333333", "44444444444", "55555555555")
            } else {
                customers = listOf("44444444444", "66666666666")
                none_customers = listOf("33333333333", "55555555555")
            }
        }
        return delay<FilterByPhoneResponse>(TAG, response, 1000)
    }

}
