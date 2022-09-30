package com.app.ancoturf.presentation.home.quote.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.quote.remote.entity.QuoteProducts
import com.app.ancoturf.presentation.home.quote.interfaces.OnProductChangeListener
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import java.util.*


class AncoQuoteProductsAdapter(
    private val activity: AppCompatActivity,
    var products: ArrayList<QuoteProducts>,
    private val showDelete: Boolean,
    private val onProductChangeListener: OnProductChangeListener
) :
    RecyclerView.Adapter<AncoQuoteProductsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity as Context?
            ).inflate(R.layout.item_quote_product, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]

        holder.txtProductTitle.text = product.name
        var unit = product.unit
        var price = product.price
//        if (unit.equals("Square meter"))
//            unit = "m\u00B2"
//        else if (unit.equals("Quantity"))
        unit = "QTY"
        holder.txtUnit.text = unit
        Glide.with(activity).load(product.imageUrl).error(R.mipmap.ic_anco_app_icon)
            .placeholder(R.mipmap.ic_anco_app_icon).into(holder.imgProduct)

        var qty = if (Utility.isValueNull(product.qty.toString())) 0 else product.qty
        holder.edtQuantity.setText("$qty")
        if (product.margin > 0) {
            holder.txtFinalPrice.text =
                "$" + Utility.formatNumber(Utility.roundTwoDecimals((price.toFloat() + (price.toFloat() * (product.margin.toFloat() / 100f))) * qty!!.toFloat()).toFloat())
        } else {
            holder.txtFinalPrice.text =
                "$" + Utility.formatNumber(Utility.roundTwoDecimals(price.toFloat() * qty!!).toFloat())
        }

        holder.edtQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!Utility.isValueNull(holder.edtQuantity.text.toString())) {
                    products[position].qty = holder.edtQuantity.text.toString().toInt()
                    if (product.margin > 0) {
                        holder.txtFinalPrice.text =
                            "$" + Utility.formatNumber(Utility.roundTwoDecimals((products[position].price.toFloat() + (products[position].price.toFloat() * (products[position].margin.toFloat() / 100f))) * products[position].qty!!.toFloat()).toFloat())
                    } else {
                        holder.txtFinalPrice.text =
                            "$" + Utility.formatNumber(Utility.roundTwoDecimals(products[position].price.toFloat() * products[position].qty!!).toFloat())
                    }
                } else {
                    products[position].qty = null
                }
                onProductChangeListener.onQuntityChange(products[position])
            }
        })

        holder.imgProductDelete.visibility = if (showDelete) {
            View.VISIBLE
        } else {
            View.GONE
        }
        if (showDelete) {
            holder.edtQuantity.isEnabled = true
            val params = holder.txtFinalPrice.layoutParams as ConstraintLayout.LayoutParams
            params.topToTop = R.id.edtQuantity;

//            holder.edtQuantity.setBackgroundResource(R.drawable.bg_green_line_rounded_corner)
        } else {
            holder.edtQuantity.isEnabled = false
            val params = holder.txtFinalPrice.layoutParams as ConstraintLayout.LayoutParams
            params.topToBottom = R.id.imgProductDelete;
//            holder.edtQuantity.setBackgroundResource(0)
        }

        holder.imgProductDelete.setOnClickListener {
            onProductChangeListener.onItemDelete(products[position])
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct = itemView
            .findViewById(R.id.imgProduct) as ImageView
        val txtProductTitle = itemView
            .findViewById(R.id.txtProductTitle) as TextView
        val edtQuantity = itemView
            .findViewById(R.id.edtQuantity) as EditText
        val txtUnit = itemView
            .findViewById(R.id.txtUnit) as TextView
        val txtFinalPrice = itemView
            .findViewById(R.id.txtFinalPrice) as TextView
        val imgProductDelete = itemView
            .findViewById(R.id.imgProductDelete) as ImageView
    }

}