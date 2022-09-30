package com.app.ancoturf.di.component

import com.app.ancoturf.di.module.*
import com.app.ancoturf.presentation.app.AncoTurfApp
import com.app.ancoturf.presentation.home.weather.WeatherModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidInjectionModule::class, AppModule::class , ViewModelModule::class , ActivityModule::class , AccountModule::class ,
                FragmentModule::class, ProductModule::class, SettingModule::class, PortfolioModule::class, QuoteModule::class ,
                OfferModule::class , SearchModule::class ,LawnTipsModule::class ,AfterpayModule::class , OrderModule::class ,
                DateValidationModule::class, TurfCalculatorModule::class, PaymentModule::class, NotificationModule::class, InvoiceModule::class, ManageLawnModule::class, AboutUsModule::class,WeatherModule::class]
)
interface AppComponent : AndroidInjector<AncoTurfApp> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<AncoTurfApp>()
}
