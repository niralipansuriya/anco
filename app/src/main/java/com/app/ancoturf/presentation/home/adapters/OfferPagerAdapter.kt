package com.app.ancoturf.presentation.home.adapters

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import java.util.ArrayList
import android.os.Parcelable
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.widget.TextView
import com.app.ancoturf.R
import com.app.ancoturf.data.offer.remote.OfferDataResponse
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.presentation.home.offers.OffersFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition


class OfferPagerAdapter(private val activity: AppCompatActivity, var offers: List<OfferDataResponse.Data>)  : PagerAdapter() {

    override fun instantiateItem(collection: View, position: Int): Any {

        val convertView = LayoutInflater.from(activity).inflate(R.layout.item_offers, null)

        val txtOffer = convertView
            .findViewById(R.id.txtOffer) as TextView
        val txtPage = convertView
            .findViewById(R.id.txtPage) as TextView
        val mainLayout = convertView
            .findViewById(R.id.mainLayout) as androidx.constraintlayout.widget.ConstraintLayout
        val overlayView = convertView
            .findViewById(R.id.overlayView) as View

        txtOffer.text = offers[position].title
        val pageNum : Int = position + 1
        val totalPage : Int = offers.size
        txtPage.text =  "$pageNum / $totalPage"

        Glide.with(activity).load(offers[position].imageUrl).into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mainLayout.background = resource
                    } else {
                        mainLayout.setBackgroundDrawable(resource)
                    }
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
        overlayView.setBackgroundResource(R.drawable.bg_offer_overlay)

        mainLayout.setOnClickListener {
            activity.pushFragment(
                OffersFragment(offers[position]),
                true,
                true,
                false,
                R.id.flContainerHome
            )
        }

        (collection as ViewPager).addView(convertView, 0)

        return convertView
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

    fun getProductLists(): List<OfferDataResponse.Data> {
        return offers
    }

    fun setProductLists(imageList: ArrayList<OfferDataResponse.Data>) {
        this.offers = imageList
    }


    override fun getCount(): Int {
        return offers.size
    }
}