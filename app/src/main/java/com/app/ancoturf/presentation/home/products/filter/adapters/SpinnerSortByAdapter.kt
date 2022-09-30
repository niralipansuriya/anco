package com.app.ancoturf.presentation.home.products.filter.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.app.ancoturf.R
import com.app.ancoturf.data.product.remote.entity.response.ProductSortByCategories

class SpinnerSortByAdapter(private val context: Activity, var  sortByCategories: List<ProductSortByCategories>) : BaseAdapter() {

    override fun getCount(): Int {
        return sortByCategories.size
    }

    override fun getItem(position: Int): Any {
        return sortByCategories.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View {
        var convertView = convertView
        val holder: Holder
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_sort_by, null)
            holder = Holder()
            holder.txtSortTitle = convertView?.findViewById<View>(R.id.txtSortTitle) as TextView
            convertView.tag = holder
        } else {
            holder = convertView.tag as Holder
        }


        holder.txtSortTitle?.text = sortByCategories.get(position).displayName
        return convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_sort_by_dropdown, parent, false)
        }
        val url = getItem(position)
        val txtSortTitle = convertView?.findViewById<View>(R.id.txtSortTitle) as TextView
        txtSortTitle.text = sortByCategories.get(position).displayName
        return convertView
    }


    inner class Holder {
        internal var txtSortTitle: TextView? = null
    }
}