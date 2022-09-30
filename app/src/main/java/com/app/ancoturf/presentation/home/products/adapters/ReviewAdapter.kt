package com.app.ancoturf.presentation.home.products.adapters

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import com.app.ancoturf.utils.Utility

class ReviewAdapter(private val context: Context?, private val reviews: List<ProductDetailResponse.ProductReview>) :
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_review,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var date = Utility.changeDateFormat(
            reviews.get(position).createdAt,
            Utility.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS,
            Utility.DATE_FORMAT_MMMM_D_YYYY
        )

        if(reviews.get(position).user != null) {
            var lastName = ""
            if (!Utility.isValueNull(reviews.get(position).user.lastName))
                lastName = reviews.get(position).user.lastName.substring(0, 1) + "."
            holder.txtReviewerName.text =
                Html.fromHtml(
                    context!!.getString(
                        R.string.review_item_title,
                        (reviews.get(position).user.firstName + " " + lastName),
                        date
                    )
                )
        } else {
            holder.txtReviewerName.text = ""
        }
        holder.txtReview.text = reviews.get(position).reviewText

        holder.ratingUserReview.rating = reviews.get(position).rating.toFloat()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtReviewerName = itemView
            .findViewById(R.id.txtReviewerName) as TextView
        val txtReview = itemView
            .findViewById(R.id.txtReview) as TextView
        val ratingUserReview = itemView
            .findViewById(R.id.ratingUserReview) as RatingBar
    }
}