package com.app.ancoturf.presentation.manageLawn

import android.app.Activity
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.app.ancoturf.utils.Utility
import kotlinx.android.synthetic.main.fragment_manage_lawn_drop_down.*
import kotlinx.android.synthetic.main.header.*
import javax.inject.Inject
import kotlin.math.acos

/*
https://androidexample365.com/a-simple-html-page-into-a-recyclerview-of-native-android-widgets/
https://www.tutorialsbuzz.com/2019/09/android-kotlin-styling-spinner-drop-down.html
 */



class ManageLawnDropDownFragment(private val manageLawnId: Int) : BaseFragment(),
    View.OnClickListener {

    private var title: String = ""

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var manageLawnViewModel: ManageLawnViewModel? = null
    var manageLawnDetailResponse: ManageLawnDetailResponse? = null
    var manageLawnList: ArrayList<ManageLawnDetailResponse.Data> = ArrayList()

    lateinit var dropDownManageLawnAdapter: ManageLawnDropDownAdapter

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    var linearLayoutManager : LinearLayoutManager?  = null
    var activity : Activity? = null

    override fun getContentResource(): Int = R.layout.fragment_manage_lawn_drop_down

    override fun viewModelSetup() {
        manageLawnViewModel = ViewModelProviders.of(
            requireActivity(),
            viewModelFactory
        )[ManageLawnViewModel::class.java]
        activity = requireActivity()
        initObservers()
    }

    private fun initObservers() {
        manageLawnViewModel!!.manageLawnDetailsLiveData.observe(this, Observer {
            if (it != null) {
                Log.e("resp", it.toString())
                manageLawnDetailResponse = it
                if (manageLawnDetailResponse!!.data.size > 0) {
                    manageLawnList.clear()
                    manageLawnList.addAll(manageLawnDetailResponse!!.data)
                }
                sortDataAlphabatically()
                setData()
//                nestedScroll1.visibility = View.VISIBLE
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

    private fun sortDataAlphabatically() {
        var tmplist: ArrayList<ManageLawnDetailResponse.Data> = ArrayList()
        tmplist.addAll(manageLawnList)
        manageLawnList.clear()
        manageLawnList.addAll(tmplist.sortedWith(compareBy (String.CASE_INSENSITIVE_ORDER,{ it.title })))
    }

    private fun setData() {
        setDropDown()
        setAdapter()
    }

    private fun setDropDown() {
        var firstLineArray = arrayOfNulls<String>(manageLawnList.size)
        for (i in 0 until manageLawnList.size){
            firstLineArray[i] = manageLawnList[i].title
        }
        if (firstLineArray.isNotEmpty()){
            val dataAdapter = ArrayAdapter<String>(requireContext(), R.layout.simple_spinner_item,firstLineArray)
            spinnerTitle.adapter = dataAdapter
        }
        spinnerTitle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                recyclerView.smoothScrollToPosition(position)
//                linearLayoutManager?.smoothScrollToPosition(recyclerView)
                /*recyclerView.post {
                    recyclerView.layoutManager?.scrollToPosition(position)
                }*/

            }

        }
    }

    private fun setAdapter() {
        if (manageLawnList != null && manageLawnList.size > 0) {
            dropDownManageLawnAdapter = activity?.let {
                ManageLawnDropDownAdapter(
                    it as AppCompatActivity, manageLawnList
                )
            }!!
             linearLayoutManager = LinearLayoutManager(activity)
            recyclerView.layoutManager = linearLayoutManager

            recyclerView.adapter = dropDownManageLawnAdapter
//            txtNoManageLawn.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE


        }
    }

  /*  private class MyBrowser : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String
        ): Boolean {
            view.loadUrl(url)
            view.addJavascriptInterface(object : Any() {
                @JavascriptInterface
                @Throws(Exception::class)
                fun performClick() {
                    Log.d("LOGIN::", "Clicked")
//                    Toast.makeText(, "Login clicked", Toast.LENGTH_LONG).show()
                }
            }, "login")
            return true
        }
    }*/

    override fun viewSetup() {
        Utility.showProgress(context = requireContext(), message = "", cancellable = false)
        arguments?.let {
            title = it.getString("title").toString()
            arguments = null
        }
        if (title != "")
            txtTitle.text = title

        manageLawnViewModel?.getMyLawnById(manageLawnId)
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