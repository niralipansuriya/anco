package com.app.ancoturf.domain
import com.app.ancoturf.data.BaseResponse
import io.reactivex.Maybe
import io.reactivex.Single

abstract class BaseMayBeUseCase<T> {

//    abstract fun buildSingle(data: Map<String, Any?> = emptyMap()): Single<BaseResponse<T>>
//
//    fun execute(data: Map<String, Any?> = emptyMap()): Single<BaseResponse<T>> {
//        return buildSingle(data).compose(SchedulerTransformer())
//    }

    abstract fun buildMayBe(data: Map<String, Any?>? = emptyMap()): Maybe<T>

    fun execute(data: Map<String, Any?>? = emptyMap()): Maybe<T> {
        return buildMayBe(data).compose(SchedulerTransformerForMayBe())
    }
}
