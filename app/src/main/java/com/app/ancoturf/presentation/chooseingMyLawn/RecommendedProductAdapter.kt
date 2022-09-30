package com.app.ancoturf.presentation.chooseingMyLawn

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import com.bumptech.glide.Glide
import java.util.*

class RecommendedProductAdapter(
    private val activity: AppCompatActivity,
    var products: ArrayList<ProductDetailResponse>,
    private val iSelectRecommendedProduct: ISelectRecommendedProduct
) : RecyclerView.Adapter<RecommendedProductAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendedProductAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity as Context?
            ).inflate(R.layout.item_recommend_product, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return products.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivGuide = itemView
            .findViewById(R.id.ivGuide) as ImageView
        val ivPreviousImg = itemView
            .findViewById(R.id.ivPreviousImg) as ImageView
        val ivNextImg = itemView
            .findViewById(R.id.ivNextImg) as ImageView
        val tvContentLawnType = itemView
            .findViewById(R.id.tvContentLawnType) as AppCompatTextView
        val tvRecommendedProduct = itemView
            .findViewById(R.id.tvRecommendedProduct) as AppCompatTextView
        val tvDescription = itemView
            .findViewById(R.id.tvDescription) as AppCompatTextView
        val llDots = itemView
            .findViewById(R.id.llDots) as LinearLayout

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        if(products.size > 1) {
            val pics: Array<ImageView?> = arrayOfNulls(products.size)
            holder.llDots.removeAllViews()
            for (i in 0 until products!!.size) {
                pics[i] = ImageView(activity);
                if (i == position) {
                    pics[i]?.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity, R.drawable.selected_dot
                        )
                    )
                } else {
                    pics[i]?.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity, R.drawable.deselected_dot
                        )
                    )
                }
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(8, 0, 8, 0)
                holder.llDots.addView(pics[i], params)
            }
        }

        holder.tvRecommendedProduct.text = product.productName
        Glide.with(activity).load(product.suggestImageUrl).error(R.mipmap.ic_anco_app_icon)
            .placeholder(R.mipmap.ic_anco_app_icon)
            .override(
                Constants.COMPRESS_IMAGE_OVERRIDE_WIDTH,
                Constants.COMPRESS_IMAGE_OVERRIDE_HEIGHT
            )
            .into(holder.ivGuide)
        holder.tvRecommendedProduct.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        if (products.size == 1 || position == products.size - 1) {
            holder.ivNextImg.visibility = View.INVISIBLE
        } else {
            holder.ivNextImg.visibility = View.VISIBLE
        }
        if (position == 0 || products.size == 1) {
            holder.ivPreviousImg.visibility = View.INVISIBLE
        } else {
            holder.ivPreviousImg.visibility = View.VISIBLE
        }
        holder.tvRecommendedProduct.setOnClickListener(View.OnClickListener {
            iSelectRecommendedProduct.onProductClick(position)
        })
        holder.ivNextImg.setOnClickListener(View.OnClickListener {
            iSelectRecommendedProduct.onNextPreviousClick(position, false)
        })
        holder.ivPreviousImg.setOnClickListener(View.OnClickListener {
            iSelectRecommendedProduct.onNextPreviousClick(position, true)
        })
    }
}