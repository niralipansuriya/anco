package com.app.ancoturf.presentation.manageLawn

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.manageLawn.remote.ManageLawnDataResponse
import com.app.ancoturf.extension.pushFragment
import com.bumptech.glide.Glide
import java.util.ArrayList
/*Post - geProductFullname(parameter: product_name)

get - mylawns - Get All My Lawns data

get - mylawn/{id}- Get Id wise My Lawns data*/
class ManageLawnAdapter(
    private val activity: AppCompatActivity,
    private val arrayList: ArrayList<ManageLawnDataResponse.Data>
) :
    RecyclerView.Adapter<ManageLawnAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_manage_lawn, parent, false))
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtLawnTipsTitle.text =  Html.fromHtml(arrayList[position].cat_name)
//        holder.mainLayout.setBackgroundResource(arrayList[position].bgImage)

        activity?.let { Glide.with(it).load(arrayList[position].image_url).into(holder.imgLawnTips) }
        holder.mainLayout.setOnClickListener {
            val manageLawnDetailFragment =
                ManageLawnDetailFragment(arrayList[position].cat_id)

            activity.pushFragment(
                manageLawnDetailFragment,
                true,
                true,
                false,
                R.id.flContainerHome
            )
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtLawnTipsTitle = itemView
            .findViewById(R.id.txtLawnTipsTitle) as TextView
        val mainLayout = itemView
            .findViewById(R.id.mainLayout) as androidx.constraintlayout.widget.ConstraintLayout
        val imgLawnTips = itemView
            .findViewById(R.id.imgLawnTips) as ImageView

    }
    fun addItems(postItems: ArrayList<ManageLawnDataResponse.Data>) {
        arrayList.addAll(postItems)
        notifyDataSetChanged()
    }


}