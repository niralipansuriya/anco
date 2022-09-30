package com.app.ancoturf.presentation.home.portfolio.adapters

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.portfolio.remote.entity.PortfolioResponse
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.presentation.home.portfolio.AddEditPortfolioFragment
import com.app.ancoturf.presentation.home.portfolio.utils.ItemTouchHelperAdapter
import com.app.ancoturf.presentation.home.portfolio.utils.ItemTouchHelperViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.util.*

class PortfolioAdapter(
    private val activity: AppCompatActivity?,
    private val portfolios: ArrayList<PortfolioResponse.Data>
) :
    RecyclerView.Adapter<PortfolioAdapter.ViewHolder>(), ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity
            ).inflate(R.layout.item_portfolio, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return portfolios.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtPortfolioTitle.text = portfolios[position].projectName
        holder.txtTotalItems.text =
            activity!!.getString(R.string.items, portfolios[position].portfolioProductCount)
        Glide.with(activity).load(portfolios[position].featuredImage.imageUrl)
            .into(holder.ivBackground)
//        Glide.with(activity).load(imgList[0]).into(holder.imgQuoteProductOne)


        /*Glide.with(activity).load(portfolios[position].featuredImage.imageUrl)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        holder.mainLayout.background = resource
                    } else {
                        holder.mainLayout.setBackgroundDrawable(resource)
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })*/
      /*  if (portfolios[position].featuredImage != null) {

            Glide.with(activity).load(portfolios[position].featuredImage.imageUrl)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            holder.mainLayout.background = resource
                        } else {
                            holder.mainLayout.setBackgroundDrawable(resource)
                        }
                        return false
                    }
                })
        }*/

        holder.overlayView.setBackgroundResource(R.drawable.bg_offer_overlay)


        holder.mainLayout.setOnClickListener {
            activity.hideKeyboard()
            val bundle = Bundle()
            bundle.putInt("portfolioMode", Constants.VIEW_PORTFOLIO)
            bundle.putInt("portfolioId", portfolios[position].id)
            activity.pushFragment(
                AddEditPortfolioFragment().apply {
                    arguments = bundle
                },
                true,
                true,
                false,
                R.id.flContainerHome
            )
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        ItemTouchHelperViewHolder {
        val txtPortfolioTitle = itemView
            .findViewById(R.id.txtPortfolioTitle) as TextView
        val txtTotalItems = itemView
            .findViewById(R.id.txtTotalItems) as TextView
        val mainLayout = itemView
            .findViewById(R.id.mainLayout) as ConstraintLayout
        val overlayView = itemView
            .findViewById(R.id.overlayView) as View
        val ivBackground = itemView
            .findViewById(R.id.ivBackground) as ImageView
        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        override fun onItemClear() {
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(portfolios, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
    }

}