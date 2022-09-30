package com.app.ancoturf.extension

import com.app.ancoturf.data.common.exceptions.AncoHttpException
import com.app.ancoturf.domain.common.CustomErrorConsumer
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

fun <T> Single<T>.customSubscribe(success: (T) -> Unit, errorRes: (AncoHttpException?) -> Unit): Disposable {
    return this.subscribe(Consumer {
        success(it)
    }, object : CustomErrorConsumer() {
        override fun accept(it: AncoHttpException) {
            errorRes(it)
        }
    })
}

fun <T> Maybe<T>.customSubscribe(success: (T) -> Unit, errorRes: (AncoHttpException?) -> Unit): Disposable {
    return this.subscribe(Consumer {
        success(it)
    }, object : CustomErrorConsumer() {
        override fun accept(it: AncoHttpException) {
            errorRes(it)
        }
    })
}

fun <T> Flowable<T>.customSubscribe(success: (T) -> Unit, errorRes: (String?) -> Unit): Disposable {
    return this.subscribe(Consumer {
        success(it)
    }, object : CustomErrorConsumer() {
        override fun accept(it: AncoHttpException) {
            error(it)
        }
    })
}