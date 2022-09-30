package com.app.ancoturf.domain

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SchedulerTransformer<T> : SingleTransformer<T, T> {

    override fun apply(upstream: Single<T>): SingleSource<T> {
        return upstream.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


}