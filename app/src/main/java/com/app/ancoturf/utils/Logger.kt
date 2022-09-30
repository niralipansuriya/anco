package com.app.ancoturf.utils

import android.util.Log

class Logger {
    companion object{
        fun log(msg:String){
            Log.v("AppLog",msg)
        }
    }
}