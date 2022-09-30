package com.app.ancoturf.presentation.home.quote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.product.remote.entity.response.ProductsResponse
import com.app.ancoturf.presentation.home.portfolio.interfaces.OnProductSelectedListener
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.*


class QuoteProductsAdapter(
    private val activity: AppCompatActivity,
    var products: ArrayList<ProductsResponse.Data>,
    var onProductSelectedListener: OnProductSelectedListener
) :
    RecyclerView.Adapter<QuoteProductsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity
            ).inflate(R.layout.item_quote_product_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]

        var horizontalSpacing = Utility.dpToPx(activity!!.resources.getDimension(R.dimen._16sdp))
//        int screenWidthHeight = Utility.isLandScapeMode(activity) ? (Utility.getScreenWidth() - (verticalSpacing + Utility.dpToPx(135))) : Utility.getScreenHeight();
        var screenWidthHeight = activity?.let { Utility.getScreenWidth(it) }

        val tabWidth = (screenWidthHeight / 2) - horizontalSpacing

        holder.mainLayout.layoutParams.width = tabWidth - 20
        holder.imgProduct.layoutParams.height = tabWidth - 20
        holder.imgProduct.layoutParams.width = tabWidth - 20

        holder.txtProductTitle.text = product.productName
//
        Glide.with(activity).load(product.featureImageUrl).into(holder.imgProduct)

        Glide.with(activity)
            .load(product.featureImageUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(holder.imgProduct)

//        Glide.with(activity).load(product.featureImageUrl).into(object : SimpleTarget<Drawable>() {
//            override fun onResourceReady(
//                resource: Drawable,
//                transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
//            ) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    holder.imgProduct.setBackground(resource)
//                }
//            }
//        })

        val price = Utility.roundTwoDecimals(product.price)
        var unit = product.productUnit
        if (unit.equals("Square meter"))
            unit = "m\u00B2"
        else if (unit.equals("Quantity"))
            unit = "Qty"

        holder.txtPrize.text =  if(unit.equals("Qty")) "$$price" else "$$price/$unit"

        if (product.selected) {
            holder.imgProduct.setBackgroundResource(R.drawable.bg_green_thick_line_rounded)
        } else {
            holder.imgProduct.setBackgroundResource(R.drawable.bg_grey_thick_line_rounded)
        }

        holder.mainLayout.setOnClickListener {
            if (!products[position].selected) {
//                products[position].selected = false
//            else {
                for (i in 0 until products.size)
                    products[i].selected = false
                products[position].selected = true
            }
            notifyDataSetChanged()
            onProductSelectedListener.onProductSelectedListener(products[position], products[position].selected)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct = itemView
            .findViewById(R.id.imgProduct) as ImageView
        val txtProductTitle = itemView
            .findViewById(R.id.txtProductTitle) as TextView
        val txtPrize = itemView
            .findViewById(R.id.txtPrize) as TextView
        val mainLayout = itemView
            .findViewById(R.id.mainLayout) as ConstraintLayout
    }

}