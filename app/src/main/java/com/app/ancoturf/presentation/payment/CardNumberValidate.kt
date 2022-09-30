package com.app.ancoturf.presentation.payment

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText

class CardNumberValidate(val edTxt: EditText) : TextWatcher {
    var isDelete: Boolean = false;

    init {
        edTxt.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    isDelete = true;
                }
                return false;
            }

        })
    }

    override fun afterTextChanged(s: Editable?) {
        if (isDelete) {
            isDelete = false
            return
        }
        var `val` = s.toString()
        var a: String? = ""
        var b: String? = ""
        var c: String? = ""
        var d: String? = ""
        if (`val`.isNotEmpty()) {
            `val` = `val`.replace(" ", "")
            if (`val`.length >= 4) {
                a = `val`.substring(0, 4)
            } else if (`val`.length < 4) {
                a = `val`.substring(0, `val`.length)
            }
            if (`val`.length >= 8) {
                b = `val`.substring(4, 8)
                c = `val`.substring(8, `val`.length)
            } else if (`val`.length in 5..7) {
                b = `val`.substring(4, `val`.length)
            }
            if (`val`.length >= 12) {
                c = `val`.substring(8, 12)
                d = `val`.substring(12, `val`.length)

            } else if (`val`.length in 9..11) {
                c = `val`.substring(8, `val`.length)
            }
            val stringBuffer = StringBuffer()
            if (a != null && a.isNotEmpty()) {
                stringBuffer.append(a)
                if (a.length == 4) {
                    stringBuffer.append(" ")
                }
            }
            if (b != null && b.isNotEmpty()) {
                stringBuffer.append(b)
                if (b.length == 4) {
                    stringBuffer.append(" ")
                }
            }
            if (c != null && c.isNotEmpty()) {
                stringBuffer.append(c)
                if (c.length == 4) {
                    stringBuffer.append(" ")
                }
            }


            if (d != null && d.isNotEmpty()) {
                stringBuffer.append(d)
            }
            edTxt.removeTextChangedListener(this)
            edTxt.setText(stringBuffer.toString())
            edTxt.setSelection(edTxt.text.toString().length)
            edTxt.addTextChangedListener(this)
        } else {
            edTxt.removeTextChangedListener(this)
            edTxt.setText("")
            edTxt.addTextChangedListener(this)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    companion object {

        private val TAG = CardNumberValidate::class.java
            .simpleName
    }
}