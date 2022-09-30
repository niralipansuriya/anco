package com.app.ancoturf.presentation.home.products.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.app.ancoturf.R
import com.app.ancoturf.data.offer.ClsRating


class SpinnerRatingAdapter(private val context: Activity, var  ratings: List<ClsRating>) : BaseAdapter() {

    override fun getCount(): Int {
        return ratings.size
    }

    override fun getItem(position: Int): Any {
        return ratings.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View {
        var convertView = convertView
        val holder: Holder
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_rating, null)
            holder = Holder()
            holder.txtRating = convertView!!.findViewById<View>(R.id.txtRating) as TextView
            convertView.tag = holder
        } else {
            holder = convertView.tag as Holder
        }


        holder.txtRating?.text = ratings.get(position).title
        return convertView
    }


    inner class Holder {
        internal var txtRating: TextView? = null
    }
}