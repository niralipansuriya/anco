package com.app.ancoturf.presentation.home.portfolio

import android.content.Intent
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.portfolio.remote.entity.NonAncoProductRequest
import com.app.ancoturf.data.portfolio.remote.entity.PortfolioDetailResponse
import com.app.ancoturf.data.product.remote.entity.response.ProductCategoryData
import com.app.ancoturf.data.product.remote.entity.response.ProductsResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.openLogoutAlertDialog
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.portfolio.adapters.AncoPortfolioProductsAdapter
import com.app.ancoturf.presentation.home.portfolio.adapters.NonAncoProductsAdapter
import com.app.ancoturf.presentation.home.portfolio.interfaces.OnAncoProductChangeListener
import com.app.ancoturf.presentation.home.portfolio.interfaces.OnNonAncoProductChangeListener
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.view_portfolio_images.*
import javax.inject.Inject

class ViewPortfolioFragment() : BaseFragment(), View.OnClickListener {
    var image: PortfolioDetailResponse.PortfolioImage? = null
    var portfolioId: Int? = 0


    constructor(image: PortfolioDetailResponse.PortfolioImage?, portfolioId: Int) : this() {
        this.image = image
        this.portfolioId = portfolioId
    }

    override fun getContentResource(): Int {
        return R.layout.view_portfolio_images
    }

    private var portfolioMode: Int = Constants.VIEW_PORTFOLIO

    private var portfolioImages: ArrayList<PortfolioDetailResponse.PortfolioImage> = ArrayList()


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var portfolioViewModel: PortfolioViewModel? = null

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private var productCategories = ArrayList<ProductCategoryData>()
    private var portfolioDetailResponse: PortfolioDetailResponse? = null
    private var ancoAddedProductList = ArrayList<PortfolioDetailResponse.Product>()
    private val nonAncoProductList = ArrayList<NonAncoProductRequest>()
    private var nonAncoAddedProductList = ArrayList<PortfolioDetailResponse.CustomProduct>()
    private val deletedCustomProductIds = ArrayList<Int>()
    private val deletedProductIds = ArrayList<Int>()


    private var currentpage: Int = 0


    override fun viewModelSetup() {
        portfolioViewModel = ViewModelProviders.of(
            requireActivity(),
            viewModelFactory
        )[PortfolioViewModel::class.java]
        initObservers()
    }

    private fun initObservers() {
        portfolioViewModel!!.productCategoryLiveData.observe(this, Observer {
            if (productCategories == null)
                productCategories = ArrayList()
            productCategories.clear()
            productCategories.addAll(it)
        })

        portfolioViewModel!!.addEditPortfolioLiveData.observe(this, Observer {
            if (it != null) {
                if (it.success) {
                    shortToast(it.message)
                    (requireActivity() as AppCompatActivity).hideKeyboard()
                    requireActivity().supportFragmentManager.popBackStack()
                    portfolioViewModel?._addEditPortfolioLiveData?.value = null
                }
            }
        })

        portfolioViewModel!!.errorLiveData.observe(this, Observer {
            if (!Utility.isValueNull(portfolioViewModel!!.errorLiveData.value)) {
                if (it.equals(ErrorConstants.UNAUTHORIZED_ERROR_CODE)) {
                    if (activity is HomeActivity) {
                        (activity as HomeActivity).cartViewModel?.deleteProduct(null)
                        (activity as HomeActivity).cartViewModel?.deleteCoupon(null)
                    } else if (activity is PaymentActivity) {
                        (activity as PaymentActivity).cartViewModel?.deleteProduct(null)
                        (activity as PaymentActivity).cartViewModel?.deleteCoupon(null)
                    } else if (activity is ProfileActivity) {
                        (activity as ProfileActivity).cartViewModel?.deleteProduct(null)
                        (activity as ProfileActivity).cartViewModel?.deleteCoupon(null)
                    }
                    (requireActivity() as AppCompatActivity).openLogoutAlertDialog(sharedPrefs = sharedPrefs)
                } else {
                    it?.let { it1 -> shortToast(it1) }
                }
                portfolioViewModel!!._errorLiveData.value = null
            }
        })

        portfolioViewModel!!.ancoProductLiveData.observe(this, Observer {
            setAncoProduct()
        })

        portfolioViewModel!!.portfolioDetailLiveData.observe(this, Observer {
            portfolioDetailResponse = it
            if (nonAncoAddedProductList == null)
                nonAncoAddedProductList = ArrayList()
            nonAncoAddedProductList.clear()
            portfolioDetailResponse?.customProducts?.let { it1 -> nonAncoAddedProductList.addAll(it1) }

            if (ancoAddedProductList == null)
                ancoAddedProductList = ArrayList()
            ancoAddedProductList.clear()
            portfolioDetailResponse?.products?.let { it1 -> ancoAddedProductList.addAll(it1) }

            setData()
        })
    }

    override fun viewSetup() {
        if (image == null) {
            nestedScroll.visibility = VISIBLE
            portfolio_image.visibility = GONE

            nestedScroll.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS)
            nestedScroll.setFocusable(true)
            nestedScroll.setFocusableInTouchMode(true)
            nestedScroll.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    v?.requestFocusFromTouch()
                    return false
                }
            })


            portfolioViewModel?.callGetProductCategories()
            if (portfolioMode != Constants.ADD_PORTFOLIO && portfolioId != 0) {
                portfolioViewModel?.callGetPortfolioDetails(portfolioId!!)
            }

            edtProjectBudget.inputType =
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
            setData()

        } else {
            nestedScroll.visibility = GONE
            portfolio_image.visibility = VISIBLE
            if (image?.featured!!) {
                //portfolio_image.setBackgroundResource(R.drawable.bg_green_line)
            } else
                portfolio_image.setBackgroundResource(R.drawable.bg_project_images)
            Glide.with(requireContext()).load(image?.imageUrl).into(portfolio_image)

            portfolio_image.setOnClickListener(this)
        }
    }

    private fun setData() {
        when (portfolioMode) {

            Constants.VIEW_PORTFOLIO -> {


                txtAddAncoTurfProduct.visibility = View.GONE
                listAncoTurfProduct.visibility = View.VISIBLE
                txtAddNonAncoProduct.visibility = View.GONE
                listNonAncoProduct.visibility = View.VISIBLE
                txtAddProduct.visibility = View.GONE
                txtAddPortfolioImage.visibility = View.GONE
                txtCoverImageInfoLabel.visibility = View.GONE
                txtPublishPortfolio.text = getString(R.string.back_to_portfolio)

//                scrollImages.visibility = View.GONE
                reyImage.visibility = View.GONE
                imgArrowNext.visibility = View.GONE
                imgArrowPrevious.visibility = View.GONE
                edtProjectName.isEnabled = false
                edtProjectDesc.isEnabled = false
                edtProjectAddress.isEnabled = false
                edtProjectBudget.isEnabled = false
            }
        }

        if (portfolioDetailResponse != null && portfolioMode != Constants.ADD_PORTFOLIO) {

            edtProjectName.setText(portfolioDetailResponse!!.projectName)
//            edtProjectName.visibility = View.GONE
//            txtProjectNameLabel.visibility = View.GONE
            edtProjectBudget.setText(
                "$" + (Utility.formatNumber(
                    portfolioDetailResponse!!.budget.toString().replace(
                        "$",
                        ""
                    ).replace(",", "").toLong()
                ))
            )
            edtProjectAddress.setText(portfolioDetailResponse!!.address)
            edtProjectDesc.setText(portfolioDetailResponse!!.projectDescription)

            portfolioImages.clear()

            if (portfolioDetailResponse!!.portfolioImages == null || portfolioDetailResponse!!.portfolioImages.size == 0) {
                portfolioImages.add(
                    PortfolioDetailResponse.PortfolioImage(
                        id = -1,
                        imagePlaceHolder = R.drawable.img_place_holder
                    )
                )

            } else {
                portfolioImages.addAll(portfolioDetailResponse!!.portfolioImages)
            }

            currentpage = 0;

            //set anco product adapter
            setAncoProduct()

            //set non anco product adapter
            setNonAncoProduct()

            if ((portfolioDetailResponse!!.products == null || portfolioDetailResponse!!.products.size == 0) && (portfolioDetailResponse!!.customProducts == null || portfolioDetailResponse!!.customProducts.size == 0) && portfolioMode == Constants.VIEW_PORTFOLIO) {
                txtProjectUtilisedLabel.visibility = View.GONE
            } else {
                txtProjectUtilisedLabel.visibility = View.VISIBLE
            }

        } else {
            edtProjectName.visibility = View.VISIBLE
            txtProjectNameLabel.visibility = View.VISIBLE

//            portfolioImages.clear()

            if (portfolioImages == null || portfolioImages.size == 0) {
                portfolioImages.add(PortfolioDetailResponse.PortfolioImage(imagePlaceHolder = R.drawable.img_place_holder))
            }



            setAncoProduct()
            setNonAncoProduct()

        }

        if (listAncoTurfProduct.visibility == View.GONE && listNonAncoProduct.visibility == View.GONE) {
            if (portfolioMode != Constants.VIEW_PORTFOLIO) {
                txtAddNonAncoProduct.visibility = View.VISIBLE
                txtAddAncoTurfProduct.visibility = View.VISIBLE
            }
            txtAddProduct.visibility = View.GONE
        } else {
            txtAddNonAncoProduct.visibility = View.GONE
            txtAddAncoTurfProduct.visibility = View.GONE
            if (portfolioMode != Constants.VIEW_PORTFOLIO)
                txtAddProduct.visibility = View.VISIBLE
        }
    }


    private fun convertToCustomProductList(nonAncoProductList: java.util.ArrayList<NonAncoProductRequest>): ArrayList<PortfolioDetailResponse.CustomProduct> {
        val nonAncoProductLocalList = ArrayList<PortfolioDetailResponse.CustomProduct>()
        for (i in 0 until nonAncoProductList!!.size) {
            var nonAnco = PortfolioDetailResponse.CustomProduct()
            nonAnco.id = nonAncoProductList[i].id
            nonAnco.description = nonAncoProductList[i].descriptions
            nonAnco.name = nonAncoProductList[i].name
            nonAnco.price = nonAncoProductList[i].price.toString()
            var pivot = PortfolioDetailResponse.CustomProduct.Pivot()
            pivot.quantity = nonAncoProductList[i].qty
            pivot.portfolioId = portfolioId!!
            nonAnco.pivot = pivot
            nonAncoProductLocalList.add(nonAnco)
        }
        return nonAncoProductLocalList
    }

    private fun setNonAncoProduct() {
        var nonAncoProductLocalList = ArrayList<PortfolioDetailResponse.CustomProduct>()
        if (nonAncoAddedProductList != null && nonAncoAddedProductList.size > 0) {
            nonAncoProductLocalList.addAll(nonAncoAddedProductList)
        }

        if (nonAncoProductList != null && nonAncoProductList.size > 0) {
            nonAncoProductLocalList.addAll(convertToCustomProductList(nonAncoProductList))
        }

        if (nonAncoProductLocalList != null && nonAncoProductLocalList.size > 0) {
            val nonAncoProductsAdapter = NonAncoProductsAdapter(
                requireActivity() as AppCompatActivity,
                nonAncoProductLocalList,
                portfolioMode != Constants.VIEW_PORTFOLIO,
                object : OnNonAncoProductChangeListener {
                    override fun onItemDelete(product: PortfolioDetailResponse.CustomProduct) {
                        portfolioViewModel?.isAnythingEdited = true
                        if (product.id != 0)
                            deletedCustomProductIds.add(product.id)
                        if (isInLocalCustomProducts(product) != null)
                            nonAncoProductList.remove(isInLocalCustomProducts(product))
                        else
                            nonAncoAddedProductList.remove(product)
                        setNonAncoProduct()
                    }

                    override fun onClick(product: PortfolioDetailResponse.CustomProduct) {

                    }
                })
            listNonAncoProduct.adapter = nonAncoProductsAdapter
            txtAddAncoTurfProduct.visibility = View.GONE
            txtAddNonAncoProduct.visibility = View.GONE
            if (portfolioMode != Constants.VIEW_PORTFOLIO)
                txtAddProduct.visibility = View.VISIBLE
            listNonAncoProduct.visibility = View.VISIBLE
        } else {
            if (listAncoTurfProduct.visibility == View.VISIBLE) {
                txtAddNonAncoProduct.visibility = View.GONE
                txtAddAncoTurfProduct.visibility = View.GONE
                if (portfolioMode != Constants.VIEW_PORTFOLIO)
                    txtAddProduct.visibility = View.VISIBLE
            } else {
                if (portfolioMode != Constants.VIEW_PORTFOLIO) {
                    txtAddAncoTurfProduct.visibility = View.VISIBLE
                    txtAddNonAncoProduct.visibility = View.VISIBLE
                }
                txtAddProduct.visibility = View.GONE
            }
            listNonAncoProduct.visibility = View.GONE
        }
    }

    private fun convertToProductList(products: ArrayList<ProductsResponse.Data>?): ArrayList<PortfolioDetailResponse.Product> {
        val ancoProductList = ArrayList<PortfolioDetailResponse.Product>()
        for (i in 0 until products!!.size) {
            var product = PortfolioDetailResponse.Product()
            product.id = products[i].productId
            product.featureImageUrl = products[i].featureImageUrl
            product.price = products[i].price
            product.name = products[i].productName
            product.productUnit = products[i].productUnit
            product.inStock = products[i].inStock
            product.minimumQuantity = products[i].minimumQuantity
            var pivot = PortfolioDetailResponse.Product.Pivot()
            pivot.quantity = products[i].qty
            pivot.productId = products[i].productId
            pivot.portfolioId = portfolioId!!
            product.pivot = pivot
            ancoProductList.add(product)
        }
        return ancoProductList
    }

    private fun isInProductList(id: Int): Boolean {
        if (portfolioDetailResponse != null && portfolioDetailResponse!!.products != null && portfolioDetailResponse!!.products.size > 0) {
            for (i in 0 until portfolioDetailResponse!!.products.size) {
                if (portfolioDetailResponse!!.products[i].id == id)
                    return true
            }
        }
        return false
    }

    private fun isInLocalProductList(id: Int): Int {
        if (portfolioViewModel?.ancoProductLiveData!!.value != null && portfolioViewModel?.ancoProductLiveData!!.value!!.size > 0) {
            for (i in 0 until portfolioViewModel?.ancoProductLiveData!!.value!!.size) {
                if (portfolioViewModel?.ancoProductLiveData!!.value!!.get(i).productId == id)
                    return i
            }
        }
        return -1
    }

    private fun isInLocalCustomProducts(product: PortfolioDetailResponse.CustomProduct): NonAncoProductRequest? {
        var nonAnco = NonAncoProductRequest()
        nonAnco.id = 0
        nonAnco.descriptions = product.description
        nonAnco.name = product.name
        nonAnco.price = product.price.toFloat()
        nonAnco.qty = product.pivot.quantity
        if (nonAncoProductList.contains(nonAnco)) {
            return nonAnco
        }
        return null
    }

    private fun setAncoProduct() {
        val ancoProductList = ArrayList<PortfolioDetailResponse.Product>()
        if (ancoAddedProductList != null && ancoAddedProductList.size > 0) {
            ancoProductList.addAll(ancoAddedProductList)
        }

        if (portfolioViewModel?.ancoProductLiveData!!.value != null) {
            ancoProductList.addAll(convertToProductList(portfolioViewModel?.ancoProductLiveData!!.value))
        }

        if (ancoProductList != null && ancoProductList.size > 0) {
            val ancoProductsAdapter = AncoPortfolioProductsAdapter(
                requireActivity() as AppCompatActivity,
                ancoProductList, portfolioMode != Constants.VIEW_PORTFOLIO,
                object : OnAncoProductChangeListener {
                    override fun onItemDelete(product: PortfolioDetailResponse.Product) {
                        portfolioViewModel?.isAnythingEdited = true
                        if (isInProductList(product.id)) {
                            deletedProductIds.add(product.id)
                            ancoAddedProductList.remove(product)
                            portfolioViewModel?.portfolioDetailLiveData?.value?.products?.remove(
                                product
                            )
                        } else if (isInLocalProductList(product.id) != -1) {
                            portfolioViewModel?.ancoProductLiveData!!.value!!.removeAt(
                                isInLocalProductList(product.id)
                            )
                        }
                        setAncoProduct()
                    }

                    override fun onQuntityChange(product: PortfolioDetailResponse.Product) {
                        portfolioViewModel?.isAnythingEdited = true
                        if (portfolioViewModel!!.ancoProductLiveData.value != null && portfolioViewModel!!.ancoProductLiveData.value!!.size > 0) {
                            for (i in 0 until portfolioViewModel!!.ancoProductLiveData.value!!.size) {
                                if (product.id == portfolioViewModel!!.ancoProductLiveData.value?.get(
                                        i
                                    )?.productId
                                )
                                    portfolioViewModel!!.ancoProductLiveData.value?.get(i)?.qty =
                                        product.pivot.quantity
                            }
                        } else {
                            ancoAddedProductList.removeAt(getIndex(product.id))
                            portfolioViewModel!!._ancoProductLiveData.value = ArrayList()
                            portfolioViewModel!!._ancoProductLiveData.value?.add(
                                convertToProductResponse(product)
                            )
                        }
                    }
                })
            listAncoTurfProduct.adapter = ancoProductsAdapter
            txtAddAncoTurfProduct.visibility = View.GONE
            txtAddNonAncoProduct.visibility = View.GONE
            if (portfolioMode != Constants.VIEW_PORTFOLIO)
                txtAddProduct.visibility = View.VISIBLE
            listAncoTurfProduct.visibility = View.VISIBLE
        } else {
            if (listNonAncoProduct.visibility == View.VISIBLE) {
                txtAddAncoTurfProduct.visibility = View.GONE
                txtAddNonAncoProduct.visibility = View.GONE
                if (portfolioMode != Constants.VIEW_PORTFOLIO)
                    txtAddProduct.visibility = View.VISIBLE
            } else {
                if (portfolioMode != Constants.VIEW_PORTFOLIO) {
                    txtAddAncoTurfProduct.visibility = View.VISIBLE
                    txtAddNonAncoProduct.visibility = View.VISIBLE
                }
                txtAddProduct.visibility = View.GONE
            }
            listAncoTurfProduct.visibility = View.GONE
        }
    }

    private fun getIndex(id: Int): Int {
        if (ancoAddedProductList != null && ancoAddedProductList.size > 0) {
            for (i in 0..ancoAddedProductList.size) {
                if (ancoAddedProductList[i].id == id)
                    return i
            }
        }
        return -1
    }

    private fun convertToProductResponse(product: PortfolioDetailResponse.Product): ProductsResponse.Data {
        var productResp: ProductsResponse.Data = ProductsResponse.Data()
        productResp.qty = product.pivot.quantity
        productResp.productId = product.id
        return productResp;
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.portfolio_image -> {
                if (image!=null && portfolioImages?.size>0){
                    var intent = Intent(activity,ViewImageActivity::class.java)
                    intent.putExtra(ViewImageActivity.IMAGES,portfolioImages)
                    intent.putExtra(ViewImageActivity.POSITION,portfolioImages.indexOf(image!!))
                    startActivity(intent)
                }
            }
        }
    }
}