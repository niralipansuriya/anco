package com.app.ancoturf.presentation.home.portfolio.adapters

import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.product.remote.entity.response.ProductsResponse
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.home.portfolio.interfaces.OnProductSelectedListener
import com.app.ancoturf.utils.InputFilterMinMax
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import java.util.ArrayList

class PortfolioProductsAdapter(
    private val activity: AppCompatActivity,
    var arrayList: ArrayList<ProductsResponse.Data>,
    var onProductSelectedListener: OnProductSelectedListener
) :
    RecyclerView.Adapter<PortfolioProductsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity
            ).inflate(R.layout.item_portfolio_product, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = arrayList[position]

        var horizontalSpacing = Utility.dpToPx(activity!!.resources.getDimension(R.dimen._8sdp))
//        int screenWidthHeight = Utility.isLandScapeMode(activity) ? (Utility.getScreenWidth() - (verticalSpacing + Utility.dpToPx(135))) : Utility.getScreenHeight();
        var screenWidthHeight = activity?.let { Utility.getScreenWidth(it) }

        val tabWidth = (screenWidthHeight) / 2 - horizontalSpacing

        holder.mainLayout.layoutParams.width = tabWidth
        holder.selectedView.layoutParams.width = tabWidth
        holder.imgProduct.layoutParams.width = tabWidth
        holder.linImageProduct.layoutParams.width = tabWidth

        holder.txtProductTitle.text = product.productName
//
        Glide.with(activity).load(product.featureImageUrl).into(holder.imgProduct)

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

        val price = Utility.formatNumber(Utility.roundTwoDecimals(product.price).toFloat())
        var unit = product.productUnit
        if (unit.equals("Square meter"))
            unit = "m\u00B2"
        else if (unit.equals("Quantity"))
            unit = ""
        holder.txtUnit.text = unit

        var qty = 0
        if (product.qty == 0) {
            qty = product.minimumQuantity.toInt()
        } else {
            qty = product.qty
        }
        holder.edtQuantity.setText("$qty")
        arrayList[position].qty = holder.edtQuantity.text.toString().toInt()
//        holder.edtQuantity.setFilters(
//            arrayOf<InputFilter>(
//                InputFilterMinMax(
//                    product.minimumQuantity  , Constants.MAX_NUMBER
//                )
//            )
//        )

        holder.edtQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!Utility.isValueNull(holder.edtQuantity.text.toString()))
                    arrayList[position].qty = holder.edtQuantity.text.toString().toInt()
            }
        })

        if (product.selected)
            holder.selectedView.visibility = View.VISIBLE
        else
            holder.selectedView.visibility = View.GONE

        holder.mainLayout.setOnClickListener {
            if(!Utility.isValueNull(holder.edtQuantity.text.toString()) && arrayList[position].qty > 0) {
                if (arrayList[position].qty >= arrayList[position].minimumQuantity) {
                    arrayList[position].selected = !product.selected
                    notifyDataSetChanged()
                    onProductSelectedListener.onProductSelectedListener(
                        arrayList[position],
                        arrayList[position].selected
                    )
                } else {
                    activity.shortToast(activity.getString(R.string.invalid_product_quantity_message_for_add_to_cart , arrayList[position].minimumQuantity.toString()))
                }
            } else {
                activity.shortToast(activity.getString(R.string.blank_product_quantity_message))
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct = itemView
            .findViewById(R.id.imgProduct) as ImageView
        val txtProductTitle = itemView
            .findViewById(R.id.txtProductTitle) as TextView
        val txtUnit = itemView
            .findViewById(R.id.txtUnit) as TextView
        val edtQuantity = itemView
            .findViewById(R.id.edtQuantity) as EditText
        val mainLayout = itemView
            .findViewById(R.id.mainLayout) as ConstraintLayout
        val selectedView = itemView
            .findViewById(R.id.selectedView) as View
        val linImageProduct = itemView
            .findViewById(R.id.linImageProduct) as LinearLayout
    }

}