package com.app.ancoturf.utils

import android.text.InputFilter
import android.text.Spanned

class InputFilterMinMax(private var min: Int, private var max: Int) : InputFilter {
//class InputFilterMinMax(private var min: Int) : InputFilter {

//    private var max:Float = 0f

//    fun InputFilterMinMax(min: Float, max: Float) {
//        this.min = min
//        this.max = max
//    }
//
//    fun InputFilterMinMax(min: Int, max: Int) {
//        this.min = min.toFloat()
//        this.max = max.toFloat()
//    }
//
//    fun InputFilterMinMax(min: String, max: String) {
//        this.min = Integer.parseInt(min).toFloat()
//        this.max = Integer.parseInt(max).toFloat()
//    }

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val input = (dest.toString() + source.toString()).toInt()
            if (isInRange(min, max, input))
//            if (isInRange(min, input))
                return null
        } catch (nfe: NumberFormatException) {
        }

        return ""
    }

    private fun isInRange(a: Int, b: Int, c: Int): Boolean {
        return if (b > a) c >= a && c <= b else c >= b && c <= a
    }

//    private fun isInRange(a: Int, c: Float): Boolean {
//        return c >= a
//    }
}