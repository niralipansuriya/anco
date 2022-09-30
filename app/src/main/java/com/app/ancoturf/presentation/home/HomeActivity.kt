package com.app.ancoturf.presentation.home

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.cart.CartViewModel
import com.app.ancoturf.presentation.chooseingMyLawn.ChooseMyLawnIntroFragment
import com.app.ancoturf.presentation.common.base.BaseActivity
import com.app.ancoturf.presentation.home.order.OrderDetailsFragment
import com.app.ancoturf.presentation.home.portfolio.AddEditPortfolioFragment
import com.app.ancoturf.presentation.home.shop.ShopFragment
import com.app.ancoturf.presentation.home.tracking.TrackingFragment
import com.app.ancoturf.presentation.manageLawn.ManageLawnFragment
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.utils.Logger
import kotlinx.android.synthetic.main.activity_home.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.inject.Inject


class HomeActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var isOpenFirstTime: Boolean = false

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    var cartViewModel: CartViewModel? = null
    var mAppHeight: Int = 0

    var currentOrientation = -1

    //    var broadcastReceiver: BroadcastReceiver? = null
    override fun getContentResource(): Int = R.layout.activity_home


    override fun viewModelSetup() {
        cartViewModel = ViewModelProviders.of(this, viewModelFactory)[CartViewModel::class.java]
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun viewSetup() {
      /*  try {
            val info: PackageInfo = packageManager.getPackageInfo(
                "com.facebook.samples.hellofacebook",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Logger.log("KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }*/

        try {
            val info = packageManager.getPackageInfo(
                "com.app.ancoturf",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Logger.log("KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT))

//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        /*  val filter = IntentFilter()
          filter.addAction(Constants.GUEST_USER_TO_LOGIN_VIA_CHECKOUT_ACTION)
          registerReceiver(broadcastReceiver,filter)*/
        layoutFooterView.initFooter(this, HomeActivity::class.java.simpleName)
        isNeedToRecreateActivity(false)
        when {
            intent?.hasExtra(Constants.
            KEY_DEEP_LINKING)!! -> {
                Logger.log("nnn KEY_DEEP_LINKING homeActivity")

                pushFragment(
                    HomeFragment().apply { arguments = intent.extras },
                    false,
                    true,
                    false,
                    R.id.flContainerHome
                )
                intent = null
            }
            intent.hasExtra(Constants.CHOOSE_MY_LAWN) ->{
                var bundle = Bundle()
                bundle.putBoolean("isShowDoNotPage",false)
                var chooseMyLawnIntroFragment =     ChooseMyLawnIntroFragment()
                chooseMyLawnIntroFragment.arguments = bundle
                pushFragment(
                    chooseMyLawnIntroFragment,
                    false,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }intent.hasExtra(Constants.SHOP_FRAGMENT) ->{
                pushFragment(
                    ShopFragment(),
                    false,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }
            intent.hasExtra(Constants.MANAGE_MY_LAWN) ->{
                pushFragment(
                    ManageLawnFragment(),
                    false,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }
            intent.hasExtra(Constants.TRACKING_FRAGMENT) -> {
                if (sharedPrefs.isLogged) {
                    pushFragment(
                        TrackingFragment(),
                        false,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                } else {
                    pushFragment(
                        OrderDetailsFragment(null, 0),
                        false,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                }
            }
            intent.hasExtra(Constants.ORDER_STATUS_CHANGED) -> {
                pushFragment(
                    HomeFragment().apply { arguments = intent.extras },
                    false,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }
            intent.hasExtra(Constants.FROM_GUEST_USER_REGISTRATION) -> {
                if (sharedPrefs.totalProductsInCart > 0) {
                    pushFragment(
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
            intent.hasExtra(Constants.PAYMENT_DUE_TYPE) -> {
                layoutFooterView.startActivity(PaymentActivity::class.java, false)
                finishAffinity()
            }
            else -> {
                pushFragment(
                    HomeFragment(),
                    false,
                    true,
                    false,
                    R.id.flContainerHome
                )


                /* if (sharedPrefs.isHelpChooseTurf || sharedPrefs.isHomeOpen) {
                     pushFragment(
                         HomeFragment(),
                         false,
                         true,
                         false,
                         R.id.flContainerHome
                     )

                 } else {
                     pushFragment(
                         ChooseMyLawnIntroFragment(),
                         false,
                         true,
                         false,
                         R.id.flContainerHome
                     )
                 }
                 sharedPrefs.isHomeOpen = true*/
            }
        }
        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        rootLayout.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            rootLayout.getWindowVisibleDisplayFrame(r)
            val screenHeight = rootLayout.rootView.height
            val keypadHeight = screenHeight - r.bottom
            if (keypadHeight > screenHeight * 0.15) {
//                Toast.makeText(this, "Keyboard is showing", Toast.LENGTH_LONG).show()
                layoutFooterView.visibility = View.GONE
            } else {
//                Toast.makeText(this, "keyboard closed", Toast.LENGTH_LONG).show()
                layoutFooterView.visibility = View.VISIBLE
            }
        }

        /* broadcastReceiver = object : BroadcastReceiver(){
             override fun onReceive(context: Context?, intent: Intent?) {
                 Logger.log("nnn : In broadcastReceiver")
                 when (intent?.action) {
                     Constants.GUEST_USER_TO_LOGIN_VIA_CHECKOUT_ACTION ->{
                         Logger.log("nnn :Bottom Tab updated")
                         layoutFooterView.setDefaultTab()
                     }
                 }
             }
         }
 */
        /* if (savedInstanceState?.getInt("portfolioMode") != null) {
             savedInstanceState=null
             pushFragment(
                 AddEditPortfolioFragment().apply { arguments = savedInstanceState },
                 true,
                 true,
                 false,
                 R.id.flContainerHome
             )
         }*/
    }


    public fun updateBottomTab() {
        Logger.log("nnn :Bottom Tab updated")
        layoutFooterView.setDefaultTab()
    }

    private fun isNeedToRecreateActivity(isNeedToRecreateActivity: Boolean) {
        if (layoutFooterView != null) {
            layoutFooterView.setNeedToRecreateActivity(isNeedToRecreateActivity)
        }
    }

    fun hideShowFooter(shouldVisible: Boolean) {
        layoutFooterView.visibility = if (shouldVisible) View.VISIBLE else View.GONE
    }

    fun showCartDetails(imgCart: ImageView, txtCartProducts: TextView, inCart: Boolean) {
//        if (inCart) {
        if (sharedPrefs.totalProductsInCart > 0) {
            imgCart.setColorFilter(ContextCompat.getColor(this, R.color.theme_green))
            txtCartProducts.visibility = View.VISIBLE
            txtCartProducts.text = "${sharedPrefs.totalProductsInCart}"
        } else {
            imgCart.setColorFilter(null)
            txtCartProducts.visibility = View.GONE
            sharedPrefs.quoteID = ""
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Logger.log("nnn: Request above:" + requestCode)

        //65637
        super.onActivityResult(requestCode, resultCode, data)
        Logger.log("nnn: Request:" + requestCode)
        if (requestCode == 101) {
            Logger.log("Got the checkout")
        }
    }

    /*override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(broadcastReceiver)
        }catch (e : Exception){
            e.printStackTrace()
        }
    }*/

    override fun onConfigurationChanged(newConfig: Configuration) {
        //super.onConfigurationChanged(newConfig)
        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            shortToast("keyboard visible")
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            shortToast("keyboard hidden")
        }
    }


    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.flContainerHome)
        if (currentFragment != null && currentFragment is AddEditPortfolioFragment) {
            (currentFragment as AddEditPortfolioFragment).onBack()
        } else {
            super.onBackPressed()
        }
    }
}
