package com.app.ancoturf.data.util

import android.net.ConnectivityManager

fun hasNetwork(cm: ConnectivityManager) = cm.activeNetworkInfo?.isConnected ?: false