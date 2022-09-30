package com.app.ancoturf.presentation.guideline

import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
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
import com.app.ancoturf.presentation.home.portfolio.PortfoliosFragment
import com.app.ancoturf.presentation.home.quote.QuotesFragment
import com.app.ancoturf.presentation.notification.NotificationFragment
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import kotlinx.android.synthetic.main.fragment_guideline.*
import kotlinx.android.synthetic.main.header.*
import javax.inject.Inject

class GuidelineFragment : BaseFragment(), View.OnClickListener {

    private var guideScreen: Int? = 0 //0 : Portfolio screen and 1 : Quote Screen
    private var guideDataList: ArrayList<GuideModel>? = ArrayList()
    private var adapter: GuidelinePagerAdapter? = null

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun getContentResource(): Int = R.layout.fragment_guideline

    override fun viewModelSetup() {

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
            guideScreen = it.getInt("guideScreen")
            arguments = null
        }
        if (guideScreen == 1) {
            txtHeaderLabel.text = getString(R.string.quote_title)
        }
        setViewPager()
        eventListener()
    }

    private fun eventListener() {
        btnGetStarted.setOnClickListener(this)
        btnNext.setOnClickListener(this)
        btnSkip.setOnClickListener(this)
        chkDontask.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (guideScreen == 0) {
                    sharedPrefs.checkUncheckGuideScreenDontShow = isChecked
                } else {
                    sharedPrefs.checkUncheckGuideScreenDontShowQuote = isChecked
                }
            }
        })
        imgMore.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
    }

    private fun setViewPager() {
        setViewPagerData()
        if (adapter == null) {
            adapter = GuidelinePagerAdapter(requireActivity() as AppCompatActivity, guideDataList!!,false)
            viewPager.adapter = adapter
            pageIndicatorViewModen.setViewPager(viewPager)
        }
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == (guideDataList?.size?.minus(1))) {
                    showHideButtons(true)
                } else {
                    showHideButtons(false)
                }
            }
        })
    }

    private fun showHideButtons(isShowGetStarted: Boolean) {
        btnNext.visibility = if (isShowGetStarted) View.GONE else View.VISIBLE
        btnSkip.visibility = if (isShowGetStarted) View.GONE else View.VISIBLE
        btnGetStarted.visibility = if (!isShowGetStarted) View.GONE else View.VISIBLE
    }

    private fun setViewPagerData() {
        if (guideScreen == 0) {
            guideDataList?.clear()
            guideDataList?.add(
                GuideModel(
                    R.drawable.anco___onboarding___portfolio_01,
                    getString(R.string.guide_desc_portfolio_1)
                )
            )
            guideDataList?.add(
                GuideModel(
                    R.drawable.anco___onboarding___portfolio_02,
                    getString(R.string.guide_desc_portfolio_2)
                )
            )
        } else {
            guideDataList?.clear()
            guideDataList?.add(
                GuideModel(
                    R.drawable.anco___onboarding___quote_01,
                    getString(R.string.guide_desc_quote_1)
                )
            )
            guideDataList?.add(
                GuideModel(
                    R.drawable.anco___onboarding___quote_02,
                    getString(R.string.guide_desc_quote_2)
                )
            )
            guideDataList?.add(
                GuideModel(
                    R.drawable.anco___onboarding___quote_03_3x,
                    getString(R.string.guide_desc_quote_3)
                )
            )
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSkip -> {
                requireActivity().onBackPressed()
                if (guideScreen == 0) {
                    (requireActivity() as AppCompatActivity).pushFragment(
                        PortfoliosFragment(),
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                } else {
                    (requireActivity() as AppCompatActivity).pushFragment(
                        QuotesFragment(),
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                }
            }
            R.id.btnNext -> {
                viewPager.currentItem = viewPager.currentItem + 1
            }
            R.id.btnGetStarted -> {
                requireActivity().onBackPressed()
                if (guideScreen == 0) {
                    (requireActivity() as AppCompatActivity).pushFragment(
                        PortfoliosFragment(),
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                } else {
                    (requireActivity() as AppCompatActivity).pushFragment(
                        QuotesFragment(),
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                }
            }
            R.id.imgLogo -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
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
                    false,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.imgBell -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    NotificationFragment(),
                    false,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.imgMore -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                (requireActivity() as AppCompatActivity).pushFragment(
                    ContactFragment(),
                    false,
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
                        false,
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