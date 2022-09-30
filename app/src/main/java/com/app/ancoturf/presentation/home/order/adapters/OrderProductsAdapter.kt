package com.app.ancoturf.presentation.home.order.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
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
import com.app.ancoturf.data.order.remote.entity.response.OrderDataResponse
import com.app.ancoturf.data.quote.remote.entity.QuoteProducts
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.presentation.cart.interfaces.OnCartProductChangeListener
import com.app.ancoturf.presentation.home.quote.interfaces.OnProductChangeListener
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import java.util.*

class OrderProductsAdapter(
    private val activity: AppCompatActivity,
    var products: ArrayList<OrderDataResponse.Product>
) :
    RecyclerView.Adapter<OrderProductsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity as Context?
            ).inflate(R.layout.item_order_detail_product, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]

        holder.txtProductTitle.text = product.name
        var unit = product.productUnit
        var price = product.price
        if (unit.equals("Square meter"))
            unit = "m\u00B2"
        else if (unit.equals("Quantity"))
            unit = "QTY"


        holder.txtUnit.text = unit
        Glide.with(activity).load(product.featureImageUrl).into(holder.imgProduct)

        var qty = if (Utility.isValueNull(product.pivot.quantity.toString())) 0 else product.pivot.quantity
        holder.edtQuantity.setText("$qty")
        holder.txtFinalPrice.text = "$" + Utility.formatNumber(Utility.roundTwoDecimals(product.pivot.totalBasePrice!!).toFloat())

        if (position == products.size - 1)
            holder.view.visibility = View.GONE
        else
            holder.view.visibility = View.VISIBLE

        holder.edtQuantity.isEnabled = false

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
        val view = itemView
            .findViewById(R.id.view) as View
    }

}