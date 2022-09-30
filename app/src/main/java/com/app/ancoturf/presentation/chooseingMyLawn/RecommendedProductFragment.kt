package com.app.ancoturf.presentation.chooseingMyLawn

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.home.products.ProductDetailFragment
import com.app.ancoturf.presentation.home.products.ProductsViewModel
import com.app.ancoturf.presentation.home.turfcalculator.TurfCalculatorFragment
import com.app.ancoturf.utils.Utility
import com.app.ancoturf.utils.customeview.DotsIndicatorDecoration
import com.app.ancoturf.utils.customeview.SnapHelperOneByOne
import kotlinx.android.synthetic.main.fragment_recommended_product.*
import java.util.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [RecommendedProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecommendedProductFragment : BaseFragment(), View.OnClickListener {
    var recommendProduct : String = ""
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var productsViewModel: ProductsViewModel? = null
    var productDetailResponse: ProductDetailResponse? = null
    var productList : ArrayList<ProductDetailResponse> = ArrayList()
    var productId : Int = 0
    var recommendedProductAdapter : RecommendedProductAdapter? = null


    //            textView.paintFlags = textView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getContentResource(): Int = R.layout.fragment_recommended_product

    override fun viewModelSetup() {
        productsViewModel = ViewModelProviders.of(
            requireActivity(),
            viewModelFactory
        )[ProductsViewModel::class.java]
        initObservers()
    }


    private fun initObservers() {
        productsViewModel!!.productFullNameLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                Log.e("resp", it.toString())
                productList.clear()
                productList.addAll(it)
                setAdapter()

            }
        })
        productsViewModel!!.errorLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                productsViewModel?._errorLiveData?.value = null
            }
        })
    }

    private fun setAdapter() {
        if (productList.size > 0){
            val linearSnapHelper: LinearSnapHelper = SnapHelperOneByOne()
            val linearLayoutManager = LinearLayoutManager(activity)
            linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            rvProduct.layoutManager = linearLayoutManager

            txtNoOfProduct.text = if(productList.size > 1)   "${productList.size} products recommended" else "1 product recommended"
            recommendedProductAdapter = RecommendedProductAdapter(
                activity as AppCompatActivity,productList,
                object : ISelectRecommendedProduct {
                    override fun onProductClick(position: Int) {
                        (requireActivity() as AppCompatActivity).pushFragment(
                            ProductDetailFragment(productList[position].productId),
                            true,
                            true,
                            false,
                            R.id.flContainerHome
                        )
                    }

                    override fun onNextPreviousClick(position: Int, isPrevious: Boolean) {
                        if (isPrevious){
                            rvProduct.scrollToPosition(linearLayoutManager.findLastCompletelyVisibleItemPosition() - 1)
                        }else {
                            rvProduct.scrollToPosition(linearLayoutManager.findLastCompletelyVisibleItemPosition() + 1)
                        }
                    }
                })

            rvProduct.adapter = recommendedProductAdapter
            if (rvProduct.getOnFlingListener() == null)
                linearSnapHelper.attachToRecyclerView(rvProduct);
          /*  if (productList.size > 1) {
                val radius = resources.getDimensionPixelSize(R.dimen._4sdp)
                val dotsHeight = resources.getDimensionPixelSize(R.dimen._10sdp)
                val color: Int = ContextCompat.getColor(requireContext(), R.color.theme_green)
                val colorDis: Int = ContextCompat.getColor(requireContext(), R.color.que_deselected_green)
                rvProduct.addItemDecoration(
                    DotsIndicatorDecoration(
                        radius,
                        radius * 3,
                        dotsHeight,
                        colorDis,
                        color
                    )
                )
//                PagerSnapHelper().attachToRecyclerView(rvProduct)
            }*/
        }
    }

    override fun viewSetup() {
        arguments?.let {
            recommendProduct = it.getString("recommendProduct").toString()
            arguments = null
        }
        Utility.showProgress(requireContext(), "", false)
        productsViewModel?.callGetProductFullName(recommendProduct)
        tvRecommendedProduct.paintFlags =   Paint.UNDERLINE_TEXT_FLAG
        eventListener()
    }

    private fun eventListener() {
        btnYes.setOnClickListener(this)
        btnNo.setOnClickListener(this)
        tvRecommendedProduct.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnYes ->{
                if (productList.size > 0) {
                    var revCurPos: Int = 0
                    revCurPos = rvProduct.getCurrentPosition()
                    val bundle = Bundle()
                    bundle.putInt("productId", productList[revCurPos].productId)

                    (requireActivity() as AppCompatActivity).pushFragment(
                        TurfCalculatorFragment().apply {
                            arguments = bundle
                        },
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                }
            }
            R.id.btnNo   ->{
                if (productList.size > 0) {
                    var revCurPos: Int = 0
                    revCurPos = rvProduct.getCurrentPosition()
                    (requireActivity() as AppCompatActivity).pushFragment(
                        ProductDetailFragment(productList[revCurPos].productId),
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                }
            }
        }
    }
    private fun RecyclerView.getCurrentPosition() : Int {
        return (this.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
    }
}