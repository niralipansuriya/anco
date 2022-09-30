package com.app.ancoturf.presentation.guideline

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.app.ancoturf.R
import com.app.ancoturf.databinding.ItemGuidelineBinding
import com.app.ancoturf.presentation.welcomeOnBoard.IOnBoardOptions

class GuidelinePagerAdapter(
    private val activity: AppCompatActivity,
    var guideDataList : ArrayList<GuideModel>,
    var isShowButtonPart : Boolean
): PagerAdapter() {

    private var iOnBoardOptions : IOnBoardOptions? = null
    override fun isViewFromObject(view: View, arg1: Any): Boolean {
        return view === arg1 as android.view.View
    }

    public fun setIOnBoardOptions(iOnBoardOptions: IOnBoardOptions){
        this.iOnBoardOptions = iOnBoardOptions
    }

    override fun getCount(): Int {
       return guideDataList.size
    }

//    private var tvSubtitle: AppCompatTextView? = null
    private var tvDescription: AppCompatTextView? = null
    private var btnChoosingMyLawn: AppCompatTextView? = null
    private var btnManagingMyLawn: AppCompatTextView? = null
    private var btnTrackMyDelivery: AppCompatTextView? = null
    private var btnTrackMyDeliveryNoLogin: AppCompatTextView? = null
    private var btnBrowseOurShopNoLogin: AppCompatTextView? = null
    private var btnReturningCustomer: AppCompatTextView? = null
    private var btnNewTradeCustomers: AppCompatTextView? = null
    private var btnBrowseOurShop: AppCompatTextView? = null
    private var ivGuide: AppCompatImageView? = null
    private var csButtonGrp: ConstraintLayout? = null


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var guideModel = guideDataList[position]
        val convertView =
            LayoutInflater.from(activity as Context?).inflate(R.layout.item_guideline, null)

        ivGuide  = convertView.findViewById(R.id.ivGuide) as AppCompatImageView
        tvDescription  = convertView.findViewById(R.id.tvDescription) as AppCompatTextView
        btnChoosingMyLawn  = convertView.findViewById(R.id.btnChoosingMyLawn) as AppCompatTextView
        btnManagingMyLawn  = convertView.findViewById(R.id.btnManagingMyLawn) as AppCompatTextView
        btnTrackMyDelivery  = convertView.findViewById(R.id.btnTrackMyDelivery) as AppCompatTextView
        btnTrackMyDeliveryNoLogin  = convertView.findViewById(R.id.btnTrackMyDeliveryNoLogin) as AppCompatTextView
        btnBrowseOurShopNoLogin  = convertView.findViewById(R.id.btnBrowseOurShopNoLogin) as AppCompatTextView
        btnReturningCustomer  = convertView.findViewById(R.id.btnReturningCustomer) as AppCompatTextView
        btnNewTradeCustomers  = convertView.findViewById(R.id.btnNewTradeCustomers) as AppCompatTextView
        btnBrowseOurShop  = convertView.findViewById(R.id.btnBrowseOurShop) as AppCompatTextView
        csButtonGrp  = convertView.findViewById(R.id.csButtonGrp) as ConstraintLayout

        if (isShowButtonPart && position == 2){
            csButtonGrp?.visibility = View.VISIBLE
        }else{
            csButtonGrp?.visibility = View.GONE
        }
//        tvSubtitle?.text =  guideModel.subTitle
        ivGuide?.setImageResource(guideModel.drawable)
        tvDescription?.text =  guideModel.description

        if (iOnBoardOptions != null){
            btnBrowseOurShop?.setOnClickListener {
                iOnBoardOptions?.getBrowseOurShopClick()
            }
            btnBrowseOurShopNoLogin?.setOnClickListener {
                iOnBoardOptions?.getBrowseOurShopNoLoginClick()
            }
            btnChoosingMyLawn?.setOnClickListener {
                iOnBoardOptions?.getChooseMyLawnClick()
            }
            btnManagingMyLawn?.setOnClickListener {
                iOnBoardOptions?.getManagingMyLawnClick()
            }
            btnTrackMyDelivery?.setOnClickListener {
                iOnBoardOptions?.getTrackMyDeliveryClick()
            }
            btnTrackMyDeliveryNoLogin?.setOnClickListener {
                iOnBoardOptions?.getTrackMyDeliveryNoLoginClick()
            }
            btnReturningCustomer?.setOnClickListener {
                iOnBoardOptions?.getReturningCustomerClick()
            }
            btnNewTradeCustomers?.setOnClickListener {
                iOnBoardOptions?.getNewTradeCustomerClick()
            }
        }
        (container as ViewPager).addView(convertView, 0)
        return convertView
    }
    override fun destroyItem(arg0: View, arg1: Int, arg2: Any) {
        (arg0 as ViewPager).removeView(arg2 as View)
    }
}