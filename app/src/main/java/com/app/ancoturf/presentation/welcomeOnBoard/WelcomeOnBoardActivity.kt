package com.app.ancoturf.presentation.welcomeOnBoard

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.CompoundButton
import androidx.viewpager.widget.ViewPager
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.presentation.common.base.BaseActivity
import com.app.ancoturf.presentation.guideline.GuideModel
import com.app.ancoturf.presentation.guideline.GuidelinePagerAdapter
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.order.OrderDetailsFragment
import com.app.ancoturf.presentation.home.tracking.TrackingFragment
import com.app.ancoturf.presentation.login.LoginActivity
import com.app.ancoturf.presentation.signup.SignUpActivity
import kotlinx.android.synthetic.main.activity_welcome_on_board.*
import javax.inject.Inject

class WelcomeOnBoardActivity : BaseActivity(), View.OnClickListener {

    private var adapter: GuidelinePagerAdapter? = null
    private var guideDataList: ArrayList<GuideModel>? = ArrayList()
    var context : Context? = null


    @Inject
    lateinit var sharedPrefs: SharedPrefs


    override fun getContentResource(): Int = R.layout.activity_welcome_on_board

    override fun viewModelSetup() {
    }

    override fun viewSetup() {
        context = this
        if (!sharedPrefs.isLogged) {
            sharedPrefs.userType = 1
        }
        eventListener()
        setViewPager()
    }


    private fun eventListener() {
        btnNext.setOnClickListener(this)
        chkDontask.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                sharedPrefs.isHowCanWeHelp = isChecked
            }
        })
    }

    private fun setHowCanWeHelpVisible(isVisible: Boolean) {
        btnNext.visibility = if (isVisible) View.GONE else View.VISIBLE
        pageIndicatorViewModen.visibility = if (isVisible) View.GONE else View.VISIBLE
        chkDontask.visibility = if (isVisible) View.VISIBLE else View.GONE
        txtHeaderLabel.text =
            if (isVisible) getString(R.string.how_can_we_help_nyou_today) else getString(R.string.welcome_to_the_nanco_app)
    }


    private fun setViewPager() {
        setViewPagerData()
        if (adapter == null) {
            adapter = GuidelinePagerAdapter(this, guideDataList!!,true)
            viewPager.adapter = adapter
            pageIndicatorViewModen.setViewPager(viewPager)
            adapter?.setIOnBoardOptions(object : IOnBoardOptions{
                override fun getChooseMyLawnClick() {
                    var i = Intent(context, HomeActivity::class.java)
                    i.putExtra(Constants.CHOOSE_MY_LAWN,Constants.CHOOSE_MY_LAWN)
                    startActivity(i)
//                    finish()
                }

                override fun getManagingMyLawnClick() {
                    var i = Intent(context, HomeActivity::class.java)
                    i.putExtra(Constants.MANAGE_MY_LAWN,Constants.MANAGE_MY_LAWN)
                    startActivity(i)
//                    finish()
                }

                override fun getNewTradeCustomerClick() {
                    var intent = Intent(context, SignUpActivity::class.java)
                    intent.putExtra(Constants.IS_SIGN_UP_LANDSCAPER,true)
                    startActivity(intent)
//                    finish()
                }

                override fun getReturningCustomerClick() {
                    var intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
//                    finish()
                }

                override fun getTrackMyDeliveryClick() {
                    var i = Intent(context, HomeActivity::class.java)
                    i.putExtra(Constants.TRACKING_FRAGMENT,Constants.TRACKING_FRAGMENT)
                    startActivity(i)
//                    finish()
                }

                override fun getTrackMyDeliveryNoLoginClick() {
                    var i = Intent(context, HomeActivity::class.java)
                    i.putExtra(Constants.TRACKING_FRAGMENT,Constants.TRACKING_FRAGMENT)
                    startActivity(i)
//                    finish()
                }

                override fun getBrowseOurShopNoLoginClick() {
                    var i = Intent(context, HomeActivity::class.java)
                    i.putExtra(Constants.SHOP_FRAGMENT,Constants.SHOP_FRAGMENT)
                    startActivity(i)
//                    finish()
                }

                override fun getBrowseOurShopClick() {
                    var i = Intent(context, HomeActivity::class.java)
                    i.putExtra(Constants.SHOP_FRAGMENT,Constants.SHOP_FRAGMENT)
                    startActivity(i)
//                    finish()
                }

            })
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
                if (position == 2) {
                    setHowCanWeHelpVisible(true)
                } else {
                    setHowCanWeHelpVisible(false)
                }
            }
        })
    }

    private fun setViewPagerData() {
        guideDataList?.clear()
        guideDataList?.add(
            GuideModel(
                R.drawable.ic_onboarding___retail___screen_1,
                getString(R.string.this_is_your_1_resource_for_all)
            )
        )
        guideDataList?.add(
            GuideModel(
                R.drawable.ic_onboarding___retail___screen_2,
                getString(R.string.our_app_allows_you_to_track)
            )
        )
        guideDataList?.add(
            GuideModel(
                R.drawable.ic_onboarding___retail___screen_3,
                ""
            )
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnNext -> {
                viewPager.currentItem = viewPager.currentItem + 1
            }

        }
    }

}