package com.app.ancoturf.presentation.home.portfolio.adapters

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.portfolio.remote.entity.PortfolioDetailResponse
import com.app.ancoturf.data.portfolio.remote.entity.PortfolioResponse
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.presentation.home.portfolio.AddEditPortfolioFragment
import com.app.ancoturf.presentation.home.portfolio.utils.ItemClickListener
import com.app.ancoturf.presentation.home.portfolio.utils.ItemTouchHelperAdapter
import com.app.ancoturf.presentation.home.portfolio.utils.ItemTouchHelperViewHolder
import com.app.ancoturf.presentation.home.portfolio.utils.OnStartDragListener
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.fragment_add_portfolio.*
import java.util.*

class ImagesPortfolioAdapter(private val activity: AppCompatActivity?,private val onItemClick : ItemClickListener,private val portfolioImages: ArrayList<PortfolioDetailResponse.PortfolioImage>, private val onItemSwap : ItemTouchHelperAdapter) :
    RecyclerView.Adapter<ImagesPortfolioAdapter.ViewHolder>(), ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity
            ).inflate(R.layout.item_portfolio_scroll_image, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return portfolioImages.size
    }


    override fun onBindViewHolder(holder: ViewHolder, i: Int) {


        if (!(Utility.isValueNull(portfolioImages[i].imageUrl)))
            Glide.with(activity!!).load(portfolioImages[i].imageUrl).into(
                holder.imgPortfolio
            )
        else if (!(Utility.isValueNull(portfolioImages[i].uri)))
            Glide.with(activity!!).load(portfolioImages[i].uri).into(holder.imgPortfolio)

        holder.imgDelete.setOnClickListener {
            onItemClick.onItemClick(portfolioImages[holder.adapterPosition],it,holder.adapterPosition)
        }
        holder.imgPortfolio.setOnClickListener {
            onItemClick.onItemClick("",it,holder.adapterPosition)
        }

        val gd =
            GestureDetector(activity!!, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent?): Boolean {
                    Log.d("onDoubleTap",holder.adapterPosition.toString())
                    onItemClick.onItemClick("DoubleTapped",holder.imgPortfolio,holder.adapterPosition)
                    return true
                }
            })

        holder.imgPortfolio.setOnTouchListener { v, event -> gd.onTouchEvent(event) }


       /* holder.imgPortfolio.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                gd.onTouchEvent(event)
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    onStartDragListener.onStartDrag(holder);
                }
                return false;
            }
        })*/

    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        ItemTouchHelperViewHolder {
        val imgPortfolio = itemView
            .findViewById(R.id.imgPortfolio) as ImageView
        val imgDelete = itemView
            .findViewById(R.id.imgDelete) as ImageView

        override fun onItemSelected() {
//            itemView.setBackgroundColor(Color.LTGRAY);
        }

        override fun onItemClear() {
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
//        Collections.swap(portfolioImages,fromPosition,toPosition)
//        notifyItemMoved(fromPosition,toPosition)
        onItemSwap.onItemMove(fromPosition,toPosition)
        Log.d("onItemMove","from: "+fromPosition+" to : "+toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
    }

}