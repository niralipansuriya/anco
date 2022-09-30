package com.app.ancoturf.extension

import com.app.ancoturf.BuildConfig
import com.app.ancoturf.data.common.LogStatus

fun String?.getBoolean(): Boolean {
    return this.equals("True", true)
}

fun String.printLog(status: LogStatus, tag: String) {
    val isDebug = BuildConfig.DEBUG
    if (!isDebug) {
        return
    }
    when (status) {
        LogStatus.REQUEST -> println("AncoTurf request Log: $tag --> $this")
        LogStatus.RESPONSE -> println("AncoTurf response Log: $tag --> $this")
        else -> println("AncoTurf error Log: $tag --> $this")
    }
}
