package com.app.ancoturf.domain
import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.utils.Logger
import io.reactivex.Single

abstract class BaseUseCase<T> {

    abstract fun buildSingle(data: Map<String, Any?>? = emptyMap()): Single<T>

    fun execute(data: Map<String, Any?>? = emptyMap()): Single<T> {
        Logger.log("nnn print->"+data?.entries)
        return buildSingle(data).compose(SchedulerTransformer())
    }

    suspend fun executeNow(data: Map<String, Any?>? = emptyMap()): Single<T> {
        return buildSingle(data).compose(SchedulerTransformer())
    }


}
