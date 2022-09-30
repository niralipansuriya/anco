package com.app.ancoturf.presentation.cart.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.cart.persistance.entity.ProductDB
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.presentation.cart.interfaces.OnCartProductChangeListener
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import java.util.*

class CartProductsAdapter(
    private val activity: AppCompatActivity,
    var products: ArrayList<ProductDB>,
    private val showDelete: Boolean,
    private val onProductChangeListener: OnCartProductChangeListener
) :
    RecyclerView.Adapter<CartProductsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (showDelete) ViewHolder(
            LayoutInflater.from(
                activity as Context?
            ).inflate(R.layout.item_cart_product, parent, false)
        ) else ViewHolder(
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

        holder.txtProductTitle.text = product.product_name
        var unit = product.product_unit
        var price = product.price
        if (unit.equals("Square meter"))
            unit = "m\u00B2"
        else if (unit.equals("Quantity"))
            unit = "QTY"
        holder.txtUnit.text = unit
        Glide.with(activity).load(product.feature_img_url).into(holder.imgProduct)

        var qty = if (Utility.isValueNull(product.qty.toString())) 0 else product.qty
        holder.edtQuantity.setText("$qty")
        holder.txtFinalPrice.text =
            "$" + Utility.formatNumber(Utility.roundTwoDecimals(product.total_price!!).toFloat())

        if (position == products.size - 1)
            holder.view.visibility = View.GONE
        else
            holder.view.visibility = View.VISIBLE

        if (showDelete) {
            if (products[position].is_free_products == 1){
                holder.imgProductDelete.visibility = View.GONE
                holder.edtQuantity.isEnabled = false
            }else{
                holder.imgProductDelete.visibility = View.VISIBLE
                holder.edtQuantity.isEnabled = true
            }
            holder.edtQuantity.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    activity.hideKeyboard()
                    holder.edtQuantity.clearFocus()
                    if (!Utility.isValueNull(holder.edtQuantity.text.toString())) {
                        products[position].qty = holder.edtQuantity.text.toString().toInt()
//                        holder.txtFinalPrice.text =
//                            "$" + Utility.roundTwoDecimals(products[position].price!!.toFloat() * products[position].qty!!)
                    } else {
                        products[position].qty = 0
                    }
                    onProductChangeListener.onQuantityChange(products[position])
                }
                true
            }

//            holder.edtQuantity.addTextChangedListener(object : TextWatcher {
//                override fun afterTextChanged(p0: Editable?) {
//                }
//
//                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                }
//
//                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                    if (!Utility.isValueNull(holder.edtQuantity.text.toString())) {
//                        products[position].qty = holder.edtQuantity.text.toString().toInt()
//                        holder.txtFinalPrice.text =
//                            "$" + Utility.roundTwoDecimals(products[position].price!!.toFloat() * products[position].qty!!)
//                    } else {
//                        products[position].qty = 0
//                    }
//                    onProductChangeListener.onQuantityChange(products[position])
//                }
//            })

            holder.imgProductDelete.setOnClickListener {
                onProductChangeListener.onItemDelete(products[position])
            }
        } else {
            holder.edtQuantity.isEnabled = false
            holder.imgProductDelete.visibility = View.GONE
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
        val view = itemView
            .findViewById(R.id.view) as View
    }
}