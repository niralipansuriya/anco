package com.app.ancoturf.presentation.home.tracking

import RouteDataModel
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.util.Xml
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.ancoturf.R
import com.app.ancoturf.data.common.ApiCallback
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.common.network.BaseService
import com.app.ancoturf.data.order.remote.entity.response.OrderDetailResponse
import com.app.ancoturf.data.tracking.MapDirectionRequest
import com.app.ancoturf.extension.longToast
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.tracking.service.TrackingService
import com.app.ancoturf.presentation.notification.NotificationFragment
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Utility
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_live_tracking.*
import kotlinx.android.synthetic.main.header.*
import kotlinx.android.synthetic.main.invoice_fragment.imgBack
import org.xmlpull.v1.XmlPullParser
import retrofit2.Call
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class LiveTrackingFragment(var order: OrderDetailResponse) : BaseFragment(), OnMapReadyCallback,
    View.OnClickListener, ApiCallback {
    override fun getContentResource(): Int {
        return R.layout.fragment_live_tracking
    }

    override fun viewModelSetup() {

    }

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private var mMap: GoogleMap? = null

    override fun viewSetup() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        imgBack.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgSearch.setOnClickListener(this)

        if (sharedPrefs?.sessionId != null && sharedPrefs?.sessionId != "") {
            callLogOffNavman(false)
        } else
            callInitApi()

    }

    private fun callLogOffNavman(onBackpressed: Boolean) {
        this.onBackPressed = onBackpressed
        var xmlRequestToLogOff = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\n" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "\t<soap:Body>\n" +
                "\t\t<DoLogoff xmlns=\"http://onlineavl2.navmanwireless.com/0907/\">\n" +
                "\t\t\t<request>\n" +
                "\t\t\t\t<Session>\n" +
                "\t\t\t\t\t<SessionId>${if (sharedPrefs?.sessionId != "") sharedPrefs?.sessionId else "00000000-0000-0000-0000-000000000000"}</SessionId>\n" +
                "\t\t\t\t</Session>\n" +
                "\t\t\t</request>\n" +
                "\t\t</DoLogoff>\n" +
                "\t</soap:Body>\n" +
                "</soap:Envelope>"
        var apiService: TrackingService =
            BaseService.retrofitNowmanInstance()?.create(TrackingService::class.java)!!

        var call: Call<String> = apiService?.callInitXmlRequest(xmlRequestToLogOff)!!
        Utility.showProgress(activity!!, "", false)
        callApi(LOGOFF_NAVMAN_API, call, this)
    }

    private fun callInitApi() {
        var xmlRequest: String = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\n" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "<soap:Body>\n" +
                "<DoLogin xmlns=\"http://onlineavl2.navmanwireless.com/0907/\">\n" +
                "<request>\n" +
                "<Session>\n" +
                "<SessionId>00000000-0000-0000-0000-000000000000</SessionId>\n" +
                "</Session>\n" +
                "<UserCredential>\n" +
                "<UserName>anco.sharp.api</UserName>\n" +
                "<Password>0ln7zruj</Password>\n" +
                "<ApplicationID>00000000-0000-0000-0000-000000000000</ApplicationID>\n" +
                "<ClientID>00000000-0000-0000-0000-000000000000</ClientID>\n" +
                "<ClientVersion></ClientVersion>\n" +
                "</UserCredential>\n" +
                "<IPAddress></IPAddress>\n" +
                "</request>\n" +
                "</DoLogin>\n" +
                "</soap:Body>\n" +
                "</soap:Envelope>"

        var apiService: TrackingService =
            BaseService.retrofitNowmanInstance()?.create(TrackingService::class.java)!!

        var call: Call<String> = apiService?.callInitXmlRequest(xmlRequest)!!
        Utility.showProgress(activity!!, "", false)
        callApi(INIT_XML_REQUEST, call, this)

    }

    override fun onMapReady(map: GoogleMap?) {
        mMap = map
    }

    var onBackPressed: Boolean = false
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> {
                if (sharedPrefs?.sessionId != null && sharedPrefs?.sessionId != "")
                    callLogOffNavman(true)
                else
                    requireActivity().supportFragmentManager.popBackStack()
            }
            R.id.imgBell ->{
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
//                (requireActivity() as AppCompatActivity).hideKeyboard()
                if (sharedPrefs?.totalProductsInCart!! > 0) {
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

    var sessionId: String? = null
    var ownerId: String? = null
    override fun onSuccess(type: String, response: String) {
        when (type) {
            INIT_XML_REQUEST -> {
                if (response != null && response != "") {
                    var parser: XmlPullParser = Xml.newPullParser()
                    var inputStream =
                        ByteArrayInputStream(response.toByteArray(StandardCharsets.UTF_8))
                    parser.setInput(inputStream, null)
                    var eventType = parser.eventType
                    loop@ while (eventType != XmlPullParser.END_DOCUMENT) {
                        var name: String
                        when (eventType) {
                            XmlPullParser.START_DOCUMENT -> {
                            }
                            XmlPullParser.START_TAG -> {
                                name = parser.name
                                if (name.equals(
                                        "SessionId",
                                        true
                                    )
                                ) {
                                    sessionId = parser.nextText()
                                    if (sessionId != null && ownerId != null)
                                        break@loop
                                } else if (name == "OwnerID") {
                                    ownerId = parser.nextText()
                                    if (sessionId != null && ownerId != null)
                                        break@loop
                                }
                            }
                            XmlPullParser.END_TAG -> {
                                name = parser.name
                            }
                        }
                        eventType = parser.next()
                    }
                    Log.e("Soaprequest", "SessionId=${sessionId} OwnerId=${ownerId}")
                    if (sessionId != null && ownerId != null) {
                        sharedPrefs?.sessionId = sessionId!!
                        getDirectionLatLng()
                    } else {
                        Utility.cancelProgress()
                        callLogOffNavman(false)
                    }
                } else
                    Utility.cancelProgress()
            }
            GET_DIRECTION_LATLNG -> {
                try {
                    if (response != null && response != "") {
                        var parser: XmlPullParser = Xml.newPullParser()
                        var inputStream =
                            ByteArrayInputStream(response.toByteArray(StandardCharsets.UTF_8))
                        parser.setInput(inputStream, null)
                        var eventType = parser.eventType
                        var latitude: String? = null
                        var longitude: String? = null
                        var vehicleId: String? = null
                        loop@ while (eventType != XmlPullParser.END_DOCUMENT) {
                            var name: String
                            when (eventType) {
                                XmlPullParser.START_DOCUMENT -> {
                                }
                                XmlPullParser.START_TAG -> {
                                    name = parser.name
                                    if (name.equals("VehicleId", true)) {
                                        vehicleId = parser.nextText()
                                    } else if (name.equals("Latitude", true)) {
                                        if (vehicleId?.equals(
                                                order?.driver?.pivot?.vehicle_id!!,
                                                true
                                            )!!
                                        )
                                            latitude = parser.nextText()
                                    } else if (name.equals("Longitude", true)) {
                                        if (vehicleId?.equals(
                                                order?.driver?.pivot?.vehicle_id!!,
                                                true
                                            )!!
                                        ) {
                                            longitude = parser.nextText()
                                            break@loop
                                        }
                                    }else{
//                                        longToast("Sorry we are currently experiencing technical difficulties. Please try again later.")
                                        Utility.cancelProgress()

                                    }
                                }
                                XmlPullParser.END_TAG -> {
                                    name = parser.name
                                }
                            }

                            eventType = parser.next()
                        }

                        Log.e("Soaprequest", "Lat={${latitude}, ${longitude}}")
                        if (latitude != null && longitude != null) {
                            var origin: String = "${latitude},${longitude}"
                            var destination: String =
                                "${order?.shippingAddressLine1!!}${order?.shippingCity!!}${order?.shippingState}${order?.shippingCountry!!}"

                            var mapDirectionRequest = MapDirectionRequest(origin, destination)

                            var trackingService =
                                BaseService.retrofitGoogleMapDirectionInstance()
                                    ?.create(TrackingService::class.java)

                            var call: Call<Object> =
                                trackingService?.mapDirectionApi(mapDirectionRequest)!!
                            callApiMapDirectionApi(GOOGLE_MAP_DIRECTION_API, call, this)

                        } else
                            Utility.cancelProgress()

                    } else
                        Utility.cancelProgress()
                }catch (e : Exception ){
                    e.printStackTrace()
                }
            }
            GOOGLE_MAP_DIRECTION_API -> {
                Utility.cancelProgress()
                var routeData: RouteDataModel =
                    Gson().fromJson(response, RouteDataModel::class.java)
                if (routeData?.routes?.size > 0) {
                    tvDistance.text =
                        routeData?.routes?.get(0)?.legs?.get(0)?.distance?.text
                    tvDuration.text = routeData?.routes?.get(0)?.legs?.get(0)?.duration?.text

                    var cal = Calendar.getInstance()

                    cal.add(
                        Calendar.SECOND,
                        routeData?.routes?.get(0)?.legs?.get(0)?.duration?.value
                    )

                    tvETATimeStamp.text =
                        "ETA ${SimpleDateFormat("hh:mm:ss aa").format(cal.time)}"

                    setupMap(
                        LatLng(
                            routeData?.routes?.get(0)?.legs?.get(0)?.start_location?.lat.toDouble(),
                            routeData?.routes?.get(0)?.legs?.get(0)?.start_location?.lng.toDouble()
                        ),
                        LatLng(
                            routeData?.routes?.get(0)?.legs?.get(0)?.end_location?.lat.toDouble(),
                            routeData?.routes?.get(0)?.legs?.get(0)?.end_location?.lng.toDouble()
                        )
                    )

                    handler.postDelayed(handlerCallBack, 10 * 1000L)
                }else{
                    longToast("We are unable to display any information on your driver tracking at the present moment. Please try again shortly.")
                }

            }
            LOGOFF_NAVMAN_API -> {
                Utility.cancelProgress()
                sharedPrefs?.sessionId = ""
                if (!onBackPressed)
                    callInitApi()
                else
                    requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }


    var handlerCallBack = Runnable {
        Log.e("Handler", "handler callback")
        getDirectionLatLng()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (handler != null)
            handler.removeCallbacks(handlerCallBack)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (handler != null)
            handler.removeCallbacks(handlerCallBack)
    }

    var handler: Handler = Handler()
    private fun setupMap(startAddress: LatLng, endAddress: LatLng) {
        if (mMap != null) {
            mMap!!.clear()
            var latLongBuilder = LatLngBounds.Builder()
            mMap!!.addMarker(
                MarkerOptions().position(startAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.anco_truck))
            )
            mMap!!.addMarker(
                MarkerOptions().position(endAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
            )
            latLongBuilder.include(startAddress)
            latLongBuilder.include(endAddress)
            mMap!!.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    latLongBuilder.build(),
                    120
                )
            )
        }
    }

    override fun onError(type: String, errorMessage: String) {
        Utility.cancelProgress()
    }

    private fun getDirectionLatLng() {
        var xmlRequest = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\n" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "\t<soap:Body>\n" +
                "\t\t<GetVehicleSnapShots xmlns=\"http://onlineavl2.navmanwireless.com/0907/\">\n" +
                "\t\t\t<request>\n" +
                "\t\t\t\t<Session>\n" +
                "\t\t\t\t\t<SessionId>${sessionId}</SessionId>\n" +
                "\t\t\t\t</Session>\n" +
                "\t\t\t\t<Version>0</Version>\n" +
                "\t\t\t\t<OwnerId>${ownerId}</OwnerId>\n" +
                "\t\t\t\t<MaximumSerializableEventSubType>SMDP_EVENT_UNKNOWN</MaximumSerializableEventSubType>\n" +
                "\t\t\t\t<FetchTemperatureData>true</FetchTemperatureData>\n" +
                "\t\t\t</request>\n" +
                "\t\t</GetVehicleSnapShots>\n" +
                "\t</soap:Body>\n" +
                "</soap:Envelope>"
        Log.e("LiveTracking", "getDirection : " + xmlRequest)
        var trackingService =
            BaseService.retrofitNowmanInstance()?.create(TrackingService::class.java)
        var call: Call<String> = trackingService?.callInitXmlRequest(xmlRequest)!!
        callApi(GET_DIRECTION_LATLNG, call, this)
    }
}