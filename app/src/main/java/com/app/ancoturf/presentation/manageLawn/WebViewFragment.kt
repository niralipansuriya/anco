package com.app.ancoturf.presentation.manageLawn

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.ancoturf.R
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import kotlinx.android.synthetic.main.fragment_webview.*
import kotlinx.android.synthetic.main.header.*
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.URL
import javax.inject.Inject


class WebViewFragment : BaseFragment(), View.OnClickListener {
    var title : String?  = ""
    var url : String?  = ""
//    var from : Int = 0 // 0 Open from uri, 1 : Terms&condition, 2 : PrivacyPolicy
    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun getContentResource(): Int = R.layout.fragment_webview

    override fun viewModelSetup() {

    }

    private fun setData() {
        /*webView.settings.javaScriptEnabled = true
        val pdf = url
//        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=$pdf")
        webView.settings.setPluginState(WebSettings.PluginState.ON);
        webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=$pdf")*/
//        webView.loadUrl(pdf)

//        val input: InputStream = Loadfile().execute(url)
//        pdfView.fromStream(Loadfile().execute(url))

        MyAsyncTask(requireActivity() as HomeActivity, url!!).execute()
     /*   when(from) {
            0 -> {
                MyAsyncTask(requireActivity() as HomeActivity, url!!).execute()
            }
            1->{
                pdfView.fromAsset("Terms_Condition.pdf")
                    .enableSwipe(true) *//* allows to block changing pages using swipe*//*
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .load()
            }

            2->{
                pdfView.fromAsset("Privacy_Policy.pdf")
                    .enableSwipe(true) *//* allows to block changing pages using swipe*//*
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .load()
            }

        }
*/
    }

    companion object{
        class MyAsyncTask internal constructor(context: HomeActivity, var strUrl : String) : AsyncTask<Unit, Unit, InputStream>() {
            private val activityReference: WeakReference<HomeActivity> = WeakReference(context)
            override fun onPreExecute() {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
                activity.progressBar.visibility = View.VISIBLE
            }

            override fun doInBackground(vararg params: Unit): InputStream {
                return URL(strUrl).openStream()
            }

            override fun onPostExecute(result: InputStream) {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
                activity.progressBar.visibility = View.GONE
                activity.pdfView.fromStream(result).load()
            }

        }
    }

    override fun viewSetup() {
        arguments?.let {

            url = it.getString("url")
            title = it.getString("title")
//            from = it.getInt("from")
            arguments = null
        }
        txtTitle.text =title
        if (activity is HomeActivity) {
            (activity as HomeActivity).hideShowFooter(true)
            (activity as HomeActivity).showCartDetails(imgCart, txtCartProducts, false)
        } else if (activity is ProfileActivity) {
            (activity as ProfileActivity).hideShowFooter(true)
            (activity as ProfileActivity).showCartDetails(imgCart, txtCartProducts, false)
        } else if (activity is PaymentActivity) {
            (activity as PaymentActivity).hideShowFooter(true)
            (activity as PaymentActivity).showCartDetails(imgCart, txtCartProducts, false)
        }

        imgBack.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        setData()
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.imgBell -> {
                openNotificationFragment()
            }
            R.id.imgBack -> {
                requireActivity().supportFragmentManager.popBackStack()
            }
            R.id.imgMore -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    ContactFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }


            R.id.imgCart -> {
                //                (requireActivity() as AppCompatActivity).hideKeyboard()
                if (sharedPrefs.totalProductsInCart > 0) {
                    (requireActivity() as AppCompatActivity).pushFragment(
                        CartFragment(),
                        true,
                        true,
                        false, -
                        R.id.flContainerHome
                    )
                } else {
                    shortToast(getString(R.string.no_product_in_cart_message))
                }
            }

            R.id.imgLogo -> {
                requireActivity().startActivity(Intent(requireActivity(), HomeActivity::class.java))
                requireActivity().finishAffinity()
            }

            R.id.imgSearch -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    SearchFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }
        }
    }


}