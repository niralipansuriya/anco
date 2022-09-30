package com.app.ancoturf.presentation.home.aboutUs

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.lawntips.remote.LawnTipsDetailResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.extension.openLogoutAlertDialog
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Logger
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.android.synthetic.main.fragment_about_us_detail.*
import kotlinx.android.synthetic.main.fragment_about_us_detail.imgBack
import kotlinx.android.synthetic.main.fragment_about_us_detail.imgManageLawnFutureImage
import kotlinx.android.synthetic.main.fragment_about_us_detail.linContainer
import kotlinx.android.synthetic.main.fragment_about_us_detail.nestedScroll1
import kotlinx.android.synthetic.main.fragment_about_us_detail.txtTitle
import kotlinx.android.synthetic.main.fragment_about_us_detail.webView
import kotlinx.android.synthetic.main.fragment_manage_lawn_detail.*
import kotlinx.android.synthetic.main.header.*
import java.util.regex.Pattern
import javax.inject.Inject


class AboutUsDetailFragment(private val lawnTipsId: Int) : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var aboutUsViewModel: AboutUsViewModel? = null

    var lawntipsDetailResponse: LawnTipsDetailResponse? = null

    var lawnTipsDescription: ArrayList<LawnTipsDetailResponse.Description> = ArrayList()

    //review fields
    var reviewOpen = false


    @Inject
    lateinit var sharedPrefs: SharedPrefs


    override fun getContentResource(): Int = R.layout.fragment_about_us_detail
    override fun viewModelSetup() {
        aboutUsViewModel = ViewModelProviders.of(
            requireActivity(),
            viewModelFactory
        )[AboutUsViewModel::class.java]
        initObservers()
    }

    private fun initObservers() {
        aboutUsViewModel!!.lawntipsDetailsLiveData.observe(this, Observer {
            if (it != null) {
                Log.e("resp", it.toString())
                lawntipsDetailResponse = it
                setData()

                nestedScroll1.visibility = View.VISIBLE
            }
        })

        aboutUsViewModel!!.errorLiveData.observe(this, Observer {
            if (!Utility.isValueNull(aboutUsViewModel!!.errorLiveData.value)) {
                Utility.cancelProgress()
                if (it.equals(ErrorConstants.UNAUTHORIZED_ERROR_CODE)) {
                    if (activity is HomeActivity) {
                        (activity as HomeActivity).cartViewModel?.deleteProduct(null)
                        (activity as HomeActivity).cartViewModel?.deleteCoupon(null)
                    } else if (activity is PaymentActivity) {
                        (activity as PaymentActivity).cartViewModel?.deleteProduct(null)
                        (activity as PaymentActivity).cartViewModel?.deleteCoupon(null)
                    } else if (activity is ProfileActivity) {
                        (activity as ProfileActivity).cartViewModel?.deleteProduct(null)
                        (activity as ProfileActivity).cartViewModel?.deleteCoupon(null)
                    }
                    (requireActivity() as AppCompatActivity).openLogoutAlertDialog(sharedPrefs = sharedPrefs)
                }
                aboutUsViewModel!!._errorLiveData.value = null
            }
        })
    }

    private fun setData() {
        txtTitle?.text = lawntipsDetailResponse?.title
        if (lawntipsDetailResponse?.featureImageUrl?.length!! > 0) {
            imgManageLawnFutureImage.visibility = View.VISIBLE
            Glide.with(requireActivity()).load(lawntipsDetailResponse?.featureImageUrl)
                .into(imgManageLawnFutureImage)
        }else{
            imgManageLawnFutureImage.visibility = View.GONE
        }
        var tmpstr = lawntipsDetailResponse?.details
       /* if (tmpstr?.contains("https://www.youtube.com")!!){
            tmpstr = tmpstr?.replace("https://www.youtube.com","www.youtube.com")
        }*/
/*        if (tmpstr?.contains("www.youtube.com")!!) {
            val videoView = LayoutInflater.from(activity).inflate(R.layout.item_videoview, null)
            val txtTitle: AppCompatTextView = videoView.findViewById(R.id.txtTitle)
            txtTitle.visibility = View.GONE
            val youTubePlayerView: YouTubePlayerView = videoView.findViewById(R.id.player)
            lifecycle.addObserver(youTubePlayerView)

            youTubePlayerView.addYouTubePlayerListener(object :
                AbstractYouTubePlayerListener() {
                override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                    //YsJmu6A1ZoY" width="640" height="360" class="note-video-clip"><
//                youTubePlayer.loadVideo("YsJmu6A1ZoY", 0f)
                    youTubePlayer.loadVideo(
                        extractVideoIdFromUrl(lawntipsDetailResponse?.details!!),
                        0f
                    )

                }
            })
            var str = lawntipsDetailResponse?.details
            linContainer.addView(videoView)
//            var anchorTagsRemoved = tmpstr?.replace("/<a.*>.*?<\/a>/ig",'');
//            tmpstr = tmpstr?.replace("/<iframe.*>.*?<\\/iframe>/ig","")
//            tmpstr = tmpstr?.substring(tmpstr.indexOf("</p>"))
//            var startInd =
            var tmpstrReplace =
                tmpstr?.substring(tmpstr.indexOf("<iframe"), tmpstr.indexOf("</iframe>") + 9)
            tmpstr = tmpstr?.replace(tmpstrReplace, "")
        }*/
        Logger.log("nnn 2->" + tmpstr)
        webView.settings.domStorageEnabled = true
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()
        webView.loadData(tmpstr!!, "text/html", "UTF-8")
    }
    val videoIdRegex = arrayOf(
        "\\?vi?=([^&]*)",
        "watch\\?.*v=([^&]*)",
        "(?:embed|vi?)/([^/?]*)",
        "^([A-Za-z0-9\\-]*)"
    )

    fun extractVideoIdFromUrl(url: String): String {
        for (regex in videoIdRegex) {
            val compiledPattern = Pattern.compile(regex)
            val matcher = compiledPattern.matcher(url)

            if (matcher.find()) {
                var tmpStr = matcher.group(1)
                if (tmpStr.isNotEmpty()) {
                    var stringsep = "\""
                    var pos = tmpStr.indexOf(stringsep)
//                tmpStr.substring(0,pos+stringsep.length-1)
                    Log.d("nnn->", "" + tmpStr.substring(pos + stringsep.length - 1))
//                return matcher.group(1)
                    return tmpStr.substring(0, pos + stringsep.length - 1)
                } else {
                    return ""
                }
            }
        }
        return ""
    }


    override fun viewSetup() {
        Utility.showProgress(context = requireContext(), message = "", cancellable = false)
        aboutUsViewModel?.getAboutUsDetail(lawnTipsId)

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
        imgMore.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgSearch.setOnClickListener(this)

        nestedScroll1.visibility = View.GONE

    }


    override fun onDestroyView() {
        super.onDestroyView()
        aboutUsViewModel?._lawntipsDetailsLiveData?.value = null
    }

    override fun onClick(view: View?) {
        if (view == null) return
        when (view.id) {

            R.id.imgBell -> {
                openNotificationFragment()
            }
            R.id.imgBack -> {
                requireActivity().supportFragmentManager.popBackStack()
            }
            R.id.txtBackToLawnTips -> {
                requireActivity().supportFragmentManager.popBackStack()
            }
            R.id.imgLogo -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    HomeFragment(),
                    false,
                    true,
                    false,
                    R.id.flContainerHome
                )
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

            R.id.imgSearch -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    SearchFragment(),
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
                        false,
                        R.id.flContainerHome
                    )
                } else {
                    shortToast(getString(R.string.no_product_in_cart_message))
                }
            }

        }
    }


}