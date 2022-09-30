package com.app.ancoturf.presentation.manageLawn

import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.manageLawn.remote.ManageLawnDetailResponse
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.presentation.home.products.ProductDetailFragment
import com.bumptech.glide.Glide
import java.util.*


class ManageLawnDropDownAdapter (
    private val activity: AppCompatActivity,
    private val arrayList: ArrayList<ManageLawnDetailResponse.Data>
) :
    RecyclerView.Adapter<ManageLawnDropDownAdapter.ViewHolder>() {

//    var activity : AppCompatActivity? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(activity)
                .inflate(R.layout.item_drop_down_recycler_webview, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTitle.text = Html.fromHtml(arrayList[position].title)
//        holder.mainLayout.setBackgroundResource(arrayList[position].bgImage)

        activity?.let {
            Glide.with(it).load(arrayList[position].featureImageUrl).into(holder.imgManageLawnFutureImage)
        }
        var tmpstr =arrayList[position].myLawnDetails
        holder.webView.webViewClient = MyBrowser(activity)
        holder.webView.webChromeClient = WebChromeClient()
        holder.webView.loadData(tmpstr, "text/html", "UTF-8")
        /*holder.mainLayout.setOnClickListener {
            val manageLawnDetailFragment =
                ManageLawnDetailFragment(arrayList[position].cat_id)

            activity.pushFragment(
                manageLawnDetailFragment,
                true,
                true,
                false,
                R.id.flContainerHome
            )
        }*/
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitle = itemView
            .findViewById(R.id.txtTitle) as TextView
        val webView = itemView
            .findViewById(R.id.webView) as WebView
        val imgManageLawnFutureImage = itemView
            .findViewById(R.id.imgManageLawnFutureImage) as ImageView

    }
//https://www.ancoturf.com.au/shop/?id=12
    private class MyBrowser(val activity: AppCompatActivity) : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String
        ): Boolean {
            view.loadUrl(url)
            var strId = url.substringAfterLast("=")
            activity.pushFragment(
                ProductDetailFragment(strId.toInt()),
                true,
                true,
                false,
                R.id.flContainerHome
            )
            view.addJavascriptInterface(object : Any() {

                @JavascriptInterface
                @Throws(Exception::class)
                fun performClick() {
                    Log.d("LOGIN::", "Clicked")
//                    Toast.makeText(activity, "Login clicked", Toast.LENGTH_LONG).show()
                }
            }, "ok")
            return true
        }
    }


    /*   fun addItems(postItems: ArrayList<ManageLawnDataResponse.Data>) {
        arrayList.addAll(postItems)
        notifyDataSetChanged()
    }*/
}
