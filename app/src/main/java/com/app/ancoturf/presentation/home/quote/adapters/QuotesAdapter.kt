package com.app.ancoturf.presentation.home.quote.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.quote.remote.entity.response.QuoteDetailsResponse
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.presentation.home.order.interfaces.OnBack
import com.app.ancoturf.presentation.home.quote.AddCustomerInfoFragment
import com.app.ancoturf.presentation.home.quote.AddEditQuoteFragment
import com.app.ancoturf.presentation.home.quote.interfaces.OnQuoteButtonsClickedListener
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide


class QuotesAdapter(
    private val activity: AppCompatActivity,
    var quotes: ArrayList<QuoteDetailsResponse>,
    var onQuoteButtonsClickedListener: OnQuoteButtonsClickedListener,
    var showAll: Boolean,
    var onButtonClick: OnBack
) :
    RecyclerView.Adapter<QuotesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity
            ).inflate(R.layout.item_quotes, parent, false)
        )
    }

    override fun getItemCount(): Int {
        if (showAll)
            return quotes.size
        return if (quotes.size >= 10) 10 else quotes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var quote = quotes[position]
//        if(quote.products != null && quote.products.size > 0  && quote.customProducts != null && quote.customProducts.size > 0) {
//
        var imgList = ArrayList<String>()
        if (quote.products != null && quote.products.size > 0) {
            for (i in 0 until quote.products.size) {
                if (!Utility.isValueNull(quote.products[i].featureImageUrl) && imgList.size < 6) {
                    imgList.add(quote.products[i].featureImageUrl!!)
                }
            }
        }

        if (quote.customProducts != null && quote.customProducts.size > 0) {

            for (i in 0 until quote.customProducts.size) {
                if (!Utility.isValueNull(quote.customProducts[i].imageUrl) && imgList.size < 6) {
                    imgList.add(quote.customProducts[i].imageUrl!!)
                }
            }
        }

//        holder.listQuoteImages.layoutManager = layoutManager
//        var imageAdapter = QuoteImageAdapter(activity, imgList)
//        holder.listQuoteImages.adapter = imageAdapter

        holder.imgProductPlaceHolder.visibility = View.GONE
        holder.listQuoteImages.visibility = View.GONE

        when (imgList.size) {
            0 -> {
                holder.linearOne.visibility = View.VISIBLE
                holder.imgQuoteProductOne.visibility = View.VISIBLE
                if ((quote.products != null && quote.products.size > 0) || (quote.customProducts != null && quote.customProducts.size > 0)) {
                    holder.imgQuoteProductOne.scaleType = ImageView.ScaleType.CENTER
                    Glide.with(activity).load(R.mipmap.ic_anco_app_icon)
                        .into(holder.imgQuoteProductOne)
                } else {
                    holder.imgQuoteProductOne.scaleType = ImageView.ScaleType.CENTER
                    Glide.with(activity).load(R.drawable.img_quote_plce_holder).override(300, 300)
                        .into(holder.imgQuoteProductOne)
                }
                holder.imgQuoteProductTwo.visibility = View.GONE
                holder.linearTwo.visibility = View.GONE
                holder.linearThree.visibility = View.GONE
            }
            1 -> {
                holder.linearOne.visibility = View.VISIBLE
                holder.imgQuoteProductOne.visibility = View.VISIBLE
                holder.imgQuoteProductOne.scaleType = ImageView.ScaleType.FIT_XY
                Glide.with(activity).load(imgList[0]).into(holder.imgQuoteProductOne)
                holder.imgQuoteProductTwo.visibility = View.GONE
                holder.linearTwo.visibility = View.GONE
                holder.linearThree.visibility = View.GONE
            }
            2 -> {
                holder.linearOne.visibility = View.VISIBLE
                holder.imgQuoteProductOne.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[0]).into(holder.imgQuoteProductOne)
                holder.imgQuoteProductTwo.visibility = View.GONE
                holder.linearTwo.visibility = View.VISIBLE
                holder.imgQuoteProductThree.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[1]).into(holder.imgQuoteProductThree)
                holder.imgQuoteProductFour.visibility = View.GONE
                holder.linearThree.visibility = View.GONE
            }
            3 -> {
                holder.linearOne.visibility = View.VISIBLE
                holder.imgQuoteProductOne.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[0]).into(holder.imgQuoteProductOne)
                holder.imgQuoteProductTwo.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[1]).into(holder.imgQuoteProductTwo)
                holder.linearTwo.visibility = View.VISIBLE
                holder.imgQuoteProductThree.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[2]).into(holder.imgQuoteProductThree)
                holder.imgQuoteProductFour.visibility = View.GONE
                holder.linearThree.visibility = View.GONE
            }
            4 -> {
                holder.linearOne.visibility = View.VISIBLE
                holder.imgQuoteProductOne.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[0]).into(holder.imgQuoteProductOne)
                holder.imgQuoteProductTwo.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[1]).into(holder.imgQuoteProductTwo)
                holder.linearTwo.visibility = View.VISIBLE
                holder.imgQuoteProductThree.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[2]).into(holder.imgQuoteProductThree)
                holder.imgQuoteProductFour.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[3]).into(holder.imgQuoteProductFour)
                holder.linearThree.visibility = View.GONE
            }
            5 -> {
                holder.linearOne.visibility = View.VISIBLE
                holder.imgQuoteProductOne.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[0]).into(holder.imgQuoteProductOne)
                holder.imgQuoteProductTwo.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[1]).into(holder.imgQuoteProductTwo)
                holder.linearTwo.visibility = View.VISIBLE
                holder.imgQuoteProductThree.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[2]).into(holder.imgQuoteProductThree)
                holder.imgQuoteProductFour.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[3]).into(holder.imgQuoteProductFour)
                holder.linearThree.visibility = View.VISIBLE
                holder.imgQuoteProductFive.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[4]).into(holder.imgQuoteProductFive)
                holder.imgQuoteProductSix.visibility = View.GONE
            }
            6 -> {
                holder.linearOne.visibility = View.VISIBLE
                holder.imgQuoteProductOne.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[0]).into(holder.imgQuoteProductOne)
                holder.imgQuoteProductTwo.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[1]).into(holder.imgQuoteProductTwo)
                holder.linearTwo.visibility = View.VISIBLE
                holder.imgQuoteProductThree.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[2]).into(holder.imgQuoteProductThree)
                holder.imgQuoteProductFour.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[3]).into(holder.imgQuoteProductFour)
                holder.linearThree.visibility = View.VISIBLE
                holder.imgQuoteProductFive.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[4]).into(holder.imgQuoteProductFive)
                holder.imgQuoteProductSix.visibility = View.VISIBLE
                Glide.with(activity).load(imgList[5]).into(holder.imgQuoteProductSix)
            }
        }
//        System.gc()

//        } else {
//            holder.imgProductPlaceHolder.visibility = View.VISIBLE
//            holder.listQuoteImages.visibility = View.GONE
//        }

        if (!Utility.isValueNull(quote.createdAt)) {
            holder.txtDate.text = activity.getString(
                R.string.quote_date,
                Utility.changeDateFormat(
                    quote.createdAt,
                    Utility.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS,
                    Utility.DATE_FORMAT_DD_MM_YY
                )
            )
        } else {
            holder.txtDate.text = activity.getString(R.string.quote_date, "")
        }

        if (quote.customer != null && !Utility.isValueNull(quote.customer.customerAddress)) {
            holder.txtAddress.visibility = View.VISIBLE
            holder.txtAddress.text = quote.customer.customerAddress
        } else {
            holder.txtAddress.visibility = View.GONE
        }

        if (!Utility.isValueNull(quote.id.toString())) {
            holder.txtQuoteNumber.visibility = View.VISIBLE
            holder.txtQuoteNumber.text =
                activity.getString(R.string.quote_number, quote.id.toString())
        } else {
            holder.txtQuoteNumber.visibility = View.GONE
        }

        if (!Utility.isValueNull(quote.totalCost)) {
            holder.txtQuotePrice.visibility = View.VISIBLE
            holder.txtQuotePrice.text = activity.getString(
                R.string.quote_price,
                Utility.formatNumber(Utility.roundTwoDecimals(quote.totalCost.toDouble()).toFloat())
            )
        } else {
            holder.txtQuotePrice.visibility = View.GONE
        }

        if (!Utility.isValueNull(quote.status)) {
            holder.txtStatus.text = quote.status
            if (quote.status.equals("Pending")) {
                holder.txtStatus.minLines = 1
                holder.txtView.visibility = View.VISIBLE
                holder.txtDuplicate.visibility = View.VISIBLE
                holder.txtResend.visibility = View.VISIBLE
            } else if (quote.status.equals("Sent")) {
                holder.txtStatus.minLines = 1
                holder.txtView.visibility = View.VISIBLE
                holder.txtDuplicate.visibility = View.VISIBLE
                holder.txtResend.visibility = View.VISIBLE
            } else if (quote.status.equals("Draft")) {
                holder.txtStatus.minLines = 2
                holder.txtView.visibility = View.VISIBLE
                holder.txtDuplicate.visibility = View.GONE
                holder.txtResend.visibility = View.GONE
            }
        } else {
            holder.txtStatus.text = ""
        }

        holder.txtView.setOnClickListener {
            onButtonClick.onClickOfBack(true)

            if (quote.customer != null) {
                val bundle = Bundle()
                if (quote.status.equals("Draft") || quote.status.equals("Pending")) {
                    bundle.putInt("quoteMode", Constants.DRAFT_QUOTE)
                } else
                    bundle.putInt("quoteMode", Constants.VIEW_QUOTE)
                bundle.putInt("quoteId", quotes[position].id)
                activity.pushFragment(
                    AddEditQuoteFragment().apply { arguments = bundle },
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            } else {
                activity.pushFragment(
                    AddCustomerInfoFragment(quotes[position], null),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }
        }


        holder.txtResend.setOnClickListener {
            onQuoteButtonsClickedListener.onClickOfResend(quotes[position])
        }

        holder.txtDuplicate.setOnClickListener {
            onQuoteButtonsClickedListener.onClickOfDuplicate(quotes[position])
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val listQuoteImages = itemView
            .findViewById(R.id.listQuoteImages) as RecyclerView
        val txtDate = itemView
            .findViewById(R.id.txtDate) as TextView
        val txtAddress = itemView
            .findViewById(R.id.txtAddress) as TextView
        val txtQuoteNumber = itemView
            .findViewById(R.id.txtQuoteNumber) as TextView
        val txtQuotePrice = itemView
            .findViewById(R.id.txtQuotePrice) as TextView
        val txtCreateOrder = itemView
            .findViewById(R.id.txtCreateOrder) as TextView
        val txtStatus = itemView
            .findViewById(R.id.txtStatus) as TextView
        val txtView = itemView
            .findViewById(R.id.txtView) as TextView
        val txtDuplicate = itemView
            .findViewById(R.id.txtDuplicate) as TextView
        val txtResend = itemView
            .findViewById(R.id.txtResend) as TextView
        val imgProductPlaceHolder = itemView
            .findViewById(R.id.imgProductPlaceHolder) as ImageView
        val linearOne = itemView
            .findViewById(R.id.linearOne) as LinearLayout
        val linearTwo = itemView
            .findViewById(R.id.linearTwo) as LinearLayout
        val linearThree = itemView
            .findViewById(R.id.linearThree) as LinearLayout
        val imgQuoteProductOne = itemView
            .findViewById(R.id.imgQuoteProductOne) as ImageView
        val imgQuoteProductTwo = itemView
            .findViewById(R.id.imgQuoteProductTwo) as ImageView
        val imgQuoteProductThree = itemView
            .findViewById(R.id.imgQuoteProductThree) as ImageView
        val imgQuoteProductFour = itemView
            .findViewById(R.id.imgQuoteProductFour) as ImageView
        val imgQuoteProductFive = itemView
            .findViewById(R.id.imgQuoteProductFive) as ImageView
        val imgQuoteProductSix = itemView
            .findViewById(R.id.imgQuoteProductSix) as ImageView
    }


    fun addItems(postItems: ArrayList<QuoteDetailsResponse>) {
        quotes.addAll(postItems)
        notifyDataSetChanged()
    }


}