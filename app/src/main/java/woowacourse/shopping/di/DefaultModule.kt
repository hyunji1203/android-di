package woowacourse.shopping.di

import android.content.Context
import com.example.bbottodi.di.Module
import woowacourse.shopping.data.CartProductDao
import woowacourse.shopping.data.ShoppingDatabase
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.data.repository.InDiskCartRepository
import woowacourse.shopping.data.repository.InMemoryCartRepository
import woowacourse.shopping.model.repository.ProductRepository
import woowacourse.shopping.ui.cart.DateFormatter
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

object DefaultModule : Module {
    fun provideCartProductDao(context: Context): CartProductDao {
        val localDatabase = ShoppingDatabase.getInstance(context)
        return localDatabase.cartProductDao()
    }

    fun provideDateFormatter(context: Context): DateFormatter {
        return DateFormatter(context)
    }

    fun provideProductRepository(): KFunction<ProductRepository> {
        return ::createProductRepository
    }

    fun createProductRepository(): ProductRepository {
        return DefaultProductRepository()
    }

    fun provideInDiskCartRepository(): KClass<InDiskCartRepository> {
        return InDiskCartRepository::class
    }

    fun provideInMemoryCartRepository(): KClass<InMemoryCartRepository> {
        return InMemoryCartRepository::class
    }
}