package com.app.ancoturf.presentation.manageLawn

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.manageLawn.remote.ManageLawnDataResponse
import com.app.ancoturf.data.offer.ClsQuickLinks
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.extension.openLogoutAlertDialog
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.faq.FAQFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.home.adapters.QuickLinksAdapter
import com.app.ancoturf.presentation.home.interfaces.IDataPositionWise
import com.app.ancoturf.presentation.home.quote.QuotePDFFragment
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Utility
import kotlinx.android.synthetic.main.fragment_manage_lawn.*
import kotlinx.android.synthetic.main.header.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [ManageLawnFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ManageLawnFragment : BaseFragment(), View.OnClickListener, IDataPositionWise {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var manageLawnViewModel: ManageLawnViewModel? = null
    var manageLawnAdapter: ManageLawnAdapter? = null
    var manageLawn: ArrayList<ManageLawnDataResponse.Data> = ArrayList()
    var howLayTurfList: java.util.ArrayList<ClsQuickLinks> = java.util.ArrayList()

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    lateinit var quickLinksAdapter: QuickLinksAdapter
    var quickLinkList: java.util.ArrayList<ClsQuickLinks> = java.util.ArrayList()

    var pageNo = 1
    private var isLoad = false
    var isBack = false
    var itemCount = 0

    var isForHowToLay = false
    var cat_id_how_to_play = 0
    var title : String? =""

    override fun getContentResource(): Int = R.layout.fragment_manage_lawn

    override fun viewModelSetup() {
        manageLawnViewModel = ViewModelProviders.of(
            requireActivity(),
            viewModelFactory
        )[ManageLawnViewModel::class.java]
        initObservers()
    }

    private fun initObservers() {
        manageLawnViewModel?.manageLawnLiveData?.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                manageLawn.clear()
                manageLawn.addAll(it.data)
                setData()
                if (isForHowToLay) {
                    txtTitle.text = title
                    setAdapter()
                } else {
                    txtTitle.text = activity?.getString(R.string.managing_my_lawn_oneline)
                    setNewAdapter()
                }
//                setAdapter()
                manageLawnViewModel!!._manageLawnLiveData.value = null
            }
        })
        manageLawnViewModel!!.errorLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                if (it == ErrorConstants.UNAUTHORIZED_ERROR_CODE) {
                    (requireActivity() as AppCompatActivity).openLogoutAlertDialog(sharedPrefs = sharedPrefs)
                } else {
                    shortToast(it)
                    if (it == "No LawnTips found") {
                        if (manageLawn == null)
                            manageLawn = ArrayList()
                        manageLawn.clear()
                        setNewAdapter()
//                        setAdapter()
                    }
                }
                manageLawnViewModel?._errorLiveData?.value = null
            }
        })
    }

    private fun setNewAdapter() {
//        quickLinksAdapter = QuickLinksAdapter(activity, quickLinkList, this)
        if (quickLinkList != null && quickLinkList.size > 0) {
            quickLinksAdapter = activity?.let {
                QuickLinksAdapter(
                    it as AppCompatActivity, quickLinkList, this
                )
            }!!
            listManageLawn.adapter = quickLinksAdapter
            txtNoManageLawn.visibility = View.GONE
            listManageLawn.visibility = View.VISIBLE

        }
    }

    private fun setData() {
        quickLinkList.clear()
        for (i in 0 until manageLawn.size) {
            if (manageLawn[i].cat_id == 1) {
                var detailsSort = manageLawn[i].details
                var listSteps = detailsSort.toList()
                var listStepsSorted = listSteps.sortedBy { it.id }
                /*for (j in listSteps.indices)
                    howLayTurfList.add(
                        ClsQuickLinks(
                            manageLawn[i].details[j].title,
                            0,
                            manageLawn[i].details[j].featureImageUrl,
                            Fragment()
                        )
                    )*/

                for (j in listStepsSorted.indices)
                    howLayTurfList.add(
                        ClsQuickLinks(
                            listStepsSorted[j].title,
                            0,
                            listStepsSorted[j].featureImageUrl,
                            Fragment()
                        )
                    )
            }
            quickLinkList.add(
                ClsQuickLinks(
                    manageLawn[i].cat_name,
                    manageLawn[i].cat_id,
                    manageLawn[i].image_url,
                    Fragment()
                )
            )
        }
    }

    override fun viewSetup() {
        arguments?.let {
            isForHowToLay = it.getBoolean("isForHowToLay")
            cat_id_how_to_play = it.getInt("cat_id_how_to_play")
            title = it.getString("title")
            arguments = null
        }
        if (activity is HomeActivity) {
            (activity as HomeActivity).hideShowFooter(true)
            (activity as HomeActivity).showCartDetails(imgCart, txtCartProducts, false)
        }
        howLayTurfList.clear()
        quickLinkList.clear()
        Utility.showProgress(requireContext(), "", false)
        manageLawnViewModel?.getMyLawn("1")
        if (isForHowToLay) {
            txtTitle.text = title
//            manageLawnAdapter = ManageLawnAdapter(activity as AppCompatActivity, manageLawn)howLayTurfList
            quickLinksAdapter = QuickLinksAdapter(activity, howLayTurfList, this)


        } else {

            quickLinksAdapter = QuickLinksAdapter(activity, quickLinkList, this)
        }
        if (isForHowToLay) {
            setAdapter()
//            listManageLawn.adapter = manageLawnAdapter
            listManageLawn.adapter = quickLinksAdapter

        } else {
            setNewAdapter()
            listManageLawn.adapter = quickLinksAdapter

        }
        imgBack.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
        //pagination
//        getManageLawnWithPaging()
    }

    private fun setAdapter() {
        /*if (manageLawn != null && manageLawn.size > 0) {
            manageLawnAdapter = activity?.let {
                ManageLawnAdapter(
                    it as AppCompatActivity, manageLawn
                )
            }!!
            listManageLawn.adapter = manageLawnAdapter
            txtNoManageLawn.visibility = View.GONE
            listManageLawn.visibility = View.VISIBLE
        }*/
        if (howLayTurfList != null && howLayTurfList.size > 0) {

          /*  var list = howLayTurfList.toList().reversed()
            list.sortedBy { it. }*/
            quickLinksAdapter = activity?.let {

                QuickLinksAdapter(
                    it as AppCompatActivity, howLayTurfList as java.util.ArrayList<ClsQuickLinks>, this
                )
            }!!
            listManageLawn.adapter = quickLinksAdapter
            txtNoManageLawn.visibility = View.GONE
            listManageLawn.visibility = View.VISIBLE

        }
    }


    private fun getManageLawnWithPaging() {
        listManageLawn.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(@NonNull recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                if (!isLoad) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == manageLawn.size - 1) {
                        //bottom of list!

                        if (manageLawnViewModel?._isNextPageUrl?.value != null)
                            if (manageLawnViewModel?._isNextPageUrl?.value!!)
                                manageLawnViewModel?.getMyLawn("" + pageNo++)!!

                        Handler().postDelayed({
                            isLoad = !manageLawnViewModel!!._isNextPageUrl.value!!
                        }, 500)

                    }
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imgBack -> {
                requireActivity().supportFragmentManager.popBackStack()
            }
            R.id.imgBell -> {
                openNotificationFragment()
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

            R.id.imgCart -> {
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

            R.id.imgLogo -> {
                //(requireActivity() as AppCompatActivity).hideKeyboard()
                (requireActivity() as AppCompatActivity).pushFragment(
                    HomeFragment(),
                    false,
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
        }
    }

    override fun getDataFromPos(position: Int, name: String) {
        if (isForHowToLay) {
            val bundle = Bundle()
            bundle.putString("title", quickLinkList[position].title)
            bundle.putInt("positionSteps", position)
            val manageLawnDetailFragment =
//                ManageLawnDetailFragment(howLayTurfList[position].cat_id)
                ManageLawnDetailFragment(cat_id_how_to_play)

            (requireActivity() as AppCompatActivity).pushFragment(
                manageLawnDetailFragment.apply {
                    arguments = bundle
                },
                true,
                true,
                false,
                R.id.flContainerHome
            )
        } else {
            when (name) {
                "1" -> {
                    val bundle = Bundle()
                    bundle.putBoolean("isForHowToLay", true)
                    bundle.putString("title", quickLinkList[position].title)
                    bundle.putInt("cat_id_how_to_play", manageLawn[position].cat_id)
                    (requireActivity() as AppCompatActivity).pushFragment(
                        ManageLawnFragment().apply {
                            arguments = bundle
                        },
                        true,
                        false,
                        false,
                        R.id.flContainerHome
                    )
                }
                "3" -> {
                    val bundle = Bundle()
//                    bundle.putString("title", activity?.getString(R.string.weed_disease_identification))
                    bundle.putString("title", quickLinkList[position].title)
                    (requireActivity() as AppCompatActivity).pushFragment(
                        ManageLawnDropDownFragment(manageLawn[position].cat_id).apply {
                            arguments = bundle
                        },
                        true,
                        false,
                        false,
                        R.id.flContainerHome
                    )
                } "4" -> {
                    val bundle = Bundle()
//                    bundle.putString("title", activity?.getString(R.string.grass_disease_identification))
                    bundle.putString("title", quickLinkList[position].title)
                    (requireActivity() as AppCompatActivity).pushFragment(
                        ManageLawnDropDownFragment(manageLawn[position].cat_id).apply {
                            arguments = bundle
                        },
                        true,
                        false,
                        false,
                        R.id.flContainerHome
                    )
                }

                "5" -> {
                    val bundle = Bundle()
//                    bundle.putString("title", activity?.getString(R.string.insect_identification))
                    bundle.putString("title", quickLinkList[position].title)
                    (requireActivity() as AppCompatActivity).pushFragment(
                        ManageLawnDropDownFragment(manageLawn[position].cat_id).apply {
                            arguments = bundle
                        },
                        true,
                        false,
                        false,
                        R.id.flContainerHome
                    )
                }
              "2" -> {
/*
                    val bundle = Bundle()
                bundle.putBoolean("isQuote", false)
                  bundle.putString("title", quickLinkList[position].title)
                    (requireActivity() as AppCompatActivity).pushFragment(
                        QuotePDFFragment(manageLawn[position].details[0].featureImageUrl).apply { arguments = bundle },
                        true,
                        false,
                        false,
                        R.id.flContainerHome
                    )*/


                    val bundle = Bundle()
                bundle.putString("url", manageLawn[position].details[0].featureImageUrl)
                  bundle.putString("title", quickLinkList[position].title)
                    (requireActivity() as AppCompatActivity).pushFragment(
                        WebViewFragment().apply { arguments = bundle },
                        true,
                        false,
                        false,
                        R.id.flContainerHome
                    )
                }

                "6" -> {
                val bundle = Bundle()
//                    bundle.putString("title", activity?.getString(R.string.insect_identification))
                bundle.putString("title", quickLinkList[position].title)
                    (requireActivity() as AppCompatActivity).pushFragment(
                    FAQFragment().apply { arguments = bundle },
                        true,
                        false,
                        false,
                        R.id.flContainerHome
                    )
                }

            }
        }
    }
}