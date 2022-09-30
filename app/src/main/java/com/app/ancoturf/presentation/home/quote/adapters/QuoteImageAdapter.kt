package com.app.ancoturf.presentation.home.quote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import java.util.*


class QuoteImageAdapter(
    private val activity: AppCompatActivity,
    var quoteImages: ArrayList<String>
) :
    RecyclerView.Adapter<QuoteImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity
            ).inflate(R.layout.item_quote_image, parent, false)
        )

    }

    override fun getItemCount(): Int {
        if(quoteImages.size > 6)
            return 6
        return quoteImages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(!Utility.isValueNull(quoteImages[position])) {
            Glide.with(activity).load(quoteImages[position]).into(holder.imgQuoteProduct)
        }

    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgQuoteProduct = itemView
            .findViewById(R.id.imgQuoteProduct) as ImageView
        val mainLayout = itemView
            .findViewById(R.id.mainLayout) as ConstraintLayout

    }


}