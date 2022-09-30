package com.app.ancoturf.presentation.home.products.adapters

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.app.ancoturf.data.product.remote.entity.response.ProductsResponse
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.home.interfaces.OnProductAddedToCart
import com.app.ancoturf.presentation.home.products.ProductDetailFragment
import com.app.ancoturf.presentation.home.turfcalculator.TurfCalculatorFragment
import com.app.ancoturf.utils.InputFilterMinMax
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import kotlinx.android.synthetic.main.fragment_product_detail.*
import java.util.*
import kotlin.math.roundToInt


class ProductsAdapter(private val activity: AppCompatActivity, var arrayList: ArrayList<ProductsResponse.Data> , var onProductAddedToCart: OnProductAddedToCart) :
    RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {
    private var mFirebaseAnalytics: FirebaseAnalytics? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity)

        return ViewHolder(
            LayoutInflater.from(
                activity
            ).inflate(R.layout.item_product, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = arrayList[position]
        holder.txtProductTitle.text = product.productName

        var horizontalSpacing = Utility.dpToPx(activity!!.resources.getDimension(R.dimen._8sdp))
//        int screenWidthHeight = Utility.isLandScapeMode(activity) ? (Utility.getScreenWidth() - (verticalSpacing + Utility.dpToPx(135))) : Utility.getScreenHeight();
        var screenWidthWidth = activity?.let { Utility.getScreenWidth(it) }

        var tabWidth = (screenWidthWidth) / 2 - horizontalSpacing

//        holder.mainLayout.layoutParams.width = (tabWidth - activity.resources.getDimension(R.dimen._2sdp)).roundToInt()
//        holder.imgProduct.layoutParams.width = (tabWidth - activity.resources.getDimension(R.dimen._2sdp)).roundToInt()
//        holder.linImageProduct.layoutParams.width = (tabWidth - activity.resources.getDimension(R.dimen._2sdp)).roundToInt()
//        holder.txtAddToCart.layoutParams.width = (tabWidth - activity.resources.getDimension(R.dimen._6sdp)).roundToInt()

        holder.txtAddToCart.setBackgroundResource(R.drawable.bg_green_rounded_corner)

        Glide.with(activity).load(product.featureImageUrl).into(holder.imgProduct)

//            Glide.with(activity).load(product.featureImageUrl).into(object : SimpleTarget<Drawable>() {
//                override fun onResourceReady(resource: Drawable, transition: com.bumptech.glide.request.transition.Transition<in Drawable>?) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                        holder.imgProduct.setBackground(resource)
//                    }
//                }
//            })

        val price = Utility.roundTwoDecimals(product.price)
        var unit = product.productUnit
        holder.txtCalculationLabel.visibility = View.INVISIBLE
        if (unit.equals("Square meter")) {
            unit = "m\u00B2"
            holder.txtCalculationLabel.visibility = View.VISIBLE
        }
        else if (unit.equals("Quantity"))
            unit = "Qty"

        if (unit.equals("Qty"))
            holder.txtPrize.text = "$${Utility.formatNumber(price.toFloat())}"
        else
            holder.txtPrize.text = "$${Utility.formatNumber(price.toFloat())} per $unit"

        holder.txtUnit.text = unit

        var qty = 0
        if (product.qty == 0) {
            qty = product.minimumQuantity
            product.qty = product.minimumQuantity
        } else {
            qty = product.qty
        }
        holder.edtQuantity.setText("$qty")
        holder.edtQuantity.setFilters(
            arrayOf<InputFilter>(
                InputFilterMinMax(
                    1 , Constants.MAX_NUMBER
                )
            )
        )

        if (Utility.isValueNull(holder.edtQuantity.text.toString()))
            holder.txtFinalPrize.text = activity.getString(R.string.product_total_price, price.toString())
        else
            holder.txtFinalPrize.text = activity.getString(
                R.string.product_total_price,
                Utility.roundTwoDecimals((price.toFloat() * (holder.edtQuantity.text.toString().toFloat())))
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
                        Utility.roundTwoDecimals((price.toFloat() * (holder.edtQuantity.text.toString().toFloat())))
                    )
                    arrayList[position].qty = holder.edtQuantity.text.toString().toInt()
                }
            }
        })

//        holder.edtQuantity.setOnEditorActionListener(object : OnEditorActionListener {
//            override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    if (Utility.isValueNull(holder.edtQuantity.text.toString()))
//                        holder.txtFinalPrize.text = activity.getString(R.string.product_total_price, price.toString())
//                    else {
//                        holder.txtFinalPrize.text = activity.getString(
//                            R.string.product_total_price,
//                            Utility.roundTwoDecimals((price.toFloat() * (holder.edtQuantity.text.toString().toFloat())))
//                        )
//                        arrayList[position].qty = holder.edtQuantity.text.toString().toInt()
//                    }
//                }
//                return false
//            }
//        })

        holder.txtAddToCart.setOnClickListener {
            activity.hideKeyboard()
            if(arrayList[position].inStock != 0) {
                if (!Utility.isValueNull(holder.edtQuantity.text.toString()) && arrayList[position].qty > 0) {
                    if (arrayList[position].qty >= arrayList[position].minimumQuantity) {
                        var productDetailResponse = ProductDetailResponse(
                            featureImageUrl = arrayList[position].featureImageUrl,
                            price = arrayList[position].price,
                            id = arrayList[position].productId,
                            inStock = arrayList[position].inStock,
                            minimumQuantity = arrayList[position].minimumQuantity,
                            productCategoryId = arrayList[position].productCategoryId,
                            productName = arrayList[position].productName,
                            productUnit = arrayList[position].productUnit,
                            productUnitId = arrayList[position].productUnitId,
                            qty = arrayList[position].qty
                        )
                        onProductAddedToCart.OnProductAddedToCart(productDetailResponse)
                        Log.e("quantity =========",holder.edtQuantity.text.toString())
                        val Quantity = holder.edtQuantity.text.toString()

                        val itemArrayAddtocart = Bundle().apply {
                            putString(FirebaseAnalytics.Param.ITEM_ID,arrayList[position]?.productId.toString())
                          //  putString(FirebaseAnalytics.Param.QUANTITY,productDetailResponse?.qty.toString())
                            if (Quantity!=null && !Quantity.contentEquals(""))
                            {
                                putLong(FirebaseAnalytics.Param.QUANTITY,Quantity.toLong())

                            }
                            putString(FirebaseAnalytics.Param.VALUE,arrayList[position]?.price.toString())
                            putString(FirebaseAnalytics.Param.ITEM_CATEGORY,arrayList[position]?.productCategoryId?.toString())
                            putString(FirebaseAnalytics.Param.ITEM_NAME,arrayList[position]?.productName)
                        }
                        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.ADD_TO_CART) {
                            param(FirebaseAnalytics.Param.CURRENCY, "USD")
                            param(FirebaseAnalytics.Param.ITEMS, arrayOf(itemArrayAddtocart))
                        }

/*nikita
                        val bundle = Bundle()
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, arrayList[position]?.productId.toString())
                        bundle.putString(FirebaseAnalytics.Param.VALUE, arrayList[position]?.price.toString())
                        bundle.putString(FirebaseAnalytics.Param.QUANTITY, arrayList[position]?.qty.toString())
                        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, arrayList[position]?.productCategoryId.toString())
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, arrayList[position]?.productName)
                        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle)
*/
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

        holder.mainLayout.setOnClickListener {
            activity.hideKeyboard()
            activity.pushFragment(
                ProductDetailFragment(arrayList.get(position).productId),
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

    fun addItems(postItems: ArrayList<ProductsResponse.Data>?) {
        if(postItems != null) {
            arrayList.addAll(postItems)
            notifyDataSetChanged()
        }
    }

}