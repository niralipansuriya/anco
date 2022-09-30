package com.app.ancoturf.presentation.home.portfolio.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.app.ancoturf.R
import com.app.ancoturf.data.portfolio.remote.entity.PortfolioDetailResponse
import com.app.ancoturf.presentation.home.portfolio.utils.ZoomableDraweeView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ProgressBarDrawable
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchy
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.view.SimpleDraweeView

class ViewImageAdapter(var images: ArrayList<PortfolioDetailResponse.PortfolioImage>) :
    PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var view: View = LayoutInflater.from(container.context)
            .inflate(R.layout.item_image_viewpager, container, false)

        /*var simpleDraweeView = view.findViewById(R.id.imgZoomable) as SimpleDraweeView
        simpleDraweeView.setImageURI(images[position].imageUrl)*/

        var zoomableDraweeView: ZoomableDraweeView =
            view.findViewById(R.id.imgZoomable) as ZoomableDraweeView
        zoomableDraweeView.controller =
            Fresco.newDraweeControllerBuilder().setUri(images[position].imageUrl).build()

        var hierarchy: GenericDraweeHierarchy =
            GenericDraweeHierarchyBuilder(container.resources).build()
        hierarchy.actualImageScaleType = ScalingUtils.ScaleType.FIT_CENTER
        hierarchy.setProgressBarImage(ProgressBarDrawable())

        zoomableDraweeView.hierarchy = hierarchy

        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}