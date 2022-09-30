package com.app.ancoturf.utils.customeview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.presentation.contact.ContactActivity
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.login.LoginActivity
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import kotlinx.android.synthetic.main.layout_footer.view.*


class FooterView : LinearLayout, View.OnClickListener {


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    private var activity: Activity? = null
    private var selectedTab: String? = null
    private var needToRecreateActivity: Boolean = false
    private var sharedPrefs = SharedPrefs(context)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun initFooter(activity: Activity?, selectedTab: String?) {
        this.activity = activity
        this.selectedTab = selectedTab
        inflateFooter()
//        this.layoutParams.height = getNavigationBarHeight(activity , Configuration.ORIENTATION_PORTRAIT)
    }

    private fun getNavigationBarHeight(context: Activity?, orientation: Int): Int {
        val resources = context?.resources

        val id = resources?.getIdentifier(
            if (orientation == Configuration.ORIENTATION_PORTRAIT) "navigation_bar_height" else "navigation_bar_height_landscape",
            "dimen",
            "android"
        )
        if (id != null) {
            return if (id > 0) {
                resources.getDimensionPixelSize(id)
            } else 0
        } else
            return 0
    }

    /*
    * Inflating custom view and setting as footer
    * */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun inflateFooter() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.layout_footer, this)

        txtHome.setOnClickListener(this)
        imgHome.setOnClickListener(this)
        txtPayments.setOnClickListener(this)
        imgPayments.setOnClickListener(this)
        txtContacts.setOnClickListener(this)
        imgContacts.setOnClickListener(this)
        txtProfile.setOnClickListener(this)
        imgProfile.setOnClickListener(this)
        linHome.setOnClickListener(this)
        linPayments.setOnClickListener(this)
        linContacts.setOnClickListener(this)
        linProfile.setOnClickListener(this)

        setDefaultTab()
        when (activity) {
            is HomeActivity -> {
                txtHome.setTextColor(this.activity?.let {
                    ContextCompat.getColor(
                        it,
                        android.R.color.black
                    )
                }!!)
//                imgDiscover.setImagePlaceHolder(R.drawable.ic_selected_plane)
                imgHome.imageTintList =
                    ColorStateList.valueOf(this.activity?.let {
                        ContextCompat.getColor(
                            it,
                            R.color.theme_green
                        )
                    }!!)
            }
            is PaymentActivity -> {
                txtPayments.setTextColor(this.activity?.let {
                    ContextCompat.getColor(
                        it,
                        android.R.color.black
                    )
                }!!)
//                imgAlerts.setImagePlaceHolder(R.drawable.ic_alerts_h)
                imgPayments.imageTintList =
                    ColorStateList.valueOf(this.activity?.let {
                        ContextCompat.getColor(
                            it,
                            R.color.theme_green
                        )
                    }!!)
            }
            is ContactActivity -> {//selectedTab.equals(AccountFragmentActivity.class.getSimpleName())
                txtContacts.setTextColor(this.activity?.let {
                    ContextCompat.getColor(
                        it,
                        android.R.color.black
                    )
                }!!)
//                imgMyTrip.setImagePlaceHolder(R.drawable.ic_my_trips_h)
                imgContacts.imageTintList =
                    ColorStateList.valueOf(this.activity?.let {
                        ContextCompat.getColor(
                            it,
                            R.color.theme_green
                        )
                    }!!)
            }
            is ProfileActivity -> {
                txtProfile.setTextColor(this.activity?.let {
                    ContextCompat.getColor(
                        it,
                        android.R.color.black
                    )
                }!!)
//                imgAccount.setImagePlaceHolder(R.drawable.ic_account_h)
                imgProfile.imageTintList =
                    ColorStateList.valueOf(this.activity?.let {
                        ContextCompat.getColor(
                            it,
                            R.color.theme_green
                        )
                    }!!)
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public fun setDefaultTab() {
        if (sharedPrefs.userType == Constants.RETAILER || sharedPrefs.userType == Constants.GUEST_USER)
            linPayments.visibility = View.GONE


        if (sharedPrefs.userType == Constants.GUEST_USER)
            txtProfile.text  =  "Login"


        txtHome.setTextColor(this.activity?.let {
            ContextCompat.getColor(
                it,
                android.R.color.black
            )
        }!!)
        txtPayments.setTextColor(this.activity?.let {
            ContextCompat.getColor(
                it,
                android.R.color.black
            )
        }!!)
        txtContacts.setTextColor(this.activity?.let {
            ContextCompat.getColor(
                it,
                android.R.color.black
            )
        }!!)
        txtProfile.setTextColor(this.activity?.let {
            ContextCompat.getColor(
                it,
                android.R.color.black
            )
        }!!)

        imgHome.imageTintList =
            ColorStateList.valueOf(this.activity?.let {
                ContextCompat.getColor(
                    it,
                    R.color.grey
                )
            }!!)
        imgPayments.imageTintList =
            ColorStateList.valueOf(this.activity?.let {
                ContextCompat.getColor(
                    it,
                    R.color.grey
                )
            }!!)
        imgContacts.imageTintList =
            ColorStateList.valueOf(this.activity?.let {
                ContextCompat.getColor(
                    it,
                    R.color.grey
                )
            }!!)
        imgProfile.imageTintList =
            ColorStateList.valueOf(this.activity?.let {
                ContextCompat.getColor(
                    it,
                    R.color.grey
                )
            }!!)

//        imgDiscover.setImagePlaceHolder(R.drawable.ic_deselected_plane)
//        imgAlerts.setImagePlaceHolder(R.drawable.ic_alerts)
//        imgMyTrip.setImagePlaceHolder(R.drawable.ic_my_trips)
//        imgAccount.setImagePlaceHolder(R.drawable.ic_account)
    }

    override fun onClick(view: View?) {
        if (view == null) return
        when {
            view.id == R.id.txtHome || view.id == R.id.imgHome || view.id == R.id.linHome -> {
                startHomeActivity()
            }
            view.id == R.id.txtPayments || view.id == R.id.imgPayments || view.id == R.id.linPayments -> {
                startPaymentActivity()
            }
            view.id == R.id.txtContacts || view.id == R.id.imgContacts || view.id == R.id.linContacts -> {
                startContactActivity()
            }
            view.id == R.id.txtProfile || view.id == R.id.imgProfile || view.id == R.id.linProfile -> {
                if (!sharedPrefs?.isLogged) {
                    var loginIntent = Intent(activity, LoginActivity::class.java)
                    activity?.startActivity(loginIntent)
                    activity?.finishAffinity()
                } else
                    startProfileActivity()
            }
        }
    }

    private fun startHomeActivity() {
        startActivity(HomeActivity::class.java, isNeedToRecreateActivity())

    }

    private fun startPaymentActivity() {
        startActivity(PaymentActivity::class.java, isNeedToRecreateActivity())
    }

    private fun startContactActivity() {
        startActivity(ContactActivity::class.java, isNeedToRecreateActivity())
    }

    private fun startProfileActivity() {
        startActivity(ProfileActivity::class.java, isNeedToRecreateActivity())
    }

    public fun startActivity(className: Class<*>, reCreate: Boolean) {
//        AppLogger.d("reCreate", " :: $reCreate")
        val intent = Intent(activity, className)
        if (!reCreate) {
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        } else {
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        activity?.startActivity(intent)
    }

    private fun isNeedToRecreateActivity(): Boolean {
        return needToRecreateActivity
    }

    fun setNeedToRecreateActivity(needToRecreateActivity: Boolean) {
        this.needToRecreateActivity = needToRecreateActivity
    }
}