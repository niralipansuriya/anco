package com.app.ancoturf.presentation.app

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.app.ancoturf.di.component.DaggerAppComponent
import com.app.ancoturf.utils.SampleLifecycleListener
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import android.app.Activity
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.app.ancoturf.presentation.common.base.BaseActivity
import com.facebook.FacebookSdk
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.stetho.Stetho


class AncoTurfApp : DaggerApplication(), LifecycleObserver {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }



    private var mCurrentActivity: Activity? = null

    private val lifecycleListener: SampleLifecycleListener by lazy {
        SampleLifecycleListener()
    }

    override fun onCreate() {
        super.onCreate()

        FacebookSdk.sdkInitialize(applicationContext);
        //for vector images
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        Stetho.initializeWithDefaults(this);
        setupLifecycleListener()
        Fresco.initialize(this)
    }

    private fun setupLifecycleListener() {
        ProcessLifecycleOwner.get().lifecycle
            .addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        Log.d("SampleLifecycle", "Returning to foreground…")
        if (mCurrentActivity != null && mCurrentActivity is BaseActivity)
            (mCurrentActivity as BaseActivity).callBackGroundAPIs()

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() {
        Log.d("SampleLifecycle", "Moving to background…")
    }

    fun getCurrentActivity(): Activity? {
        return mCurrentActivity
    }

    fun setCurrentActivity(mCurrentActivity: Activity?) {
        this.mCurrentActivity = mCurrentActivity
    }


}
