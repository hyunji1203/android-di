package woowacourse.shopping

import com.example.bbottodi.di.DiApplication
import woowacourse.shopping.di.ApplicationModule

class ShoppingApplication : DiApplication() {

    override fun onCreate() {
        module = { context -> ApplicationModule(context) }
        super.onCreate()
    }

    private fun initContainer() {
//        container.apply {
//            addInstanceLegacy(CartProductDao::class, provideCartProductDao(applicationContext))
//            addInstanceLegacy(ProductRepository::class, provideProductRepository())
//            addInstanceLegacy(
//                CartRepository::class,
//                listOf(Inject::class.simpleName!!, InDisk::class.simpleName!!),
//                injectLegacy(container, provideInDiskCartRepository()),
//            )
//            addInstanceLegacy(
//                CartRepository::class,
//                listOf(Inject::class.simpleName!!, InMemory::class.simpleName!!),
//                injectLegacy(container, provideInMemoryCartRepository()),
//            )
//        }
    }
}
