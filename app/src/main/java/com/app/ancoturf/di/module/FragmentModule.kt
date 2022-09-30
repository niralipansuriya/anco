package com.app.ancoturf.di.module

import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.cart.CheckoutFragment
import com.app.ancoturf.presentation.chooseingMyLawn.ChooseMyLawnIntroFragment
import com.app.ancoturf.presentation.chooseingMyLawn.ChooseMyLawnQuestionFragment
import com.app.ancoturf.presentation.chooseingMyLawn.RecommendedProductFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.faq.FAQFragment
import com.app.ancoturf.presentation.guideline.GuidelineFragment
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.home.aboutUs.AboutUsDetailFragment
import com.app.ancoturf.presentation.home.aboutUs.AboutUsFragment
import com.app.ancoturf.presentation.home.lawntips.LawnTipsDetailFragment
import com.app.ancoturf.presentation.home.lawntips.LawnTipsFragment
import com.app.ancoturf.presentation.home.offers.OfferDetailFragment
import com.app.ancoturf.presentation.home.offers.OffersFragment
import com.app.ancoturf.presentation.home.order.OrderDetailsFragment
import com.app.ancoturf.presentation.home.order.OrderFragment
import com.app.ancoturf.presentation.home.order.QuickOrderFragment
import com.app.ancoturf.presentation.home.order.filter.OrderFilterFragment
import com.app.ancoturf.presentation.home.products.filter.ProductFilterFragment
import com.app.ancoturf.presentation.home.portfolio.AddEditPortfolioFragment
import com.app.ancoturf.presentation.home.portfolio.PortfolioProductsFragment
import com.app.ancoturf.presentation.home.portfolio.PortfoliosFragment
import com.app.ancoturf.presentation.home.portfolio.ViewPortfolioFragment
import com.app.ancoturf.presentation.home.products.ProductDetailFragment
import com.app.ancoturf.presentation.home.products.ProductsFragment
import com.app.ancoturf.presentation.home.quote.*
import com.app.ancoturf.presentation.home.quote.filter.QuoteFilterFragment
import com.app.ancoturf.presentation.home.shop.ShopFragment
import com.app.ancoturf.presentation.home.tracking.LiveTrackingFragment
import com.app.ancoturf.presentation.home.tracking.TrackingFragment
import com.app.ancoturf.presentation.home.turfcalculator.TurfCalculatorFragment
import com.app.ancoturf.presentation.home.weather.WeatherFragment
import com.app.ancoturf.presentation.notification.NotificationFragment
import com.app.ancoturf.presentation.payment.BillHistoryFragment
import com.app.ancoturf.presentation.invoice.InvoiceFragment
import com.app.ancoturf.presentation.manageLawn.ManageLawnDetailFragment
import com.app.ancoturf.presentation.manageLawn.ManageLawnDropDownFragment
import com.app.ancoturf.presentation.manageLawn.ManageLawnFragment
import com.app.ancoturf.presentation.manageLawn.WebViewFragment
import com.app.ancoturf.presentation.payment.LastPaymentFragment
import com.app.ancoturf.presentation.payment.PaymentFragment
import com.app.ancoturf.presentation.profile.ChangePasswordFragment
import com.app.ancoturf.presentation.profile.ProfileFragment
import com.app.ancoturf.presentation.search.SearchFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    internal abstract fun homeFragment(): HomeFragment

    @ContributesAndroidInjector
    internal abstract fun shopFragment(): ShopFragment

    @ContributesAndroidInjector
    internal abstract fun productsFragment(): ProductsFragment

    @ContributesAndroidInjector
    internal abstract fun filterFragment(): ProductFilterFragment

    @ContributesAndroidInjector
    internal abstract fun productDetailFragment(): ProductDetailFragment

    @ContributesAndroidInjector
    internal abstract fun portfoliosFragment(): PortfoliosFragment

    @ContributesAndroidInjector
    internal abstract fun addPortfoliosFragment(): AddEditPortfolioFragment

    @ContributesAndroidInjector
    internal abstract fun portfolioProductsFragment(): PortfolioProductsFragment

    @ContributesAndroidInjector
    internal abstract fun contactFragment(): ContactFragment

    @ContributesAndroidInjector
    internal abstract fun profileFragment(): ProfileFragment

    @ContributesAndroidInjector
    internal abstract fun quotesFragment(): QuotesFragment

    @ContributesAndroidInjector
    internal abstract fun addBusinessDetailsFragment(): AddBusinessDetailsFragment

    @ContributesAndroidInjector
    internal abstract fun addCustomerInfoFragment(): AddCustomerInfoFragment

    @ContributesAndroidInjector
    internal abstract fun addEditQuoteFragment(): AddEditQuoteFragment

    @ContributesAndroidInjector
    internal abstract fun quoteFilterFragment(): QuoteFilterFragment

    @ContributesAndroidInjector
    internal abstract fun offersFragment(): OffersFragment

    @ContributesAndroidInjector
    internal abstract fun offerDetailFragment(): OfferDetailFragment

    @ContributesAndroidInjector
    internal abstract fun cartFragment(): CartFragment

    @ContributesAndroidInjector
    internal abstract fun checkoutFragment(): CheckoutFragment

    @ContributesAndroidInjector
    internal abstract fun orderFragment(): OrderFragment

    @ContributesAndroidInjector
    internal abstract fun orderDetailsFragment(): OrderDetailsFragment

    @ContributesAndroidInjector
    internal abstract fun quickOrderFragment(): QuickOrderFragment

    @ContributesAndroidInjector
    internal abstract fun orderFilterFragment(): OrderFilterFragment

    @ContributesAndroidInjector
    internal abstract fun turfCalculatorFragment(): TurfCalculatorFragment

    @ContributesAndroidInjector
    internal abstract fun lawnTipsFragment(): LawnTipsFragment

    @ContributesAndroidInjector
    internal abstract fun lawnTipsDetailFragment(): LawnTipsDetailFragment

    @ContributesAndroidInjector
    internal abstract fun searchFragment(): SearchFragment

    @ContributesAndroidInjector
    internal abstract fun changePasswordFragment(): ChangePasswordFragment

    @ContributesAndroidInjector
    internal abstract fun paymentFragmentFragment(): PaymentFragment


    @ContributesAndroidInjector
    internal abstract fun billHistoryFragment(): BillHistoryFragment

    @ContributesAndroidInjector
    internal abstract fun lastPaymentFragment(): LastPaymentFragment

    @ContributesAndroidInjector
    internal abstract fun invoiceFragment(): InvoiceFragment

    @ContributesAndroidInjector
    internal abstract fun trackingFragment():TrackingFragment

    @ContributesAndroidInjector
    internal abstract fun liveTrackingFragment():LiveTrackingFragment

    @ContributesAndroidInjector
    internal abstract fun viewPortfolioFragment():ViewPortfolioFragment

    @ContributesAndroidInjector
    internal abstract fun quotePDFFragment():QuotePDFFragment

    @ContributesAndroidInjector
    internal abstract fun notificationFragment():NotificationFragment

    @ContributesAndroidInjector
    internal abstract fun guidelineFragment():GuidelineFragment

    @ContributesAndroidInjector
    internal abstract fun chooseMyLawnIntroFragment():ChooseMyLawnIntroFragment

    @ContributesAndroidInjector
    internal abstract fun chooseMyLawnQuestionFragment(): ChooseMyLawnQuestionFragment

    @ContributesAndroidInjector
    internal abstract fun recommendedProductFragment(): RecommendedProductFragment

    @ContributesAndroidInjector
    internal abstract fun manageLawnFragment(): ManageLawnFragment

    @ContributesAndroidInjector
    internal abstract fun manageLawnDetailFragment(): ManageLawnDetailFragment

    @ContributesAndroidInjector
    internal abstract fun aboutUs(): AboutUsFragment

    @ContributesAndroidInjector
    internal abstract fun aboutUsDetail(): AboutUsDetailFragment

    @ContributesAndroidInjector
    internal abstract fun manageLawnDropDown(): ManageLawnDropDownFragment

    @ContributesAndroidInjector
    internal abstract fun faq(): FAQFragment

    @ContributesAndroidInjector
    internal abstract fun webView(): WebViewFragment

    @ContributesAndroidInjector
    internal abstract fun weather(): WeatherFragment


}