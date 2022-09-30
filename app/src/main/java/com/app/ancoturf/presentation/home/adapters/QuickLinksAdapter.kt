package com.app.ancoturf.presentation.home.adapters

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.offer.ClsQuickLinks
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.presentation.home.interfaces.IDataPositionWise
import com.app.ancoturf.presentation.home.order.OrderDetailsFragment
import com.app.ancoturf.presentation.home.portfolio.PortfoliosFragment
import com.app.ancoturf.presentation.home.shop.ShopFragment
import com.bumptech.glide.Glide
import java.util.*

class QuickLinksAdapter(private val context: Context?, private val arrayList: ArrayList<ClsQuickLinks>,var iDataPositionWise: IDataPositionWise) :
    RecyclerView.Adapter<QuickLinksAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickLinksAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_quick_link, parent, false))
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: QuickLinksAdapter.ViewHolder, position: Int) {
        holder.txtLinkTitle.text =  Html.fromHtml(arrayList[position].title)
//        holder.mainLayout.setBackgroundResource(arrayList[position].bgImage)
        if (arrayList[position].bgImgUrl != ""){
            context?.let {
                Glide.with(it).load(arrayList[position].bgImgUrl).into(holder.imgCategory)
            }
        }else {
            context?.let {
                Glide.with(it).load(arrayList[position].bgImage).into(holder.imgCategory)
            }
        }


        if (arrayList[position].fragment != ShopFragment() || arrayList[position].fragment != PortfoliosFragment()) {
            holder.mainLayout.setOnClickListener {

                iDataPositionWise.getDataFromPos(position,arrayList[position].bgImage.toString())
                /*var activity = context as AppCompatActivity

                when (arrayList[position].title) {
//                    1, 3, 4 ,5,7 -> {
//                        activity.hideKeyboard()
//                        activity.pushFragment(
//                            arrayList[position].fragment,
//                            true,
//                            true,
//                            false,
//                            R.id.flContainerHome
//                        )
//                    }
                    activity.getString(R.string.choose_my_lawn),activity.getString(R.string.manage_my_lawn),activity.getString(R.string.shop_title), activity.getString(R.string.portfolio_title) , activity.getString(R.string.quote_title), activity.getString(R.string.turf_calculator_title), activity.getString(R.string.quick_order_title),activity.getString(R.string.lawn_tips_title),activity.getString(R.string.about_us) -> {
                        activity.hideKeyboard()
                        activity.pushFragment(
                            arrayList[position].fragment,
                            true,
                            true,
                            false,
                            R.id.flContainerHome
                        )
                    }
                    *//* activity.getString(R.string.portfolio_title) , activity.getString(R.string.quote_title)-> {
                        activity.hideKeyboard()
                        activity.pushFragment(
                            arrayList[position].fragment,
                            false,
                            true,
                            false,
                            R.id.flContainerHome
                        )
                    }*//*
                    activity.getString(R.string.orders_title) -> {
                        var sharedPrefs = SharedPrefs(context)
                        if (sharedPrefs.isLogged) {
                            activity.pushFragment(
                                arrayList[position].fragment,
                                true,
                                true,
                                false,
                                R.id.flContainerHome
                            )
                        } else {
                            activity.pushFragment(
                                OrderDetailsFragment(null,0),
                                true,
                                true,
                                false,
                                R.id.flContainerHome
                            )
                        }
                    }
                    activity.getString(R.string.tracking_title) ->{
                        var sharedPrefs = SharedPrefs(context)
                        if (sharedPrefs.isLogged) {
                            activity.pushFragment(
                                arrayList[position].fragment,
                                true,
                                true,
                                false,
                                R.id.flContainerHome
                            )
                        } else {
                            activity.pushFragment(
                                OrderDetailsFragment(null,0),
                                true,
                                true,
                                false,
                                R.id.flContainerHome
                            )
                        }
                    }
                }*/
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtLinkTitle = itemView
            .findViewById(R.id.txtLinkTitle) as TextView
        val mainLayout = itemView
            .findViewById(R.id.mainLayout) as androidx.constraintlayout.widget.ConstraintLayout
        val imgCategory = itemView
            .findViewById(R.id.imgCategory) as ImageView
        val view = itemView
            .findViewById(R.id.view) as View
    }
}