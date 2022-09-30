package com.app.ancoturf.di.module

import com.app.ancoturf.di.scope.ActivityScoped
import com.app.ancoturf.presentation.contact.ContactActivity
import com.app.ancoturf.presentation.forgotpassword.ForgotPasswordActivity
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.portfolio.ViewImageActivity
import com.app.ancoturf.presentation.login.LoginActivity
import com.app.ancoturf.presentation.payment.westpac.AddCardActivity
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.signup.OpenPDFActivity
import com.app.ancoturf.presentation.signup.SignUpActivity
import com.app.ancoturf.presentation.splash.SplashActivity
import com.app.ancoturf.presentation.welcome.WelcomeActivity
import com.app.ancoturf.presentation.welcomeOnBoard.WelcomeOnBoardActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * We want Dagger.Android to create a Subcomponent which has a parent Component of whichever module
 * ActivityModule is on, in our case that will be [AppComponent]. You never
 * need to tell [AppComponent] that it is going to have all these subcomponents
 * nor do you need to tell these subcomponents that [AppComponent] exists.
 * We are also telling Dagger.Android that this generated SubComponent needs to include the
 * specified modules and be aware of a scope annotation [@ActivityScoped].
 * When Dagger.Android annotation processor runs it will create 2 subcomponents for us.
 */
@Module
abstract class ActivityModule {

    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun splashActivity(): SplashActivity

    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun loginActivity(): LoginActivity

    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun welcomeActivity(): WelcomeActivity

    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun signUpActivity(): SignUpActivity

    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun homeActivity(): HomeActivity

    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun paymentActivity(): PaymentActivity

    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun contactActivity(): ContactActivity

    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun profileActivity(): ProfileActivity

    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun forgotPasswordActivity(): ForgotPasswordActivity

    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun addCardActivity(): AddCardActivity

    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun viewImageActivity():ViewImageActivity

    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun welcomeOnBoardActivity(): WelcomeOnBoardActivity

  @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun openPDFActivity(): OpenPDFActivity



}
