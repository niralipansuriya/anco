package com.app.ancoturf.presentation.home.quote

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.ancoturf.BuildConfig
import com.app.ancoturf.R
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
import es.voghdev.pdfviewpager.library.asset.CopyAsset
import es.voghdev.pdfviewpager.library.asset.CopyAssetThreadImpl
import es.voghdev.pdfviewpager.library.remote.DownloadFile
import es.voghdev.pdfviewpager.library.util.FileUtil
import kotlinx.android.synthetic.main.fragment_quote_p_d_f.*
import kotlinx.android.synthetic.main.header.*
import java.io.File
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class QuotePDFFragment(var pdfUrl: String) : BaseFragment(), View.OnClickListener,
    DownloadFile.Listener {


    @Inject
    lateinit var sharedPrefs: SharedPrefs
    var remotePDFViewPager: RemotePDFViewPager? = null
    var pdfFilePath: String? = null

    var isQuote: Boolean = false
    var title : String?  = ""

    override fun getContentResource(): Int {
        return R.layout.fragment_quote_p_d_f
    }

    override fun viewModelSetup() {
    }

    override fun viewSetup() {
        pdfUrl.let {
            /*invoiceWebView.webViewClient = WebViewClient()
            invoiceWebView.settings.setSupportZoom(true)
            invoiceWebView.settings.javaScriptEnabled = true
            invoiceWebView.loadUrl("https://docs.google.com/gview?embedded=true&url=${BuildConfig.API_BASE_URL}${pdfUrl}")*/
            Log.e("Quote", "pdf url :${BuildConfig.API_BASE_URL}${pdfUrl}")

            arguments?.let {
                isQuote = it.getBoolean("isQuote")
                title = it.getString("title")
                arguments = null
            }
            if (isQuote) {
                txtTitle.text = getString(R.string.quote_pdf)
                imgBack.visibility = View.VISIBLE
                imgDownloadPdf.visibility = View.VISIBLE
                imgShare.visibility = View.VISIBLE
                remotePDFViewPager =
                    RemotePDFViewPager(context, "${BuildConfig.API_BASE_URL}${pdfUrl}", this)
            } else {
                txtTitle.text =title
                txtTitle.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen._18ssp))
                imgBack.visibility = View.VISIBLE
                imgDownloadPdf.visibility = View.GONE
                imgShare.visibility = View.GONE

             /*   val copyAsset: CopyAsset = CopyAssetThreadImpl(context, Handler())
                copyAsset.copy("LawnCare.pdf",  Environment.getExternalStorageDirectory().getAbsolutePath())*/
//                txtTitle.text =
//                we.getSettings().setUseWideViewPort(true);
                remotePDFViewPager =
                    RemotePDFViewPager(context, "${pdfUrl}", this)


                remotePDFViewPager!!.scaleX = 1.5f
            }
        }

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
        imgDownloadPdf.setOnClickListener(this)
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
                shareIntent.putExtra(Intent.EXTRA_TEXT, "${BuildConfig.API_BASE_URL}${pdfUrl}")
                startActivity(Intent.createChooser(shareIntent, "Share Invoice PDF"))
            }
            R.id.imgDownloadPdf -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (activity?.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        downloadFile()
                    } else {
                        requestPermissions(
                            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            111
                        )
                    }
                } else {
                    downloadFile()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111) {
            downloadFile()
        }
    }

    fun downloadFile() {
//        if ("${BuildConfig.API_BASE_URL}${pdfUrl}" != null) {
        if ("${pdfUrl}" != null) {
            /*var sourceFile = File(pdfFilePath)
            try {
                copyFile(
                    sourceFile,
                    "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/${sourceFile.name}"
                )
                Toast.makeText(activity, "File downloaded", Toast.LENGTH_SHORT).show()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }*/

            downloadFileUsingDownloadManager();
        }
    }

    private fun downloadFileUsingDownloadManager() {
        val downloadManager =
            activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(
            if (isQuote) Uri.parse("${BuildConfig.API_BASE_URL}${pdfUrl}") else Uri.parse("${pdfUrl}")
        )
        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI or
                    DownloadManager.Request.NETWORK_MOBILE
        )

        var timeStamp = System.currentTimeMillis();
// set title and description
        // set title and description
        request.setTitle("anco_quote_pdf_${timeStamp}.pdf")

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

//set the local destination for download file to a path within the application's external files directory
        //set the local destination for download file to a path within the application's external files directory
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "anco_quote_pdf_${timeStamp}.pdf"
        )
        request.setMimeType("application/pdf")
        downloadManager!!.enqueue(request)
    }

    fun copyFile(file: File, destinationFile: String) {
        var dest = File(destinationFile)

        file.copyTo(dest, overwrite = true)
    }

    override fun onSuccess(url: String?, destinationPath: String?) {
        try {
            pdfFilePath = destinationPath
            progressBarForInvoice?.visibility = View.GONE
            var adapter = PDFPagerAdapter(context, FileUtil.extractFileNameFromURL(url))
            remotePDFViewPager?.adapter = adapter
            invoiceWebView?.addView(remotePDFViewPager,LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onFailure(e: Exception?) {
        progressBarForInvoice?.visibility = View.GONE
        (requireActivity() as AppCompatActivity).showAlert(getString(R.string.pdfLoadFailed))
    }

    override fun onProgressUpdate(progress: Int, total: Int) {
    }
}
