package com.app.ancoturf.presentation.home.products.adapters

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide

class BenefitsAdapter(private val context: Context?, private val arrayList: List<ProductDetailResponse.ProductBenefits>) :
    RecyclerView.Adapter<BenefitsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                context
            ).inflate(R.layout.item_benefits, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var horizontalSpacing = Utility.dpToPx(context!!.resources.getDimension(R.dimen._16sdp))
//        int screenWidthHeight = Utility.isLandScapeMode(activity) ? (Utility.getScreenWidth() - (verticalSpacing + Utility.dpToPx(135))) : Utility.getScreenHeight();
        var screenWidthHeight = context?.let { Utility.getScreenWidth(it) }

        val tabWidth = (screenWidthHeight) / 2

        holder.mainLayout.layoutParams.width = tabWidth

        holder.txtBenefitTitle.text =  Html.fromHtml(arrayList[position].displayName)

        context?.let { Glide.with(it).load(arrayList[position].imageUrl).into(holder.imgBenefit) }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtBenefitTitle = itemView
            .findViewById(R.id.txtBenefitTitle) as TextView
        val imgBenefit = itemView
            .findViewById(R.id.imgBenefit) as ImageView
        val mainLayout = itemView
            .findViewById(R.id.mainLayout) as ConstraintLayout
    }
}