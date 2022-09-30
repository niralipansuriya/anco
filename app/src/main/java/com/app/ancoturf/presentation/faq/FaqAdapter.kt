package com.app.ancoturf.presentation.faq

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.aboutUs.FAQResponse
import com.app.ancoturf.utils.customeview.ExpandableLayout
import kotlinx.android.synthetic.main.item_faq.view.*

class FaqAdapter(var list: ArrayList<FAQResponse.Data>) : RecyclerView.Adapter<FaqAdapter.ViewHolder>() {

    lateinit var context: Context
    var prePos :Int = -1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(faqModel: FAQResponse.Data) {
            itemView.tvQuestion.text = faqModel.que + ""
            itemView.tvAnswer.text = faqModel.ans + ""
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_faq, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var faqModel: FAQResponse.Data = list.get(position)
        if (faqModel.isOpen!!) {
            holder.itemView.expand_layout.setExpand(true)
            holder.itemView.tvQuestion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sub_box,0,0,0)

        }else{
            holder.itemView.expand_layout.setExpand(false)
            holder.itemView.tvQuestion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_box,0,0,0)
        }
        holder.bindData(faqModel)
        holder.itemView.expand_layout.setOnClickListener {
            // Expand when you click on cell
            holder.itemView.expand_layout.setOnExpandListener(object :
                ExpandableLayout.OnExpandListener {
                override fun onExpand(expanded: Boolean) {
                  /*  if (prePos != -1 && prePos != position){
                        list.get(prePos).isOpen = false
                        notifyItemChanged(prePos)
                        prePos = -1
                    }*/
                    if (faqModel.isOpen!!){
                        faqModel.isOpen = false
                        holder.itemView.tvQuestion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_box,0,0,0)
                        holder.itemView.expand_layout.setExpand(false)
                    }else{
                        prePos = position
                        faqModel.isOpen = true
                        holder.itemView.tvQuestion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sub_box,0,0,0)
                        holder.itemView.expand_layout.setExpand(true)
                    }
                }
            })
        }
    }
}