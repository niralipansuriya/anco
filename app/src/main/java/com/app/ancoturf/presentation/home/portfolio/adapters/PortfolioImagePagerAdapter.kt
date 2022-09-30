package com.app.ancoturf.presentation.home.portfolio.adapters

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.app.ancoturf.R
import com.app.ancoturf.data.portfolio.remote.entity.PortfolioDetailResponse
import com.app.ancoturf.presentation.home.portfolio.interfaces.OnPagerImageClickListener
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlin.math.roundToInt


class PortfolioImagePagerAdapter(
    private val activity: AppCompatActivity,
    var imageList: List<PortfolioDetailResponse.PortfolioImage>,
    var onPagerImageClickListener: OnPagerImageClickListener?
) : PagerAdapter() {

    private var imgPortfolio: ImageView? = null

    override fun instantiateItem(collection: View, position: Int): Any {

        val convertView =
            LayoutInflater.from(activity as Context?).inflate(R.layout.item_portfolio_image, null)

        imgPortfolio = convertView
            .findViewById(R.id.imgPortfolio) as ImageView

        if (onPagerImageClickListener == null) {
            setImageHeightWidthForViewMode()
        }

        if (!Utility.isValueNull(imageList[position].imageUrl)) {
//            imgPortfolio.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(activity).load(imageList.get(position).imageUrl).apply(
                RequestOptions()
                    .placeholder(R.drawable.img_place_holder)
                    .fitCenter()
            ).into(imgPortfolio!!)

        } else if (!Utility.isValueNull(imageList.get(position).uri)) {
//            imgPortfolio.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(activity).load(imageList.get(position).uri).apply(
                RequestOptions()
                    .placeholder(R.drawable.img_place_holder)
                    .fitCenter()
            ).into(imgPortfolio!!)
        } else {
            imgPortfolio!!.setImageResource(imageList[position].imagePlaceHolder)
            imgPortfolio!!.scaleType = ImageView.ScaleType.CENTER
        }

        if (imageList[position].featured) {
            //imgPortfolio!!.setBackgroundResource(R.drawable.bg_green_line)
        } else {
            imgPortfolio!!.setBackgroundResource(R.drawable.bg_project_images)
        }

        convertView.setOnClickListener {
            if (onPagerImageClickListener != null) {
                onPagerImageClickListener!!.OnPagerImageClickListener()
            }
        }

        (collection as ViewPager).addView(convertView, 0)

        return convertView
    }

    public fun setImageHeightWidthForViewMode() {
        imgPortfolio!!.setPadding(0, 0, 0, 0)
        imgPortfolio!!.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        imgPortfolio!!.layoutParams.height =
            activity.resources.getDimension(R.dimen._165sdp).roundToInt()
        imgPortfolio!!.requestLayout()
    }

    override fun destroyItem(arg0: View, arg1: Int, arg2: Any) {
        (arg0 as ViewPager).removeView(arg2 as View)
    }

    override fun finishUpdate(arg0: View) {}

    override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
        return arg0 === arg1 as View

    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun restoreState(arg0: Parcelable?, arg1: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }

    override fun startUpdate(arg0: View) {}


    override fun getCount(): Int {
        return imageList.size
    }
}