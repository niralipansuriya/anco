package com.app.ancoturf.presentation.home.order

import android.app.Dialog
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.order.remote.entity.response.OrderDataResponse
import com.app.ancoturf.data.order.remote.entity.response.OrderDetailResponse
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.openLogoutAlertDialog
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.cart.CartViewModel
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.home.order.adapters.QuickOrderAdapter
import com.app.ancoturf.presentation.home.order.adapters.ReorderProductsAdapter
import com.app.ancoturf.presentation.home.order.filter.OrderFilterFragment
import com.app.ancoturf.presentation.home.order.interfaces.OnOrderButtonsClickedListener
import com.app.ancoturf.presentation.notification.NotificationFragment
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Utility
import kotlinx.android.synthetic.main.fragment_quick_order.*
import kotlinx.android.synthetic.main.header.*
import javax.inject.Inject


class QuickOrderFragment : BaseFragment(),
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var orderViewModel: OrderViewModel? = null
    private var cartViewModel: CartViewModel? = null


    var orderAdapter: QuickOrderAdapter? = null
    var orders: ArrayList<OrderDetailResponse> = ArrayList()

    var productCategories = ArrayList<OrderDataResponse.Product>()

    var showAll: Boolean = false

    var addToCartProduct: ProductDetailResponse? = null

    private var isBack = false
    var pageNo = 1
    private var isLoad = false
    var itemCount = 0

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    var checkedProduct = ArrayList<OrderDataResponse.Product>()

    override fun getContentResource(): Int = R.layout.fragment_quick_order
    override fun viewModelSetup() {
        orderViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[OrderViewModel::class.java]

        cartViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[CartViewModel::class.java]

        initObservers()
    }

    override fun viewSetup() {
        if(activity is HomeActivity) {
            (activity as HomeActivity).hideShowFooter(true)
            (activity as HomeActivity).showCartDetails(imgCart, txtCartProducts, false)
        } else if(activity is ProfileActivity) {
            (activity as ProfileActivity).hideShowFooter(true)
            (activity as ProfileActivity).showCartDetails(imgCart, txtCartProducts, false)
        } else if(activity is PaymentActivity) {
            (activity as PaymentActivity).hideShowFooter(true)
            (activity as PaymentActivity).showCartDetails(imgCart, txtCartProducts, false)
        }

        Utility.showProgress(requireContext(), "", false)
        listOrder.visibility = View.GONE
        if(activity is HomeActivity) {
            (activity as HomeActivity).showCartDetails(imgCart, txtCartProducts, false)
        } else if(activity is PaymentActivity) {
            (activity as PaymentActivity).showCartDetails(imgCart, txtCartProducts, false)
        } else if(activity is ProfileActivity) {
            (activity as ProfileActivity).showCartDetails(imgCart, txtCartProducts, false)
        }

        Utility.showProgress(requireContext(), "", false)
        listOrder.visibility = View.GONE
        orderViewModel?.callGetOrders()
        imgFilter.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
        txtShowAllOrder.setOnClickListener(this)

       /* if (orderViewModel?.numberOfFilters != 0) {
            txtNumberOfFilters.visibility = View.VISIBLE
            txtNumberOfFilters.text = "" + orderViewModel?.numberOfFilters
            txtClearFilters.visibility = View.VISIBLE
            txtClearFilters.setOnClickListener(this)

        } else {
            txtNumberOfFilters.visibility = View.GONE
            txtClearFilters.visibility = View.GONE
        }*/

        if (isBack)
            setAdapter()
        if (showAll)
            getOrderWithPaging()
        if (orderViewModel?.filterAvailable!!)
            orders.clear()
    }

    companion object {
        fun newInstance() = OrderFragment()
    }

    private fun initObservers() {
        orderViewModel!!.orderLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                if (orders == null)
                    orders = ArrayList()

                if (pageNo == 1) {
                    orders.addAll(it)
                    setAdapter()
                } else
                    orderAdapter?.addItems(it)
                orderViewModel!!._orderLiveData.value = null
            }
        })

        orderViewModel!!.errorLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                if (it.equals(ErrorConstants.UNAUTHORIZED_ERROR_CODE)) {
                    (requireActivity() as AppCompatActivity).openLogoutAlertDialog(sharedPrefs = sharedPrefs)
                } else {
                    it?.let { it1 -> shortToast(it1) }
                    if (it.equals("No order found")) {
                        if (orders == null)
                            orders = ArrayList()
                        orders.clear()
                        setAdapter()
                    }
                }
                orderViewModel?._errorLiveData?.value = null
            }
        })

        cartViewModel!!.productInsertionLiveData.observe(this, Observer {
            if (it != null && it) {
                Utility.cancelProgress()
                shortToast("Product added successfully")
                if (checkedProduct.size > 0) {
                    checkedProduct.removeAt(0)
                    if (checkedProduct.size > 0)
                        addToCartFromDialog(checkedProduct[0])
                    else {
                        isBack = true
                        (requireActivity() as AppCompatActivity).pushFragment(
                            CartFragment(),
                            true,
                            true,
                            false,
                            R.id.flContainerHome
                        )
                    }
                }
//                if (checkedProduct.size == 1 || checkedProduct.size == 0) {
//                    Log.d("trace", "true ----------======================")
//                    isBack = true
//                    (requireActivity() as AppCompatActivity).pushFragment(
//                        CartFragment(),
//                        true,
//                        true,
//                        false,
//                        R.id.flContainerHome
//                    )
//                }
                cartViewModel!!._productInsertionLiveData.value = null
            }
        })

        cartViewModel!!.productLiveData.observe(this, Observer {
            if (addToCartProduct != null) {
                if (it != null) {
                    addToCartProduct!!.qty = addToCartProduct!!.qty + it.qty
                } else {
                    sharedPrefs.totalProductsInCart = sharedPrefs.totalProductsInCart + 1
                }
                cartViewModel!!.insertProduct(addToCartProduct!!, it != null)
                addToCartProduct = null
            }
        })
    }

    private fun setAdapter() {
        if (orders != null && orders.size > 0) {
            orderAdapter = activity?.let {
                QuickOrderAdapter(
                    it as AppCompatActivity, orders,
                    object : OnOrderButtonsClickedListener {
                        override fun onClickOfReorder(orderDetailResponse: OrderDetailResponse) {
                            productCategories =
                                orderDetailResponse.products as ArrayList<OrderDataResponse.Product>

                            openReorderDialog(productCategories, orderDetailResponse.referenceNumber)
                        }
                    },
                    showAll
                )
            }!!
            listOrder.adapter = orderAdapter
            txtNoOrder.visibility = View.GONE
            listOrder.visibility = View.VISIBLE
            imgFilter.visibility = View.VISIBLE

            if (showAll) {
                txtSubTitle.text = (activity as AppCompatActivity).resources.getString(R.string.order_all)
                txtShowAllOrder.visibility = View.GONE
            } else {
                txtSubTitle.visibility = View.VISIBLE
                txtShowAllOrder.visibility = View.VISIBLE
            }
        } else {
            if (orderViewModel!!.isFilterApplied()) {
                txtNoOrder.visibility = View.VISIBLE

                listOrder.visibility = View.GONE
                imgFilter.visibility = View.VISIBLE
                txtSubTitle.visibility = View.GONE
                txtShowAllOrder.visibility = View.GONE
            } else {
                txtNoOrder.visibility = View.GONE
                listOrder.visibility = View.GONE

                imgFilter.visibility = View.GONE
                txtSubTitle.visibility = View.GONE
                txtShowAllOrder.visibility = View.GONE
            }
        }
    }

    override fun onClick(view: View?) {
        if (view == null) return

        when (view.id) {
            R.id.imgBack -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                requireActivity().supportFragmentManager.popBackStack()
            }
             R.id.imgFilter -> {
                 pageNo = 1
                 orderViewModel!!.isfilterApplied = false
                 (requireActivity() as AppCompatActivity).pushFragment(
                     OrderFilterFragment(),
                     true,
                     true,
                     false,
                     R.id.flContainerHome
                 )
             }

            R.id.txtShowAllOrder -> {
                showAll = true
                txtShowAllOrder.visibility = View.GONE
                txtSubTitle.text = (activity as AppCompatActivity).resources.getString(R.string.order_all)
                setAdapter()
                //pagination
                getOrderWithPaging()

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

            R.id.imgBell ->{
                openNotificationFragment()
            }

            R.id.imgMore -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
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
                isBack = true
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

            /*R.id.txtClearFilters -> {
                (requireActivity() as AppCompatActivity).openAlertDialogWithTwoClick(
                    title = getString(R.string.app_name),
                    message = getString(R.string.clear_filter_confirmation_message),
                    positiveButton = getString(android.R.string.ok),
                    nagetiveButton = getString(android.R.string.cancel),
                    onDialogButtonClick = object : OnDialogButtonClick {
                        override fun onPositiveButtonClick() {

                            orderViewModel?.data?.put(UseCaseConstants.ORDER_STATUS, "")
                            orderViewModel?.data?.put(UseCaseConstants.DELIVERY_STATUS, "")
                            orderViewModel?.data?.put(UseCaseConstants.PRICE_MIN, "")
                            orderViewModel?.data?.put(UseCaseConstants.PRICE_MAX, "")
                            orderViewModel?.data?.put(UseCaseConstants.ADDRESS, "")
                            orderViewModel?.data?.put(UseCaseConstants.SORT_BY, "")

                            if (orderViewModel?.orderStatusLiveData?.value != null && orderViewModel?.orderStatusLiveData?.value!!.size > 0) {
                                for (i in 0 until orderViewModel?.orderStatusLiveData?.value!!.size) {
                                    orderViewModel?.orderStatusLiveData?.value!![i].checked =
                                        false
                                }
                            }


                            pageNo = 1
                            orderViewModel?.title = getString(R.string.shop_all)
                            orderViewModel?.numberOfFilters = 0
                            orderViewModel?.filterAvailable = false


                            Utility.showProgress(requireContext(), "", false)
                            orderViewModel?.callGetOrders()

                            txtNumberOfFilters.visibility = View.GONE
                            txtClearFilters.visibility = View.GONE

                        }

                        override fun onNegativeButtonClick() {
                        }
                    })
            }*/
        }
    }

    private fun openReorderDialog(
        response: ArrayList<OrderDataResponse.Product>,
        refName: String
    ) {
        // Create custom dialog object
        val dialog = Dialog(requireContext())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_quick_reorder)
        dialog.show()

        val window = dialog.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window.setGravity(Gravity.CENTER)

        val listProducts = dialog.findViewById(R.id.listProducts) as RecyclerView
        val txtOrderRefNumber = dialog.findViewById(R.id.txtOrderRefNumber) as AppCompatTextView
        val btnCancel = dialog.findViewById(R.id.btnCancel) as AppCompatTextView
        val btnAddToCart = dialog.findViewById(R.id.btnAddToCart) as AppCompatTextView

        val imgClose = dialog.findViewById(R.id.imgClose) as ImageView

        imgClose.setOnClickListener {
            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        txtOrderRefNumber.text = "#$refName"
        val reorderProductsAdapter = ReorderProductsAdapter(
            requireActivity() as AppCompatActivity,
            response
        )
        listProducts.adapter = reorderProductsAdapter

        btnAddToCart.setOnClickListener {
            if(checkedProduct == null)
                checkedProduct = ArrayList()
            checkedProduct.clear()
            checkedProduct.addAll(reorderProductsAdapter.getCheckedProduct())
            if (checkedProduct.size > 0) {
                var isEverythingInStock = true
                for(i in 0 until checkedProduct.size) {
                    if(checkedProduct[i].inStock == 0) {
                        isEverythingInStock = false
                        shortToast(checkedProduct[i].name+" is currently not in stock")
                        break
                    }
                }
                if(isEverythingInStock) {
                    addToCartFromDialog(checkedProduct[0])
                    dialog.cancel()
                }
            } else
                shortToast("Please select at least one product")
        }
    }

    private fun addToCartFromDialog(response: OrderDataResponse.Product) {
        if (!Utility.isValueNull(response.pivot.quantity.toString()) && response.pivot.quantity > 0) {
            var productDetailResponse = ProductDetailResponse(
                featureImageUrl = response.featureImageUrl,
                price = response.price.toFloat(),
                id = response.pivot.productId.toInt(),
                inStock = response.inStock.toInt(),
                minimumQuantity = response.minimumQuantity.toInt(),
                productCategoryId = response.productCategoryId.toInt(),
                productName = response.name,
                productUnit = response.productUnit,
                productUnitId = response.productUnitId.toInt(),
                qty = response.pivot.quantity
            )
            addToCartProduct = productDetailResponse
            Utility.showProgress(requireContext() , "" , false)
            cartViewModel?.getProductById(productDetailResponse.id)
        } else {
            shortToast("Product quantity should be greater than 0")
        }
//        shortToast("Product added to cart")
    }


    private fun getOrderWithPaging() {
        listOrder.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(@NonNull recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                if (!isLoad) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == orders.size - 1) {
                        //bottom of list!
                        pageNo++
                        if (orderViewModel?._isNextPageUrl?.value != null)
                            if (orderViewModel?._isNextPageUrl?.value!!)
                                orderViewModel?.callGetAllOrdersWithPaging("" + pageNo)!!

                        Handler().postDelayed({
                            isLoad = !orderViewModel!!._isNextPageUrl.value!!
                        }, 500)

                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        orderViewModel?.data?.put(UseCaseConstants.ORDER_STATUS, "")
        orderViewModel?.data?.put(UseCaseConstants.DELIVERY_STATUS, "")
        orderViewModel?.data?.put(UseCaseConstants.PRICE_MIN, "")
        orderViewModel?.data?.put(UseCaseConstants.PRICE_MAX, "")
        orderViewModel?.data?.put(UseCaseConstants.ADDRESS, "")
        orderViewModel?.data?.put(UseCaseConstants.SORT_BY, "")
        orderViewModel?.data?.put(UseCaseConstants.PER_PAGE, "")
        //orderViewModel?.filterAvailable = false
        //orderViewModel?.numberOfFilters = 0
    }

}
