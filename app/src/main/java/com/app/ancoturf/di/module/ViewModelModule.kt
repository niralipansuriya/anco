package com.app.ancoturf.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.ancoturf.di.ViewModelKey
import com.app.ancoturf.presentation.cart.CartViewModel
import com.app.ancoturf.presentation.cart.DeliveryDatesViewModel
import com.app.ancoturf.presentation.common.base.ViewModelFactory
import com.app.ancoturf.presentation.forgotpassword.ForgotPasswordViewModel
import com.app.ancoturf.presentation.home.aboutUs.AboutUsViewModel
import com.app.ancoturf.presentation.home.lawntips.LawnTipsViewModel
import com.app.ancoturf.presentation.home.offers.OffersViewModel
import com.app.ancoturf.presentation.home.order.OrderViewModel
import com.app.ancoturf.presentation.home.portfolio.PortfolioViewModel
import com.app.ancoturf.presentation.home.products.ProductsViewModel
import com.app.ancoturf.presentation.home.quote.QuoteViewModel
import com.app.ancoturf.presentation.home.shop.ShopViewModel
import com.app.ancoturf.presentation.home.turfcalculator.TurfCalculatorViewModel
import com.app.ancoturf.presentation.home.weather.WeatherViewModel
import com.app.ancoturf.presentation.invoice.InvoiceViewModel
import com.app.ancoturf.presentation.login.LoginViewModel
import com.app.ancoturf.presentation.manageLawn.ManageLawnViewModel
import com.app.ancoturf.presentation.notification.NotificationViewModel
import com.app.ancoturf.presentation.payment.PaymentViewModel
import com.app.ancoturf.presentation.profile.ProfileViewModel
import com.app.ancoturf.presentation.search.SearchViewModel
import com.app.ancoturf.presentation.signup.SignupViewModel
import com.app.ancoturf.presentation.splash.SplashViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SignupViewModel::class)
    abstract fun bindSignupViewModel(signupViewModel: SignupViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ShopViewModel::class)
    abstract fun bindShopViewModel(shopViewModel: ShopViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindSplashViewModel(splashViewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProductsViewModel::class)
    abstract fun bindProductsViewModel(productsViewModel: ProductsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ForgotPasswordViewModel::class)
    abstract fun bindForgotPasswordViewModel(forgotPasswordViewModel: ForgotPasswordViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PortfolioViewModel::class)
    abstract fun bindPortfolioViewModel(portfolioViewModel: PortfolioViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(QuoteViewModel::class)
    abstract fun bindQuoteViewModel(quoteViewModel: QuoteViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OrderViewModel::class)
    abstract fun bindOrderViewModel(orderViewModel: OrderViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OffersViewModel::class)
    abstract fun bindOffersViewModel(offersViewModel: OffersViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CartViewModel::class)
    abstract fun bindCartViewModel(cartViewModel: CartViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DeliveryDatesViewModel::class)
    abstract fun bindDeliveryDatesViewModel(deliveryDatesViewModel: DeliveryDatesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TurfCalculatorViewModel::class)
    abstract fun bindTurfCalculatorViewModel(turfCalculatorViewModel: TurfCalculatorViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LawnTipsViewModel::class)
    abstract fun bindLawnTipsViewModel(lawntipsViewModel: LawnTipsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(searchViewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun bindProfileViewModel(profileViewModel: ProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PaymentViewModel::class)
    abstract fun bindPaymentViewModel(paymentViewModel: PaymentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NotificationViewModel::class)
    abstract fun bindNotificationViewModel(notificationViewModel: NotificationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(InvoiceViewModel::class)
    abstract fun bindInvoiceViewModel(invoiceViewModel: InvoiceViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ManageLawnViewModel::class)
    abstract fun bindManageLawnViewModel(manageLawnViewModel: ManageLawnViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AboutUsViewModel::class)
    abstract fun bindAboutUsViewModel(aboutUsViewModel: AboutUsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WeatherViewModel::class)
    abstract fun bindWeatherViewModel(weatherViewModel: WeatherViewModel): ViewModel

}