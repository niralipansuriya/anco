package com.app.ancoturf.presentation.home.products.filter.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.product.remote.entity.response.ProductCategoryData
import com.app.ancoturf.presentation.home.products.filter.ProductFilterFragment
import java.util.*


class ProductCategoryFilterAdapter(
    private val activity: AppCompatActivity,
    var arrayList: ArrayList<ProductCategoryData>,
    var productFilterFragment: ProductFilterFragment
) :
    RecyclerView.Adapter<ProductCategoryFilterAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity
            ).inflate(R.layout.item_category, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.txtTitle.text = arrayList.get(position).displayName

        holder.imgCheck.setImageResource(if (arrayList[position].checked) R.drawable.ic_checkbox_h else R.drawable.ic_checkbox)
        productFilterFragment.setSelectedCategories(arrayList)

        holder.mainLayout.setOnClickListener {
            arrayList[position].checked = !arrayList[position].checked
            productFilterFragment.setSelectedCategories(arrayList)
            notifyDataSetChanged()
        }

        holder.view.visibility = if (position == arrayList.size - 1) View.GONE else View.VISIBLE

    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitle = itemView
            .findViewById(R.id.txtTitle) as TextView

        val imgCheck = itemView
            .findViewById(R.id.imgCheck) as ImageView

        val view = itemView
            .findViewById(R.id.view) as View

        val mainLayout = itemView
            .findViewById(R.id.mainLayout) as ConstraintLayout

    }

}