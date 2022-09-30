package com.app.ancoturf.domain

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SchedulerTransformerForMayBe<T> : MaybeTransformer<T, T> {

    override fun apply(upstream: Maybe<T>): MaybeSource<T> {
        return upstream.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}