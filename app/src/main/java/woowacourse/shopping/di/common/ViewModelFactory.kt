package woowacourse.shopping.di.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.ShoppingApplication.Companion.autoDependencyInjector

object ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return autoDependencyInjector.inject(modelClass)
    }
}
