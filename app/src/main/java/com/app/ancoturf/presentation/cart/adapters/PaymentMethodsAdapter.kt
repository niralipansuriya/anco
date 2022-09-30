package com.app.ancoturf.presentation.cart.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.cart.remote.entity.PaymentMethodsResponse
import com.app.ancoturf.presentation.cart.interfaces.OnPaymentMethodSelectedListener
import java.util.*


class PaymentMethodsAdapter(
    private val activity: AppCompatActivity,
    var arrayList: ArrayList<PaymentMethodsResponse.PaymentMethod>,
    var onPaymentMethodSelectedListener: OnPaymentMethodSelectedListener
) :
    RecyclerView.Adapter<PaymentMethodsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity
            ).inflate(R.layout.item_payment_methods, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.txtTitle.text = arrayList.get(position).displayName

        holder.imgCheck.setImageResource(if (arrayList[position].checked) R.drawable.ic_radio_on else R.drawable.ic_radio_off)

        holder.mainLayout.setOnClickListener {
            for (i in 0 until arrayList.size)
                arrayList[i].checked = false
            arrayList[position].checked = !arrayList[position].checked
            notifyDataSetChanged()
            onPaymentMethodSelectedListener.onPaymentMethodSelected(arrayList[position])
        }

        holder.view.visibility = if (position == arrayList.size - 1) View.GONE else View.VISIBLE
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitle = itemView
            .findViewById(R.id.txtTitle) as TextView

        val imgCheck = itemView
            .findViewById(R.id.imgCheck) as ImageView

        val view = itemView
            .findViewById(R.id.view) as View

        val mainLayout = itemView
            .findViewById(R.id.mainLayout) as ConstraintLayout
    }

    fun setPaymentMethods(arrayList: ArrayList<PaymentMethodsResponse.PaymentMethod>) {
        this.arrayList = arrayList
    }
    fun getSelectedItem() : PaymentMethodsResponse.PaymentMethod? {
        for (i in 0 until arrayList.size) {
            if(arrayList[i].checked)
                return arrayList[i]
        }
        return null
    }

}