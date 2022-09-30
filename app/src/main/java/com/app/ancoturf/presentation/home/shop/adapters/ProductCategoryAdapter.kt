package com.app.ancoturf.presentation.home.shop.adapters

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.product.remote.entity.response.ProductCategoryData
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.presentation.chooseingMyLawn.ChooseMyLawnIntroFragment
import com.app.ancoturf.presentation.home.portfolio.PortfolioProductsFragment
import com.app.ancoturf.presentation.home.products.ProductsFragment
import com.app.ancoturf.presentation.home.quote.interfaces.OnProductCategorySelected
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import java.util.*
import javax.inject.Inject


class ProductCategoryAdapter(
    private val activity: AppCompatActivity,
    var arrayList: ArrayList<ProductCategoryData>,
    var dialog: Dialog?,
    var onProductCategorySelected: OnProductCategorySelected?
) :
    RecyclerView.Adapter<ProductCategoryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                activity
            ).inflate(R.layout.item_quick_link, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var horizontalSpacing = Utility.dpToPx(activity!!.resources.getDimension(R.dimen._4sdp))
        var horizontalSpacingDialog =
            Utility.dpToPx(activity!!.resources.getDimension(R.dimen._8sdp))
//        int screenWidthHeight = Utility.isLandScapeMode(activity) ? (Utility.getScreenWidth() - (verticalSpacing + Utility.dpToPx(135))) : Utility.getScreenHeight();
        var screenWidthHeight = activity?.let { Utility.getScreenWidth(it) }

        var tabWidth = (screenWidthHeight) / 2 - horizontalSpacing
        if (dialog != null)
            tabWidth = ((screenWidthHeight) / 2) - horizontalSpacingDialog

        holder.mainLayout.layoutParams.width = tabWidth
        holder.mainLayout.layoutParams.height = tabWidth
        holder.view.layoutParams.width = tabWidth
        holder.view.layoutParams.height = tabWidth

        holder.txtLinkTitle.text = arrayList[position].displayName

        Glide.with(activity).load(arrayList[position].imageUrl).into(holder.imgCategory)

//        Glide.with(activity).load(arrayList[position].imageUrl).into(object : SimpleTarget<Drawable>() {
//            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    holder.mainLayout.setBackground(resource)
//                }
//            }
//        })

        holder.mainLayout.setOnClickListener {
            if (dialog != null) {
                dialog!!.dismiss()
                val portfolioProductsFragment =
                    PortfolioProductsFragment(
                        arrayList.get(position).id.toString(),
                        arrayList.get(position).displayName
                    )
                activity.hideKeyboard()
                activity.pushFragment(
                    portfolioProductsFragment,
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            } else if (onProductCategorySelected != null) {
                onProductCategorySelected?.onProductCategorySelected(arrayList.get(position))
            } else {
                var sharedPrefs = SharedPrefs(activity)
                if (arrayList[position].displayName == "Turf" && !sharedPrefs.isHelpChooseTurf) {
                    val bundle = Bundle()
                    bundle.putString("categoryIds", arrayList[position].id.toString())
                    bundle.putString("title", "")
                    bundle.putBoolean("fromRelated", false)
                    val chooseMyLawnIntroFragment =
                        ChooseMyLawnIntroFragment().apply { arguments = bundle }
                    activity.hideKeyboard()
                    activity.pushFragment(
                        chooseMyLawnIntroFragment,
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                } else {
                    val bundle = Bundle()
                    bundle.putString("categoryIds", arrayList[position].id.toString())
                    bundle.putString("title", "")
                    bundle.putBoolean("fromRelated", false)
                    val productsFragment =
                        ProductsFragment().apply { arguments = bundle }
                    activity.hideKeyboard()
                    activity.pushFragment(
                        productsFragment,
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                }
               /* val bundle = Bundle()
                bundle.putString("categoryIds", arrayList.get(position).id.toString())
                bundle.putString("title", "")
                bundle.putBoolean("fromRelated", false)
                val productsFragment =
                    ProductsFragment().apply { arguments = bundle }
                activity.hideKeyboard()
                activity.pushFragment(
                    productsFragment,
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )*/
            }
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtLinkTitle = itemView
            .findViewById(R.id.txtLinkTitle) as TextView
        val mainLayout = itemView
            .findViewById(R.id.mainLayout) as androidx.constraintlayout.widget.ConstraintLayout
        val view = itemView
            .findViewById(R.id.view) as View
        val imgCategory = itemView
            .findViewById(R.id.imgCategory) as ImageView

    }


}