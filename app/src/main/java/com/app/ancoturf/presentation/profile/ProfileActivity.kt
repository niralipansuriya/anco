package com.app.ancoturf.presentation.profile

import android.content.Intent
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.presentation.cart.CartViewModel
import com.app.ancoturf.presentation.common.base.BaseActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.home.portfolio.AddEditPortfolioFragment
import com.app.ancoturf.presentation.login.LoginActivity
import com.app.ancoturf.utils.AndroidBug5497Workaround
import com.app.ancoturf.utils.Utility
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.layoutFooterView
import kotlinx.android.synthetic.main.header.*
import javax.inject.Inject

class ProfileActivity : BaseActivity() {

    @Inject
    lateinit var sharedPrefs :SharedPrefs

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var cartViewModel: CartViewModel? = null

    override fun getContentResource(): Int = R.layout.activity_profile

    override fun viewModelSetup() {
        cartViewModel = ViewModelProviders.of(this , viewModelFactory)[CartViewModel::class.java]
    }

    override fun viewSetup() {
        layoutFooterView.initFooter(this, ProfileActivity::class.java.simpleName)
//
//        imgCart.setColorFilter(null)
//        txtCartProducts.visibility = View.GONE

        isNeedToRecreateActivity(false)
        pushFragment(
            ProfileFragment(),
            false,
            true,
            false,
            R.id.flContainerHome
        )

        txtVerson.text = Utility.getVersion(this)

        rootLayout.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            rootLayout.getWindowVisibleDisplayFrame(r)
            val screenHeight = rootLayout.getRootView().getHeight()
            val keypadHeight = screenHeight - r.bottom
            if (keypadHeight > screenHeight * 0.15) {
//                Toast.makeText(this, "Keyboard is showing", Toast.LENGTH_LONG).show()
                layoutFooterView.visibility = View.GONE
            } else {
//                Toast.makeText(this, "keyboard closed", Toast.LENGTH_LONG).show()
                layoutFooterView.visibility = View.VISIBLE
            }
        }
    }

    /***
     * @param isNeedToRecreateActivity = need to recreate activity or not
     */
    private fun isNeedToRecreateActivity(isNeedToRecreateActivity: Boolean) {
        if (layoutFooterView != null) {
            layoutFooterView.setNeedToRecreateActivity(isNeedToRecreateActivity)
        }
    }

    fun hideShowFooter(shouldVisible: Boolean) {
        layoutFooterView.visibility = if (shouldVisible) View.VISIBLE else View.GONE
    }

    fun showCartDetails(imgCart: ImageView, txtCartProducts: TextView, inCart: Boolean) {
        // if (inCart) {
        if(sharedPrefs.totalProductsInCart > 0) {
            imgCart.setColorFilter(ContextCompat.getColor(this, R.color.theme_green))
            txtCartProducts.visibility = View.VISIBLE
            txtCartProducts.text = "${sharedPrefs.totalProductsInCart}"
        } else {
            imgCart.setColorFilter(null)
            txtCartProducts.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.flContainerHome)
        if(currentFragment != null && currentFragment is AddEditPortfolioFragment) {
            (currentFragment as AddEditPortfolioFragment).onBack()
        } else {
            super.onBackPressed()
        }
    }
}
