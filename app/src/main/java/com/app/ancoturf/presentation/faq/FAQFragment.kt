package com.app.ancoturf.presentation.faq

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.aboutUs.FAQResponse
import com.app.ancoturf.data.aboutUs.FAQTagsResponse
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.domain.AboutUs.usecases.FAQTagsUseCase
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.extension.openLogoutAlertDialog
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.aboutUs.AboutUsViewModel
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Utility
import kotlinx.android.synthetic.main.fragment_f_a_q.*
import kotlinx.android.synthetic.main.fragment_f_a_q.imgBack
import kotlinx.android.synthetic.main.header.*
import javax.inject.Inject


class FAQFragment : BaseFragment(), View.OnClickListener {
    @Inject
    lateinit var sharedPrefs: SharedPrefs

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var aboutUsViewModel: AboutUsViewModel? = null
    var adapterFAQ: FaqAdapter? = null
    var adapterFAQNew: FAQAdapterNew? = null
    var faqList: ArrayList<FAQResponse.Data>? = ArrayList()
    var faqListMain: ArrayList<FAQResponse.Data>? = ArrayList()
    var faqTagList: ArrayList<FAQTagsResponse.Data>? = ArrayList()
    var title : String? = ""

    override fun getContentResource(): Int = R.layout.fragment_f_a_q

    override fun viewModelSetup() {
        aboutUsViewModel = ViewModelProviders.of(
            requireActivity(),
            viewModelFactory
        )[AboutUsViewModel::class.java]
        initObservers()
    }

    private fun initObservers() {


        aboutUsViewModel?.faqLiveData?.observe(this, Observer {

            if (it != null) {
                Utility.cancelProgress()

                faqListMain?.clear()
                faqList?.clear()
                faqListMain?.addAll(it.data)
                faqList?.addAll(it.data)

//                setDropDownData()
                setAdapter()
                aboutUsViewModel!!._faqLivaData.value = null
            }
        })

        aboutUsViewModel?.faqTagLiveData?.observe(this, Observer {

            if (it != null) {
                Utility.cancelProgress()

                faqTagList?.clear()
                var addAllData = FAQTagsResponse.Data()
                addAllData.display_name = "All"
                faqTagList?.add(addAllData)
                faqTagList?.addAll(it.data)
                sortDataAlphabatically()
                setDropDownData()
//                setAdapter()
                aboutUsViewModel!!._faqLivaData.value = null
            }
        })
        aboutUsViewModel!!.errorLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                if (it == ErrorConstants.UNAUTHORIZED_ERROR_CODE) {
                    (requireActivity() as AppCompatActivity).openLogoutAlertDialog(sharedPrefs = sharedPrefs)
                } else {
                    shortToast(it)
                    if (it == "No LawnTips found") {
                        if (faqList == null)
                            faqList = ArrayList()
                        faqList?.clear()
                        setAdapter()
                    }
                }
                aboutUsViewModel?._errorLiveData?.value = null
            }
        })

    }

    private fun sortDataAlphabatically() {
        var tmplist: ArrayList<FAQTagsResponse.Data> = ArrayList()
        tmplist.addAll(faqTagList!!)
        faqTagList?.clear()
        faqTagList?.addAll(tmplist.sortedWith(compareBy (String.CASE_INSENSITIVE_ORDER,{ it.display_name})))
    }

    private fun setDropDownData() {
        var tmpFaqDropdown : ArrayList<String> = ArrayList()
       for(i in 0 until faqTagList!!.size){
               tmpFaqDropdown.add(faqTagList!![i].display_name)
       }
        if (tmpFaqDropdown.size > 0){
            var faqDropDownNameArr =tmpFaqDropdown.toTypedArray()
            val dataAdapter = ArrayAdapter<String>(this!!.context!!, R.layout.simple_spinner_item,faqDropDownNameArr)
            spinnerFAQ.adapter = dataAdapter
            spinnerFAQ.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    var filterFaq : ArrayList<FAQResponse.Data> = ArrayList()
                    var strCatSel : String = "All"
                    strCatSel = faqTagList!![position].display_name

                    if (strCatSel != "All"){
                        for (i in 0 until faqList!!.size){
                            for (j in 0 until faqList!![i].details.size){
                                if (faqList!![i].details!![j].display_name == strCatSel){
                                    filterFaq.add(faqList!![i])
                                    break
                                }
                            }
                        }
                        faqListMain!!.clear()
                        faqListMain!!.addAll(filterFaq)
                    }else{
                        faqListMain!!.clear()
                        faqListMain!!.addAll(faqList!!)
                    }
                   /* if (adapterFAQ != null){
                        adapterFAQ!!.notifyDataSetChanged()
                    }*/
                    if (adapterFAQNew != null){
                        adapterFAQNew!!.notifyDataSetChanged()
                    }
//                linearLayoutManager?.smoothScrollToPosition(recyclerView)
                    /*recyclerView.post {
                        recyclerView.layoutManager?.scrollToPosition(position)
                    }*/

                }

            }
        }
    }

    fun setAdapter() {
        if (faqList != null && faqList!!.size > 0) {
            /*adapterFAQ = activity?.let {
                FaqAdapter(
                    faqListMain!!
                )
            }!!
            listAboutUs.adapter = adapterFAQ*/

            adapterFAQNew = activity?.let {
                FAQAdapterNew(
                    faqListMain!!
                )
            }!!
            listAboutUs.adapter = adapterFAQNew
            txtAboutUs.visibility = View.GONE
            listAboutUs.visibility = View.VISIBLE
        }
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
        arguments?.let {
            title = it.getString("title")
            txtTitle.text = title
            arguments = null
        }
        Utility.showProgress(requireContext(), "", false)
        aboutUsViewModel?.getFAQ("1")
        aboutUsViewModel?.getFAQTags("1")
     /*   adapterFAQ = faqList?.let { FaqAdapter(it) }
        listAboutUs.adapter = adapterFAQ*/

        adapterFAQNew = faqList?.let { FAQAdapterNew(it) }
        listAboutUs.adapter = adapterFAQNew
        imgMore.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgBack.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imgBack -> {
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
//                    shortToast(getString(R.string.no_product_in_cart_message))
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