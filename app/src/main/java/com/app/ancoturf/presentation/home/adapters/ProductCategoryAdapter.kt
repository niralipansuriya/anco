package com.app.ancoturf.presentation.home.adapters

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.product.remote.entity.ProductCategoryData
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.presentation.home.products.ProductsFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import java.util.*


class ProductCategoryAdapter(private val activity: AppCompatActivity, var arrayList: ArrayList<ProductCategoryData>) :
    RecyclerView.Adapter<ProductCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCategoryAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_quick_link, parent, false))
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.txtLinkTitle.text = arrayList[position].displayName

        Glide.with(activity).load(arrayList[position].imageUrl).into(object : SimpleTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: com.bumptech.glide.request.transition.Transition<in Drawable>?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.mainLayout.setBackground(resource)
                }
            }
        })

//        holder.mainLayout.setOnClickListener(View.OnClickListener {
//            val productsFragment = ProductsFragment(arrayList.get(position).id.toString() , arrayList.get(position).displayName)
//            activity.pushFragment(
//                productsFragment,
//                true,
//                true,
//                false,
//                R.id.flContainerHome
//            )
//        })
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtLinkTitle = itemView
            .findViewById(R.id.txtLinkTitle) as TextView
        val mainLayout = itemView
            .findViewById(R.id.mainLayout) as androidx.constraintlayout.widget.ConstraintLayout

    }


}