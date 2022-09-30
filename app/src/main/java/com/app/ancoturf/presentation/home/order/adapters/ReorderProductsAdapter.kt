package com.app.ancoturf.presentation.home.order.adapters

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.order.remote.entity.response.OrderDataResponse
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.home.order.interfaces.OnReorderProductChangeListener
import com.app.ancoturf.utils.InputFilterMinMax
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import java.util.*

class ReorderProductsAdapter(
    private val activity: AppCompatActivity,
    var products: ArrayList<OrderDataResponse.Product>) :
    RecyclerView.Adapter<ReorderProductsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity as Context?
            ).inflate(R.layout.item_reorder_product, parent, false)
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

        holder.imgProductChecked.setImageResource(if (products[position].isChecked) R.drawable.ic_radio_on else R.drawable.ic_radio_off)

        var qty = if (Utility.isValueNull(product.pivot.quantity.toString())) 0 else product.pivot.quantity
        holder.edtQuantity.setText("$qty")

        holder.edtQuantity.filters = arrayOf<InputFilter>(
            InputFilterMinMax(
                1, Constants.MAX_NUMBER
            )
        )

        if (position == products.size - 1)
            holder.view.visibility = View.GONE
        else
            holder.view.visibility = View.VISIBLE

        holder.edtQuantity.isEnabled = true


        holder.edtQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                activity.hideKeyboard(holder.edtQuantity)
                if (!Utility.isValueNull(holder.edtQuantity.text.toString()) && products[position].pivot.quantity > 0)
                {
                    if(holder.edtQuantity.text.toString().toInt() >= products[position].minimumQuantity.toInt()) {
                        products[position].pivot.quantity = holder.edtQuantity.text.toString().toInt()
                    } else {
                        activity.shortToast(activity.getString(R.string.invalid_product_quantity_message_for_add_to_cart ,
                            products[position].minimumQuantity
                        ))
                    }
                }
                else {
                    activity.shortToast(activity.getString(R.string.blank_product_quantity_message))
                }
            }

        })

//        holder.edtQuantity.setOnEditorActionListener { v, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                activity.hideKeyboard(holder.edtQuantity)
//                if (!Utility.isValueNull(holder.edtQuantity.text.toString()) && products[position].pivot.quantity > 0)
//                {
//                    if(holder.edtQuantity.text.toString().toInt() >= products[position].minimumQuantity.toInt()) {
//                        products[position].pivot.quantity = holder.edtQuantity.text.toString().toInt()
//                    } else {
//                        activity.shortToast(activity.getString(R.string.invalid_product_quantity_message_for_add_to_cart ,
//                            products[position].minimumQuantity
//                        ))
//                    }
//                }
//                else {
//                    activity.shortToast(activity.getString(R.string.blank_product_quantity_message))
//                }
//            }
//            true
//        }

        holder.imgProductChecked.setOnClickListener {
            products[position].isChecked = !products[position].isChecked
            holder.imgProductChecked.setImageResource(if (products[position].isChecked) R.drawable.ic_radio_on else R.drawable.ic_radio_off)
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

        val imgProductChecked = itemView
            .findViewById(R.id.imgProductChecked) as ImageView
        val view = itemView
            .findViewById(R.id.view) as View
    }

    fun getCheckedProduct() : ArrayList<OrderDataResponse.Product>
    {
        var checkedProduct : ArrayList<OrderDataResponse.Product>  = ArrayList()

        for (response : OrderDataResponse.Product in products)
        {
            if (response.isChecked)
                checkedProduct.add(response)
        }

        return  checkedProduct
    }

}