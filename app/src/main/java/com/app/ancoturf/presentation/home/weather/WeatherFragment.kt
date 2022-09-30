    package com.app.ancoturf.presentation.home.weather

import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.app.ancoturf.utils.Utility
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_weather.*
import kotlinx.android.synthetic.main.fragment_weather.tvMax
import kotlinx.android.synthetic.main.fragment_weather.tvMin
import kotlinx.android.synthetic.main.fragment_weather.tvMostly
import kotlinx.android.synthetic.main.fragment_weather.tvTemperature
import kotlinx.android.synthetic.main.fragment_webview.*
import kotlinx.android.synthetic.main.header.*
import java.lang.Exception
import javax.inject.Inject


    class WeatherFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    var weatherHeaderAdapter : WeatherHeaderAdapter? = null
        var weatherRowAdapter : WeatherRowAdapter? = null
        private var weatherResponseTmp: WeatherResponse? = null
        private var nextSevenDay: List<WeatherResponse.NextSevenDay> = emptyList()
        private var nextSevenDayAdap: List<WeatherResponse.NextSevenDay> = emptyList()
        private var timeWiseData: List<WeatherResponse.Timewise> = emptyList()
        private  var indexNo = 0
        private  var indexNoDay = 0


        override fun getContentResource(): Int = R.layout.fragment_weather

    override fun viewModelSetup() {

    }

        private fun setAdapter() {
            val linearLayoutManager = LinearLayoutManager(activity)
            linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            rvHeaderWeather.layoutManager = linearLayoutManager
            weatherHeaderAdapter = WeatherHeaderAdapter( activity as AppCompatActivity,timeWiseData)
            rvHeaderWeather.adapter = weatherHeaderAdapter
            var indexDay = indexNo + 1
            if(indexDay == 24){
                indexDay = 0
            }
            rvHeaderWeather.scrollToPosition(indexDay)
            nextSevenDayAdap =    nextSevenDay.subList(1,nextSevenDay.size)

            val linearLayoutManagerForecast = LinearLayoutManager(activity)
            rvDetailWeather.layoutManager = linearLayoutManagerForecast
            weatherRowAdapter = WeatherRowAdapter( activity as AppCompatActivity,nextSevenDayAdap)
            rvDetailWeather.adapter = weatherRowAdapter


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
            ivBackWeather.setOnClickListener(this)
            arguments?.let {
                if (it.containsKey("Weather")) {
                    weatherResponseTmp = it.getParcelable("Weather")
                    timeWiseData = weatherResponseTmp?.getData()?.timewise!!
                    nextSevenDay = weatherResponseTmp?.getData()?.nextSevenDay!!

                    var dateHH = Utility.changeDateFormat(
                        weatherResponseTmp?.getData()?.current?.currentDate.toString(),
                        Utility.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS,
                        Utility.DATE_FORMAT_hh
                    )

                     indexNo = dateHH.toInt()

                    tvMostly.text = weatherResponseTmp?.getData()?.timewise!![indexNo].precis
                    var precisCode = weatherResponseTmp?.getData()?.timewise!![indexNo].precisCode
                    var isNight = if(weatherResponseTmp?.getData()?.timewise!![indexNo].night == "1") true else false
                    try{
                        val imagename = Utility.getImageName(precisCode!!.toString(),isNight)
                        val res =
                            resources.getIdentifier(imagename, "drawable", activity!!.packageName)
                        ivWeatherDetail.setImageResource(res)
                    }catch (e : Exception){
                        e.printStackTrace()
                    }

                    """${weatherResponseTmp?.getData()?.current?.airTemperature.toString()}°""".also { tvTemperature.text = it }
                    ("Max "+weatherResponseTmp?.getData()?.current?.maximumAirTemperature.toString()+"°").also { tvMax.text = it }
                    ("Min  "+weatherResponseTmp?.getData()?.current?.minimumAirTemperature.toString()+"°").also { tvMin.text = it }
                    setAdapter()
                }
            }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBackWeather -> {
                requireActivity().supportFragmentManager.popBackStack()
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
}