package com.app.ancoturf.presentation.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.offer.ClsQuickLinks
import com.app.ancoturf.data.offer.remote.OfferDataResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.openLogoutAlertDialog
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.chooseingMyLawn.ChooseMyLawnIntroFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.guideline.GuidelineFragment
import com.app.ancoturf.presentation.home.aboutUs.AboutUsFragment
import com.app.ancoturf.presentation.home.adapters.OfferPagerAdapter
import com.app.ancoturf.presentation.home.adapters.QuickLinksAdapter
import com.app.ancoturf.presentation.home.interfaces.IDataPositionWise
import com.app.ancoturf.presentation.home.offers.OffersViewModel
import com.app.ancoturf.presentation.home.order.OrderDetailsFragment
import com.app.ancoturf.presentation.home.order.OrderFragment
import com.app.ancoturf.presentation.home.order.QuickOrderFragment
import com.app.ancoturf.presentation.home.portfolio.PortfoliosFragment
import com.app.ancoturf.presentation.home.products.ProductDetailFragment
import com.app.ancoturf.presentation.home.quote.QuotesFragment
import com.app.ancoturf.presentation.home.shop.ShopFragment
import com.app.ancoturf.presentation.home.tracking.TrackingFragment
import com.app.ancoturf.presentation.home.turfcalculator.TurfCalculatorFragment
import com.app.ancoturf.presentation.home.weather.WeatherFragment
import com.app.ancoturf.presentation.home.weather.WeatherResponse
import com.app.ancoturf.presentation.home.weather.WeatherViewModel
import com.app.ancoturf.presentation.manageLawn.ManageLawnFragment
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Logger
import com.app.ancoturf.utils.Utility
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.header.*
import java.io.IOException
import java.util.*
import javax.inject.Inject


class HomeFragment : BaseFragment(), View.OnClickListener, IDataPositionWise {

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var offersViewModel: OffersViewModel? = null
    private var weatherResponseTmp: WeatherResponse? = null
    private var currentWeather: WeatherResponse.Current? = null
    private var timeWise: List<WeatherResponse.Timewise> = emptyList()

    lateinit var offerPagerAdapter: OfferPagerAdapter
    var offerList: ArrayList<OfferDataResponse.Data> = ArrayList()
    lateinit var mGoogleApiClient: GoogleApiClient
    private var mLastLocation: Location? = null


    lateinit var quickLinksAdapter: QuickLinksAdapter
    var quickLinkList: ArrayList<ClsQuickLinks> = ArrayList()
    private var weatherViewModel: WeatherViewModel? = null

    private val LOCATION_PERMISSION_REQUEST_CODE = 11
    private val OPEN_SETTING_CODE = 12
    private var currentLocation: Location? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null


    override fun getContentResource(): Int = R.layout.fragment_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)
    }

    override fun viewModelSetup() {
        offersViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[OffersViewModel::class.java]
        weatherViewModel =
            ViewModelProviders.of(this, viewModelFactory)[WeatherViewModel::class.java]
        initObservers()
        if (checkLocationPermission()) {
            initLocationApi()
        }
    }

    private fun initLocationApi() {
        if (checkLocationEnable()) {

            Thread.sleep(200)
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(activity!!)

            fusedLocationProviderClient!!.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        /* Logger.log(
                             location!!.latitude.toString() + " testdwi" + location.longitude
                         )*/
                        /*  weatherViewModel?.callWeatherAPI(
                             "-37.931910",
                              "145.190020"
                          )
  */

                        weatherViewModel?.callWeatherAPI(
                            location!!.latitude.toString(),
                            location!!.longitude.toString()
                        )
                        getAddressFromLocation(
                            location!!.latitude, location!!.longitude, activity!!, GeoCodeHandler()
                        )
                    }
                }
            if (ActivityCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        } else {
            val dialog = AlertDialog.Builder(activity!!)
            dialog.setMessage("Please Enable Location")
            dialog.setCancelable(false)
            dialog.setPositiveButton("Okay") { paramDialogInterface, paramInt ->
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                this.startActivityForResult(myIntent, OPEN_SETTING_CODE)
            }
            dialog.setNegativeButton("No") { paramDialogInterface, paramInt ->
                initLocationApi()
            }
            dialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_SETTING_CODE) {
            initLocationApi()
        }
    }

    internal inner class GeoCodeHandler : Handler() {
        override fun handleMessage(message: Message) {
            val locationAddress: String
            locationAddress = when (message.what) {
                1 -> {
                    val bundle = message.data
                    bundle.getString("address").toString()
                }
                else -> null.toString()
            }
            tvCityName.text = if (locationAddress != null) locationAddress else ""
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (checkLocationPermission()) {
                initLocationApi()
            }
        }
    }

    fun checkLocationEnable(): Boolean {
        val mLocationManager =
            activity!!.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || mLocationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    override fun viewSetup() {

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

        Utility.showProgress(requireContext(), "", false)
        offersViewModel?.callGetOffers("1")

        var userInfo = Gson().fromJson(sharedPrefs.userInfo, UserInfo::class.java)
        txtWelcomeLabel.text = if (userInfo != null) getString(
            R.string.welcome_text,
            userInfo.firstName + " " + userInfo.lastName
        ) else getString(R.string.welcome_text, "")

        setQuickLinkData()
        quickLinksAdapter = QuickLinksAdapter(activity, quickLinkList, this)
        listQuickLinks.adapter = quickLinksAdapter

        imgMore.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        csWeatherHome.setOnClickListener(this)

        arguments?.let {
            if (arguments?.getBoolean(Constants.ORDER_STATUS_CHANGED) == true) {
                Logger.log("nnn arguments homefragment-> true")

                (requireActivity() as AppCompatActivity).pushFragment(
                    OrderFragment().apply { arguments = it },
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            } else if (arguments?.containsKey(Constants.PRODUCT_ID_PARAM)!!) {

                (requireActivity() as AppCompatActivity).pushFragment(
                    ProductDetailFragment(
                        arguments?.getString(Constants.PRODUCT_ID_PARAM)!!.toInt()
                    ),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )

            } else {
                Logger.log("nnn arguments homefragment-> true")

                (requireActivity() as AppCompatActivity).pushFragment(
                    QuotesFragment().apply { arguments = it },
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }
            arguments = null
        }
    }

    private fun initObservers() {
        offersViewModel!!.offersLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                Log.e("resp", it.toString())
                if (offerList == null)
                    offerList = ArrayList()
                offerList.clear()
                offerList.addAll(it.data)
                setAdapter()
                offersViewModel!!._offersLiveData.value = null
            }
        })

        offersViewModel!!.errorLiveData.observe(this, Observer {
            Utility.cancelProgress()
            if (!Utility.isValueNull(offersViewModel!!.errorLiveData.value)) {
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
                } else {
                    it?.let { it1 ->
//                        shortToast(it1)
                        if (offerList != null)
                            offerList.clear()
                        setAdapter()
                    }
                    offersViewModel!!._errorLiveData.value = null
                }
            }
        })

        weatherViewModel!!.weatherLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()

                weatherResponseTmp = it
                Log.e("resp", it.toString())
                currentWeather = weatherResponseTmp?.getData()?.current
                timeWise = weatherResponseTmp?.getData()?.timewise!!
                activity?.runOnUiThread(Runnable {
//                    tvCityName.text = currentWeather?.stnName.toString()
//                    tvCityName.text = currentWeather?.stnName.toString()
                    var date = Utility.changeDateFormat(
                        currentWeather?.currentDate.toString(),
                        Utility.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS,
                        Utility.DATE_FORMAT_dd_MM_hh_mm_aa
                    )
                    var dateHH = Utility.changeDateFormat(
                        currentWeather?.currentDate.toString(),
                        Utility.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS,
                        Utility.DATE_FORMAT_hh
                    )

                    var indexNo = dateHH.toInt()
                    tvMostly.text = timeWise[indexNo].precis
                    var precisCode = timeWise[indexNo].precisCode
                    var isNight = if (timeWise[indexNo].night == "1") true else false
                    try {
                        val imagename = Utility.getImageName(precisCode!!.toString(), isNight)
                        val res =
                            resources.getIdentifier(imagename, "drawable", activity!!.packageName)
                        ivWeather.setImageResource(res)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    tvDateTime.text = date
                    """${currentWeather?.airTemperature.toString()}°""".also {
                        tvTemperature.text = it
                    }
                    (currentWeather?.maximumAirTemperature.toString() + "°").also {
                        tvMaxVal.text = it
                    }
                    (currentWeather?.minimumAirTemperature.toString() + "°").also {
                        tvMinVal.text = it
                    }


                })
            }

        })
    }


    fun getAddressFromLocation(
        latitude: Double,
        longitude: Double, context: Context, handler: Handler
    ) {
        val thread = object : Thread() {
            override fun run() {
                val geoCoder = Geocoder(
                    context,
                    Locale.getDefault()
                )
                var result: String = null.toString()
                try {
                    val addressList = geoCoder.getFromLocation(
                        latitude, longitude, 1
                    )
                    if ((addressList != null && addressList.size > 0)) {
                        val address = addressList.get(0)
                        /*val sb = StringBuilder()
                        for (i in 0 until address.maxAddressLineIndex) {
                            sb.append(address.getAddressLine(i)).append("\n")
                        }
                        sb.append(address.locality).append("\n")
                        sb.append(address.locality)
                       sb.append(address.postalCode).append("\n")
                        sb.append(address.countryName)*/
                        //if (address.locality != null) {
                            result = address.locality.toString()

                        //}
                    }
                } catch (e: IOException) {
                    Log.e(tag, "Unable connect to GeoCoder", e)
                } finally {
                    val message = Message.obtain()
                    message.target = handler
                    message.what = 1
                    val bundle = Bundle()
                    result = result
                    bundle.putString("address", result)
                    message.data = bundle
                    message.sendToTarget()
                }
            }
        }
        thread.start()
    }

    private fun setAdapter() {
        if (offerList != null && offerList.size > 0) {
            pagerOffers.visibility = View.VISIBLE
            offerPagerAdapter = OfferPagerAdapter(requireActivity() as AppCompatActivity, offerList)
            pagerOffers.adapter = offerPagerAdapter
        } else {
            pagerOffers.visibility = View.GONE
        }
        nestedScroll.scrollTo(0, 0)
    }


    private fun setQuickLinkData() {
        quickLinkList.clear()
        quickLinkList.add(
            ClsQuickLinks(
                getString(R.string.choose_my_lawn),
                R.drawable.img_1,
                "",
                ChooseMyLawnIntroFragment()
            )
        )
        quickLinkList.add(
            ClsQuickLinks(
                getString(R.string.manage_my_lawn),
                R.drawable.img_2,
                "",
                ManageLawnFragment()
            )
        )
        quickLinkList.add(
            ClsQuickLinks(
                getString(R.string.orders_title),
                R.drawable.bg_item_order, "",
                OrderFragment()
            )
        )
        quickLinkList.add(
            ClsQuickLinks(
                getString(R.string.shop_title),
                R.drawable.bg_item_shop, "",
                ShopFragment()
            )
        )
        if (sharedPrefs.isLogged) {
            quickLinkList.add(
                ClsQuickLinks(
                    getString(R.string.tracking_title),
                    R.drawable.bg_item_tracking, "",
                    TrackingFragment()
                )
            )
            if (sharedPrefs.userType == Constants.LANDSCAPER || sharedPrefs?.userType == Constants.WHOLESALER) {
                if (!sharedPrefs.checkUncheckGuideScreenDontShow) {
                    val bundle = Bundle()
                    bundle.putInt("guideScreen", 0)
                    var guidePortFrag: GuidelineFragment =
                        GuidelineFragment().apply { arguments = bundle }


                    quickLinkList.add(
                        ClsQuickLinks(
                            getString(R.string.portfolio_title),
                            R.drawable.anco___onboarding___quote_02, "",
                            guidePortFrag
                        )
                    )
                } else {
                    quickLinkList.add(
                        ClsQuickLinks(
                            getString(R.string.portfolio_title),
                            R.drawable.bg_item_portfolio, "",
                            PortfoliosFragment()
                        )
                    )
                }
                if (!sharedPrefs.checkUncheckGuideScreenDontShowQuote) {
                    val bundleQ = Bundle()
                    bundleQ.putInt("guideScreen", 1)
                    var guideQuoteFrag: GuidelineFragment =
                        GuidelineFragment().apply { arguments = bundleQ }
                    quickLinkList.add(
                        ClsQuickLinks(
                            getString(R.string.quote_title),
                            R.drawable.bg_item_quote, "",
                            guideQuoteFrag
                        )
                    )
                } else {
                    quickLinkList.add(
                        ClsQuickLinks(
                            getString(R.string.quote_title),
                            R.drawable.bg_item_quote, "",
                            QuotesFragment()
                        )
                    )
                }
                quickLinkList.add(
                    ClsQuickLinks(
                        getString(R.string.quick_order_title),
                        R.drawable.bg_item_quick_order, "",
                        QuickOrderFragment()
                    )
                )
            }
        }

        quickLinkList.add(
            ClsQuickLinks(
                getString(R.string.turf_calculator_title),
                R.drawable.bg_item_turf_calculator, "",
                TurfCalculatorFragment()
            )
        )

        quickLinkList.add(
            ClsQuickLinks(
                getString(R.string.about_us),
                R.drawable.bg_item_lawn_tips, "",
                AboutUsFragment()
            )
        )
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.csWeatherHome -> {
                if (weatherResponseTmp != null) {
                    val bundle = Bundle()
                    bundle.putParcelable("Weather", weatherResponseTmp)
                    val weatherFragment = WeatherFragment().apply { arguments = bundle }
                    (requireActivity() as AppCompatActivity).pushFragment(
                        weatherFragment,
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                }
            }
            R.id.imgMore -> {
//                (requireActivity() as AppCompatActivity).hideKeyboard()
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
                Log.d("trace ", "======================  ${sharedPrefs.totalProductsInCart}")
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

            R.id.imgSearch -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    SearchFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }
            R.id.imgBell -> {
                openNotificationFragment()
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        activity!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                } else {
                    requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
                return false
            } else {
                return true
            }
        } else {
            return true
        }
    }

    override fun getDataFromPos(position: Int, name: String) {
        when (quickLinkList[position].title
        ) {
            activity?.getString(R.string.choose_my_lawn), activity?.getString(R.string.manage_my_lawn), activity?.getString(
                R.string.shop_title
            ), activity?.getString(R.string.portfolio_title), activity?.getString(R.string.quote_title), activity?.getString(
                R.string.turf_calculator_title
            ), activity?.getString(R.string.quick_order_title), activity?.getString(R.string.lawn_tips_title), activity?.getString(
                R.string.about_us
            ) -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                (requireActivity() as AppCompatActivity).pushFragment(
                    quickLinkList[position].fragment,
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }
            activity?.getString(R.string.orders_title) -> {
                if (sharedPrefs.isLogged) {
                    (requireActivity() as AppCompatActivity).pushFragment(
                        quickLinkList[position].fragment,
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                } else {
                    (requireActivity() as AppCompatActivity).pushFragment(
                        OrderDetailsFragment(null, 0),
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                }
            }
            activity?.getString(R.string.tracking_title) -> {
                if (sharedPrefs.isLogged) {
                    (requireActivity() as AppCompatActivity).pushFragment(
                        quickLinkList[position].fragment,
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                } else {
                    (requireActivity() as AppCompatActivity).pushFragment(
                        OrderDetailsFragment(null, 0),
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                }
            }
        }
    }
}
