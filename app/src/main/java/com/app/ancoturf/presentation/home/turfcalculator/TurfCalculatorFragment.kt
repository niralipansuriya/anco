package com.app.ancoturf.presentation.home.turfcalculator

import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.cart.persistance.entity.ProductDB
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.portfolio.remote.entity.QuoteAncoProductRequest
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import com.app.ancoturf.data.product.remote.entity.response.ProductsResponse
import com.app.ancoturf.domain.common.ErrorConstants
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
import com.app.ancoturf.presentation.home.portfolio.interfaces.OnProductSelectedListener
import com.app.ancoturf.presentation.home.products.ProductsFragment
import com.app.ancoturf.presentation.home.quote.AddBusinessDetailsFragment
import com.app.ancoturf.presentation.home.quote.adapters.QuoteProductsAdapter
import com.app.ancoturf.presentation.notification.NotificationFragment
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Utility
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.fragment_turf_calculator.*
import kotlinx.android.synthetic.main.header.*
import java.util.*
import javax.inject.Inject

class TurfCalculatorFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private var turfCalculatorViewModel: TurfCalculatorViewModel? = null
    private var cartViewModel: CartViewModel? = null

    var quoteProductsAdapter: QuoteProductsAdapter? = null
    var turfProducts: ArrayList<ProductsResponse.Data> = ArrayList()
    var shapeFinalTotal = 0.0
    var selectedProduct: ProductsResponse.Data? = null
    var finalCost = 0.0

    var addToCartProduct: ProductDetailResponse? = null
    var isUpdate = false
    var productId : Int = 0
    var productIdQty : ArrayList<ProductDB> = ArrayList()
    private var mFirebaseAnalytics: FirebaseAnalytics? = null




    override fun getContentResource(): Int = R.layout.fragment_turf_calculator

    override fun viewModelSetup() {
        turfCalculatorViewModel = ViewModelProviders.of(
            requireActivity(),
            viewModelFactory
        )[TurfCalculatorViewModel::class.java]
        cartViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[CartViewModel::class.java]
        initObservers()
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
            productId = it.getInt("productId")
            arguments = null
        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        turfCalculatorViewModel?.callGetTurfProducts()
        setAdapter()
        Utility.showProgress(context = requireActivity(), message = "", cancellable = false)
        imgLogo.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        txtAddToCart.setOnClickListener(this)
        imgSelectShapeDropdown.setOnClickListener(this)
        layoutTurfProductLabel.setOnClickListener(this)
        layoutShapeLabel.setOnClickListener(this)
        imgChooseTurfVarietyDropdown.setOnClickListener(this)

        if (sharedPrefs.userType == Constants.LANDSCAPER)
            txtAddToQuote.visibility = View.VISIBLE
        else
            txtAddToQuote.visibility = View.GONE
        txtAddToQuote.setOnClickListener(this)
        txtFinalCost.text = Html.fromHtml(getString(R.string.final_cost, "$", "0.0"))
        addShapeLayout()
    }

    private fun addShapeLayout() {
        var selectedShape = Constants.SQUARE
        var shapeTotal = 0.0
        val shapeView = LayoutInflater.from(activity).inflate(R.layout.item_turf_shape, null)
        val imgShapeSquare: ImageView = shapeView.findViewById(R.id.imgShapeSquare)
        val txtShapeSquare: TextView = shapeView.findViewById(R.id.txtShapeSquare)
        val imgShapeRectangle: ImageView = shapeView.findViewById(R.id.imgShapeRectangle)
        val txtShapeRectangle: TextView = shapeView.findViewById(R.id.txtShapeRectangle)
        val imgShapeCircle: ImageView = shapeView.findViewById(R.id.imgShapeCircle)
        val txtShapeCircle: TextView = shapeView.findViewById(R.id.txtShapeCircle)
        val imgShapeTriangle: ImageView = shapeView.findViewById(R.id.imgShapeTriangle)
        val txtShapeTriangle: TextView = shapeView.findViewById(R.id.txtShapeTriangle)
        val txtWidthLabel: TextView = shapeView.findViewById(R.id.txtWidthLabel)
        val txtLengthLabel: TextView = shapeView.findViewById(R.id.txtLengthLabel)
        val edtWidth: TextView = shapeView.findViewById(R.id.edtWidth)
        val edtLength: TextView = shapeView.findViewById(R.id.edtLength)
        val txtCalculateShape: TextView = shapeView.findViewById(R.id.txtCalculateShape)
        val txtShapeTotal: TextView = shapeView.findViewById(R.id.txtShapeTotal)
        val txtClearShape: TextView = shapeView.findViewById(R.id.txtClearShape)

        imgShapeSquare.setColorFilter(ContextCompat.getColor(requireContext(), R.color.theme_green))
        imgShapeRectangle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
        imgShapeCircle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
        imgShapeTriangle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
        txtWidthLabel.text = getString(R.string.base_input)
        txtLengthLabel.visibility = View.VISIBLE
        txtLengthLabel.text = getString(R.string.length_input)
        txtShapeTotal.text = Html.fromHtml(
            getString(
                R.string.shape_total,
                Utility.formatNumber(Utility.roundTwoDecimals(shapeTotal).toFloat()),
                getString(R.string.meter_square)
            )
        )
        txtShapeFinalTotal.text = Html.fromHtml(
            getString(
                R.string.shape_final_total,
                Utility.formatNumber(Utility.roundTwoDecimals(shapeFinalTotal).toFloat()),
                getString(R.string.meter_square)
            )
        )

        imgShapeSquare.setOnClickListener {
            selectedShape = Constants.SQUARE
            imgShapeSquare.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.theme_green
                )
            )
            imgShapeRectangle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            imgShapeCircle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            imgShapeTriangle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
//            txtWidthLabel.text = getString(R.string.width_input) //nnn 19/11/2020
            txtWidthLabel.text = getString(R.string.base_input)
            txtLengthLabel.visibility = View.VISIBLE
            edtLength.visibility = View.VISIBLE
//            txtLengthLabel.text = getString(R.string.length_input) //nnn 19/11/2020
            txtLengthLabel.text = getString(R.string.height_input)
        }
        txtShapeSquare.setOnClickListener {
            selectedShape = Constants.SQUARE
            imgShapeSquare.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.theme_green
                )
            )
            imgShapeRectangle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            imgShapeCircle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            imgShapeTriangle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
//            txtWidthLabel.text = getString(R.string.width_input) //nnn 19/11/2020
            txtWidthLabel.text = getString(R.string.base_input)
            txtLengthLabel.visibility = View.VISIBLE
            edtLength.visibility = View.VISIBLE
//            txtLengthLabel.text = getString(R.string.length_input) //nnn 19/11/2020
            txtLengthLabel.text = getString(R.string.height_input)
        }

        imgShapeRectangle.setOnClickListener {
            selectedShape = Constants.RECTANGLE
            imgShapeSquare.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            imgShapeRectangle.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.theme_green
                )
            )
            imgShapeCircle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            imgShapeTriangle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            txtWidthLabel.text = getString(R.string.base_input)
            txtLengthLabel.visibility = View.VISIBLE
            edtLength.visibility = View.VISIBLE
            txtLengthLabel.text = getString(R.string.height_input)
        }

        txtShapeRectangle.setOnClickListener {
            selectedShape = Constants.RECTANGLE
            imgShapeSquare.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            imgShapeRectangle.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.theme_green
                )
            )
            imgShapeCircle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            imgShapeTriangle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            txtWidthLabel.text = getString(R.string.base_input)
            txtLengthLabel.visibility = View.VISIBLE
            edtLength.visibility = View.VISIBLE
            txtLengthLabel.text = getString(R.string.height_input)
        }

        imgShapeCircle.setOnClickListener {
            selectedShape = Constants.CIRCLE
            imgShapeSquare.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            imgShapeRectangle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            imgShapeCircle.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.theme_green
                )
            )
            imgShapeTriangle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            txtWidthLabel.text = getString(R.string.radius_input)
            txtLengthLabel.visibility = View.GONE
            edtLength.visibility = View.GONE
        }

        txtShapeCircle.setOnClickListener {
            selectedShape = Constants.CIRCLE
            imgShapeSquare.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            imgShapeRectangle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            imgShapeCircle.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.theme_green
                )
            )
            imgShapeTriangle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            txtWidthLabel.text = getString(R.string.radius_input)
            txtLengthLabel.visibility = View.GONE
            edtLength.visibility = View.GONE
        }

        imgShapeTriangle.setOnClickListener {
            selectedShape = Constants.TRIANGLE
            imgShapeSquare.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            imgShapeRectangle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            imgShapeCircle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            imgShapeTriangle.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.theme_green
                )
            )
            txtWidthLabel.text = getString(R.string.base_input)
            txtLengthLabel.visibility = View.VISIBLE
            edtLength.visibility = View.VISIBLE
            txtLengthLabel.text = getString(R.string.height_input)
        }

        txtShapeTriangle.setOnClickListener {
            selectedShape = Constants.TRIANGLE
            imgShapeSquare.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            imgShapeRectangle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            imgShapeCircle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            imgShapeTriangle.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.theme_green
                )
            )
            txtWidthLabel.text = getString(R.string.base_input)
            txtLengthLabel.visibility = View.VISIBLE
            txtLengthLabel.text = getString(R.string.height_input)
        }

        txtCalculateShape.setOnClickListener {
            if (selectedShape == Constants.SQUARE) {
                when {
                    Utility.isValueNull(edtWidth.text.toString()) || Utility.isValueNull(edtLength.text.toString()) -> shortToast(
                        "Please enter both the width and length of your turf"
                    )
//                    Utility.isValueNull(edtLength.text.toString()) -> shortToast("Please enter length")
                    else -> {
                        shapeFinalTotal -= shapeTotal
                        shapeTotal =
                            edtWidth.text.toString().toDouble() * edtLength.text.toString().toDouble()
                        shapeFinalTotal += shapeTotal
                        txtShapeTotal.text = Html.fromHtml(
                            getString(
                                R.string.shape_total,
                                Utility.formatNumber(Utility.roundTwoDecimals(shapeTotal).toFloat()),
                                getString(R.string.meter_square)
                            )
                        )
                        txtShapeFinalTotal.text = Html.fromHtml(
                            getString(
                                R.string.shape_final_total,
                                Utility.formatNumber(Utility.roundTwoDecimals(shapeFinalTotal).toFloat()),
                                getString(R.string.meter_square)
                            )
                        )
                        selectedProduct?.let {
                            txtFinalCost.text = Html.fromHtml(
                                getString(
                                    R.string.final_cost,
                                    "$",
                                    Utility.formatNumber(Utility.roundTwoDecimals(shapeFinalTotal * it!!.price).toFloat())
                                )
                            )
                        }
                    }
                }
            } else if (selectedShape == Constants.RECTANGLE) {
                when {
                    Utility.isValueNull(edtWidth.text.toString()) -> shortToast("Please enter base")
                    Utility.isValueNull(edtLength.text.toString()) -> shortToast("Please enter height")
                    else -> {
                        shapeFinalTotal -= shapeTotal
                        shapeTotal =
                            edtWidth.text.toString().toDouble() * edtLength.text.toString().toDouble()
                        shapeFinalTotal += shapeTotal
                        txtShapeTotal.text = Html.fromHtml(
                            getString(
                                R.string.shape_total,
                                Utility.formatNumber(Utility.roundTwoDecimals(shapeTotal).toFloat()),
                                getString(R.string.meter_square)
                            )
                        )
                        txtShapeFinalTotal.text = Html.fromHtml(
                            getString(
                                R.string.shape_final_total,
                                Utility.formatNumber(Utility.roundTwoDecimals(shapeFinalTotal).toFloat()),
                                getString(R.string.meter_square)
                            )
                        )
                        selectedProduct?.let {
                            txtFinalCost.text = Html.fromHtml(
                                getString(
                                    R.string.final_cost,
                                    "$",
                                    Utility.formatNumber(Utility.roundTwoDecimals(shapeFinalTotal * it!!.price).toFloat())
                                )
                            )
                        }
                    }
                }
            } else if (selectedShape == Constants.CIRCLE) {
                when {
                    Utility.isValueNull(edtWidth.text.toString()) -> shortToast("Please enter the radius of your turf")
                    else -> {
                        shapeFinalTotal -= shapeTotal
                        shapeTotal =
                            3.14 * (edtWidth.text.toString().toDouble() * edtWidth.text.toString().toDouble())
                        shapeFinalTotal += shapeTotal
                        txtShapeTotal.text = Html.fromHtml(
                            getString(
                                R.string.shape_total,
                                Utility.formatNumber(Utility.roundTwoDecimals(shapeTotal).toFloat()),
                                getString(R.string.meter_square)
                            )
                        )
                        txtShapeFinalTotal.text = Html.fromHtml(
                            getString(
                                R.string.shape_final_total,
                                Utility.formatNumber(Utility.roundTwoDecimals(shapeFinalTotal).toFloat()),
                                getString(R.string.meter_square)
                            )
                        )
                        selectedProduct?.let {
                            txtFinalCost.text = Html.fromHtml(
                                getString(
                                    R.string.final_cost,
                                    "$",
                                    Utility.formatNumber(Utility.roundTwoDecimals(shapeFinalTotal * it!!.price).toFloat())
                                )
                            )
                        }
                    }
                }
            } else if (selectedShape == Constants.TRIANGLE) {
                when {
                    Utility.isValueNull(edtWidth.text.toString()) || Utility.isValueNull(edtLength.text.toString()) -> shortToast(
                        "Please enter a base and a height"
                    )
//                    Utility.isValueNull(edtLength.text.toString()) -> shortToast("Please enter height")
                    else -> {
                        shapeFinalTotal -= shapeTotal
                        shapeTotal =
                            (edtWidth.text.toString().toDouble() * edtLength.text.toString().toDouble()) / 2
                        shapeFinalTotal += shapeTotal
                        txtShapeTotal.text = Html.fromHtml(
                            getString(
                                R.string.shape_total,
                                Utility.formatNumber(Utility.roundTwoDecimals(shapeTotal).toFloat()),
                                getString(R.string.meter_square)
                            )
                        )
                        txtShapeFinalTotal.text = Html.fromHtml(
                            getString(
                                R.string.shape_final_total,
                                Utility.formatNumber(Utility.roundTwoDecimals(shapeFinalTotal).toFloat()),
                                getString(R.string.meter_square)
                            )
                        )
                        selectedProduct?.let {
                            txtFinalCost.text = Html.fromHtml(
                                getString(
                                    R.string.final_cost,
                                    "$",
                                    Utility.formatNumber(Utility.roundTwoDecimals(shapeFinalTotal * it!!.price).toFloat())
                                )
                            )
                        }
                    }
                }
            }
        }

        txtClearShape.setOnClickListener {
            if (selectedShape == Constants.SQUARE) {
                when {
                    Utility.isValueNull(edtWidth.text.toString()) || Utility.isValueNull(edtLength.text.toString()) -> shortToast(
                        "Please enter both the width and length of your turf"
                    )
//                    Utility.isValueNull(edtLength.text.toString()) -> shortToast("Please enter length")
                    else -> {
                        shapeTotal =
                            edtWidth.text.toString().toDouble() * edtLength.text.toString().toDouble()
                        if (shapeFinalTotal != 0.0)
                            shapeFinalTotal -= shapeTotal

                        shapeTotal = 0.0
                        txtShapeTotal.text = ""
                        edtWidth.text = ""
                        edtLength.text = ""
                        txtShapeFinalTotal.text = Html.fromHtml(
                            getString(
                                R.string.shape_final_total,
                                Utility.formatNumber(Utility.roundTwoDecimals(shapeFinalTotal).toFloat()),
                                getString(R.string.meter_square)
                            )
                        )
                        selectedProduct?.let {
                            txtFinalCost.text = Html.fromHtml(
                                getString(
                                    R.string.final_cost,
                                    "$",
                                    Utility.formatNumber(Utility.roundTwoDecimals(shapeFinalTotal * it!!.price).toFloat())
                                )
                            )
                        }
                    }
                }
            } else if (selectedShape == Constants.RECTANGLE) {
                when {
                    Utility.isValueNull(edtWidth.text.toString()) || Utility.isValueNull(edtLength.text.toString()) -> shortToast(
                        "Please enter a width and a height"
                    )
//                    Utility.isValueNull(edtLength.text.toString()) -> shortToast("Please enter height")
                    else -> {
                        shapeTotal =
                            edtWidth.text.toString().toDouble() * edtLength.text.toString().toDouble()
                        if (shapeFinalTotal != 0.0)
                            shapeFinalTotal -= shapeTotal
                        shapeTotal = 0.0
                        txtShapeTotal.text = ""
                        edtWidth.text = ""
                        edtLength.text = ""
                        txtShapeFinalTotal.text = Html.fromHtml(
                            getString(
                                R.string.shape_final_total,
                                Utility.formatNumber(Utility.roundTwoDecimals(shapeFinalTotal).toFloat()),
                                getString(R.string.meter_square)
                            )
                        )
                        selectedProduct?.let {
                            txtFinalCost.text = Html.fromHtml(
                                getString(
                                    R.string.final_cost,
                                    "$",
                                    Utility.formatNumber(Utility.roundTwoDecimals(shapeFinalTotal * it!!.price).toFloat())
                                )
                            )
                        }
                    }
                }
            } else if (selectedShape == Constants.CIRCLE) {
                when {
                    Utility.isValueNull(edtWidth.text.toString()) -> shortToast("Please enter the radius of your turf")
                    else -> {
                        shapeTotal =
                            3.14 * (edtWidth.text.toString().toDouble() * edtWidth.text.toString().toDouble())
                        if (shapeFinalTotal != 0.0)
                            shapeFinalTotal -= shapeTotal
                        shapeTotal = 0.0
                        txtShapeTotal.text = ""
                        edtWidth.text = ""
                        txtShapeFinalTotal.text = Html.fromHtml(
                            getString(
                                R.string.shape_final_total,
                                Utility.formatNumber(Utility.roundTwoDecimals(shapeFinalTotal).toFloat()),
                                getString(R.string.meter_square)
                            )
                        )
                        selectedProduct?.let {
                            txtFinalCost.text = Html.fromHtml(
                                getString(
                                    R.string.final_cost,
                                    "$",
                                    Utility.formatNumber(Utility.roundTwoDecimals(shapeFinalTotal * it!!.price).toFloat())
                                )
                            )
                        }
                    }
                }
            } else if (selectedShape == Constants.TRIANGLE) {
                when {
                    Utility.isValueNull(edtWidth.text.toString()) -> shortToast("Please enter base")
                    Utility.isValueNull(edtLength.text.toString()) -> shortToast("Please enter height")
                    else -> {

                        shapeTotal =
                            (edtWidth.text.toString().toDouble() * edtLength.text.toString().toDouble()) / 2
                        if (shapeFinalTotal != 0.0)
                            shapeFinalTotal -= shapeTotal
                        shapeTotal = 0.0
                        txtShapeTotal.text = ""
                        edtWidth.text = ""
                        edtLength.text = ""
                        txtShapeFinalTotal.text = Html.fromHtml(
                            getString(
                                R.string.shape_final_total,
                                Utility.formatNumber(Utility.roundTwoDecimals(shapeFinalTotal).toFloat()),
                                getString(R.string.meter_square)
                            )
                        )
                        selectedProduct?.let {
                            txtFinalCost.text = Html.fromHtml(
                                getString(
                                    R.string.final_cost,
                                    "$",
                                    Utility.formatNumber(Utility.roundTwoDecimals(shapeFinalTotal * it!!.price).toFloat())
                                )
                            )
                        }
                    }
                }
            }
        }

        txtAddAnotherShape.setOnClickListener {
            if (shapeTotal > 0.0) {
                addShapeLayout()
            } else
                shortToast(getString(R.string.calculate_turf_first))

        }

        linShapes.addView(shapeView)

        txtStartOver.setOnClickListener {

            nestedScroll.scrollX = 0
            nestedScroll.scrollY = 0

            linShapes.removeAllViews()
            addShapeLayout()
            shapeTotal = 0.0
            shapeFinalTotal = 0.0
            txtShapeFinalTotal.text = Html.fromHtml(
                getString(
                    R.string.shape_final_total,
                    Utility.formatNumber(Utility.roundTwoDecimals(shapeFinalTotal).toFloat()),
                    getString(R.string.meter_square)
                )
            )


        }
    }

    private fun initObservers() {
        turfCalculatorViewModel!!.turfProductsLiveData.observe(this, Observer {
            if (turfProducts == null)
                turfProducts = ArrayList()
            turfProducts.clear()
            if (it != null) {
                turfProducts.addAll(it)
                if (productId != 0) {
                    for (i in 0 until turfProducts.size) {
                        if (turfProducts[i].productId == productId) {
                            turfProducts[i].selected = true
                            selectedProduct = turfProducts[i]
                            break
                        }
                    }
                }
                setAdapter()
//            openBothMenu()
                openBothMenuNew()
            }
        })


        cartViewModel!!.productInsertionLiveData.observe(this, Observer {
            if (it != null && it) {
                Handler().postDelayed({
                    Utility.cancelProgress()
                    shortToast(
                        if (!isUpdate) getString(R.string.product_successfully_added_msg) else getString(
                            R.string.product_successfully_updated_msg
                        )
                    )
                    (activity as HomeActivity).showCartDetails(imgCart, txtCartProducts, false)
                    if (activity is HomeActivity) {
                        (activity as HomeActivity).showCartDetails(imgCart, txtCartProducts, false)
                    } else if (activity is PaymentActivity) {
                        (activity as PaymentActivity).showCartDetails(
                            imgCart,
                            txtCartProducts,
                            false
                        )
                    } else if (activity is ProfileActivity) {
                        (activity as ProfileActivity).showCartDetails(
                            imgCart,
                            txtCartProducts,
                            false
                        )
                    }
                    addToCartProduct = null
                    cartViewModel!!._productInsertionLiveData.value = null

                    shapeFinalTotal = 0.0
                    txtShapeFinalTotal.text = Html.fromHtml(
                        getString(
                            R.string.shape_final_total,
                            Utility.formatNumber(Utility.roundTwoDecimals(shapeFinalTotal).toFloat()),
                            getString(R.string.meter_square)
                        )
                    )

                    linShapes.removeAllViews()
                    (requireActivity() as AppCompatActivity).pushFragment(
                        CartFragment(),
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                }, 1000)
            } else {
                if (isUpdate) {
                    shortToast(getString(R.string.product_updation_failed_msg))
                }
            }
        })

        cartViewModel!!.productLiveData.observe(this, Observer {
            if (addToCartProduct != null) {
                if (it != null) {
                    isUpdate = true
                    addToCartProduct!!.qty = shapeFinalTotal.toInt()
                } else {
                    isUpdate = false
                    sharedPrefs.totalProductsInCart = sharedPrefs.totalProductsInCart + 1
                }
                cartViewModel!!.insertProduct(addToCartProduct!!, isUpdate)
            }
        })


        turfCalculatorViewModel!!.errorLiveData.observe(this, Observer {
            if (!Utility.isValueNull(turfCalculatorViewModel!!.errorLiveData.value)) {
                if (it.equals(ErrorConstants.UNAUTHORIZED_ERROR_CODE)) {
                    (requireActivity() as AppCompatActivity).openLogoutAlertDialog(sharedPrefs = sharedPrefs)
                } else {
                    it?.let { it1 -> shortToast(it1) }
                }
                turfCalculatorViewModel!!._errorLiveData.value = null
            }
        })

        cartViewModel!!.AvailableQtyLiveData.observe(this, Observer {
            if (it != null) {
                if (it.success){
                    cartViewModel?.getProductById(selectedProduct!!.productId)
                }else{
                    Utility.cancelProgress()
                    shortToast(it.message)
                }
                cartViewModel!!._availableQtyLiveData.value = null
            }
        })
    }

    private fun openBothMenu() {
        layoutTurfProduct.visibility =
            if (layoutTurfProduct.visibility == VISIBLE) GONE else VISIBLE
        imgChooseTurfVarietyDropdown.rotation =
            if (layoutTurfProduct.visibility == VISIBLE) 180f else 0f
        nestedScroll.fling(0)
        nestedScroll.smoothScrollTo(0, 0)


        layoutShape.visibility = if (layoutShape.visibility == VISIBLE) GONE else VISIBLE
        imgSelectShapeDropdown.rotation =
            if (layoutShape.visibility == VISIBLE) 180f else 0f
    }
    private fun openBothMenuNew() {
        layoutTurfProduct.visibility = VISIBLE
        imgChooseTurfVarietyDropdown.rotation = 180f
        nestedScroll.fling(0)
        nestedScroll.smoothScrollTo(0, 0)

        layoutShape.visibility =  VISIBLE
        imgSelectShapeDropdown.rotation = 180f
    }

    private fun setAdapter() {
//        if(productCategoryAdapter == null) {
        quoteProductsAdapter = activity?.let {
            QuoteProductsAdapter(
                it as AppCompatActivity,
                turfProducts,
                object : OnProductSelectedListener {
                    override fun onProductSelectedListener(
                        product: ProductsResponse.Data,
                        selected: Boolean
                    ) {
                        if (shapeFinalTotal > 0.0) {
                            selectedProduct = product
                            finalCost =
                                Utility.roundTwoDecimals(shapeFinalTotal * product.price).toDouble()
                            txtFinalCost.text = Html.fromHtml(
                                getString(
                                    R.string.final_cost,
                                    "$",
                                    Utility.formatNumber(Utility.roundTwoDecimals(shapeFinalTotal * product.price).toFloat())
                                )
                            )
                        } else
                            shortToast("Previous calculate the first shape before moving onto the next shape")
                    }
                })
        }!!
        listTurfProduct.adapter = quoteProductsAdapter
//        } else{
//            productCategoryAdapter!!.offers = orderStatuses
//            productCategoryAdapter!!.notifyDataSetChanged()
//        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.imgBell -> {
                openNotificationFragment()
            }

            R.id.txtShopAll -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                val bundle = Bundle()
                bundle.putString("categoryIds", "")
                bundle.putString("title", "")
                bundle.putBoolean("fromRelated", false)
                (requireActivity() as AppCompatActivity).pushFragment(
                    ProductsFragment().apply {
                        arguments = bundle
                    },
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.imgLogo -> {
                // (requireActivity() as AppCompatActivity).hideKeyboard()
                (requireActivity() as AppCompatActivity).pushFragment(
                    HomeFragment(),
                    false,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.imgBell -> {
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

            R.id.txtAddToCart -> {
                if (selectedProduct != null) {
                    if (shapeFinalTotal > 0) {
                        if (shapeFinalTotal <= Constants.MAX_NUMBER) {
                            if (selectedProduct!!.inStock != 0) {
                                Utility.showProgress(requireContext(), "", false)
                                addToCartProduct = ProductDetailResponse(
                                    featureImageUrl = selectedProduct!!.featureImageUrl,
                                    price = selectedProduct!!.price,
                                    id = selectedProduct!!.productId,
                                    inStock = selectedProduct!!.inStock,
                                    minimumQuantity = selectedProduct!!.minimumQuantity,
                                    productCategoryId = selectedProduct!!.productCategoryId,
                                    productName = selectedProduct!!.productName,
                                    productUnit = selectedProduct!!.productUnit,
                                    productUnitId = selectedProduct!!.productUnitId,
                                    qty = shapeFinalTotal.toInt()
                                )
                                if (addToCartProduct != null) {
                                    productIdQty.clear()
                                    var productDB = ProductDB(
                                        product_id = addToCartProduct!!.id,
                                        feature_img_url = "",
                                        qty = addToCartProduct!!.qty,
                                        price = 0f,
                                        product_category_id = 0,
                                        product_name = "",
                                        product_unit = "",
                                        product_unit_id = 0,
                                        base_total_price = 0f,
                                        is_turf = 0,
                                        total_price = 0f,
                                        product_redeemable_against_credit = false
                                    )
                                    productIdQty.add(productDB!!)
                                }

                                cartViewModel?.getAvailableQty(productIdQty)

                                val bundle = Bundle()
                                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, selectedProduct?.productId.toString())
                                bundle.putString(FirebaseAnalytics.Param.VALUE, selectedProduct?.price.toString())
                                bundle.putString(FirebaseAnalytics.Param.QUANTITY, selectedProduct?.qty.toString())
                                bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, selectedProduct?.productCategoryId.toString())
                                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, selectedProduct?.productName)
                                mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle)
//                                cartViewModel?.getProductById(selectedProduct!!.productId)
                            } else
                                shortToast(getString(R.string.out_of_stock_message))
                        } else
                            shortToast("Please enter a quantity below ${Constants.MAX_NUMBER + 1}")
                    } else
                        shortToast(getString(R.string.no_shape_selected_msg))
                } else {
                    shortToast(getString(R.string.no_turf_product_selected_msg))
                }
            }

            R.id.txtAddToQuote -> {
                if (selectedProduct != null) {
                    if (shapeFinalTotal > 0) {
                        if (shapeFinalTotal <= Constants.MAX_NUMBER) {
                            val quoteAncoProductRequest = QuoteAncoProductRequest(
                                selectedProduct!!.productId,
                                shapeFinalTotal.toInt(),
                                0
                            )
                            shapeFinalTotal = 0.0
                            txtShapeFinalTotal.text = ""
                            (requireActivity() as AppCompatActivity).pushFragment(
                                AddBusinessDetailsFragment(quoteAncoProductRequest),
                                true,
                                true,
                                false,
                                R.id.flContainerHome
                            )
                        } else
                            shortToast("Please enter a quantity below ${Constants.MAX_NUMBER + 1}")
                    } else
                        shortToast(getString(R.string.no_shape_selected_msg))
                } else {
                    shortToast(getString(R.string.no_turf_product_selected_msg))
                }
            }

            R.id.imgSelectShapeDropdown, R.id.layoutShapeLabel -> {
                layoutShape.visibility = if (layoutShape.visibility == VISIBLE) GONE else VISIBLE
                imgSelectShapeDropdown.rotation =
                    if (layoutShape.visibility == VISIBLE) 180f else 0f
                /*if (layoutTurfProduct.visibility == View.VISIBLE) {
                    layoutTurfProduct.visibility = View.GONE
                    imgChooseTurfVarietyDropdown.rotation = 0f
                }*/
            }

            R.id.imgChooseTurfVarietyDropdown, R.id.layoutTurfProductLabel -> {
                layoutTurfProduct.visibility =
                    if (layoutTurfProduct.visibility == VISIBLE) GONE else VISIBLE
                imgChooseTurfVarietyDropdown.rotation =
                    if (layoutTurfProduct.visibility == VISIBLE) 180f else 0f
              /*  if (layoutShape.visibility == View.VISIBLE) {
                    layoutShape.visibility = View.GONE
                    imgSelectShapeDropdown.rotation = 0f
                }*/
//                nestedScroll.fullScroll(View.FOCUS_UP)
                nestedScroll.fling(0)
                nestedScroll.smoothScrollTo(0, 0)
            }
        }
    }
}
