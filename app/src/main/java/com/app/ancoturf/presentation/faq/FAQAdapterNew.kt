package com.app.ancoturf.presentation.faq

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.aboutUs.FAQResponse
import kotlinx.android.synthetic.main.item_faq.view.tvAnswer
import kotlinx.android.synthetic.main.item_faq.view.tvQuestion
import kotlinx.android.synthetic.main.item_faq_new.view.*
//import net.cachapa.expandablelayout.ExpandableLayout


class FAQAdapterNew(var list: ArrayList<FAQResponse.Data>) : RecyclerView.Adapter<FAQAdapterNew.ViewHolder>() {

    lateinit var context: Context
    var prePos :Int = -1


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private var expandableLayout: ExpandableLayout? = null
        fun bindData(faqModel: FAQResponse.Data) {
            itemView.tvQuestion.text = faqModel.que + ""
            itemView.tvAnswer.text = faqModel.ans + ""
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_faq_new, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var faqModel: FAQResponse.Data = list.get(position)
        if (faqModel.isOpen!!) {
//            holder.itemView.expand_layout.setExpand(true)
            holder.itemView.tvQuestion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sub_box,0,0,0)
            holder.itemView.tvAnswer.visibility  = View.VISIBLE

        }else{
//            holder.itemView.tvQuestion.setExpand(false)
            holder.itemView.tvQuestion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_box,0,0,0)
            holder.itemView.tvAnswer.visibility  = View.GONE
        }
        holder.bindData(faqModel)
        holder.itemView.tvQuestion.setOnClickListener {
            // Expand when you click on cell
            if (faqModel.isOpen!!){
                holder.itemView.tvAnswer.visibility  = View.GONE
                list.get(position).isOpen = false
//                faqModel.isOpen = false
                holder.itemView.tvQuestion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_box,0,0,0)
            }else{
                prePos = position
                holder.itemView.tvAnswer.visibility  = View.VISIBLE
                list.get(position).isOpen = true
//                faqModel.isOpen = true
                holder.itemView.tvQuestion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sub_box,0,0,0)
            }
        }
    }
}