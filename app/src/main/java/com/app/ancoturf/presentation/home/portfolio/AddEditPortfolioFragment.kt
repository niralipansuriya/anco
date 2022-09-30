package com.app.ancoturf.presentation.home.portfolio

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.portfolio.remote.entity.NonAncoProductRequest
import com.app.ancoturf.data.portfolio.remote.entity.PortfolioDetailResponse
import com.app.ancoturf.data.product.remote.entity.response.ProductCategoryData
import com.app.ancoturf.data.product.remote.entity.response.ProductsResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.extension.*
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.home.portfolio.adapters.*
import com.app.ancoturf.presentation.home.portfolio.interfaces.OnAncoProductChangeListener
import com.app.ancoturf.presentation.home.portfolio.interfaces.OnNonAncoProductChangeListener
import com.app.ancoturf.presentation.home.portfolio.interfaces.OnPagerImageClickListener
import com.app.ancoturf.presentation.home.portfolio.utils.ItemClickListener
import com.app.ancoturf.presentation.home.portfolio.utils.ItemTouchHelperAdapter
import com.app.ancoturf.presentation.home.portfolio.utils.SimpleItemTouchHelperCallback
import com.app.ancoturf.presentation.home.shop.adapters.ProductCategoryAdapter
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.CheckImageOrientation
import com.app.ancoturf.utils.InputFilterMinMax
import com.app.ancoturf.utils.Utility
import com.app.ancoturf.utils.interfces.OnDialogButtonClick
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.fragment_add_portfolio.*
import kotlinx.android.synthetic.main.header.*
import java.io.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class AddEditPortfolioFragment() :
    BaseFragment(),
    View.OnClickListener, ItemTouchHelperAdapter, ItemClickListener {

    private var portfolioMode: Int = Constants.VIEW_PORTFOLIO
    private var portfolioId: Int = 0

    private var portfolioImages: ArrayList<PortfolioDetailResponse.PortfolioImage> = ArrayList()
    private var cameraUri: Uri? = null
    private var selectedPhotoUri: Uri? = null
    private var selectedPhotoUriList: ArrayList<Uri>? = ArrayList()
    private var selectedFilePath: String? = null
    private var mItemTouchHelper: ItemTouchHelper? = null

    var portfolioImagesAdapter: ImagesPortfolioAdapter? = null
    var slidingImageAdapter: SlidingImageAdapter? = null


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
    private val deletedImageIds = ArrayList<Int>()
    private val deletedCustomProductIds = ArrayList<Int>()
    private val deletedProductIds = ArrayList<Int>()
    private var projectNameTextWatcher: TextWatcher? = null
    private var projectBudgetTextWatcher: TextWatcher? = null
    private var projectAddressTextWatcher: TextWatcher? = null
    private var projectDescTextWatcher: TextWatcher? = null

    private var currentpage: Int = 0
    private var adapterViewModeImages: PortfolioImagePagerAdapter? = null

    override fun getContentResource(): Int = R.layout.fragment_add_portfolio

    override fun viewModelSetup() {
        portfolioViewModel = ViewModelProviders.of(
            requireActivity(),
            viewModelFactory
        )[PortfolioViewModel::class.java]
        initObservers()
    }

    override fun viewSetup() {
        arguments?.let {
            portfolioMode = it.getInt("portfolioMode")
            portfolioId = it.getInt("portfolioId")
            arguments = null
        }

        projectNameTextWatcher = MyTextWatcher(edtProjectName)
        projectBudgetTextWatcher = MyTextWatcher(edtProjectBudget)
        projectAddressTextWatcher = MyTextWatcher(edtProjectAddress)
        projectDescTextWatcher = MyTextWatcher(edtProjectDesc)


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

        txtAddAncoTurfProduct.setOnClickListener(this)
        txtAddNonAncoProduct.setOnClickListener(this)
        txtAddPortfolioImage.setOnClickListener(this)
        txtAddProduct.setOnClickListener(this)
        txtPublishPortfolio.setOnClickListener(this)
        txtDeletePortfolio.setOnClickListener(this)
        imgBack.setOnClickListener(this)
        imgEdit.setOnClickListener(this)
        imgShare.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        edtProjectAddress.setOnClickListener(this)


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
            Utility.showProgress(requireContext(), "", false)
            portfolioViewModel?.callGetPortfolioDetails(portfolioId)
        }

        edtProjectBudget.inputType =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        setData()
    }

    /*var binding: FragmentAddPortfolioBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_portfolio, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        viewSetup()
    }

    private fun setupViewModel() {
        requireActivity().requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        portfolioViewModel = ViewModelProviders.of(
            requireActivity()
        )[PortfolioViewModel::class.java]
        initObservers()
        binding?.setVariable(0,portfolioViewModel)
    }

    private fun viewSetup() {
        arguments?.let {
            portfolioMode = it.getInt("portfolioMode")
            portfolioId = it.getInt("portfolioId")
            arguments = null
        }

        projectNameTextWatcher = MyTextWatcher(edtProjectName)
        projectBudgetTextWatcher = MyTextWatcher(edtProjectBudget)
        projectAddressTextWatcher = MyTextWatcher(edtProjectAddress)
        projectDescTextWatcher = MyTextWatcher(edtProjectDesc)


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

        txtAddAncoTurfProduct.setOnClickListener(this)
        txtAddNonAncoProduct.setOnClickListener(this)
        txtAddPortfolioImage.setOnClickListener(this)
        txtAddProduct.setOnClickListener(this)
        txtPublishPortfolio.setOnClickListener(this)
        imgBack.setOnClickListener(this)
        imgEdit.setOnClickListener(this)
        imgShare.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        edtProjectAddress.setOnClickListener(this)


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
            Utility.showProgress(requireContext(), "", false)
            portfolioViewModel?.callGetPortfolioDetails(portfolioId)
        }

        edtProjectBudget.inputType =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        setData()
    }*/


    inner class MyTextWatcher(private val editText: EditText) : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when (editText.id) {
                R.id.edtProjectName -> {
                    portfolioViewModel?.isAnythingEdited = true
                }
                R.id.edtProjectBudget -> {
                    portfolioViewModel?.isAnythingEdited = true
                    if (!Utility.isValueNull(s.toString()) && !Utility.isValueNull(
                            s.toString().replace(
                                "$",
                                ""
                            ).replace(",", "")
                        ) && before != count
                    )
                        edtProjectBudget.setText(
                            "$" + (Utility.formatNumber(
                                s.toString().replace(
                                    "$",
                                    ""
                                ).replace(",", "").toLong()
                            ))
                        )
                    edtProjectBudget.text?.length?.let { edtProjectBudget.setSelection(it) }
                }
                R.id.edtProjectAddress -> {
                    portfolioViewModel?.isAnythingEdited = true
                }

                R.id.edtProjectDesc -> {
                    portfolioViewModel?.isAnythingEdited = true
                }
            }
        }
    }

    private fun setData() {
        imagePagerIndicator.visibility = View.GONE

        when (portfolioMode) {
            Constants.ADD_PORTFOLIO -> {
                rlViewModeImages.visibility = View.GONE
                nestedScroll.visibility = View.VISIBLE
                txtAddAncoTurfProduct.visibility = View.VISIBLE
                projectImagePager.visibility = View.VISIBLE
                imagePagerIndicator.visibility = View.VISIBLE
                txtProjectImagesLabel.visibility = View.VISIBLE
                listAncoTurfProduct.visibility = View.GONE
                txtDeletePortfolio.visibility = View.GONE
                txtAddNonAncoProduct.visibility = View.VISIBLE
                listNonAncoProduct.visibility = View.GONE
                txtAddProduct.visibility = View.GONE
                txtAddPortfolioImage.visibility = View.VISIBLE
                txtCoverImageInfoLabel.visibility = View.VISIBLE
                txtPublishPortfolio.text = getString(R.string.publish_portfolio)
                imgEdit.visibility = View.GONE
                imgShare.visibility = View.GONE
                reyImage.visibility = View.GONE
                imgArrowNext.visibility = View.GONE
                imgArrowPrevious.visibility = View.GONE
                edtProjectName.isEnabled = true
                edtProjectDesc.isEnabled = true
                edtProjectAddress.isEnabled = true
                edtProjectAddress.isScrollContainer = true
                edtProjectAddress.overScrollMode = View.OVER_SCROLL_ALWAYS
                edtProjectAddress.scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
                edtProjectAddress.movementMethod = ScrollingMovementMethod()
                edtProjectAddress.isVerticalScrollBarEnabled = true
                edtProjectAddress.isFocusable = true
                edtProjectAddress.setOnTouchListener(object : View.OnTouchListener {
                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                        if (edtProjectAddress.hasFocus()) {
                            v?.parent?.requestDisallowInterceptTouchEvent(true)
                            when (event?.action) {
                                MotionEvent.ACTION_SCROLL -> {
                                    v?.parent?.requestDisallowInterceptTouchEvent(false);
                                    return true
                                }
                            }
                        }
                        return false;
                    }
                })
                edtProjectBudget.isEnabled = true
            }
            Constants.EDIT_PORTFOLIO -> {
                txtDeletePortfolio.visibility = View.VISIBLE
                nestedScroll.visibility = View.VISIBLE
                rlViewModeImages.visibility = View.GONE
                projectImagePager.visibility = View.VISIBLE
                imagePagerIndicator.visibility = View.VISIBLE
                txtProjectImagesLabel.visibility = View.VISIBLE
                txtAddAncoTurfProduct.visibility = View.GONE
                listAncoTurfProduct.visibility = View.VISIBLE
                txtAddNonAncoProduct.visibility = View.GONE
                listNonAncoProduct.visibility = View.VISIBLE
                txtAddProduct.visibility = View.VISIBLE
                txtAddPortfolioImage.visibility = View.VISIBLE
                txtCoverImageInfoLabel.visibility = View.VISIBLE
                txtPublishPortfolio.text = getString(R.string.apply)
                imgShare.visibility = View.GONE
                imgEdit.visibility = View.VISIBLE
                imgEdit.setColorFilter(
                    ContextCompat.getColor(
                        activity as AppCompatActivity,
                        R.color.theme_green
                    )
                )
                reyImage.visibility = View.VISIBLE
                imgArrowNext.visibility = View.VISIBLE
                imgArrowPrevious.visibility = View.VISIBLE
                edtProjectName.isEnabled = true
                edtProjectDesc.isEnabled = true
                edtProjectAddress.isEnabled = true
                edtProjectAddress.isScrollContainer = true
                edtProjectAddress.overScrollMode = View.OVER_SCROLL_ALWAYS
                edtProjectAddress.scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
                edtProjectAddress.movementMethod = ScrollingMovementMethod()
                edtProjectAddress.isVerticalScrollBarEnabled = true
                edtProjectAddress.isFocusable = true
                edtProjectAddress.setOnTouchListener(object : View.OnTouchListener {
                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                        if (edtProjectAddress.hasFocus()) {
                            v?.parent?.requestDisallowInterceptTouchEvent(true)
                            when (event?.action) {
                                MotionEvent.ACTION_SCROLL -> {
                                    v?.parent?.requestDisallowInterceptTouchEvent(false);
                                    return true
                                }
                            }
                        }
                        return false;
                    }
                })
                edtProjectBudget.isEnabled = true
            }


            Constants.VIEW_PORTFOLIO -> {
                //Dev_N
//                nestedScrollViewMode.visibility = View.VISIBLE

//                vpImages.visibility = View.VISIBLE
                txtDeletePortfolio.visibility = View.GONE
                nestedScroll.visibility = View.VISIBLE
                rlViewModeImages.visibility = View.VISIBLE

                projectImagePager.visibility = View.GONE
                imagePagerIndicator.visibility = View.GONE
                txtProjectImagesLabel.visibility = View.GONE


                txtAddAncoTurfProduct.visibility = View.GONE
                listAncoTurfProduct.visibility = View.VISIBLE
                txtAddNonAncoProduct.visibility = View.GONE
                listNonAncoProduct.visibility = View.VISIBLE
                txtAddProduct.visibility = View.GONE
                txtAddPortfolioImage.visibility = View.GONE
                txtCoverImageInfoLabel.visibility = View.GONE
                txtPublishPortfolio.text = getString(R.string.back_to_portfolio)
                imgShare.visibility = View.VISIBLE
                imgEdit.visibility = View.VISIBLE
                imgEdit.setColorFilter(
                    ContextCompat.getColor(
                        activity as AppCompatActivity,
                        android.R.color.black
                    )
                )
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
        setTextWatchers()

        if (portfolioDetailResponse != null && portfolioMode != Constants.ADD_PORTFOLIO) {
            txtTitle.text = portfolioDetailResponse!!.projectName
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

            if (portfolioDetailResponse!!.portfolioImages.size == 0) {
                portfolioImages.add(
                    PortfolioDetailResponse.PortfolioImage(
                        id = -1,
                        imagePlaceHolder = R.drawable.img_place_holder
                    )
                )
                imagePagerIndicator.visibility = View.GONE
            } else {
                portfolioImages.addAll(portfolioDetailResponse!!.portfolioImages)

                imagePagerIndicator.visibility =
                    if (portfolioMode == Constants.ADD_PORTFOLIO || portfolioMode == Constants.EDIT_PORTFOLIO) View.VISIBLE else View.GONE
            }


            if (adapterViewModeImages == null) {
                adapterViewModeImages = PortfolioImagePagerAdapter(
                    requireActivity() as AppCompatActivity,
                    portfolioImages,

                    object : OnPagerImageClickListener {
                        override fun OnPagerImageClickListener() {
                            if (portfolioMode == Constants.VIEW_PORTFOLIO) {
                                var intent =
                                    Intent(requireActivity(), ViewImageActivity::class.java)
                                intent.putExtra(ViewImageActivity.IMAGES, portfolioImages)
                                intent.putExtra(
                                    ViewImageActivity.POSITION,
                                    viewPagerOfViewModen.currentItem
                                )
                                startActivity(intent)
                            }
                        }
                    }
                )
                viewPagerOfViewModen.adapter = adapterViewModeImages
                pageIndicatorViewModen.setViewPager(viewPagerOfViewModen)
                pageIndicatorViewModen.visibility =
                    if (portfolioImages.size > 1) View.VISIBLE else View.GONE
            }
            currentpage = 0;
            //set image pager adapter
            setImagePager(currentpage)

            val portfolioImagePagerAdapter = PortfolioImagePagerAdapter(
                requireActivity() as AppCompatActivity,
                portfolioImages,
                object : OnPagerImageClickListener {
                    override fun OnPagerImageClickListener() {
                        if (!portfolioMode.equals(Constants.VIEW_PORTFOLIO)) {
                            if (portfolioImages != null && portfolioImages.size < sharedPrefs.max_portfolio_images) {
                                openImageCaptureDialog()
                            } else {
                                shortToast("You can add maximum " + sharedPrefs.max_portfolio_images + " images")
                            }
                        }
                    }
                }
            )


            projectImagePager.adapter = portfolioImagePagerAdapter

            projectImagePager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    currentpage = position
                }
            })

            imagePagerIndicator.setViewPager(projectImagePager)

            //set horizontal view
            if (portfolioMode != Constants.VIEW_PORTFOLIO)
                setImageHorizontalView()

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
            txtTitle.text = getString(R.string.new_portfolio)
            edtProjectName.visibility = View.VISIBLE
            txtProjectNameLabel.visibility = View.VISIBLE


            if (portfolioImages.size == 0) {
//                portfolioImages.add(PortfolioDetailResponse.PortfolioImage(imagePlaceHolder = R.drawable.img_place_holder))
                portfolioImages.add(
                    PortfolioDetailResponse.PortfolioImage(
                        id = -1,
                        imagePlaceHolder = R.drawable.img_place_holder
                    )
                ) //nnn
            }
            val portfolioImagePagerAdapter = PortfolioImagePagerAdapter(
                requireActivity() as AppCompatActivity,
                portfolioImages,
                object : OnPagerImageClickListener {
                    override fun OnPagerImageClickListener() {
                        if (!portfolioMode.equals(Constants.VIEW_PORTFOLIO)) {
                            if (portfolioImages != null && portfolioImages.size < sharedPrefs.max_portfolio_images) {
                                openImageCaptureDialog()
                            } else {
                                shortToast("You can add maximum " + sharedPrefs.max_portfolio_images + " images")
                            }
                        }
                    }
                }
            )

            if (portfolioMode != Constants.VIEW_PORTFOLIO)
                setImageHorizontalView()

            projectImagePager.adapter = portfolioImagePagerAdapter
            imagePagerIndicator.setViewPager(projectImagePager)

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

    private fun isInList(product: PortfolioDetailResponse.Product): Int {
        if (portfolioViewModel!!._ancoProductLiveData.value != null && portfolioViewModel!!._ancoProductLiveData.value!!.size > 0) {
            for (i in 0 until portfolioViewModel!!._ancoProductLiveData.value!!.size) {
                if (portfolioViewModel!!._ancoProductLiveData.value!![i].productId == product.id)
                    return i
            }
        }
        return -1
    }

    private fun setTextWatchers() {
        if (!portfolioMode.equals(Constants.VIEW_PORTFOLIO)) {
            edtProjectName.addTextChangedListener(projectNameTextWatcher)
            edtProjectBudget.addTextChangedListener(projectBudgetTextWatcher)
            edtProjectAddress.addTextChangedListener(projectAddressTextWatcher)
            edtProjectDesc.addTextChangedListener(projectDescTextWatcher)
        } else {
            edtProjectName.removeTextChangedListener(projectNameTextWatcher)
            edtProjectBudget.removeTextChangedListener(projectBudgetTextWatcher)
            edtProjectAddress.removeTextChangedListener(projectAddressTextWatcher)
            edtProjectDesc.removeTextChangedListener(projectDescTextWatcher)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        edtProjectName.removeTextChangedListener(projectNameTextWatcher)
        edtProjectBudget.removeTextChangedListener(projectBudgetTextWatcher)
        edtProjectAddress.removeTextChangedListener(projectAddressTextWatcher)
        edtProjectDesc.removeTextChangedListener(projectDescTextWatcher)
    }

    private fun setImagePager(currentItem: Int) {
        if (portfolioImages.size == 0) {
            portfolioImages.add(
                PortfolioDetailResponse.PortfolioImage(
                    id = -1,
                    imagePlaceHolder = R.drawable.img_place_holder
                )
            )
            imagePagerIndicator.visibility = View.GONE
        } else {
            if (portfolioMode == Constants.VIEW_PORTFOLIO)
                imagePagerIndicator.visibility = View.GONE
            else
                imagePagerIndicator.visibility = View.VISIBLE
        }

        val portfolioImagePagerAdapter = PortfolioImagePagerAdapter(
            requireActivity() as AppCompatActivity,
            portfolioImages,
            object : OnPagerImageClickListener {
                override fun OnPagerImageClickListener() {
                    if (!portfolioMode.equals(Constants.VIEW_PORTFOLIO)) {
                        if (portfolioImages != null && portfolioImages.size < sharedPrefs.max_portfolio_images) {
                            openImageCaptureDialog()
                        } else {
                            shortToast("You can add maximum " + sharedPrefs.max_portfolio_images + " images")
                        }
                    }
                }
            }
        )
        projectImagePager.adapter = portfolioImagePagerAdapter
        imagePagerIndicator.setViewPager(projectImagePager)
        projectImagePager.setCurrentItem(currentItem)

        if (portfolioImages.size > 1 && portfolioMode == Constants.ADD_PORTFOLIO || portfolioMode == Constants.EDIT_PORTFOLIO)
            imagePagerIndicator.visibility = View.VISIBLE
        else
            imagePagerIndicator.visibility = View.GONE
    }

    private fun setImageHorizontalView() {

        if (!(portfolioImages.size == 1 && Utility.isValueNull(
                portfolioImages.get(0).imageUrl
            ) && Utility.isValueNull(portfolioImages.get(0).uri))
        ) {
            reyImage.visibility = View.VISIBLE
            val linearLayoutManager = LinearLayoutManager(activity)
            linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            reyImage.layoutManager = linearLayoutManager
            imgArrowNext.visibility = View.VISIBLE
            imgArrowPrevious.visibility = View.VISIBLE


            if (portfolioImagesAdapter == null) {
                portfolioImagesAdapter = activity?.let {
                    ImagesPortfolioAdapter(
                        it as AppCompatActivity, this,
                        portfolioImages, this
                    )
                }!!
                reyImage.adapter = portfolioImagesAdapter

//                if (portfolioMode == Constants.ADD_PORTFOLIO) {
                val callback: ItemTouchHelper.Callback =
                    SimpleItemTouchHelperCallback(portfolioImagesAdapter as ItemTouchHelperAdapter)
                mItemTouchHelper = ItemTouchHelper(callback)
                mItemTouchHelper!!.attachToRecyclerView(reyImage)
//                }
            } else {
                portfolioImagesAdapter?.notifyDataSetChanged()
            }

        } else {
            imgArrowNext.visibility = View.GONE
            imgArrowPrevious.visibility = View.GONE
            reyImage.visibility = View.GONE
        }


        /*linPagerImages.removeAllViews()
        if (!(portfolioImages.size == 1 && Utility.isValueNull(
                portfolioImages.get(0).imageUrl
            ) && Utility.isValueNull(portfolioImages.get(0).uri))
        ) {
            scrollImages.visibility = View.VISIBLE
            reyImage.visibility = View.VISIBLE
            imgArrowNext.visibility = View.VISIBLE
            imgArrowPrevious.visibility = View.VISIBLE
            for (i in 0 until portfolioImages.size) {
                val portfolioImageView: View =
                    LayoutInflater.from(activity)
                        .inflate(R.layout.item_portfolio_scroll_image, null)
                val imgPortfolio: ImageView = portfolioImageView.findViewById(R.id.imgPortfolio)

                if (!(Utility.isValueNull(portfolioImages[i].imageUrl)))
                    Glide.with(requireActivity()).load(portfolioImages[i].imageUrl).into(
                        imgPortfolio
                    )
                else if (!(Utility.isValueNull(portfolioImages[i].uri)))
                    Glide.with(requireActivity()).load(portfolioImages[i].uri).into(imgPortfolio)

                val imgDelete: ImageView = portfolioImageView.findViewById(R.id.imgDelete)
                imgDelete.setOnClickListener {
                    portfolioViewModel?.isAnythingEdited = true
                    if (portfolioImages.get(i).id != 0) {
                        deletedImageIds.add(portfolioImages.get(i).id)
                    }
                    portfolioImages.removeAt(i)
                    selectedPhotoUriList?.removeAt(i)
                    setImagePager(if (i == 0) 0 else (i - 1))
                    setImageHorizontalView()
                }
                imgPortfolio.setOnClickListener {
                    projectImagePager.setCurrentItem(i)
                }

                val gd =
                    GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                        override fun onDoubleTap(e: MotionEvent?): Boolean {
                            portfolioViewModel?.isAnythingEdited = true
                            for (j in 0 until portfolioImages.size)
                                portfolioImages[j].featured = false
                            portfolioImages[i].featured = true
                            setImagePager(i)
                            projectImagePager.setCurrentItem(i)
                            return true
                        }
                    })

                imgPortfolio.setOnTouchListener { v, event -> gd.onTouchEvent(event) }

                linPagerImages.addView(portfolioImageView)
            }
        } else {
            imgArrowNext.visibility = View.GONE
            imgArrowPrevious.visibility = View.GONE
            scrollImages.visibility = View.GONE
            reyImage.visibility = View.GONE
        }*/
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
                                    portfolioViewModel!!.ancoProductLiveData.value?.get(i)
                                        ?.qty =
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
            pivot.portfolioId = portfolioId
            product.pivot = pivot
            ancoProductList.add(product)
        }
        return ancoProductList
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
                        openAddNonAncoProductDialog(product)
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
            pivot.portfolioId = portfolioId
            nonAnco.pivot = pivot
            nonAncoProductLocalList.add(nonAnco)
        }
        return nonAncoProductLocalList
    }

    private fun initObservers() {
        portfolioViewModel!!.productCategoryLiveData.observe(this, Observer {
            if (productCategories == null)
                productCategories = ArrayList()
            productCategories.clear()
            productCategories.addAll(it)
        })

        portfolioViewModel!!.deletePortfolioLiveData.observe(this, Observer {
            if (it != null) {
                if (it.success) {
                    shortToast(it.message)
                    (requireActivity() as AppCompatActivity).hideKeyboard()
                    requireActivity().supportFragmentManager.popBackStack()
                    portfolioViewModel?._deletePortfolioLiveData?.value = null
                    portfolioViewModel?._addEditPortfolioLiveData?.value = null
                }
            }
        }
        )

        portfolioViewModel!!.addEditPortfolioLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
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
                Utility.cancelProgress()
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
            portfolioDetailResponse?.customProducts?.let { it1 ->
                nonAncoAddedProductList.addAll(
                    it1
                )
            }

            if (ancoAddedProductList == null)
                ancoAddedProductList = ArrayList()
            ancoAddedProductList.clear()
            portfolioDetailResponse?.products?.let { it1 -> ancoAddedProductList.addAll(it1) }

            setData()

            when (portfolioMode) {
                Constants.VIEW_PORTFOLIO -> {
                    if (portfolioDetailResponse != null && portfolioDetailResponse?.portfolioImages?.size!! > 0) {
//                        nestedScrollViewMode.visibility == View.VISIBLE
                        rlViewModeImages.visibility = View.VISIBLE

//                        vpImages.visibility == View.VISIBLE
                        nestedScroll.visibility = View.VISIBLE

                    } else {
//                        vpImages.visibility = View.GONE
                        rlViewModeImages.visibility = View.GONE

//                        nestedScrollViewMode.visibility = View.GONE
                        nestedScroll.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    override fun onClick(view: View?) {
        if (view == null) return
        when (view.id) {
            R.id.imgBack -> {
                onBack()
            }

            R.id.txtAddAncoTurfProduct -> {
                if (productCategories != null && productCategories.size > 0)
                    openProductCategoryDialog()
            }

            R.id.txtAddNonAncoProduct -> {
                openAddNonAncoProductDialog(null)
            }

            R.id.txtAddPortfolioImage -> {
                if (!portfolioMode.equals(Constants.VIEW_PORTFOLIO)) {
                    if (portfolioImages != null && portfolioImages.size < sharedPrefs.max_portfolio_images) {
                        openImageCaptureDialog()
                    } else {
                        shortToast("You can add maximum " + sharedPrefs.max_portfolio_images + " images")
                    }
                }
            }

            R.id.txtAddProduct -> {
                openAddProductDialog()
            }

            R.id.edtProjectAddress -> {
                if (portfolioMode.equals(Constants.VIEW_PORTFOLIO))
                    openGoogleMap()
            }

            R.id.imgShare -> {
                if (portfolioDetailResponse?.sharingUrl != "") {
                    var shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.setType("text/plain")
                    shareIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        portfolioDetailResponse?.sharingUrl!!
                    )
                    startActivity(Intent.createChooser(shareIntent, "Share Portfolio"))
                }
            }

            R.id.imgEdit -> {
                if (portfolioMode == Constants.VIEW_PORTFOLIO)
                    portfolioMode = Constants.EDIT_PORTFOLIO
                else if (portfolioMode == Constants.EDIT_PORTFOLIO)
                    portfolioMode = Constants.VIEW_PORTFOLIO
                setData()
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
                if (portfolioMode == Constants.VIEW_PORTFOLIO || portfolioMode == Constants.EDIT_PORTFOLIO) {
//                    slidingImageAdapter?.clearViewPagerData()  //Dev_N
//                    slidingImageAdapter = null   //Dev_N
                    adapterViewModeImages = null
                    portfolioMode = Constants.VIEW_PORTFOLIO
                }
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

            R.id.txtPublishPortfolio -> {
                publishPortfolio()
            }
            R.id.txtDeletePortfolio -> {
                (requireActivity() as AppCompatActivity).openAlertDialogWithTwoClick(
                    getString(R.string.app_name),
                    getString(R.string.delete_portfolio_alert),
                    getString(R.string.yes),
                    getString(R.string.no),
                    object : OnDialogButtonClick {
                        override fun onPositiveButtonClick() {
                            //Delete portfolio
                            portfolioViewModel?.callDeletePortfolio(portfolioId)
                        }

                        override fun onNegativeButtonClick() {
                        }
                    })
            }
        }
    }

    fun onBack() {
        (requireActivity() as AppCompatActivity).hideKeyboard()
        if (!portfolioMode.equals(Constants.VIEW_PORTFOLIO) && portfolioViewModel?.isAnythingEdited == true) {
            (requireActivity() as AppCompatActivity).openAlertDialogWithTwoClick(
                getString(R.string.app_name),
                "Do you want to save the portfolio details?",
                "Yes",
                "No",
                object : OnDialogButtonClick {
                    override fun onPositiveButtonClick() {
                        publishPortfolio()
                    }

                    override fun onNegativeButtonClick() {
                        requireActivity().supportFragmentManager.popBackStack()
//                        requireActivity().supportFragmentManager.popBackStack()
                    }
                })
        } else {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }


    private fun publishPortfolio() {
        when (portfolioMode) {
            Constants.ADD_PORTFOLIO -> {
                if (Utility.isValueNull(edtProjectName.text.toString())) {
                    shortToast(getString(R.string.blank_project_name))
                } else if (Utility.isValueNull(edtProjectBudget.text.toString())) {
                    shortToast(getString(R.string.blank_project_budget))
                } else if (edtProjectBudget.text.toString().replace("$", "").replace(
                        ",",
                        ""
                    ).toFloat() <= 0
                ) {
                    shortToast(getString(R.string.invalid_project_budget))
                } else if (Utility.isValueNull(edtProjectAddress.text.toString())) {
                    shortToast(getString(R.string.blank_project_address))
                } else if ((portfolioImages.size == 0) || (portfolioImages.size == 1 && Utility.isValueNull(
                        portfolioImages[0].imageUrl
                    ) && Utility.isValueNull(portfolioImages[0].uri))
                ) {
                    shortToast(getString(R.string.no_image_selected))
                } else if (Utility.isValueNull(edtProjectDesc.text.toString())) {
                    shortToast(getString(R.string.blank_project_desc))
                } else {
                    Utility.showProgress(
                        context = requireActivity(),
                        message = "",
                        cancellable = false
                    )
                    val imageIds = ArrayList<Int>()
                    val filePaths = ArrayList<String>()
                    var featuredImageIndex = 0
                    for (i in 0 until portfolioImages.size) {
                        if (Utility.isValueNull(portfolioImages[i].imageUrl)) {
                            imageIds.add(portfolioImages[i].id)
                            filePaths.add(portfolioImages[i].uri)
                            if (portfolioImages[i].featured) {
                                featuredImageIndex = i
                            }
                        }
                    }
                    portfolioViewModel?.callAddEditPortfolio(
                        0,
                        projectName = edtProjectName.text.toString(),
                        city = "City",
                        address = edtProjectAddress.text.toString(),
                        projectDescription = edtProjectDesc.text.toString(),
                        budget = edtProjectBudget.text.toString().replace("$", "").replace(
                            ",",
                            ""
                        ).toFloat(),
                        imageIds = imageIds,
                        filePaths = filePaths,
                        deletedImageIds = deletedImageIds,
                        featuredImageIndex = featuredImageIndex,
                        featuredImageId = 0,
                        nonAncoProducts = nonAncoProductList,
                        updatednonAncoProducts = ArrayList<NonAncoProductRequest>(),
                        deletedProductIds = deletedProductIds,
                        deletedCustomProductIds = deletedCustomProductIds, orderedImageIds = null
                    )
                }
            }
            Constants.EDIT_PORTFOLIO -> {
                if (Utility.isValueNull(edtProjectName.text.toString())) {
                    shortToast(getString(R.string.blank_project_name))
                } else if (Utility.isValueNull(edtProjectBudget.text.toString())) {
                    shortToast(getString(R.string.blank_project_budget))
                } else if (edtProjectBudget.text.toString().replace("$", "").replace(
                        ",",
                        ""
                    ).toFloat() <= 0
                ) {
                    shortToast(getString(R.string.invalid_project_budget))
                } else if (Utility.isValueNull(edtProjectAddress.text.toString())) {
                    shortToast(getString(R.string.blank_project_address))
                } else if ((portfolioImages.size == 0) || (portfolioImages.size == 1 && Utility.isValueNull(
                        portfolioImages[0].imageUrl
                    ) && Utility.isValueNull(portfolioImages[0].uri))
                ) {
                    shortToast(getString(R.string.no_image_selected))
                } else if (Utility.isValueNull(edtProjectDesc.text.toString())) {
                    shortToast(getString(R.string.blank_project_desc))
                } else {
                    Utility.showProgress(
                        context = requireActivity(),
                        message = "",
                        cancellable = false
                    )
                    val imageIds = ArrayList<Int>()
                    val filePaths = ArrayList<String>()
                    var featuredImageIndex = -1
                    var featuredImageId = 0
                    var orderImageIds: StringBuilder = StringBuilder()
                    for (i in 0 until portfolioImages.size) {
                        if (Utility.isValueNull(portfolioImages[i].imageUrl)) {
                            imageIds.add(portfolioImages[i].id)
                            filePaths.add(portfolioImages[i].uri)
                            orderImageIds.append("0,")
                        } else {
                            orderImageIds.append("${portfolioImages[i].id},")
                        }
                        if (i == portfolioImages.lastIndex) {
                            orderImageIds.deleteCharAt(orderImageIds.length - 1)
                        }
                    }

                    for (i in 0 until portfolioImages.size) {
                        if (portfolioImages[i].featured) {
                            if (portfolioImages[i].id != 0)
                                featuredImageId = portfolioImages[i].id
                            else {
                                /*if (portfolioDetailResponse != null && portfolioDetailResponse!!.portfolioImages != null && portfolioDetailResponse!!.portfolioImages.size > 0) {
                                    featuredImageIndex =
                                        i - portfolioDetailResponse!!.portfolioImages.size
                                } else {
                                    featuredImageIndex = i
                                }*/
                                if (portfolioDetailResponse != null && portfolioDetailResponse!!.portfolioImages != null && portfolioDetailResponse!!.portfolioImages.size > 0) {
//                                    if (portfolioImages[i].id==0){
                                    inner@ for (j in 0 until filePaths.size) {
                                        if (portfolioImages[i].uri == filePaths[j]) {
                                            featuredImageIndex = j
                                            break@inner
                                        }
                                    }
                                    /*}else {
                                        featuredImageIndex =
                                            i - portfolioDetailResponse!!.portfolioImages.size
                                    }*/
                                } else {
                                    featuredImageIndex = i
                                }
                            }
                        }
                    }

                    if (featuredImageId == 0 && featuredImageIndex == -1) {
                        if (imageIds != null && imageIds.size > 0)
                            featuredImageIndex = 0
                        else
                            featuredImageId = portfolioImages[0].id
                    }

                    var newNonAnco = ArrayList<NonAncoProductRequest>()
                    var updatedNonAnco = ArrayList<NonAncoProductRequest>()

                    for (i in 0 until nonAncoProductList.size) {
                        var isNew = true
                        if (portfolioDetailResponse != null && portfolioDetailResponse!!.customProducts != null && portfolioDetailResponse!!.customProducts.size > 0) {
                            for (j in 0 until portfolioDetailResponse!!.customProducts.size) {
                                if (portfolioDetailResponse!!.customProducts[j].id == nonAncoProductList[i].id) {
                                    isNew = false
                                    break
                                }
                            }
                        }
                        if (isNew)
                            newNonAnco.add(nonAncoProductList[i])
                        else
                            updatedNonAnco.add(nonAncoProductList[i])
                    }

                    portfolioViewModel?.callAddEditPortfolio(
                        portfolioId = portfolioId,
                        projectName = edtProjectName.text.toString(),
                        city = "",
                        address = edtProjectAddress.text.toString(),
                        projectDescription = edtProjectDesc.text.toString(),
                        budget = edtProjectBudget.text.toString().replace("$", "").replace(
                            ",",
                            ""
                        ).toFloat(),
                        imageIds = imageIds,
                        filePaths = filePaths,
                        deletedImageIds = deletedImageIds,
                        featuredImageIndex = featuredImageIndex,
                        featuredImageId = featuredImageId,
                        nonAncoProducts = newNonAnco,
                        updatednonAncoProducts = updatedNonAnco,
                        deletedProductIds = deletedProductIds,
                        deletedCustomProductIds = deletedCustomProductIds,
                        orderedImageIds = orderImageIds.toString()
                    )
                }
            }
            Constants.VIEW_PORTFOLIO -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun openProductCategoryDialog() {
        // Create custom dialog object
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_category)
        dialog.show()

        val window = dialog.window
        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        window!!.setGravity(Gravity.CENTER)

        val listProducts = dialog.findViewById(R.id.listProducts) as RecyclerView

        val imgClose = dialog.findViewById(R.id.imgClose) as ImageView
        imgClose.setOnClickListener {
            dialog.dismiss()
        }

        var categoryAdapter = ProductCategoryAdapter(
            requireActivity() as AppCompatActivity,
            productCategories,
            dialog,
            null
        )
        listProducts.adapter = categoryAdapter
    }

    private fun openAddNonAncoProductDialog(product: PortfolioDetailResponse.CustomProduct?) {
        // Create custom dialog object
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_add_non_anco_product_portfolio)
        dialog.show()

        val window = dialog.window
        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window!!.setGravity(Gravity.CENTER)

        val edtProductName = dialog.findViewById(R.id.edtProductName) as EditText
        val edtProductDescription = dialog.findViewById(R.id.edtProductDescription) as EditText
        val edtProductQuantity = dialog.findViewById(R.id.edtProductQuantity) as EditText
        edtProductQuantity.setFilters(
            arrayOf<InputFilter>(
                InputFilterMinMax(
                    0, Constants.MAX_NUMBER
                )
            )
        )

        val edtProductPrice = dialog.findViewById(R.id.edtProductPrice) as EditText
        edtProductPrice.inputType =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        edtProductPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!Utility.isValueNull(s.toString()) && !Utility.isValueNull(
                        s.toString().replace("$", "").replace(",", "")
                    ) && before != count
                )
                    edtProductPrice.setText(
                        "$" + (Utility.formatNumber(
                            s.toString().replace("$", "").replace(",", "").toLong()
                        ))
                    )
                edtProductPrice.text?.length?.let { edtProductPrice.setSelection(it) }
            }
        })

        val imgClose = dialog.findViewById(R.id.imgClose) as ImageView
        imgClose.setOnClickListener {
            dialog.dismiss()
        }

        val txtCancel = dialog.findViewById(R.id.txtCancel) as TextView
        txtCancel.setOnClickListener {
            dialog.dismiss()
        }

        if (product != null) {
            edtProductName.setText(product.name)
            edtProductDescription.setText(product.description)
            var qty = product.pivot.quantity
            edtProductQuantity.setText("$qty")
            edtProductPrice.setText(product.price)
        }

        val txtAddProduct = dialog.findViewById(R.id.txtAddProduct) as TextView
        txtAddProduct.setOnClickListener {
            if (Utility.isValueNull(edtProductName.text.toString())) {
                (requireActivity() as AppCompatActivity).shortToast(getString(R.string.black_product_name_message))
            } else if (Utility.isValueNull(edtProductDescription.text.toString())) {
                (requireActivity() as AppCompatActivity).shortToast(getString(R.string.black_product_description_message))
            } else if (Utility.isValueNull(edtProductQuantity.text.toString())) {
                (requireActivity() as AppCompatActivity).shortToast(getString(R.string.black_product_quantity_message))
            } else if (edtProductQuantity.text.toString().toInt() <= 0) {
                (requireActivity() as AppCompatActivity).shortToast(getString(R.string.invalid_product_quantity_message))
            } else if (Utility.isValueNull(edtProductPrice.text.toString())) {
                (requireActivity() as AppCompatActivity).shortToast(getString(R.string.black_product_price_message))
            } else if (edtProductPrice.text.toString().replace("$", "").replace(
                    ",",
                    ""
                ).toFloat() <= 0
            ) {
                (requireActivity() as AppCompatActivity).shortToast(getString(R.string.invalid_product_price_message))
            } else {
                if (product != null) {
                    if (getNonAncoProduct(product.id) != -1) {
                        val index = getNonAncoProduct(product.id)
                        var nonAnco = nonAncoProductList[index]
                        nonAnco.descriptions = edtProductDescription.text.toString()
                        nonAnco.name = edtProductName.text.toString()
                        nonAnco.price =
                            edtProductPrice.text.toString().replace("$", "").replace(",", "")
                                .toFloat()
                        nonAnco.qty = edtProductQuantity.text.toString().toInt()
                        nonAncoProductList.set(index, nonAnco)
                    } else if (getTotalNonAncoProduct(product.id) != -1) {
                        var nonAnco = NonAncoProductRequest()
                        nonAnco.id =
                            nonAncoAddedProductList[getTotalNonAncoProduct(product.id)].id
                        nonAnco.descriptions = edtProductDescription.text.toString()
                        nonAnco.name = edtProductName.text.toString()
                        nonAnco.price =
                            edtProductPrice.text.toString().replace("$", "").replace(",", "")
                                .toFloat()
                        nonAnco.qty = edtProductQuantity.text.toString().toInt()
                        nonAncoAddedProductList.removeAt(getTotalNonAncoProduct(product.id))
                        nonAncoProductList.add(nonAnco)
                    }
                } else {
                    var nonAnco = NonAncoProductRequest()
                    nonAnco.id = 0
                    nonAnco.descriptions = edtProductDescription.text.toString()
                    nonAnco.name = edtProductName.text.toString()
                    nonAnco.price =
                        edtProductPrice.text.toString().replace("$", "").replace(",", "")
                            .toFloat()
                    nonAnco.qty = edtProductQuantity.text.toString().toInt()
                    nonAncoProductList.add(nonAnco)
                }
                dialog.dismiss()
                portfolioViewModel?.isAnythingEdited = true
                setNonAncoProduct()
            }
        }
    }

    private fun getTotalNonAncoProduct(id: Int): Int {
        if (nonAncoAddedProductList != null && nonAncoAddedProductList.size > 0) {
            for (i in 0 until nonAncoAddedProductList.size) {
                if (nonAncoAddedProductList[i].id == id)
                    return i;
            }
        }
        return -1
    }

    private fun getNonAncoProduct(id: Int): Int {
        if (nonAncoProductList != null && nonAncoProductList.size > 0) {
            for (i in 0 until nonAncoProductList.size) {
                if (nonAncoProductList[i].id == id)
                    return i;
            }
        }
        return -1
    }

    private fun openAddProductDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_add_product)
        dialog.show()

        val window = dialog.window
        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window!!.setGravity(Gravity.CENTER)

        val imgAncoProduct = dialog.findViewById(R.id.imgAncoProduct) as ImageView
        imgAncoProduct.setOnClickListener {
            dialog.dismiss()
            openProductCategoryDialog()
        }

        val txtAncoProducts = dialog.findViewById(R.id.txtAncoProducts) as TextView
        txtAncoProducts.setOnClickListener {
            dialog.dismiss()
            openProductCategoryDialog()
        }

        val imgNonAncoProduct = dialog.findViewById(R.id.imgNonAncoProduct) as ImageView
        imgNonAncoProduct.setOnClickListener {
            dialog.dismiss()
            openAddNonAncoProductDialog(null)
        }

        val txtNonAncoProduct = dialog.findViewById(R.id.txtNonAncoProduct) as TextView
        txtNonAncoProduct.setOnClickListener {
            dialog.dismiss()
            openAddNonAncoProductDialog(null)
        }

        val imgClose = dialog.findViewById(R.id.imgClose) as ImageView
        imgClose.setOnClickListener {
            dialog.dismiss()
        }

        val txtCancel = dialog.findViewById(R.id.txtCancel) as TextView
        txtCancel.setOnClickListener {
            dialog.dismiss()
        }

        val txtDone = dialog.findViewById(R.id.txtDone) as TextView
        txtDone.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun openImageCaptureDialog() {
        val builder = AlertDialog.Builder(activity as AppCompatActivity)
        builder.setTitle(R.string.dialog_title_choose_image_from)
        builder.setItems(
            resources.getStringArray(R.array.dialog_choose_image)
        ) { _, which ->
            when (which) {
                0 -> checkCameraPermission()
                1 -> checkPermissionForGallery()
            }
        }
        builder.show()
    }

    fun mustUseScopedStorage(): Boolean {
        // Impractical must first ask for useless Storage permission...
        val exSD = Environment.getExternalStorageDirectory()
        return !exSD.canRead() // this test works only if Storage permission was granted.
    }

    private fun checkCameraPermission() {
        Dexter.withActivity(activity as AppCompatActivity)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    // permission is granted
                    takePhoto()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    // check for permanent denial of permission
                    if (response.isPermanentlyDenied) {
                        (requireActivity() as AppCompatActivity).openAlertDialogWithTwoClick(
                            title = getString(R.string.missing_permission_title)
                            , message = getString(R.string.missing_permission_message)
                            , positiveButton = "SETTING"
                            , nagetiveButton = getString(android.R.string.cancel)
                            , onDialogButtonClick = object : OnDialogButtonClick {
                                override fun onPositiveButtonClick() {
                                    openSettings()
                                }

                                override fun onNegativeButtonClick() {
                                }

                            })
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    (requireActivity() as AppCompatActivity).openAlertDialogWithTwoClick(
                        title = getString(R.string.missing_permission_title)
                        , message = getString(R.string.missing_permission_message)
                        , positiveButton = "Grant"
                        , nagetiveButton = getString(android.R.string.cancel)
                        , onDialogButtonClick = object : OnDialogButtonClick {
                            override fun onPositiveButtonClick() {
                                token.continuePermissionRequest()
                            }

                            override fun onNegativeButtonClick() {
                                token.cancelPermissionRequest()
                            }
                        })
                }
            }).check()
    }

    private fun checkPermissionForGallery() {
        Dexter.withActivity(activity as AppCompatActivity)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    // permission is granted
                    fromGallery()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    // check for permanent denial of permission
                    if (response.isPermanentlyDenied) {
                        (requireActivity() as AppCompatActivity).openAlertDialogWithTwoClick(
                            title = getString(R.string.missing_permission_title)
                            , message = getString(R.string.missing_permission_message)
                            , positiveButton = "SETTING"
                            , nagetiveButton = getString(android.R.string.cancel)
                            , onDialogButtonClick = object : OnDialogButtonClick {
                                override fun onPositiveButtonClick() {
                                    openSettings()
                                }

                                override fun onNegativeButtonClick() {
                                }

                            })
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    (requireActivity() as AppCompatActivity).openAlertDialogWithTwoClick(
                        title = getString(R.string.missing_permission_title)
                        , message = getString(R.string.missing_permission_message)
                        , positiveButton = "Grant"
                        , nagetiveButton = getString(android.R.string.cancel)
                        , onDialogButtonClick = object : OnDialogButtonClick {
                            override fun onPositiveButtonClick() {
                                token.continuePermissionRequest()
                            }

                            override fun onNegativeButtonClick() {
                                token.cancelPermissionRequest()
                            }

                        })
                }
            }).check()
    }



    // navigating user to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_STORAGE_PERMISSION_SETTINGS)
        }
    }

    private fun takePhoto() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //Create a file to store the image
        var photoFile: File? = null
        try {
            photoFile = createImageFile()
        } catch (ex: IOException) {
            ex.printStackTrace()
            (requireActivity() as AppCompatActivity).shortToast(getString(R.string.msg_photo_file_create_error))
            // Error occurred while creating the File
        }
        photoFile?.let {
            selectedFilePath = photoFile.absolutePath
            cameraUri =
                FileProvider.getUriForFile(
                    requireActivity(),
                    getString(R.string.file_provider_authorities),
                    photoFile
                )
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                pictureIntent.clipData = ClipData.newRawUri("", cameraUri)
                pictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            if (pictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(pictureIntent, REQUEST_GET_IMAGE_CAMERA)
            } else {
                (requireActivity() as AppCompatActivity).shortToast(getString(R.string.msg_app_not_found))
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val dirCreated: Boolean
        var storageDir = requireActivity().filesDir
        if (storageDir == null) {
            val externalStorage =
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            if (externalStorage == null) {
                storageDir = File(requireActivity().cacheDir, Environment.DIRECTORY_PICTURES)
                dirCreated = storageDir.exists() || storageDir.mkdirs()
            } else
                dirCreated = true
        } else {
            storageDir = File(requireActivity().filesDir, Environment.DIRECTORY_PICTURES)
            dirCreated = storageDir.exists() || storageDir.mkdirs()
        }

        return if (dirCreated) {
            File.createTempFile(
                IMG_PORTFOLIO, //prefix
                ".jpg", //suffix
                storageDir   //directory
            )
        } else
            null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            portfolioViewModel?.isAnythingEdited = true
            selectedPhotoUriList?.clear()
            if (requestCode == REQUEST_GET_IMAGE_CAMERA && cameraUri != null) {
                selectedPhotoUri = cameraUri
//                selectedPhotoUriList?.add(cameraUri!!)
                selectedPhotoUriList?.add(selectedFilePath!!.toUri())
            } else if (requestCode == REQUEST_GET_IMAGE_GALLERY && data != null) {
//content://com.google.android.apps.photos.contentprovider/0/1/mediakey%3A%2Flocal%253Ac781ebcc-1a47-457b-bae9-0011aadddba6/ORIGINAL/NONE/image%2Fjpeg/1595653593
                if (data == null || data.clipData == null) {
                    shortToast("Something went wrong please try to choose images from alternative app")
                    return
                }
                var clipData: ClipData = data.clipData!!
                /*if (clipData?.itemCount > 5) {
                    (requireActivity() as AppCompatActivity).showAlert(getString(R.string.maxFiveImagesAllow))
                    return
                }*/

                var leftImages =
                    if (portfolioImages?.size == 1 && portfolioImages[0].id == -1) sharedPrefs?.max_portfolio_images - (portfolioImages?.size - 1) else sharedPrefs?.max_portfolio_images - portfolioImages?.size

                if (clipData?.itemCount > leftImages) {
                    shortToast("You can add maximum " + sharedPrefs.max_portfolio_images + " images")
                    return
                }

                /*if (portfolioImages != null && portfolioImages.size < sharedPrefs.max_portfolio_images) {
                    openImageCaptureDialog()
                } else {
                    shortToast("You can add maximum " + sharedPrefs.max_portfolio_images + " images")
                }*/
///
//                content://com.google.android.apps.photos.contentprovider/0/1/mediakey%3A%2Flocal%253Ac781ebcc-1a47-457b-bae9-0011aadddba6/ORIGINAL/NONE/image%2Fjpeg/1595653593

                selectedPhotoUri = data?.clipData?.getItemAt(0)?.uri
                if (selectedPhotoUri.toString().contains("com.google.android.apps.photos.content")) {
                    for (i in 0 until clipData.itemCount) {
//                    selectedPhotoUriList?.add(Uri.parse(getPath(clipData.getItemAt(i).uri)))
                        selectedPhotoUriList?.add(
                            Uri.parse(
                                getPathFromInputStreamUri(
                                    requireContext(),
                                    clipData.getItemAt(i).uri
                                )
                            )
                        )
                    }
                    selectedPhotoUri?.let {
                        //                    selectedFilePath = getPath(it)
                        selectedFilePath = getPathFromInputStreamUri(requireContext(), it)
                    }
                } else {
                    for (i in 0 until clipData.itemCount) {
                        selectedPhotoUriList?.add(Uri.parse(getPath(clipData.getItemAt(i).uri)))
                    }
                    selectedPhotoUri?.let {
                        selectedFilePath = getPath(it)
                    }
                }

            }
           /* else if (requestCode == REQUEST_GET_IMAGE_GALLERY && data != null) {
//content://com.google.android.apps.photos.contentprovider/0/1/mediakey%3A%2Flocal%253Ac781ebcc-1a47-457b-bae9-0011aadddba6/ORIGINAL/NONE/image%2Fjpeg/1595653593
                if (data == null || data.clipData == null) {
//                if (data == null || data.data == null) {
                    shortToast("Something went wrong please try to choose images from alternative app")
                    return
                }
                var clipData: ClipData = data.clipData!!


                var leftImages =
                    if (portfolioImages?.size == 1 && portfolioImages[0].id == -1) sharedPrefs?.max_portfolio_images - (portfolioImages?.size - 1) else sharedPrefs?.max_portfolio_images - portfolioImages?.size

                if (clipData?.itemCount > leftImages) {
                    shortToast("You can add maximum " + sharedPrefs.max_portfolio_images + " images")
                    return
                }



                selectedPhotoUri = data?.clipData?.getItemAt(0)?.uri
                if (selectedPhotoUri.toString()
                        .contains("com.google.android.apps.photos.content")
                ) {
                    for (i in 0 until clipData.itemCount) {
                        selectedPhotoUriList?.add(
                            Uri.parse(
                                getPathFromInputStreamUri(
                                    requireContext(),
                                    clipData.getItemAt(i).uri
                                )
                            )
                        )
                    }
                    selectedPhotoUri?.let {
                        //                    selectedFilePath = getPath(it)
                        selectedFilePath = getPathFromInputStreamUri(requireContext(), it)
                    }
                } else {
                    for (i in 0 until clipData.itemCount) {
                        selectedPhotoUriList?.add(Uri.parse(getPath(clipData.getItemAt(i).uri)))
                    }
                    selectedPhotoUri?.let {
                        selectedFilePath = getPath(it)
                    }
                }

            } */else if (requestCode == REQUEST_STORAGE_PERMISSION_SETTINGS) {
                checkPermissionForGallery()
            }
            try {
                CheckImageOrientation.handleSamplingAndRotationBitmap(
                    requireContext(),
                    selectedPhotoUri!!,
                    selectedFilePath
                )?.let { bitmap ->
                    /*if (portfolioImages.size == 1 && Utility.isValueNull(
                            portfolioImages.get(0).imageUrl
                        ) && Utility.isValueNull(portfolioImages.get(0).uri)
                    ) {
//                        portfolioImages.set(0 , PortfolioDetailResponse.PortfolioImage(id = 0 , uri = (if(selectedFilePath != null) selectedFilePath else selectedPhotoUri?.toString())!!))
                        portfolioImages.set(
                            0,
                            PortfolioDetailResponse.PortfolioImage(id = 0, uri = selectedFilePath!!)
                        )
                    } else {


                        *//*if (portfolioImages != null) {
                            portfolioImages.add(
                                PortfolioDetailResponse.PortfolioImage(
                                    id = 0,
                                    uri = selectedFilePath!!
                                )
                            )
                        } else {
                            portfolioImages = ArrayList()
                            portfolioImages.add(
                                PortfolioDetailResponse.PortfolioImage(
                                    id = 0,
                                    uri = selectedFilePath!!
                                )
                            )
                        }*//*
                    }*/
                    if (portfolioImages == null)
                        portfolioImages = ArrayList()

                    /*if (portfolioImages.size == 1 && portfolioImages[0].id == -1 && portfolioImages[0].uri.equals(""))*/
                    if (portfolioImages.size == 1 && portfolioImages[0].id == -1)
                        portfolioImages.clear()

                    for (uri: Uri in selectedPhotoUriList!!) {
                        portfolioImages.add(
                            PortfolioDetailResponse.PortfolioImage(
                                id = 0,
                                uri = uri.toString()!!
                            )
                        )
                    }

                    val portfolioImagePagerAdapter = PortfolioImagePagerAdapter(
                        requireActivity() as AppCompatActivity,
                        portfolioImages,
                        object : OnPagerImageClickListener {
                            override fun OnPagerImageClickListener() {
                                if (!portfolioMode.equals(Constants.VIEW_PORTFOLIO)) {
                                    if (portfolioImages != null && portfolioImages.size < sharedPrefs.max_portfolio_images) {
                                        openImageCaptureDialog()
                                    } else {
                                        shortToast("You can add maximum " + sharedPrefs.max_portfolio_images + " images")
                                    }
                                }
                            }
                        }
                    )
                    projectImagePager.adapter = portfolioImagePagerAdapter
                    imagePagerIndicator.setViewPager(projectImagePager)
                    projectImagePager.setCurrentItem(portfolioImages.size - 1)

                    if (portfolioImages.size <= 1) {
                        imagePagerIndicator.visibility = View.GONE
                    } else {
                        if (portfolioMode == Constants.VIEW_PORTFOLIO)
                            imagePagerIndicator.visibility = View.GONE
                        else
                            imagePagerIndicator.visibility = View.VISIBLE
                    }
                    setImageHorizontalView()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun getPathFromInputStreamUri(context: Context, uri: Uri): String? {
        var filePath: String? = null
        uri.authority?.let {
            try {
                context.contentResolver.openInputStream(uri).use {
                    val photoFile: File? = createTemporalFileFrom(it)
                    filePath = photoFile?.path
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return filePath
    }


    @Throws(IOException::class)
    private fun createTemporalFileFrom(inputStream: InputStream?): File? {
        var targetFile: File? = null
        return if (inputStream == null) targetFile
        else {
            var read: Int
            val buffer = ByteArray(8 * 1024)
            targetFile = getDefaultDirectory()
            FileOutputStream(targetFile).use { out ->
                while (inputStream.read(buffer).also { read = it } != -1) {
                    out.write(buffer, 0, read)
                }
                out.flush()
            }
            targetFile
        }
    }


    private fun createTemporalFile(): File {
        return File(getDefaultDirectory(), "tempPicture.jpg")
    }

    private fun getDefaultDirectory(): File? {
        val dirCreated: Boolean
        var storageDir = requireActivity().filesDir
        if (storageDir == null) {
            val externalStorage =
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            if (externalStorage == null) {
                storageDir = File(requireActivity().cacheDir, Environment.DIRECTORY_PICTURES)
                dirCreated = storageDir.exists() || storageDir.mkdirs()
            } else
                dirCreated = true
        } else {
            storageDir = File(requireActivity().filesDir, Environment.DIRECTORY_PICTURES)
            dirCreated = storageDir.exists() || storageDir.mkdirs()
        }

        return if (dirCreated) {
            File.createTempFile(
                IMG_PROFILE, //prefix
                ".jpg", //suffix
                storageDir   //directory
            )
        } else
            null
    }

    private fun getPath(uri: Uri): String {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireActivity().contentResolver.query(uri, proj, null, null, null)
        cursor?.let {
            if (cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndexOrThrow(proj[0]))
            }
            cursor.close()
        }
        if (result == null) {
            result = RESULT_NOT_FOUND
        }
        return result as String
    }

    companion object {
        const val REQUEST_GET_IMAGE_GALLERY = 1
        const val REQUEST_GET_IMAGE_CAMERA = 2
        const val REQUEST_STORAGE_PERMISSION_SETTINGS = 7
        const val IMG_PROFILE = "IMG_PROFILE"
        const val IMG_PORTFOLIO = "IMG_PORTFOLIO"
        const val RESULT_NOT_FOUND = "Not found"
    }

    private fun openGoogleMap() {
        if (!Utility.isValueNull(edtProjectAddress.text.toString())) {
            val gmmIntentUri =
                Uri.parse("google.navigation:q=" + edtProjectAddress.text.toString())
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        portfolioViewModel?._ancoProductLiveData?.value = null
        portfolioViewModel?._addEditPortfolioLiveData?.value = null
        portfolioViewModel?._portfolioDetailLiveData?.value = null
        portfolioViewModel?.isAnythingEdited = false
    }

    override fun onItemClick(data: Any, view: View, position: Int) {
        when (view.id) {
            R.id.imgDelete -> {
                Log.d("delete>>", position.toString())
                portfolioViewModel?.isAnythingEdited = true
                if (portfolioImages.get(position).id != 0) {
                    deletedImageIds.add(portfolioImages.get(position).id)
                }
                portfolioImages.removeAt(position)
//                selectedPhotoUriList?.removeAt(position)
                currentpage = if (position == 0) 0 else (position - 1)
                setImagePager(currentpage)
                setImageHorizontalView()
            }


            R.id.imgPortfolio -> {
                Log.d("imgPortfolio>>", position.toString())
                currentpage = position
                if ((data as String).equals("DoubleTapped")) {
                    (requireActivity() as AppCompatActivity).showAlert(getString(R.string.double_tap_make_image_as_cover_portfolio))

                    portfolioViewModel?.isAnythingEdited = true
                    for (j in 0 until portfolioImages.size)
                        portfolioImages[j].featured = false
                    portfolioImages[position].featured = true
                    setImagePager(currentpage)
                }
                projectImagePager.setCurrentItem(currentpage)
            }
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(portfolioImages, fromPosition, toPosition)
        portfolioImagesAdapter?.notifyItemMoved(fromPosition, toPosition)
        setImagePager(currentpage)
        return true
    }

    override fun onItemDismiss(position: Int) {

    }

    override fun onSaveInstanceState(outState: Bundle) {
        /*outState.putInt("portfolioMode", portfolioMode)
        outState.putInt("portfolioId", portfolioId)*/
        super.onSaveInstanceState(outState)
    }

    private fun fromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            this.startActivityForResult(intent, REQUEST_GET_IMAGE_GALLERY)

            /*   val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
               intent.type = ("image/*")
               intent.action = Intent.ACTION_GET_CONTENT
               intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
               if (intent.resolveActivity(requireActivity().packageManager) != null) {
                   try {
                       this.startActivityForResult(intent, REQUEST_GET_IMAGE_GALLERY)
                   }catch (e : Exception){
                       e.printStackTrace()
                   }*/


             */
        } else {
            (requireActivity() as AppCompatActivity).shortToast(
                getString(
                    R.string.msg_app_not_found,
                    getString(R.string.gallery)
                )
            )
        }
    }
}

/*  private fun fromGallery() {
      *//*  val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
          intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)*//*
        val galleryIntent =
            Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//        startActivityForResult(galleryIntent, GALLERY_REQUEST)

        *//*  if (intent.resolveActivity(requireActivity().packageManager) != null) {
              this.startActivityForResult(intent, REQUEST_GET_IMAGE_GALLERY)
              *//*
        if (galleryIntent.resolveActivity(requireActivity().packageManager) != null) {
            this.startActivityForResult(galleryIntent, REQUEST_GET_IMAGE_GALLERY)

            *//*   val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
               intent.type = ("image/*")
               intent.action = Intent.ACTION_GET_CONTENT
               intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
               if (intent.resolveActivity(requireActivity().packageManager) != null) {
                   try {
                       this.startActivityForResult(intent, REQUEST_GET_IMAGE_GALLERY)
                   }catch (e : Exception){
                       e.printStackTrace()
                   }*//*


             *//*
        } else {
            (requireActivity() as AppCompatActivity).shortToast(
                getString(
                    R.string.msg_app_not_found,
                    getString(R.string.gallery)
                )
            )
        }
    }*/