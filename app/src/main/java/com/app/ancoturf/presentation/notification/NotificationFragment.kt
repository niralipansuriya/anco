package com.app.ancoturf.presentation.notification

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.app.ancoturf.R
import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.notification.remote.entity.response.NotificationDataResponse
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.notification.adapter.NotificationAdapter
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.payment.adapter.BillHistoryAdapter
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Utility
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.header.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class NotificationFragment : BaseFragment(), View.OnClickListener{

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var notificationViewModel: NotificationViewModel? = null
    private var notificationList : ArrayList<NotificationDataResponse.Data> = ArrayList()
    private var isLoad = false
    var pageNo = 1
    private var notificationAdapter: NotificationAdapter? = null

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun getContentResource(): Int {
      return  R.layout.fragment_notification
    }

    override fun viewModelSetup() {
        notificationViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)[NotificationViewModel::class.java]

        initObservers()
        Utility.showProgress(context = requireContext(), message = "", cancellable = false)
        notificationViewModel!!.callGetNotification("")
        getNotificationByPaging()
    }

    private fun getNotificationByPaging() {
        listNotification.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(@NonNull recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoad) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == notificationList.size - 1) {
                        //bottom of list!
                        pageNo++
                        if (notificationViewModel?._isNextPageUrl?.value != null)
                            if (notificationViewModel?._isNextPageUrl?.value!!)
                                notificationViewModel?.callGetNotification("$pageNo")!!

                        Handler().postDelayed({
                            isLoad = !notificationViewModel!!._isNextPageUrl.value!!
                        }, 500)

                    }
                }
            }
        })
    }

    private fun initObservers() {
        notificationViewModel!!._notificationLiveData.observe(this, Observer {
            if (it != null) {
                Log.e("resp", it.toString())
                if (pageNo == 1) {
                    notificationList.clear()
                    notificationList.addAll(it.data)
                    setAdapter()
                } else
                    notificationAdapter?.addItems(it.data)

            } else {
                notificationList.clear()
                setAdapter()
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        pageNo = 1
        notificationViewModel!!._notificationLiveData.value = null
    }

    private fun setAdapter() {
        Utility.cancelProgress()
        if (notificationList.size > 0) {
            notificationAdapter = activity?.let {
                NotificationAdapter(
                    it as AppCompatActivity,
                    notificationList
                )
            }!!
            listNotification.adapter = notificationAdapter
            listNotification.visibility = View.VISIBLE
            txtNoNotification.visibility = View.GONE
        } else {
            listNotification.visibility = View.GONE
            txtNoNotification.visibility = View.VISIBLE
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

        imgLogo.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgBack.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onClick(view: View?) {
        if (view == null) return
        when (view.id) {
            R.id.imgBack -> {
                requireActivity().supportFragmentManager.popBackStack()
            }
            R.id.imgLogo -> {
                requireActivity().startActivity(Intent(requireActivity(), HomeActivity::class.java))
                requireActivity().finishAffinity()
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
}
