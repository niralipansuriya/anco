package com.app.ancoturf.presentation.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.search.remote.entity.response.SearchProductResponse
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.presentation.home.products.ProductDetailFragment
import com.bumptech.glide.Glide

class SearchAdapter (
    private val activity: AppCompatActivity,
    var lastrelatedsearch: ArrayList<SearchProductResponse.SearchedProducts>
) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity as Context?
            ).inflate(R.layout.item_last_related_search, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return lastrelatedsearch.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(activity).load(lastrelatedsearch[position].featureImageUrl).into(holder.imgSearchRelatedProduct)

        holder.mainLayout.setOnClickListener {
            activity.hideKeyboard()
            activity.pushFragment(
                ProductDetailFragment(lastrelatedsearch[position].id),
                true,
                true,
                false,
                R.id.flContainerHome
            )
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgSearchRelatedProduct = itemView
            .findViewById(R.id.imgSearchRelatedProduct) as ImageView
        val mainLayout = itemView
            .findViewById(R.id.mainLayout) as ConstraintLayout
    }

}