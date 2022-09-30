package com.app.ancoturf.presentation.payment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.payment.remote.entity.BillHistoryResponse
import com.app.ancoturf.utils.Logger
import com.app.ancoturf.utils.Utility

class BillHistoryAdapter(
    private val activity: AppCompatActivity,
    var billHistory: ArrayList<BillHistoryResponse.Data>,
    var txtBillOnClick: TxtBillOnClick
) :
    RecyclerView.Adapter<BillHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity as Context?
            ).inflate(R.layout.item_bill_history, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return billHistory.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var billHistory = billHistory[position]

        if (!Utility.isValueNull(billHistory.invoiceDate)) {
            holder.txtBillIssued.text = Utility.changeDateFormat(
                billHistory.invoiceDate,
                Utility.DATE_FORMAT_YYYY_MM_DD,
                Utility.DATE_FORMAT_D_MM_YYYY
            )
        } else
            holder.txtBillIssued.text = "-"
        if (!Utility.isValueNull(billHistory.invoiceNumber))
            holder.txtBillNumber.text = billHistory.invoiceNumber
        else
            holder.txtBillNumber.text = "-"

        if (billHistory?.amountDue == "0.00") {
            holder.txtBillNumber.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.theme_green
                )
            )
        } else if (billHistory?.amountDue != "0.00" && billHistory?.amountPaid != "0.00") {
            holder.txtBillNumber.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.orange
                )
            )
        } else if (billHistory?.amountDue != "0.00" && billHistory?.amountPaid == "0.00") {
            holder.txtBillNumber.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.red
                )
            )
        }

        /*if (billHistory.pdfUrl != null && billHistory.pdfUrl != "") {
            holder.txtBillNumber.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.blue
                )
            )
        } else
            holder.txtBillNumber.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    android.R.color.black
                )
            )*/

        if (!Utility.isValueNull(billHistory.totalCartPrice))
            holder.txtAmount.text = activity?.getString(
                R.string.pending_amount_price,
                Utility.formatNumber(Utility.roundTwoDecimals(billHistory.totalCartPrice.toDouble()).toFloat())
            )
//            holder.txtAmount.text ="${Utility.formatNumber(Utility.roundTwoDecimals(billHistory.totalCartPrice.toDouble()).toFloat())}"

        else
            holder.txtAmount.text = "-"

        if (billHistory?.amountDue == "0.00") {
            holder.txtDueDate.text = "PAID"
        } else if (!Utility.isValueNull(billHistory.dueDate))
            holder.txtDueDate.text = Utility.changeDateFormat(
                billHistory.dueDate,
                Utility.DATE_FORMAT_YYYY_MM_DD,
                Utility.DATE_FORMAT_D_MM_YYYY
            )
        else
            holder.txtDueDate.text = "-"

        Logger.log("Due Date : ${holder.txtDueDate.text}")

        /*if (!Utility.isValueNull(billHistory.dueDate))
            holder.txtDueDate.text = billHistory.dueDate
        else
            holder.txtDueDate.text = "-"*/
        holder.txtBillNumber.setOnClickListener {
            billHistory.pdfUrl.let {
                if (billHistory.pdfUrl != "") {
                    txtBillOnClick.onClick(billHistory.invoiceId)
                }
            }
        }
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

    fun addItems(postItems: List<BillHistoryResponse.Data>) {
        billHistory.addAll(postItems)
        notifyDataSetChanged()
    }

    interface TxtBillOnClick {
        fun onClick(pdfUrl: String)
    }

}