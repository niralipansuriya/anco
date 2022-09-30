package com.app.ancoturf.presentation.cart.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.app.ancoturf.R
import com.app.ancoturf.data.cart.remote.entity.CountryResponse

class SpinnerCountryAdapter(private val context: Activity, var  countries: ArrayList<CountryResponse>) : BaseAdapter() {

    override fun getCount(): Int {
        return countries.size
    }

    override fun getItem(position: Int): Any {
        return countries.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View {
        var convertView = convertView
        val holder: Holder
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart_shipping_details, null)
            holder = Holder()
            holder.txtTitle = convertView?.findViewById<View>(R.id.txtTitle) as TextView
            convertView.tag = holder
        } else {
            holder = convertView.tag as Holder
        }


        holder.txtTitle?.text = countries.get(position).countryName
        return convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart_shipping_details, parent, false)
        }
        val url = getItem(position)
        val txtTitle = convertView?.findViewById<View>(R.id.txtTitle) as TextView
        txtTitle.text = countries.get(position).countryName
        return convertView
    }


    inner class Holder {
        internal var txtTitle: TextView? = null
    }
}