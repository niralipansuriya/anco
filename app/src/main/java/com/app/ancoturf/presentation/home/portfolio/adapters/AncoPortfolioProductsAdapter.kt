package com.app.ancoturf.presentation.home.portfolio.adapters

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
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.portfolio.remote.entity.PortfolioDetailResponse
import com.app.ancoturf.presentation.home.portfolio.interfaces.OnAncoProductChangeListener
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import java.util.*

class AncoPortfolioProductsAdapter(
    private val activity: AppCompatActivity,
    var arrayList: ArrayList<PortfolioDetailResponse.Product>,
    private val showDelete: Boolean,
    private val onAncoProductChangeListener: OnAncoProductChangeListener
) :
    RecyclerView.Adapter<AncoPortfolioProductsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity as Context?
            ).inflate(R.layout.item_anco_product, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = arrayList[position]

        holder.txtProductTitle.text = product.name
        var unit = product.productUnit
        if (unit.equals("Square meter"))
            unit = "m\u00B2"
        else if (unit.equals("Quantity"))
            unit = ""
        holder.txtUnit.text = unit
        Glide.with(activity).load(product.featureImageUrl).into(holder.imgProduct)

        var qty = 0
        if (product.pivot.quantity == 0) {
            qty = product.minimumQuantity
        } else {
            qty = product.pivot.quantity
        }
        holder.edtQuantity.setText("$qty")

        holder.edtQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!Utility.isValueNull(holder.edtQuantity.text.toString())) {
                    arrayList[position].pivot.quantity = holder.edtQuantity.text.toString().toInt()
                    onAncoProductChangeListener.onQuntityChange(arrayList[position])
                }
            }
        })

        holder.imgProductDelete.visibility = if (showDelete) View.VISIBLE else View.GONE
        if (showDelete) {
            holder.edtQuantity.isEnabled = true
            holder.edtQuantity.setBackgroundResource(R.drawable.bg_rect_edit_white)
        } else {
            holder.edtQuantity.isEnabled = false
            holder.edtQuantity.setBackgroundResource(0)
        }


        holder.imgProductDelete.setOnClickListener {
            onAncoProductChangeListener.onItemDelete(arrayList[position])
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
        val imgProductDelete = itemView
            .findViewById(R.id.imgProductDelete) as ImageView
    }

}