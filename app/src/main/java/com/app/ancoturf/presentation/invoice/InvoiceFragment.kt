package com.app.ancoturf.presentation.invoice

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.View.GONE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.extension.showAlert
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import es.voghdev.pdfviewpager.library.RemotePDFViewPager
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter
import es.voghdev.pdfviewpager.library.remote.DownloadFile
import es.voghdev.pdfviewpager.library.util.FileUtil
import kotlinx.android.synthetic.main.header.*
import kotlinx.android.synthetic.main.invoice_fragment.*
import javax.inject.Inject


class InvoiceFragment(var pdfUrl: String) : BaseFragment(), View.OnClickListener,
    DownloadFile.Listener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var invoiceViewModel: InvoiceViewModel? = null

    private var sharingUrl: String? = null

    override fun getContentResource(): Int {
        return R.layout.invoice_fragment
    }

    override fun viewModelSetup() {
        invoiceViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[InvoiceViewModel::class.java]

        initObservers()
        pdfUrl.let {
            /*invoiceWebView.webViewClient = WebViewClient()
            invoiceWebView.settings.setSupportZoom(true)
            invoiceWebView.settings.javaScriptEnabled = true
            invoiceWebView.loadUrl("https://docs.google.com/gview?embedded=true&url=${BuildConfig.API_BASE_URL}${pdfUrl}")*/
            Log.e("Invoice", "pdf url :${Constants.INVOICE_PDF}${pdfUrl}")
            invoiceViewModel!!.callGetInvoicePDF(pdfUrl)

            /*remotePDFViewPager =
                RemotePDFViewPager(context, "${BuildConfig.API_BASE_URL}${pdfUrl}", this)*/

        }
    }

    private fun initObservers() {
        invoiceViewModel!!.invoiceLiveData.observe(this, Observer {
            if (it != null) {
                Log.e("resp", it.toString())
                Log.e("Invoice", "pdf url :${Constants.INVOICE_PDF}${it.message}")

                sharingUrl = "${Constants.INVOICE_PDF}${it.message}"

                remotePDFViewPager =
                    RemotePDFViewPager(context, "${Constants.INVOICE_PDF}${it.message}", this)


            }
        })
    }

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    var remotePDFViewPager: RemotePDFViewPager? = null
    override fun viewSetup() {

        /*  pdfUrl.let {
              *//*invoiceWebView.webViewClient = WebViewClient()
            invoiceWebView.settings.setSupportZoom(true)
            invoiceWebView.settings.javaScriptEnabled = true
            invoiceWebView.loadUrl("https://docs.google.com/gview?embedded=true&url=${BuildConfig.API_BASE_URL}${pdfUrl}")*//*
            Log.e("Invoice", "pdf url :${BuildConfig.API_BASE_URL}${pdfUrl}")
            invoiceViewModel!!.callGetInvoicePDF(pdfUrl)

            *//*remotePDFViewPager =
                RemotePDFViewPager(context, "${BuildConfig.API_BASE_URL}${pdfUrl}", this)*//*

        }*/

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
        imgShare.setOnClickListener(this)
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
            R.id.imgShare -> {
                var shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, sharingUrl!!)
                startActivity(Intent.createChooser(shareIntent, "Share Invoice PDF"))
            }
        }
    }

    override fun onSuccess(url: String?, destinationPath: String?) {
        try {
            progressBarForInvoice?.visibility = GONE
            var adapter = PDFPagerAdapter(context, FileUtil.extractFileNameFromURL(url))
            remotePDFViewPager?.adapter = adapter
            invoiceWebView?.addView(remotePDFViewPager)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onFailure(e: Exception?) {
        progressBarForInvoice?.visibility = GONE
        (requireActivity() as AppCompatActivity).showAlert(getString(R.string.pdfLoadFailed))
    }

    override fun onProgressUpdate(progress: Int, total: Int) {

    }

    override fun onDestroy() {
        super.onDestroy()
        invoiceViewModel!!._invoiceLiveData.value = null
    }

    override fun onDetach() {
        super.onDetach()
    }
}