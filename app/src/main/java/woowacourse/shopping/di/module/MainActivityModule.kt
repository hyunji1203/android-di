package woowacourse.shopping.di.module

import com.example.bbottodi.di.Module
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.model.repository.ProductRepository

object MainActivityModule : Module {
    fun provideProductRepository(): ProductRepository {
        return DefaultProductRepository()
    }
}
