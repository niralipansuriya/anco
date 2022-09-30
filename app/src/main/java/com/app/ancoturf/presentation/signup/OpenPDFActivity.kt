package com.app.ancoturf.presentation.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.app.ancoturf.R
import com.app.ancoturf.presentation.common.base.BaseActivity
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.manageLawn.WebViewFragment
import kotlinx.android.synthetic.main.fragment_webview.*

class OpenPDFActivity :  BaseActivity(), View.OnClickListener {

    var from : Int = 1 //1 : Terms&condition, 2 : PrivacyPolicy

        override fun getContentResource(): Int = R.layout.activity_open_p_d_f



    override fun viewModelSetup() {
    }

    override fun viewSetup() {
        if (intent.hasExtra("from")){
            from = intent.getIntExtra("from",1)
        }
        setData()
        imgBack.setOnClickListener(this)
    }

    private fun setData() {
        when(from) {

            1->{
                txtTitle.text = "Terms and Condition"
                pdfView.fromAsset("Terms_Condition.pdf")
                    .enableSwipe(true) /* allows to block changing pages using swipe*/
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .load()
            }

            2->{
                txtTitle.text = "Privacy Policy"

                pdfView.fromAsset("Privacy_Policy.pdf")
                    .enableSwipe(true) /* allows to block changing pages using swipe*/
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .load()
            }

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> {
                finish()
            }
        }
    }
}