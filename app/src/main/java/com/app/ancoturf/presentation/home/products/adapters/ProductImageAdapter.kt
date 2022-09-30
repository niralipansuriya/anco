package com.app.ancoturf.presentation.home.products.adapters

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import java.util.ArrayList
import android.os.Parcelable
import android.text.TextUtils
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.widget.ImageView
import com.app.ancoturf.R
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide


class ProductImageAdapter(private val activity: AppCompatActivity, var imageList: List<ProductDetailResponse.ProductImage>)  : PagerAdapter() {


    override fun instantiateItem(collection: View, position: Int): Any {

        val convertView = LayoutInflater.from(activity).inflate(R.layout.item_view_pager, null)

        val imgViewPager = convertView.findViewById(R.id.imgViewPager) as ImageView
        if (!Utility.isValueNull(imageList[position].imageUrl)) {
            Glide.with(activity).asBitmap().load(imageList[position].imageUrl).into(imgViewPager)
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

    fun getProductLists(): List<ProductDetailResponse.ProductImage> {
        return imageList
    }

    fun setProductLists(imageList: ArrayList<ProductDetailResponse.ProductImage>) {
        this.imageList = imageList
    }


    override fun getCount(): Int {
        return imageList.size
    }
}