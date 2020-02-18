package fake

import com.android.module.pfm.R
import com.android.module.pfm.net.PfmApiService
import com.android.module.pfm.net.dto.CardCategoriesRequest
import com.android.module.pfm.net.dto.CardCategoriesResponse
import com.android.module.pfm.net.dto.CategoryTransactionsRequest
import com.android.module.pfm.net.dto.CategoryTransactionsResponse
import fake.fakeservices.FakeWebServiceStorage
import ru.bpc.mobilebanksdk.utils.Raw
import java.util.*

class PfmFakeApiService : PfmApiService {

    private val TAG = "PfmFakeService"
    private val cardCategories14: String by Raw(R.raw.card_categories_14)
    private val cardCategories7: String by Raw(R.raw.card_categories_7)
    private val cardCategories0: String by Raw(R.raw.card_categories_0)
    private val categoryTransactions: String by Raw(R.raw.category_transactions)

    override fun executeCardCategories(request: CardCategoriesRequest): CardCategoriesResponse {
        val categories = when(Random().nextInt(3)) {
            0 -> cardCategories14
            1 -> cardCategories7
            2 -> cardCategories0
            else -> cardCategories0
        }
        val response = FakeWebServiceStorage.getObjectFromJSON<CardCategoriesResponse>(categories, CardCategoriesResponse::class.java)
        return FakeApiService.delay<CardCategoriesResponse>(TAG, response, 500)
    }

    override fun executeCategoryTransactions(request: CategoryTransactionsRequest): CategoryTransactionsResponse {
        val response = FakeWebServiceStorage.getObjectFromJSON<CategoryTransactionsResponse>(categoryTransactions, CategoryTransactionsResponse::class.java)
        return FakeApiService.delay<CategoryTransactionsResponse>(TAG, response, 500)
    }
}