package com.app.ancoturf.presentation.home.portfolio.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.portfolio.remote.entity.PortfolioDetailResponse
import com.app.ancoturf.presentation.home.portfolio.interfaces.OnNonAncoProductChangeListener
import java.util.ArrayList

class NonAncoProductsAdapter(
    private val activity: AppCompatActivity,
    var arrayList: ArrayList<PortfolioDetailResponse.CustomProduct>,
    private val showDelete: Boolean,
    private val onNonAncoProductChangeListener: OnNonAncoProductChangeListener
) :
    RecyclerView.Adapter<NonAncoProductsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity
            ).inflate(R.layout.item_non_anco_product, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = arrayList[position]

        holder.txtProductTitle.text = product.name
        holder.txtProductQuantity.text = activity.getString(R.string.non_anco_product_qty, "" + product.pivot.quantity)

        holder.imgProductDelete.visibility = if (showDelete) View.VISIBLE else View.GONE

        holder.imgProductDelete.setOnClickListener {
            onNonAncoProductChangeListener.onItemDelete(arrayList[position])
        }

        holder.mainLayout.setOnClickListener {
            if (showDelete)
                onNonAncoProductChangeListener.onClick(arrayList[position])
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtProductTitle = itemView
            .findViewById(R.id.txtProductTitle) as TextView
        val txtProductQuantity = itemView
            .findViewById(R.id.txtProductQuantity) as TextView
        val imgProductDelete = itemView
            .findViewById(R.id.imgProductDelete) as ImageView
        val mainLayout = itemView
            .findViewById(R.id.mainLayout) as ConstraintLayout
    }

}