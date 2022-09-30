package com.app.ancoturf.presentation.home.lawntips

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_lawn_tips_detail.*
import kotlinx.android.synthetic.main.header.*
import javax.inject.Inject

class LawnTipsDetailFragment(private val lawnTipsId: Int) :
    BaseFragment(),
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var lawntipsViewModel: LawnTipsViewModel? = null
    var lawntipsDetailResponse: LawnTipsDetailResponse? = null
    var lawnTipsDescription: ArrayList<LawnTipsDetailResponse.Description> = ArrayList()
    //review fields
    var reviewOpen = false
    var txtTitle: TextView? = null
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun getContentResource(): Int = R.layout.fragment_lawn_tips_detail

    override fun viewModelSetup() {
        lawntipsViewModel = ViewModelProviders.of(
            requireActivity(),
            viewModelFactory
        )[LawnTipsViewModel::class.java]
        initObservers()
    }

    override fun viewSetup() {
        Utility.showProgress(context = requireContext(), message = "", cancellable = false)
        lawntipsViewModel?.getLawnTipsDetails(lawnTipsId)
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
        txtBackToLawnTips.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        nestedScroll1.visibility = View.GONE
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

    override fun onDestroyView() {
        super.onDestroyView()
        lawntipsViewModel?._lawntipsDetailsLiveData?.value = null
    }

    fun initObservers() {
        lawntipsViewModel!!.lawntipsDetailsLiveData.observe(this, Observer {
            if (it != null) {
                Log.e("resp", it.toString())
                lawntipsDetailResponse = it
                setData()

                nestedScroll1.visibility = View.VISIBLE
            }
        })

        lawntipsViewModel!!.errorLiveData.observe(this, Observer {
            if (!Utility.isValueNull(lawntipsViewModel!!.errorLiveData.value)) {
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
                } else {
//                    it?.let { it1 -> shortToast(it1) }
                }
                lawntipsViewModel!!._errorLiveData.value = null
            }
        })
    }

    fun setData() {

        txtSubTitle.text = getString(R.string.label_lawn_tips)
        txtLawnTitle.text = getString(R.string.tips)

        txtLawnTipsTitle.text = lawntipsDetailResponse?.title
        Glide.with(requireActivity()).load(lawntipsDetailResponse?.featureImageUrl)
            .into(imgLawnTipsFutureImage)
        linContainer.removeAllViews()

        for (i in 0 until lawntipsDetailResponse!!.descriptions.size) {
            var description = lawntipsDetailResponse!!.descriptions.get(i)
            val htmlView: View =
                LayoutInflater.from(activity).inflate(R.layout.item_htmlview, null)
            val txtTitle: TextView = htmlView.findViewById(R.id.txtTitle)
            txtTitle.text = description.key
            val txtHtml: WebView = htmlView.findViewById(R.id.txtHtml)
            txtHtml.loadData(description.detail, "text/html", "UTF-8")
            txtHtml.visibility = if (description.open) View.VISIBLE else View.GONE
            txtTitle.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                if (description.open) ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_up_arrow
                ) else ContextCompat.getDrawable(requireContext(), R.drawable.ic_down_arrow),
                null
            )
            txtTitle.setOnClickListener {
                description.open = !description.open
                txtHtml.visibility = if (description.open) View.VISIBLE else View.GONE
                txtTitle.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    if (description.open) ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_up_arrow
                    ) else ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_down_arrow
                    ),
                    null
                )
            }
            if (description.detail != null && description.detail.isNotEmpty())
                linContainer.addView(htmlView)
        }
        Utility.cancelProgress()
    }
}

