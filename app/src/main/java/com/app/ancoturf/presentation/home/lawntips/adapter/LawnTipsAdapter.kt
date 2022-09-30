package com.app.ancoturf.presentation.home.lawntips.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.lawntips.remote.LawnTipsDataResponse
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.presentation.home.aboutUs.AboutUsDetailFragment
import com.app.ancoturf.presentation.home.lawntips.LawnTipsDetailFragment
import com.bumptech.glide.Glide
import java.util.*

class LawnTipsAdapter(
    private val activity: AppCompatActivity,
    private val arrayList: ArrayList<LawnTipsDataResponse.Data>,
    private val isLawnTips : Boolean
) :
    RecyclerView.Adapter<LawnTipsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_lawn_tips, parent, false))
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtLawnTipsTitle.text =  Html.fromHtml(arrayList[position].title)
//        holder.mainLayout.setBackgroundResource(arrayList[position].bgImage)

        activity?.let { Glide.with(it).load(arrayList[position].featureImageUrl).into(holder.imgLawnTips) }
        holder.mainLayout.setOnClickListener {

            if (isLawnTips) {
                val lawntipsDetailFragment =
                    LawnTipsDetailFragment(arrayList[position].id)

                activity.pushFragment(
                    lawntipsDetailFragment,
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }else{
                val aboutUsDetailFragment =
                    AboutUsDetailFragment(arrayList[position].id)

                activity.pushFragment(
                    aboutUsDetailFragment,
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtLawnTipsTitle = itemView
            .findViewById(R.id.txtLawnTipsTitle) as TextView
        val mainLayout = itemView
            .findViewById(R.id.mainLayout) as androidx.constraintlayout.widget.ConstraintLayout
        val imgLawnTips = itemView
            .findViewById(R.id.imgLawnTips) as ImageView
        val view = itemView
            .findViewById(R.id.view) as View
    }
    fun addItems(postItems: ArrayList<LawnTipsDataResponse.Data>) {
        arrayList.addAll(postItems)
        notifyDataSetChanged()
    }
}