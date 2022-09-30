package com.app.ancoturf.presentation.manageLawn

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.manageLawn.remote.ManageLawnDetailResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.extension.openLogoutAlertDialog
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Logger
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.android.synthetic.main.fragment_manage_lawn_detail.*
import kotlinx.android.synthetic.main.header.*
import java.util.regex.Pattern
import javax.inject.Inject


//  448 Youtube player ProductDetailFragment

/*
* {

    "success":true,
    "message":"My Lawn Found",
    "data":{
        "id":1,
        "title":"test",
        "featured_image_url":"https://ancob2b.sharptest.com.au/uploads/88e912d44cd3b243d2dff83dc3cab869.jpg",
        "my_lawn_details":"<p>Test Description <br></p><p><iframe frameborder="0" src="//www.youtube.com/embed/YsJmu6A1ZoY" width="640" height="360" class="note-video-clip"></iframe></p><p><br></p><p>Test Description</p>",
        "created_at":"2020-10-05 15:09:25",
        "updated_at":"2020-10-05 15:11:02"
    }

}
* */
/**
 * A simple [Fragment] subclass.
 * Use the [ManageLawnDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ManageLawnDetailFragment(
    private val manageLawnId: Int
) : BaseFragment(), View.OnClickListener {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var manageLawnViewModel: ManageLawnViewModel? = null
    var manageLawnDetailResponse: ManageLawnDetailResponse? = null
    var positionSteps = 0

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    var lawnId: Int = 0


    override fun getContentResource(): Int = R.layout.fragment_manage_lawn_detail

    override fun viewModelSetup() {
        manageLawnViewModel = ViewModelProviders.of(
            requireActivity(),
            viewModelFactory
        )[ManageLawnViewModel::class.java]
        initObservers()
    }

    private fun initObservers() {
        manageLawnViewModel!!.manageLawnDetailsLiveData.observe(this, Observer {
            if (it != null) {
                Log.e("resp", it.toString())
                manageLawnDetailResponse = it
                setData()

                nestedScroll1.visibility = View.VISIBLE
            }
        })

        manageLawnViewModel!!.errorLiveData.observe(this, Observer {
            if (!Utility.isValueNull(manageLawnViewModel!!.errorLiveData.value)) {
                Utility.cancelProgress()
                if (it.equals(ErrorConstants.UNAUTHORIZED_ERROR_CODE)) {
                    if (activity is HomeActivity) {
                        (activity as HomeActivity).cartViewModel?.deleteProduct(null)
                        (activity as HomeActivity).cartViewModel?.deleteCoupon(null)
                    }
                    (requireActivity() as AppCompatActivity).openLogoutAlertDialog(sharedPrefs = sharedPrefs)
                } else {
//                    it?.let { it1 -> shortToast(it1) }
                }
                manageLawnViewModel!!._errorLiveData.value = null
            }
        })
    }

    private fun setData() {
        txtTitle.text = manageLawnDetailResponse?.data?.get(positionSteps)?.title
        Glide.with(requireActivity()).load(manageLawnDetailResponse?.data?.get(positionSteps)?.featureImageUrl)
            .into(imgManageLawnFutureImage)


        var tmpstr = manageLawnDetailResponse?.data?.get(positionSteps)?.myLawnDetails
        if (tmpstr?.contains("www.youtube.com")!!) {
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
                        extractVideoIdFromUrl(manageLawnDetailResponse?.data?.get(positionSteps)?.myLawnDetails!!),
                        0f
                    )

                }
            })
            var str = manageLawnDetailResponse?.data?.get(positionSteps)?.myLawnDetails
            linContainer.addView(videoView)
//            var anchorTagsRemoved = tmpstr?.replace("/<a.*>.*?<\/a>/ig",'');
//            tmpstr = tmpstr?.replace("/<iframe.*>.*?<\\/iframe>/ig","")
//            tmpstr = tmpstr?.substring(tmpstr.indexOf("</p>"))
//            var startInd =
            var tmpstrReplace =
                tmpstr?.substring(tmpstr.indexOf("<iframe"), tmpstr.indexOf("</iframe>") + 9)
            tmpstr = tmpstr?.replace(tmpstrReplace, "")
        }
        Logger.log("nnn 2->" + tmpstr)
        webView.webChromeClient = WebChromeClient()
        webView.loadData(tmpstr, "text/html", "UTF-8")
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

    //YsJmu6A1ZoY" width="640" height="360" class="note-video-clip"><


    override fun viewSetup() {
        Utility.showProgress(context = requireContext(), message = "", cancellable = false)
        manageLawnViewModel?.getMyLawnById(manageLawnId)

        arguments?.let {
            positionSteps = it.getInt("positionSteps")
            arguments = null
        }
        if (activity is HomeActivity) {
            (activity as HomeActivity).hideShowFooter(true)
            (activity as HomeActivity).showCartDetails(imgCart, txtCartProducts, false)
        }
        Utility.showProgress(requireContext(), "", false)
        imgBack.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> {
                requireActivity().supportFragmentManager.popBackStack()
            }
            R.id.imgBell -> {
                openNotificationFragment()
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

            R.id.imgLogo -> {
                //(requireActivity() as AppCompatActivity).hideKeyboard()
                (requireActivity() as AppCompatActivity).pushFragment(
                    HomeFragment(),
                    false,
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
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        manageLawnViewModel?._manageLawnDetailsLiveData?.value = null
    }
}