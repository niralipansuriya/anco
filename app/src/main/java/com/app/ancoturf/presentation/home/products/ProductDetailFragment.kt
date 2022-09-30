package com.app.ancoturf.presentation.home.products

import android.annotation.SuppressLint
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.*
import android.webkit.WebView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.cart.persistance.entity.ProductDB
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.offer.ClsRating
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import com.app.ancoturf.data.product.remote.entity.response.RelatedProductsResponse
import com.app.ancoturf.data.setting.remote.entity.SettingsResponse
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
import com.app.ancoturf.presentation.home.interfaces.OnProductAddedToCart
import com.app.ancoturf.presentation.home.products.adapters.*
import com.app.ancoturf.presentation.home.shop.ShopViewModel
import com.app.ancoturf.presentation.home.turfcalculator.TurfCalculatorFragment
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.InputFilterMinMax
import com.app.ancoturf.utils.Logger
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.gson.Gson
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.android.synthetic.main.fragment_add_edit_quote.*
import kotlinx.android.synthetic.main.fragment_product_detail.*
import kotlinx.android.synthetic.main.fragment_product_detail.nestedScroll
import kotlinx.android.synthetic.main.fragment_product_detail.txtProductTitle
import kotlinx.android.synthetic.main.fragment_product_detail.view.*
import kotlinx.android.synthetic.main.header.*
import kotlinx.android.synthetic.main.item_non_anco_product.view.*
import kotlinx.android.synthetic.main.item_product.view.*
import kotlinx.android.synthetic.main.item_product.view.edtQuantity
import kotlinx.android.synthetic.main.item_product.view.txtAddToCart
import kotlinx.android.synthetic.main.item_product.view.txtCalculationLabel
import kotlinx.android.synthetic.main.item_product.view.txtProductTitle
import java.util.regex.Pattern
import javax.inject.Inject
import kotlinx.android.synthetic.main.item_non_anco_product.view.txtProductTitle as txtProductTitle1


class ProductDetailFragment(private val productId: Int) : BaseFragment(),
    View.OnClickListener {

    private lateinit var dialog: Dialog

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var productsViewModel: ProductsViewModel? = null
    private var shopViewModel: ShopViewModel? = null
    private var cartViewModel: CartViewModel? = null
    var productImageAdapter: ProductImageAdapter? = null
    var productDetailResponse: ProductDetailResponse? = null
    var productsAdapter: RelatedProductsAdapter? = null
    var products: ArrayList<RelatedProductsResponse.Data> = ArrayList()

    var addToCartProduct: ProductDetailResponse? = null

    var ratings: ArrayList<ClsRating> = ArrayList()

    //review fields
    var reviewOpen = false
    var txtTitle: TextView? = null
    var listReviews: RecyclerView? = null
    var txtAddReview: TextView? = null
    var reviewView: View? = null
    var isUpdate = false
    var productIdQty: ArrayList<ProductDB> = ArrayList()
    var productById: Int? = 0
    var isFreeProductVisible : Boolean = true
    private var mFirebaseAnalytics: FirebaseAnalytics? = null



    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun getContentResource(): Int = R.layout.fragment_product_detail

    override fun viewModelSetup() {
        productsViewModel = ViewModelProviders.of(
            requireActivity(),
            viewModelFactory
        )[ProductsViewModel::class.java]
        shopViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[ShopViewModel::class.java]
        cartViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[CartViewModel::class.java]
        initObservers()
    }

    override fun viewSetup() {
        Utility.showProgress(context = requireContext(), message = "", cancellable = false)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        productsViewModel?.callGetProductDetails(productId)
        productsViewModel?.callGetRelatedProducts(productId, 2)
        if (activity is HomeActivity) {
            (activity as HomeActivity).hideShowFooter(true)
            (activity as HomeActivity).showCartDetails(imgCart, txtCartProducts, false)
        } else if (activity is PaymentActivity) {
            (activity as PaymentActivity).hideShowFooter(true)
            (activity as PaymentActivity).showCartDetails(imgCart, txtCartProducts, false)
        } else if (activity is ProfileActivity) {
            (activity as ProfileActivity).hideShowFooter(true)
            (activity as ProfileActivity).showCartDetails(imgCart, txtCartProducts, false)
        }
        setRatingData()
        imgBack.setOnClickListener(this@ProductDetailFragment)
        setRelatedProductsAdapter()
        txtShowAllRelated.setOnClickListener(this)
        imgShare.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        linReviewContainer.setOnClickListener(this)
        txtAddToCart.setOnClickListener(this)
        var ss = SpannableString(getString(R.string.calculation_label_text))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
//                shortToast("Terms and Conditions clicked")
                (requireActivity() as AppCompatActivity).pushFragment(
                    TurfCalculatorFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
//                ds.isFakeBoldText = true
                ds.color = ContextCompat.getColor(requireActivity(), android.R.color.black)
                ds.isUnderlineText = true
            }
        }
        ss.setSpan(clickableSpan, 8, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        txtCalculationLabel.text = ss
        txtCalculationLabel.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun initObservers() {
        productsViewModel!!.productDetailsLiveData.observe(this, Observer {
            if (it != null) {
                Log.e("resp", it.toString())
                productDetailResponse = it


                val Quantity = edtQuantity.text.toString()


                val itemArray = Bundle().apply {
                    Log.e("Quantity =========",edtQuantity.text.toString())

                    putString(FirebaseAnalytics.Param.ITEM_ID,productDetailResponse?.productId.toString())
                    putString(FirebaseAnalytics.Param.PRICE,productDetailResponse?.price.toString())
                 //   putString(FirebaseAnalytics.Param.QUANTITY,productDetailResponse?.qty.toString())
                    if (Quantity!=null && !Quantity.contentEquals("null")&& !Quantity.contentEquals(""))
                    {
                        putLong(FirebaseAnalytics.Param.QUANTITY,Quantity.toLong())

                    }
                    putString(FirebaseAnalytics.Param.ITEM_CATEGORY,productDetailResponse?.productCategory?.displayName)
                    putString(FirebaseAnalytics.Param.ITEM_NAME,productDetailResponse?.productName)
                }

                mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.VIEW_ITEM)
                {
                    param(FirebaseAnalytics.Param.CURRENCY, "USD")
/*
                    param(FirebaseAnalytics.Param.ITEM_ID,productDetailResponse?.productId.toString())
                    param(FirebaseAnalytics.Param.PRICE,productDetailResponse?.price.toString())
                    param(FirebaseAnalytics.Param.QUANTITY,productDetailResponse?.qty.toString())
                    param(FirebaseAnalytics.Param.ITEM_CATEGORY,productDetailResponse?.productCategory?.displayName!!)
                    param(FirebaseAnalytics.Param.ITEM_NAME,productDetailResponse?.productName!!)
*/

                     param(FirebaseAnalytics.Param.ITEMS, arrayOf(itemArray))

                }
/*nikita
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, productDetailResponse?.productId.toString())
                bundle.putString(FirebaseAnalytics.Param.VALUE, productDetailResponse?.price.toString())
                bundle.putString(FirebaseAnalytics.Param.QUANTITY, productDetailResponse?.qty.toString())
                bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, productDetailResponse?.productCategory?.displayName)
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, productDetailResponse?.productName)
                mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
*/

                setData()
            }
        })

        productsViewModel!!.relatedProductsLiveData.observe(this, Observer {
            if (it != null && it.success) {
                Log.e("resp", it.toString())
                if (products == null)
                    products = ArrayList()
                products.clear()
                it.data?.data?.let { it1 -> products.addAll(it1) }
                setRelatedProductsAdapter()
                productsViewModel!!._relatedProductsLiveData.value = null
            }
        })

        productsViewModel!!.addReviewLiveData.observe(this, Observer {
            if (it != null) {
                Log.e("resp", it.toString())
                Utility.cancelProgress()
                if (dialog != null)
                    dialog.dismiss()
                shortToast(it.message)
                productsViewModel?._addReviewLiveData?.value = null
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
                }, 1000)
            } /*else {
                if (isUpdate)
                    shortToast(getString(R.string.product_updation_failed_msg))
            }*/
        })

        cartViewModel!!.AvailableQtyLiveData.observe(this, Observer {
            if (addToCartProduct != null) {
                if (it != null) {
                    if (it.success){
                        cartViewModel?.getProductById(productById!!)
                        productById = 0
                    }else{
                        Utility.cancelProgress()
                        shortToast(it.message)
                    }
                    cartViewModel!!._availableQtyLiveData.value = null
                }
            }
        })

        cartViewModel!!.productLiveData.observe(this, Observer {
            if (addToCartProduct != null) {
                if (it != null) {
                    isUpdate = true
                    addToCartProduct!!.qty = addToCartProduct!!.qty + it.qty
                } else {
                    isUpdate = false
                    sharedPrefs.totalProductsInCart = sharedPrefs.totalProductsInCart + 1
                }
                cartViewModel!!.insertProduct(addToCartProduct!!, isUpdate)
            }
        })

        /*cartViewModel!!.productLiveData.observe(this, Observer {
            if (addToCartProduct != null) {
                if (it != null) {
                    isUpdate = true
                    addToCartProduct!!.qty = addToCartProduct!!.qty + it.qty
                } else {
                    isUpdate = false
                    sharedPrefs.totalProductsInCart = sharedPrefs.totalProductsInCart + 1
                }
                cartViewModel!!.insertProduct(addToCartProduct!! , isUpdate)
            }
        })*/

        productsViewModel!!.errorLiveData.observe(this, Observer {
            if (!Utility.isValueNull(productsViewModel!!.errorLiveData.value)) {
                Utility.cancelProgress()
                if (it.equals(ErrorConstants.UNAUTHORIZED_ERROR_CODE)) {
                    if (activity is HomeActivity) {
                        (activity as HomeActivity).cartViewModel?.deleteProduct(null)
                        (activity as HomeActivity).cartViewModel?.deleteCoupon(null)
                    } else if (activity is PaymentActivity) {
                        (activity as PaymentActivity).cartViewModel?.deleteProduct(null)
                        (activity as PaymentActivity).cartViewModel?.deleteCoupon(null)
                    } else if (activity is PaymentActivity) {
                        (activity as PaymentActivity).cartViewModel?.deleteProduct(null)
                        (activity as PaymentActivity).cartViewModel?.deleteCoupon(null)
                    }
                    (requireActivity() as AppCompatActivity).openLogoutAlertDialog(sharedPrefs = sharedPrefs)
                } else {
                    it?.let { it1 -> shortToast(it1) }
                }
                productsViewModel!!._errorLiveData.value = null
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        if (productDetailResponse != null) {
            setAdapter()
            txtProductTitle.text = productDetailResponse!!.productName
            txtProductCategory.text = productDetailResponse!!.productCategory.displayName
            var size =
                if (productDetailResponse?.productReviews != null) productDetailResponse?.productReviews!!.size else 0
            txtReviews.text = getString(R.string.number_of_reviews, size)
            if (productDetailResponse?.productReviews!!.size > 1) {
                txtCustomerReviewLabel.text = getString(R.string.customer_nreviews)
            } else {
                txtCustomerReviewLabel.text = getString(R.string.customer_nreview)
            }

            if (productDetailResponse?.productReviews != null) {
                ratingReview.rating = getAvgRating(productDetailResponse?.productReviews!!)
            } else {
                ratingReview.rating = 0f
            }

            productDetailResponse?.qty = productDetailResponse!!.minimumQuantity

            edtQuantity.setText("${productDetailResponse?.qty}")

            val price = Utility.roundTwoDecimals(productDetailResponse!!.price)
            var unit = productDetailResponse!!.productUnit
//            txtCalculationLabel.visibility = View.GONE
            if (unit.equals("Square meter")) {
                unit = "m\u00B2"
                txtCalculationLabel.visibility = View.VISIBLE
            } else if (unit.equals("Quantity")) {
                unit = "Qty"
            }

            if (unit.equals("Qty"))
                txtPrice.text = "$${Utility.formatNumber(price.toFloat())}"
            else
                txtPrice.text = "$${Utility.formatNumber(price.toFloat())} / $unit"

            txtUnit.text = unit
            if (Utility.isValueNull(edtQuantity.text.toString()))
                txtTotal.text = Html.fromHtml(
                    getString(
                        R.string.qty_total,
                        Utility.formatNumber(price.toFloat())
                    )
                )
            else
                txtTotal.text = Html.fromHtml(
                    getString(
                        R.string.qty_total,
                        Utility.formatNumber(
                            Utility.roundTwoDecimals(
                                price.toFloat() * (edtQuantity.text.toString().toFloat())
                            ).toFloat()
                        )
                    )
                )

            edtQuantity.setFilters(
                arrayOf<InputFilter>(
                    InputFilterMinMax(
                        1, Constants.MAX_NUMBER
                    )
                )
            )

            edtQuantity.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (Utility.isValueNull(edtQuantity.text.toString())) {
                        txtTotal.text = Html.fromHtml(
                            getString(
                                R.string.qty_total,
                                Utility.formatNumber(price.toFloat())
                            )
                        )
                        productDetailResponse?.qty = 0
                    } else {
                        txtTotal.text = Html.fromHtml(
                            getString(
                                R.string.qty_total,
                                Utility.formatNumber(
                                    Utility.roundTwoDecimals(
                                        (price.toFloat() * (edtQuantity.text.toString().toFloat()))
                                    ).toFloat()
                                )
                            )
                        )
                        productDetailResponse?.qty = edtQuantity.text.toString().toInt()
                    }
                }
            })

            if (productDetailResponse!!.inStock == 0) {
                txtOutOfStock.visibility = View.VISIBLE
                txtAddToCart.visibility = View.GONE
            } else {
                txtOutOfStock.visibility = View.GONE
                txtAddToCart.visibility = View.VISIBLE
            }

//            edtQuantity.setOnEditorActionListener(object : TextView.OnEditorActionListener {
//                override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
//                    if (actionId == EditorInfo.IME_ACTION_DONE) {
//                        if (Utility.isValueNull(edtQuantity.text))
//                            txtTotal.text = Html.fromHtml(getString(R.string.qty_total, price))
//                        else
//                            txtTotal.text = Html.fromHtml(
//                                getString(
//                                    R.string.qty_total,
//                                    (price.toFloat() * (edtQuantity.text.toString().toFloat())).toString()
//                                )
//                            )
//                    }
//                    return false
//                }

//            })
        }

        linContainer.removeAllViews()

        //add videos
        for (i in 0..(productDetailResponse!!.productDetails.size - 1)) {
            var productDetail = productDetailResponse!!.productDetails.get(i)
            if (productDetail.type.equals("video")) {
//                if (!this::videoView.isInitialized)
                val videoView = LayoutInflater.from(activity).inflate(R.layout.item_videoview, null)
                val txtTitle: TextView = videoView.findViewById(R.id.txtTitle)
                txtTitle.text = productDetail.title

                val youTubePlayerView: YouTubePlayerView = videoView.findViewById(R.id.player)
                lifecycle.addObserver(youTubePlayerView)

                youTubePlayerView.addYouTubePlayerListener(object :
                    AbstractYouTubePlayerListener() {
                    override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(extractVideoIdFromUrl(productDetail.metaValue), 0f)
                    }
                })
                linContainer.addView(videoView)
            }
        }

        //add benefits
        if (productDetailResponse?.productCategory?.id == 1 && productDetailResponse?.productBenefits != null && productDetailResponse?.productBenefits!!.size > 0) {
            val benefitView = LayoutInflater.from(activity).inflate(R.layout.item_benefitview, null)
            val txtTitle: TextView = benefitView.findViewById(R.id.txtTitle)
//            txtTitle.text = productDetailResponse?.productBenefitTitle
            txtTitle.text = "Benefits of " + productDetailResponse?.productName
            val listBenefits: RecyclerView = benefitView.findViewById(R.id.listBenefits)
            val benefitsAdapter = BenefitsAdapter(
                requireContext(),
                productDetailResponse!!.productBenefits
            )
            listBenefits.adapter = benefitsAdapter

            linContainer.addView(benefitView)
        }

        //add html views
        for (i in 0..(productDetailResponse!!.productDetails.size - 1)) {
            var productDetail = productDetailResponse!!.productDetails.get(i)
            if (productDetail.type.equals("html")) {
                val htmlView: View =
                    LayoutInflater.from(activity).inflate(R.layout.item_htmlview, null)
                val txtTitle: TextView = htmlView.findViewById(R.id.txtTitle)
                txtTitle.text = productDetail.title

                val txtHtml: WebView = htmlView.findViewById(R.id.txtHtml)
                txtHtml.loadData(productDetail.metaValue, "text/html", "UTF-8")
//                txtHtml.text = Html.fromHtml(productDetail.metaValue)
                txtHtml.visibility = if (productDetail.open) View.VISIBLE else View.GONE
                txtTitle.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    if (productDetail.open) ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_up_arrow
                    ) else ContextCompat.getDrawable(requireContext(), R.drawable.ic_down_arrow),
                    null
                )
                txtTitle.setOnClickListener {
                    productDetail.open = !productDetail.open
                    txtHtml.visibility = if (productDetail.open) View.VISIBLE else View.GONE
                    txtTitle.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        if (productDetail.open) ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_up_arrow
                        ) else ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_down_arrow
                        ),
                        null
                    )
                }
                linContainer.addView(htmlView)
            }
        }

        //add reviews
        reviewOpen = false
        reviewView = LayoutInflater.from(activity).inflate(R.layout.item_reviewview, null)
        txtTitle = reviewView?.findViewById(R.id.txtTitle)
        txtTitle?.text = getString(R.string.reviews_title)

        listReviews = reviewView?.findViewById(R.id.listReviews)
        if (productDetailResponse?.productReviews != null && productDetailResponse?.productReviews!!.size > 0) {
            val reviewAdapter = ReviewAdapter(
                requireContext(),
                productDetailResponse!!.productReviews
            )
            listReviews?.adapter = reviewAdapter
        }
        listReviews?.visibility = View.GONE

        txtAddReview = reviewView?.findViewById(R.id.txtAddReview)
        txtAddReview?.setOnClickListener {
            openAddReviewDialog()
        }
        txtAddReview?.visibility = View.GONE

        txtTitle?.setOnClickListener {
            reviewOpen = !reviewOpen
            listReviews?.visibility = if (reviewOpen) View.VISIBLE else View.GONE
            txtAddReview?.visibility = if (reviewOpen) View.VISIBLE else View.GONE
            txtTitle?.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                if (reviewOpen) ContextCompat.getDrawable(
                    requireContext(), R.drawable.ic_up_arrow
                ) else ContextCompat.getDrawable(requireContext(), R.drawable.ic_down_arrow),
                null
            )
        }
        linContainer.addView(reviewView)
        //nnn here
        setFeeProductData()
        Utility.cancelProgress()
    }

    private fun setFeeProductData() {
        txtFreeLabel.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            if (isFreeProductVisible) ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_up_arrow
            ) else ContextCompat.getDrawable(requireContext(), R.drawable.ic_down_arrow),
            null
        )
        txtFreeLabel.setOnClickListener {
            isFreeProductVisible = !isFreeProductVisible
            layItemProduct.visibility = if (isFreeProductVisible && productDetailResponse?.free_product != 0) View.VISIBLE else View.GONE
            txtNoFreeProduct.visibility = if (isFreeProductVisible && productDetailResponse?.free_product == 0) View.VISIBLE else View.GONE
            txtFreeLabel.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                if (isFreeProductVisible) ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_up_arrow
                ) else ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_down_arrow
                ),
                null
            )
        }
        if (productDetailResponse?.free_product_qty != 0){
            layItemProduct.visibility = View.VISIBLE
            layItemProduct.txtFreeDescription.visibility = View.VISIBLE
            layItemProduct.txtFreeDescription.text = productDetailResponse?.free_product_description
            Glide.with((requireActivity() as AppCompatActivity)).load(productDetailResponse?.free_products?.feature_image_url).into(layItemProduct.imgProduct)
            layItemProduct.txtPrize.text = "Free"
//            layItemProduct.edtQuantity.text = "1"
            layItemProduct.txtProductTitle.text = productDetailResponse?.free_products?.productName
            layItemProduct.txtAddToCart.visibility = View.GONE
            layItemProduct.linPrizeCalculation.visibility = View.GONE
            layItemProduct.txtCalculationLabel.visibility = View.GONE
            layItemProduct.txtFinalPrize.visibility = View.GONE
            layItemProduct.edtQuantity.isEnabled = false
            layItemProduct.edtQuantity.setText(productDetailResponse?.free_products?.minimum_qty.toString())
            txtNoFreeProduct.visibility = View.GONE
        }else{
//            txtNoFreeProduct.visibility = if (isFreeProductVisible) View.VISIBLE else View.GONE
            txtFreeLabel.visibility = View.GONE
            lineFree.visibility = View.GONE
            txtNoFreeProduct.visibility = View.GONE
            layItemProduct.visibility = View.GONE
        }
    }

    private fun openAddReviewDialog() {
        // Create custom dialog object
        dialog = Dialog(requireContext())
//        val dialog = Dialog(requireContext() , R.style.theme_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_add_review)
        dialog.show()

        val window = dialog.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)

        val edtUserName = dialog.findViewById(R.id.edtUserName) as EditText
        val edtEmail = dialog.findViewById(R.id.edtEmail) as EditText
        val edtReview = dialog.findViewById(R.id.edtReview) as EditText
        val spinnerRating = dialog.findViewById(R.id.spinnerRating) as Spinner

        if (sharedPrefs.isLogged) {
            edtUserName.setText(sharedPrefs.userName)
            edtUserName.isEnabled = false
            edtEmail.setText(sharedPrefs.userEmail)
            edtEmail.isEnabled = false
        } else {
            edtUserName.isEnabled = true
            edtEmail.isEnabled = true
        }

        val spinnerRatingAdapter =
            SpinnerRatingAdapter(requireActivity() as AppCompatActivity, ratings)
        spinnerRating.adapter = spinnerRatingAdapter

        val imgClose = dialog.findViewById(R.id.imgClose) as ImageView
        imgClose.setOnClickListener {
            dialog.dismiss()
        }

        val txtSubmitReview = dialog.findViewById(R.id.txtSubmitReview) as TextView
        txtSubmitReview.setOnClickListener {
            if (Utility.isValueNull(edtUserName.text.toString())) {
                (requireActivity() as AppCompatActivity).shortToast(getString(R.string.black_username_message))
            } else if (Utility.isValueNull(edtEmail.text.toString())) {
                (requireActivity() as AppCompatActivity).shortToast(getString(R.string.blank_email_message))
            } else if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.text.toString()).matches()) {
                (requireActivity() as AppCompatActivity).shortToast(getString(R.string.invalid_email_message))
            } else if (spinnerRating.selectedItemPosition == 0) {
                (requireActivity() as AppCompatActivity).shortToast(getString(R.string.blank_rating_message))
//            } else if (Utility.isValueNull(edtReview.text.toString())) {
//                (requireActivity() as AppCompatActivity).shortToast(getString(R.string.blank_review_message))
            } else {
                var user_id = 0
                if (sharedPrefs.isLogged)
                    user_id = sharedPrefs.userId
                Utility.showProgress(context = requireContext(), message = "", cancellable = false)
                productsViewModel?.callAddReview(
                    productId = productId,
                    review_text = edtReview.text.toString(),
                    rating = ratings[spinnerRating.selectedItemPosition].rating,
                    email = edtEmail.text.toString(),
                    user_id = user_id,
                    name = edtUserName.text.toString()
                )
            }
        }

    }

    private fun setRatingData() {
        ratings.add(ClsRating("Rate..", 0))
//        ratings.add(ClsRating("Perfect" , 5))
//        ratings.add(ClsRating("Good" , 4))
//        ratings.add(ClsRating("Average" , 3))
//        ratings.add(ClsRating("Not that bad" , 2))
//        ratings.add(ClsRating("Very poor" , 1))
        ratings.add(ClsRating("Excellent", 5))
        ratings.add(ClsRating("Very good", 4))
        ratings.add(ClsRating("Average", 3))
        ratings.add(ClsRating("Below Average", 2))
        ratings.add(ClsRating("Very Poor", 1))
    }

    private fun getAvgRating(productReviews: List<ProductDetailResponse.ProductReview>): Float {
        var totalRating = 0f
        for (i in 0..(productReviews.size - 1)) {
            var review = productReviews.get(i)
            totalRating = totalRating + review.rating
        }
        return totalRating / productReviews.size
    }

    val videoIdRegex = arrayOf(
        "\\?vi?=([^&]*)",
        "watch\\?.*v=([^&]*)",
        "(?:embed|vi?)/([^/?]*)",
        "^([A-Za-z0-9\\-]*)"
    )

    fun extractVideoIdFromUrl(url: String): String {

        for (regex in videoIdRegex) {
            val compiledPattern = Pattern.compile(regex)
            val matcher = compiledPattern.matcher(url)

            if (matcher.find()) {
                return matcher.group(1)
            }
        }
        return ""
    }

    private fun setAdapter() {
        if (productDetailResponse != null && productDetailResponse!!.productImages != null) {
            productImageAdapter =
                activity?.let {
                    ProductImageAdapter(
                        it as AppCompatActivity,
                        productDetailResponse!!.productImages
                    )
                }!!
            homeItemPager.adapter = productImageAdapter
            homeItemIndicator.setViewPager(homeItemPager)

            if (productDetailResponse!!.productImages.size > 1)
                homeItemIndicator.visibility = View.VISIBLE
            else
                homeItemIndicator.visibility = View.GONE
        }
    }

    private fun setRelatedProductsAdapter() {
//        if(productsAdapter == null) {
        if (products != null && products.size > 0) {
            txtRelatedItemLabel.visibility = View.VISIBLE
            txtShowAllRelated.visibility = View.VISIBLE
            listRelatedProducts.visibility = View.VISIBLE
            productsAdapter = activity?.let {
                RelatedProductsAdapter(
                    it as AppCompatActivity,
                    products,
                    2,
                    object : OnProductAddedToCart {
                        override fun OnProductAddedToCart(product: ProductDetailResponse) {
                            Utility.showProgress(requireContext(), "", false)
                            addToCartProduct = product
                            var id = if (product.productId == 0) product.id else product.productId
                            productById = id
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
                            cartViewModel?.getAvailableQty(productIdQty)
//                            cartViewModel?.getProductById(id)
                        }
                    }
                )
            }!!
            listRelatedProducts.adapter = productsAdapter
        } else {
            txtRelatedItemLabel.visibility = View.GONE
            txtShowAllRelated.visibility = View.GONE
            listRelatedProducts.visibility = View.GONE
        }
//        } else{
//            productsAdapter!!.offers = products
//            productsAdapter!!.notifyDataSetChanged()
//        }
    }

    override fun onClick(view: View?) {
        if (view == null) return
        when (view.id) {
            R.id.imgBack -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                requireActivity().supportFragmentManager.popBackStack()
            }
            R.id.txtShowAllRelated -> {
//                productsViewModel?.data?.put(UseCaseConstants.PRODUCT_CATEGORY_IDS, "")
//                productsViewModel?.data?.put(UseCaseConstants.PRODUCT_TAG_IDS, "")
//                productsViewModel?.data?.put(UseCaseConstants.PRICE_MIN, "")
//                productsViewModel?.data?.put(UseCaseConstants.PRICE_MAX, "")
//                productsViewModel?.data?.put(UseCaseConstants.SEARCH, "")
//                productsViewModel?.data?.put(UseCaseConstants.SORT_BY, "")
                productsViewModel?._tagsLiveData?.value = Gson().fromJson(
                    sharedPrefs.tags,
                    Array<SettingsResponse.Data.Tag>::class.java
                ).toList() as ArrayList<SettingsResponse.Data.Tag>
                for (i in 0 until shopViewModel?._productCategoryLiveData?.value!!.size) {
                    shopViewModel?._productCategoryLiveData?.value?.get(i)?.checked = false
                }
                productsViewModel!!.filterAvailable = false
                (requireActivity() as AppCompatActivity).hideKeyboard()
                val bundle = Bundle()
                bundle.putString("categoryIds", productId.toString())
                bundle.putString("title", productDetailResponse?.productName)
                bundle.putBoolean("fromRelated", true)
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

            R.id.imgShare -> {
                Utility.showProgress(context = requireContext(), message = "", cancellable = false)
                FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(createShareProductUri(productId = productId.toString()))
                    .setDomainUriPrefix("https://ancoapp.page.link")
                    .setAndroidParameters(DynamicLink.AndroidParameters.Builder("com.app.ancotumFirebaseAnalyticsrf").build())
                    .setIosParameters(DynamicLink.IosParameters.Builder("com.ancoturf").build())
                    .buildShortDynamicLink()
                    .addOnCompleteListener {
                        if (it?.isSuccessful) {
                            val bundle = Bundle()
                            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, productDetailResponse?.productId.toString())
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, productDetailResponse?.productName)
                            mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SHARE, bundle)
//                            shortenUrl = it.result?.shortLink!!
                            Utility.shareUrl(
                                requireActivity() as AppCompatActivity,
                                it.result?.shortLink!!.toString()
                            )


                        } else {
                            Utility.cancelProgress()
                        }
                    }.addOnFailureListener {
                        Utility.cancelProgress()
                    }

             /*   Utility.shareUrl(
                    requireActivity() as AppCompatActivity,
                    getShareUrl(productDetailResponse!!.id).toString()
                )*/
                Utility.cancelProgress()


                /*Utility.shareUrl(
                    requireActivity() as AppCompatActivity,
                    getString(R.string.share_url)
                )*/
            }

            R.id.txtAddToCart -> {
                if (productDetailResponse!!.inStock != 0) {
                    Utility.showProgress(
                        context = requireContext(),
                        message = "",
                        cancellable = false
                    )
                    if (!Utility.isValueNull(edtQuantity.text.toString()) && edtQuantity.text.toString()
                            .toInt() > 0
                    ) {
                        if (productDetailResponse != null) {
                            if (edtQuantity.text.toString()
                                    .toInt() >= productDetailResponse!!.minimumQuantity
                            ) {
                                addToCartProduct = productDetailResponse
                                if (addToCartProduct != null) {
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
                                productById = productDetailResponse!!.id
                                cartViewModel?.getAvailableQty(productIdQty)


                                val Quantity = edtQuantity.text.toString()
                                val itemArrayAddtocart = Bundle().apply {
                                    putString(FirebaseAnalytics.Param.ITEM_ID,productDetailResponse?.productId.toString())
                                   // putString(FirebaseAnalytics.Param.QUANTITY,productDetailResponse?.qty.toString())
                                    putLong(FirebaseAnalytics.Param.QUANTITY,Quantity.toLong())
                                    putString(FirebaseAnalytics.Param.VALUE,productDetailResponse?.price.toString())
                                    putString(FirebaseAnalytics.Param.ITEM_CATEGORY,productDetailResponse?.productCategory?.displayName)
                                    putString(FirebaseAnalytics.Param.ITEM_NAME,productDetailResponse?.productName)
                                }
                                mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.ADD_TO_CART) {
                                    param(FirebaseAnalytics.Param.CURRENCY, "USD")
                                   /* param(FirebaseAnalytics.Param.ITEM_ID,productDetailResponse?.productId.toString())
                                    param(FirebaseAnalytics.Param.QUANTITY,productDetailResponse?.qty.toString())
                                    param(FirebaseAnalytics.Param.VALUE,productDetailResponse?.price.toString())
                                    param(FirebaseAnalytics.Param.ITEM_CATEGORY,productDetailResponse?.productCategory?.displayName!!)
                                    param(FirebaseAnalytics.Param.ITEM_NAME,productDetailResponse?.productName!!)
*/
                                    param(FirebaseAnalytics.Param.ITEMS, arrayOf(itemArrayAddtocart))
                                }

/*Nikita
                                val bundle = Bundle()
                                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, productDetailResponse?.productId.toString())
                                bundle.putString(FirebaseAnalytics.Param.VALUE, productDetailResponse?.price.toString())
                                bundle.putString(FirebaseAnalytics.Param.QUANTITY, productDetailResponse?.qty.toString())
                                bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, productDetailResponse?.productCategory?.displayName)
                                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, productDetailResponse?.productName)
                                mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle)
*/
//                                cartViewModel?.getProductById(productDetailResponse!!.id)
                            } else {
                                Utility.cancelProgress()
                                (requireActivity() as AppCompatActivity).shortToast(
                                    requireActivity().getString(
                                        R.string.invalid_product_quantity_message_for_add_to_cart,
                                        productDetailResponse!!.minimumQuantity.toString()
                                    )
                                )
                            }
                        }
                    } else {
                        Utility.cancelProgress()
                        shortToast(getString(R.string.blank_product_quantity_message))
                    }
                } else {
                    shortToast(getString(R.string.out_of_stock_message))
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

            R.id.linReviewContainer -> {
                if (!reviewOpen) {
                    reviewOpen = !reviewOpen
                    listReviews?.visibility = if (reviewOpen) View.VISIBLE else View.GONE
                    txtAddReview?.visibility = if (reviewOpen) View.VISIBLE else View.GONE
                    txtTitle?.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        if (reviewOpen) ContextCompat.getDrawable(
                            requireContext(), R.drawable.ic_up_arrow
                        ) else ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_down_arrow
                        ),
                        null
                    )
                }
                nestedScroll.smoothScrollTo(
                    0,
                    linContainer.top + linContainer.getChildAt(linContainer.childCount - 1).top
                )
            }
        }
    }

    private fun getShareUrl(productId: Int):Uri {
        var shortenUrl  : Uri = Uri.EMPTY
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(createShareProductUri(productId = productId.toString()))
            .setDomainUriPrefix("https://ancoapp.page.link")
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder("com.app.ancoturf").build())
            .setIosParameters(DynamicLink.IosParameters.Builder("com.ancoturf").build())
            .buildShortDynamicLink()
            .addOnCompleteListener {
                if (it?.isSuccessful) {
                    shortenUrl = it.result?.shortLink!!
                } else {
                    Utility.cancelProgress()
                }
            }.addOnFailureListener {
                Utility.cancelProgress()
            }
        Logger.log("shareUrl->"+ shortenUrl)
        return shortenUrl
    }

    override fun onDestroyView() {
        super.onDestroyView()
        productsViewModel?._relatedProductsLiveData?.value = null
        productsViewModel?._productDetailsLiveData?.value = null
    }

    fun buildDeepLinkingUrl(): Uri {
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://www.example.com/"))
            .setDomainUriPrefix("https://ancoturf.page.link")
            // Open links with this app on Android
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
            // Open links with com.example.ios on iOS
            .setIosParameters(DynamicLink.IosParameters.Builder("com.example.ios").build())
            .buildDynamicLink()
        return dynamicLink.uri
//        ancoapp.page.link
    }

}
