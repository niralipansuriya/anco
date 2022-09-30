package com.app.ancoturf.presentation.home.quote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.quote.remote.entity.response.CustomersDataResponse
import com.app.ancoturf.utils.Utility
import kotlin.collections.ArrayList


class CustomersAdapter(
    private val activity: AppCompatActivity,
    var customers: ArrayList<CustomersDataResponse.Data>
) :
    RecyclerView.Adapter<CustomersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity
            ).inflate(R.layout.item_customer, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return customers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var customer = customers[position]

        if (!Utility.isValueNull(customer.customerName)) {
            holder.txtCustomerName.text = customer.customerName
            holder.txtCustomerName.visibility = View.VISIBLE
        } else {
            holder.txtCustomerName.visibility = View.GONE
        }

        if (!Utility.isValueNull(customer.customerAddress)) {
            holder.txtCustomerAddress.text = customer.customerAddress
            holder.txtCustomerAddress.visibility = View.VISIBLE
        } else {
            holder.txtCustomerAddress.visibility = View.GONE
        }

        if (!Utility.isValueNull(customer.customerEmail)) {
            holder.txtCustomerEmail.text = customer.customerEmail
            holder.txtCustomerEmail.visibility = View.VISIBLE
        } else {
            holder.txtCustomerEmail.visibility = View.GONE
        }

        holder.imgSelection.visibility = if (customer.selected) View.VISIBLE else View.GONE

        holder.mainLayout.setOnClickListener {
            if (customers[position].selected) {
                customers[position].selected = false
            } else {
                for (i in 0 until customers.size)
                    customers[i].selected = false

                customers[position].selected = true
            }
            notifyDataSetChanged()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtCustomerName = itemView
            .findViewById(R.id.txtCustomerName) as TextView
        val txtCustomerEmail = itemView
            .findViewById(R.id.txtCustomerEmail) as TextView
        val txtCustomerAddress = itemView
            .findViewById(R.id.txtCustomerAddress) as TextView
        val imgSelection = itemView
            .findViewById(R.id.imgSelection) as ImageView
        val mainLayout = itemView
            .findViewById(R.id.mainLayout) as ConstraintLayout
    }

    fun getSelectedData() : CustomersDataResponse.Data? {
        for (i in 0 until customers.size) {
            if(customers[i].selected)
                return customers[i]
        }
        return null
    }

}