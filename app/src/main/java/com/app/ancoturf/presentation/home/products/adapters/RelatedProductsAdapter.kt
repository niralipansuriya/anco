package com.app.ancoturf.presentation.home.products.adapters

import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import com.app.ancoturf.data.product.remote.entity.response.RelatedProductsResponse
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.home.interfaces.OnProductAddedToCart
import com.app.ancoturf.presentation.home.products.ProductDetailFragment
import com.app.ancoturf.presentation.home.turfcalculator.TurfCalculatorFragment
import com.app.ancoturf.utils.InputFilterMinMax
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import java.util.ArrayList
import kotlin.math.roundToInt

class RelatedProductsAdapter(
    private val activity: AppCompatActivity,
    var arrayList: ArrayList<RelatedProductsResponse.Data>,
    val count: Int, var onProductAddedToCart: OnProductAddedToCart
) :
    RecyclerView.Adapter<RelatedProductsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity
            ).inflate(R.layout.item_product, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return if (arrayList.size > 2) count else arrayList.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = arrayList[position]
        holder.txtProductTitle.text = product.productName

        holder.txtAddToCart.setBackgroundResource(R.drawable.bg_green_rounded_corner)

        var horizontalSpacing = Utility.dpToPx(activity!!.resources.getDimension(R.dimen._10sdp))
//        int screenWidthHeight = Utility.isLandScapeMode(activity) ? (Utility.getScreenWidth() - (verticalSpacing + Utility.dpToPx(135))) : Utility.getScreenHeight();
        var screenWidthHeight = activity?.let { Utility.getScreenWidth(it) }

        var tabWidth = (screenWidthHeight) / 2 - horizontalSpacing

        holder.mainLayout.layoutParams.width = (screenWidthHeight) / 2
        holder.imgProduct.layoutParams.width = (tabWidth - activity.resources.getDimension(R.dimen._2sdp)).roundToInt()
        holder.linImageProduct.layoutParams.width = (tabWidth - activity.resources.getDimension(R.dimen._2sdp)).roundToInt()
        holder.txtAddToCart.layoutParams.width = (tabWidth - activity.resources.getDimension(R.dimen._9sdp)).roundToInt()


        Glide.with(activity).load(product.featureImageUrl).into(holder.imgProduct)

//        Glide.with(activity).load(product.featureImageUrl).into(object : SimpleTarget<Drawable>() {
//            override fun onResourceReady(
//                resource: Drawable,
//                transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
//            ) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    holder.imgProduct.setBackground(resource)
//                }
//            }
//        })

        val price = Utility.roundTwoDecimals(product.price)
        var unit = product.productUnit
        if (unit.equals("Square meter"))
            unit = "m\u00B2"
        else if (unit.equals("Quantity"))
            unit = "Qty"

        var qty = 0
        if (product.qty == 0) {
            qty = product.minimumQuantity
            product.qty = product.minimumQuantity
        } else {
            qty = product.qty
        }
        holder.edtQuantity.setText("$qty")

        if (unit.equals("Qty"))
            holder.txtPrize.text = "$${Utility.formatNumber(price.toFloat())}"
        else
            holder.txtPrize.text = "$${Utility.formatNumber(price.toFloat())} per $unit"

        holder.txtUnit.text = unit
        if (Utility.isValueNull(holder.edtQuantity.text.toString()))
            holder.txtFinalPrize.text = activity.getString(R.string.product_total_price, Utility.formatNumber(price.toFloat()))
        else
            holder.txtFinalPrize.text = activity.getString(
                R.string.product_total_price,
                Utility.formatNumber(Utility.roundTwoDecimals((price.toFloat() * (holder.edtQuantity.text.toString().toFloat()))).toFloat())
            )

        holder.edtQuantity.setFilters(
            arrayOf<InputFilter>(
                InputFilterMinMax(
                    1, Constants.MAX_NUMBER
                )
            )
        )

        holder.edtQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (Utility.isValueNull(holder.edtQuantity.text.toString()))
                    holder.txtFinalPrize.text = activity.getString(R.string.product_total_price, price.toString())
                else {
                    holder.txtFinalPrize.text = activity.getString(
                        R.string.product_total_price,
                        Utility.formatNumber(Utility.roundTwoDecimals((price.toFloat() * (holder.edtQuantity.text.toString().toFloat()))).toFloat())
                    )
                    arrayList[position].qty = holder.edtQuantity.text.toString().toInt()
                }
            }
        })

//        holder.edtQuantity.setOnEditorActionListener(object : TextView.OnEditorActionListener {
//            override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    if (Utility.isValueNull(holder.edtQuantity.text))
//                        holder.txtFinalPrize.text = activity.getString(R.string.product_total_price, price.toString())
//                    else {
//                        holder.txtFinalPrize.text = activity.getString(
//                            R.string.product_total_price,
//                            Utility.roundTwoDecimals((price * (holder.edtQuantity.text.toString().toFloat())))
//                        )
//                        arrayList[position].qty = holder.edtQuantity.text.toString().toInt()
//                    }
//                }
//                return false
//            }
//        })

        holder.mainLayout.setOnClickListener {
            var id: Int = 0
            if (arrayList.get(position).id != 0)
                id = arrayList.get(position).id
            else
                id = arrayList.get(position).productId
            activity.hideKeyboard()
            activity.pushFragment(
                ProductDetailFragment(id),
                true,
                false,
                false,
                R.id.flContainerHome
            )
        }

        holder.txtAddToCart.setOnClickListener {
            activity.hideKeyboard()
            if (arrayList[position].inStock != 0) {
                if (!Utility.isValueNull(holder.edtQuantity.text.toString()) && arrayList[position].qty > 0) {
                    if (arrayList[position].qty >= arrayList[position].minimumQuantity) {
                        var productDetailResponse = ProductDetailResponse(
                            featureImageUrl = arrayList[position].featureImageUrl,
                            price = arrayList[position].price,
                            id = arrayList[position].id,
                            inStock = arrayList[position].inStock,
                            minimumQuantity = arrayList[position].minimumQuantity,
                            productCategoryId = arrayList[position].productCategoryId,
                            productName = arrayList[position].productName,
                            productUnit = arrayList[position].productUnit,
                            productUnitId = arrayList[position].productUnitId,
                            qty = arrayList[position].qty,
                            productId = arrayList[position].productId
                        )
                        onProductAddedToCart.OnProductAddedToCart(productDetailResponse)
                    } else {
                        activity.shortToast(
                            activity.getString(
                                R.string.invalid_product_quantity_message_for_add_to_cart,
                                arrayList[position].minimumQuantity.toString()
                            )
                        )
                    }
                } else {
                    activity.shortToast(activity.getString(R.string.blank_product_quantity_message))
                }
            }  else {
                activity.shortToast(activity.getString(R.string.out_of_stock_message))
            }
        }

        holder.txtCalculationLabel.setOnClickListener {
            activity.pushFragment(
                TurfCalculatorFragment(),
                true,
                true,
                false,
                R.id.flContainerHome
            )
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct = itemView
            .findViewById(R.id.imgProduct) as ImageView
        val txtProductTitle = itemView
            .findViewById(R.id.txtProductTitle) as TextView
        val txtPrize = itemView
            .findViewById(R.id.txtPrize) as TextView
        val txtUnit = itemView
            .findViewById(R.id.txtUnit) as TextView
        val edtQuantity = itemView
            .findViewById(R.id.edtQuantity) as EditText
        val txtFinalPrize = itemView
            .findViewById(R.id.txtFinalPrize) as TextView
        val txtCalculationLabel = itemView
            .findViewById(R.id.txtCalculationLabel) as TextView
        val txtAddToCart = itemView
            .findViewById(R.id.txtAddToCart) as TextView
        val mainLayout = itemView
            .findViewById(R.id.mainLayout) as ConstraintLayout
        val linImageProduct = itemView
            .findViewById(R.id.linImageProduct) as LinearLayout
    }

}