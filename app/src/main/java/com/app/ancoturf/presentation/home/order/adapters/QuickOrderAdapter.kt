package com.app.ancoturf.presentation.home.order.adapters

import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.order.remote.entity.response.OrderDetailResponse
import com.app.ancoturf.presentation.home.order.interfaces.OnBack
import com.app.ancoturf.presentation.home.order.interfaces.OnOrderButtonsClickedListener
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide

class QuickOrderAdapter(
    private val activity: AppCompatActivity,
    var orders: ArrayList<OrderDetailResponse>,
    var onuttonsClickedListener: OnOrderButtonsClickedListener,
    var showAll: Boolean
) :
    RecyclerView.Adapter<QuickOrderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity
            ).inflate(R.layout.item_quotes, parent, false)
        )
    }

    override fun getItemCount(): Int {
        if (showAll)
            return orders.size
        return if (orders.size >= 10) 10 else orders.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var order = orders[position]

        var imgList = ArrayList<String>()
        if (order.products != null && order.products.size > 0) {
            for (i in 0 until order.products.size) {
                if (!Utility.isValueNull(order.products[i].featureImageUrl) && imgList.size < 6) {
                    imgList.add(order.products[i].featureImageUrl!!)
                }
            }
        }
        holder.imgProductPlaceHolder.visibility = View.GONE
        holder.listOrderImages.visibility = View.GONE
        holder.layoutStatus.visibility = View.GONE
        holder.txtView.text = activity.getString(R.string.textReorder)

        when (imgList.size) {
            0 -> {
                holder.linearOne.visibility = View.VISIBLE
                holder.imgOrderProductOne.visibility = View.VISIBLE
                Glide.with(activity).load(R.drawable.img_place_holder_product)
                    .into(holder.imgOrderProductOne)
                holder.imgOrderProductTwo.visibility = View.GONE
                holder.linearTwo.visibility = View.GONE
                holder.linearThree.visibility = View.GONE
            }
            1 -> {
                holder.linearOne.visibility = View.VISIBLE
                holder.imgOrderProductOne.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[0]).into(holder.imgOrderProductOne)
                holder.imgOrderProductTwo.visibility = View.GONE
                holder.linearTwo.visibility = View.GONE
                holder.linearThree.visibility = View.GONE
            }
            2 -> {
                holder.linearOne.visibility = View.VISIBLE
                holder.imgOrderProductOne.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[0]).into(holder.imgOrderProductOne)
                holder.imgOrderProductTwo.visibility = View.GONE
                holder.linearTwo.visibility = View.VISIBLE
                holder.imgOrderProductThree.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[1]).into(holder.imgOrderProductThree)
                holder.imgOrderProductFour.visibility = View.GONE
                holder.linearThree.visibility = View.GONE
            }
            3 -> {
                holder.linearOne.visibility = View.VISIBLE
                holder.imgOrderProductOne.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[0]).into(holder.imgOrderProductOne)
                holder.imgOrderProductTwo.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[1]).into(holder.imgOrderProductTwo)
                holder.linearTwo.visibility = View.VISIBLE
                holder.imgOrderProductThree.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[2]).into(holder.imgOrderProductThree)
                holder.imgOrderProductFour.visibility = View.GONE
                holder.linearThree.visibility = View.GONE
            }
            4 -> {
                holder.linearOne.visibility = View.VISIBLE
                holder.imgOrderProductOne.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[0]).into(holder.imgOrderProductOne)
                holder.imgOrderProductTwo.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[1]).into(holder.imgOrderProductTwo)
                holder.linearTwo.visibility = View.VISIBLE
                holder.imgOrderProductThree.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[2]).into(holder.imgOrderProductThree)
                holder.imgOrderProductFour.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[3]).into(holder.imgOrderProductFour)
                holder.linearThree.visibility = View.GONE
            }
            5 -> {
                holder.linearOne.visibility = View.VISIBLE
                holder.imgOrderProductOne.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[0]).into(holder.imgOrderProductOne)
                holder.imgOrderProductTwo.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[1]).into(holder.imgOrderProductTwo)
                holder.linearTwo.visibility = View.VISIBLE
                holder.imgOrderProductThree.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[2]).into(holder.imgOrderProductThree)
                holder.imgOrderProductFour.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[3]).into(holder.imgOrderProductFour)
                holder.linearThree.visibility = View.VISIBLE
                holder.imgOrderProductFive.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[4]).into(holder.imgOrderProductFive)
                holder.imgOrderProductSix.visibility = View.GONE
            }
            6 -> {
                holder.linearOne.visibility = View.VISIBLE
                holder.imgOrderProductOne.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[0]).into(holder.imgOrderProductOne)
                holder.imgOrderProductTwo.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[1]).into(holder.imgOrderProductTwo)
                holder.linearTwo.visibility = View.VISIBLE
                holder.imgOrderProductThree.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[2]).into(holder.imgOrderProductThree)
                holder.imgOrderProductFour.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[3]).into(holder.imgOrderProductFour)
                holder.linearThree.visibility = View.VISIBLE
                holder.imgOrderProductFive.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[4]).into(holder.imgOrderProductFive)
                holder.imgOrderProductSix.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[5]).into(holder.imgOrderProductSix)
            }
        }


        if (!Utility.isValueNull(order.createdAt)) {
            holder.txtDate.text = activity.getString(
                R.string.quote_date,
                Utility.changeDateFormat(
                    order.createdAt,
                    Utility.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS,
                    Utility.DATE_FORMAT_DD_MM_YY
                )
            )
        } else {
            holder.txtDate.text = activity.getString(R.string.quote_date, "")
        }

        if (!Utility.isValueNull(order.shippingAddressLine1)) {

            var fullAddress = ""
            holder.txtAddress.visibility = View.VISIBLE
            fullAddress = order.shippingAddressLine1

            if (!Utility.isValueNull(order.shippingAddressLine2))
                fullAddress = fullAddress + " " + order.shippingAddressLine2
            else if (!Utility.isValueNull(order.shippingCity))
                fullAddress = fullAddress + " " + order.shippingCity
            else if (!Utility.isValueNull(order.shippingState))
                fullAddress = fullAddress + " " + order.shippingState
            else if (!Utility.isValueNull(order.shippingCountry))
                fullAddress = fullAddress + " " + order.shippingCountry
            else if (!Utility.isValueNull(order.shippingPostCode))
                fullAddress = fullAddress + " " + order.shippingPostCode

            holder.txtAddress.text = fullAddress

        } else {
            holder.txtAddress.visibility = View.GONE
        }

        if (!Utility.isValueNull(order.referenceNumber)) {
            holder.txtOrderNumber.visibility = View.VISIBLE
            holder.txtOrderNumber.text =
                activity.getString(R.string.order_number, order.referenceNumber)
        } else {
            holder.txtOrderNumber.visibility = View.GONE
        }

        if (!Utility.isValueNull(order.totalCartPrice)) {
            holder.txtOrderPrice.visibility = View.VISIBLE
            holder.txtOrderPrice.text =
                activity.getString(R.string.quote_price, Utility.formatNumber(Utility.roundTwoDecimals(order.totalCartPrice.toDouble()).toFloat()))
        } else {
            holder.txtOrderPrice.visibility = View.GONE
        }

        holder.txtView.setOnClickListener {
            onuttonsClickedListener.onClickOfReorder(orders[position])
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val listOrderImages = itemView
            .findViewById(R.id.listQuoteImages) as RecyclerView
        val txtDate = itemView
            .findViewById(R.id.txtDate) as TextView
        val txtAddress = itemView
            .findViewById(R.id.txtAddress) as TextView
        val txtOrderNumber = itemView
            .findViewById(R.id.txtQuoteNumber) as TextView
        val txtOrderPrice = itemView
            .findViewById(R.id.txtQuotePrice) as TextView
        val txtStatus = itemView
            .findViewById(R.id.txtStatus) as TextView
        val txtView = itemView
            .findViewById(R.id.txtView) as TextView
        val imgProductPlaceHolder = itemView
            .findViewById(R.id.imgProductPlaceHolder) as ImageView
        val linearOne = itemView
            .findViewById(R.id.linearOne) as LinearLayout
        val linearTwo = itemView
            .findViewById(R.id.linearTwo) as LinearLayout
        val linearThree = itemView
            .findViewById(R.id.linearThree) as LinearLayout
        val imgOrderProductOne = itemView
            .findViewById(R.id.imgQuoteProductOne) as ImageView
        val imgOrderProductTwo = itemView
            .findViewById(R.id.imgQuoteProductTwo) as ImageView
        val imgOrderProductThree = itemView
            .findViewById(R.id.imgQuoteProductThree) as ImageView
        val imgOrderProductFour = itemView
            .findViewById(R.id.imgQuoteProductFour) as ImageView
        val imgOrderProductFive = itemView
            .findViewById(R.id.imgQuoteProductFive) as ImageView
        val imgOrderProductSix = itemView
            .findViewById(R.id.imgQuoteProductSix) as ImageView
        val layoutStatus = itemView
            .findViewById(R.id.layoutStatus) as ConstraintLayout

    }

    fun addItems(postItems: ArrayList<OrderDetailResponse>) {
        orders.addAll(postItems)
        notifyDataSetChanged()
    }

}
