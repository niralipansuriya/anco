package com.app.ancoturf.presentation.payment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.payment.remote.entity.PendingPaymentResponse
import com.app.ancoturf.data.search.remote.entity.response.SearchProductResponse
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.presentation.home.products.ProductDetailFragment
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide

class PendingPaymentAdapter (
    private val activity: AppCompatActivity,
    var pendingPayments: ArrayList<PendingPaymentResponse.Invoices>
) :
    RecyclerView.Adapter<PendingPaymentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity as Context?
            ).inflate(R.layout.item_bill_history, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return pendingPayments.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        var billHistory = pendingPayments[position]

        if (!Utility.isValueNull( billHistory.invoiceDate))
            holder.txtBillIssued.text =  Utility.changeDateFormat(billHistory.invoiceDate, Utility.DATE_FORMAT_YYYY_MM_DD, Utility.DATE_FORMAT_D_MM_YY)
        else
            holder.txtBillIssued.text = "-"
        if (!Utility.isValueNull( billHistory.invoiceNumber))
            holder.txtBillNumber.text = billHistory.invoiceNumber
        else
            holder.txtBillNumber.text = "-"
        if (!Utility.isValueNull( billHistory.amountDue))
            holder.txtAmount.text = Utility.formatNumber(Utility.roundTwoDecimals(billHistory.amountDue.toDouble()).toFloat())
        else
            holder.txtAmount.text = "-"
        if (!Utility.isValueNull( billHistory.dueDate))
            holder.txtDueDate.text = billHistory.dueDate
        else
            holder.txtDueDate.text = "-"
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtBillIssued = itemView
            .findViewById(R.id.txtBillIssued) as AppCompatTextView
        val txtBillNumber = itemView
            .findViewById(R.id.txtBillNumber) as AppCompatTextView
        val txtAmount = itemView
            .findViewById(R.id.txtAmount) as AppCompatTextView
        val txtDueDate = itemView
            .findViewById(R.id.txtDueDate) as AppCompatTextView
    }

}