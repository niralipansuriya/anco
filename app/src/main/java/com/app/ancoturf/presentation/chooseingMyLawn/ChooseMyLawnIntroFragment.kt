package com.app.ancoturf.presentation.chooseingMyLawn

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.ancoturf.R
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.home.products.ProductsFragment
import com.app.ancoturf.presentation.search.SearchFragment
import kotlinx.android.synthetic.main.fragment_choose_my_lawn_intro.*
import kotlinx.android.synthetic.main.fragment_choose_my_lawn_intro.btnNext
import kotlinx.android.synthetic.main.fragment_choose_my_lawn_intro.chkDontask
import kotlinx.android.synthetic.main.fragment_choose_my_lawn_intro.txtHeaderLabel
import kotlinx.android.synthetic.main.header.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [ChooseMyLawnIntroFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChooseMyLawnIntroFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private var categoryIds: String = ""
    private var title: String = ""
    private var fromRelated: Boolean = false
    private var isShowDoNotPage: Boolean = false

    override fun getContentResource(): Int = R.layout.fragment_choose_my_lawn_intro

    override fun viewModelSetup() {
    }

    override fun viewSetup() {

        arguments?.let {
            if (it.containsKey("categoryIds")) {
                categoryIds = it.getString("categoryIds").toString()
                title = it.getString("title").toString()
                fromRelated = it.getBoolean("fromRelated")
            }
            if (it.containsKey("isShowDoNotPage")){
                isShowDoNotPage = it.getBoolean("isShowDoNotPage")
            }
            arguments = null
        }

        if (activity is HomeActivity) {
            (activity as HomeActivity).hideShowFooter(true)
            (activity as HomeActivity).showCartDetails(imgCart, txtCartProducts, false)
        }
        eventListener()

        if (categoryIds!= ""){
            setDataOfSecondPage()
        }
    }

    private fun eventListener() {
        imgCart.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        btnNext.setOnClickListener(this)
        btnYesPlease.setOnClickListener(this)
        btnNoThanks.setOnClickListener(this)

        chkDontask.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                sharedPrefs.isHelpChooseTurf = isChecked
            }
        })
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
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
            R.id.btnNext ->{
                if (isShowDoNotPage) {
                    setDataOfSecondPage()
                }else{
                    openChooseMyLawnQuestions()
                }
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
            R.id.btnYesPlease ->{
                if (sharedPrefs.isHelpChooseTurf){
                    requireActivity().supportFragmentManager.popBackStack()
                }
              openChooseMyLawnQuestions()
            }
            R.id.btnNoThanks ->{
                if (categoryIds != ""){
                    if (sharedPrefs.isHelpChooseTurf){
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                    val bundle = Bundle()
                    bundle.putString("categoryIds", categoryIds)
                    bundle.putString("title",title)
                    bundle.putBoolean("fromRelated", fromRelated)
                    val productsFragment =
                        ProductsFragment().apply { arguments = bundle }
                    (requireActivity() as AppCompatActivity).hideKeyboard()
//                    (requireActivity() as AppCompatActivity).onBackPressed()
                    (requireActivity() as AppCompatActivity).pushFragment(
                        productsFragment,
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )

                }else {
                    (requireActivity() as AppCompatActivity).pushFragment(
                        HomeFragment(),
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                }
            }

        }
    }

    private fun openChooseMyLawnQuestions() {
        (requireActivity() as AppCompatActivity).pushFragment(
            ChooseMyLawnQuestionFragment(),
            true,
            true,
            false,
            R.id.flContainerHome
        )
    }

    private fun setDataOfSecondPage() {
        if (categoryIds != ""){
            ivGuide.setImageResource(R.drawable.ic_onboarding___retail___choosing_my_lawn)
        }
        btnNext.visibility = View.GONE
        txtHeaderLabel.text = getString(R.string.want_help_choosing_turf)
        tvDescription.text = getString(R.string.we_have_built_a_customised_tool_to_help)
        btnNoThanks.visibility = View.VISIBLE
        btnYesPlease.visibility = View.VISIBLE
        chkDontask.visibility = View.VISIBLE
    }
}