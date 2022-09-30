package com.app.ancoturf.presentation.notification.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.notification.remote.entity.response.NotificationDataResponse
import com.app.ancoturf.utils.Utility

class NotificationAdapter(
    private val activity: AppCompatActivity,
    var notificationList: ArrayList<NotificationDataResponse.Data>
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitle = itemView
            .findViewById(R.id.tvTitle) as AppCompatTextView

        val tvContent = itemView
            .findViewById(R.id.tvContent) as AppCompatTextView

        val tvTime = itemView
            .findViewById(R.id.tvTime) as AppCompatTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity as Context?
            ).inflate(R.layout.item_notification, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var notification = notificationList[position]
        holder.txtTitle.text = notification.title
        holder.tvContent.text = notification.content
        if (!Utility.isValueNull(notification.created_at)) {
            holder.tvTime.text = Utility.changeDateFormat(
                notification.created_at,
                Utility.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS,
                Utility.DATE_FORMAT_dd_MMM_YYYY_HH_MM_AA
            )
        } else {
            holder.tvTime.text = activity.getString(R.string.quote_date, "")
        }
    }

    fun addItems(postItems: List<NotificationDataResponse.Data>) {
        notificationList.addAll(postItems)
        notifyDataSetChanged()
    }
}