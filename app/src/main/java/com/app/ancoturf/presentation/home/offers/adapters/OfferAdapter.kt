package com.app.ancoturf.presentation.home.offers.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.offer.remote.OfferDataResponse
import com.app.ancoturf.data.product.remote.entity.response.ProductsResponse
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.presentation.home.offers.OfferDetailFragment
import com.app.ancoturf.presentation.home.offers.OffersFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.util.*

class OfferAdapter(private val activity: AppCompatActivity, private val arrayList: ArrayList<OfferDataResponse.Data>) :
    RecyclerView.Adapter<OfferAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(activity).inflate(
                R.layout.item_offer_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtOffer.text = arrayList[position].title

        Glide.with(activity).load(arrayList[position].imageUrl).into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        holder.mainLayout.background = resource
                    } else {
                        holder.mainLayout.setBackgroundDrawable(resource)
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

            })

        holder.overlayView.setBackgroundResource(R.drawable.bg_offer_overlay)

        holder.mainLayout.setOnClickListener {
            activity.pushFragment(
                OfferDetailFragment(arrayList[position]),
                true,
                true,
                false,
                R.id.flContainerHome
            )
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtOffer = itemView
            .findViewById(R.id.txtOffer) as TextView
        val mainLayout = itemView
            .findViewById(R.id.mainLayout) as androidx.constraintlayout.widget.ConstraintLayout
        val overlayView = itemView
            .findViewById(R.id.overlayView) as View
    }
    fun addItems(postItems: ArrayList<OfferDataResponse.Data>) {
        arrayList.addAll(postItems)
        notifyDataSetChanged()
    }
}