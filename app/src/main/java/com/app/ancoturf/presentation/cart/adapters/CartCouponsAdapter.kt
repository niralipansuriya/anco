package com.app.ancoturf.presentation.cart.adapters

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
import com.app.ancoturf.data.cart.persistance.entity.CouponDB
import com.app.ancoturf.data.cart.persistance.entity.ProductDB
import com.app.ancoturf.data.quote.remote.entity.QuoteProducts
import com.app.ancoturf.presentation.cart.interfaces.OnCartCouponDeleteListener
import com.app.ancoturf.presentation.cart.interfaces.OnCartProductChangeListener
import com.app.ancoturf.presentation.home.quote.interfaces.OnProductChangeListener
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import java.util.*

class CartCouponsAdapter(
    private val activity: AppCompatActivity,
    var coupons: ArrayList<CouponDB>,
    private val onCartCouponDeleteListener: OnCartCouponDeleteListener
) :
    RecyclerView.Adapter<CartCouponsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity as Context?
            ).inflate(R.layout.item_cart_coupon, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return coupons.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val coupon = coupons[position]

        holder.txtCouponCode.text = coupon.code
        holder.txtCouponName.text = coupon.name

        holder.imgCouponDelete.setOnClickListener {
            onCartCouponDeleteListener.onItemDelete(coupons[position])
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtCouponCode = itemView
            .findViewById(R.id.txtCouponCode) as TextView
        val txtCouponName = itemView
            .findViewById(R.id.txtCouponName) as TextView
        val imgCouponDelete = itemView
            .findViewById(R.id.imgCouponDelete) as ImageView
    }

}