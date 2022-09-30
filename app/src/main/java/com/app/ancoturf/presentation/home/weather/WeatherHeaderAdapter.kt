package com.app.ancoturf.presentation.home.weather

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.utils.Logger
import com.app.ancoturf.utils.Utility
import kotlinx.android.synthetic.main.fragment_weather.*
import java.lang.Exception
import java.text.DecimalFormat
import java.text.NumberFormat

class WeatherHeaderAdapter(
    private val activity: AppCompatActivity,
    var nextSevenDay: List<WeatherResponse.Timewise>
) : RecyclerView.Adapter<WeatherHeaderAdapter.ViewHolder>() {
    var countAM :Int = 0


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTimeWeather = itemView
            .findViewById(R.id.tvTimeWeather) as AppCompatTextView
        val ivTopHeaderWeather = itemView
            .findViewById(R.id.ivTopHeaderWeather) as ImageView

        val tvTemperatureWeather = itemView
            .findViewById(R.id.tvTemperatureWeather) as AppCompatTextView


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(activity as Context?)
                .inflate(R.layout.item_weather_header, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(nextSevenDay[position].temperature != null)
        holder.tvTemperatureWeather.text = nextSevenDay[position].temperature +"°"
        var precisCode = nextSevenDay[position].precisCode
        var isNight = if(nextSevenDay[position].night == "1") true else false
        try{
            val imagename = Utility.getImageName(precisCode!!.toString(),isNight)
            val res =
                activity.resources.getIdentifier(imagename, "drawable", activity!!.packageName)
            holder.ivTopHeaderWeather.setImageResource(res)
        }catch (e : Exception){
            e.printStackTrace()
        }
        var dateHH = Utility.changeDateFormat(
            nextSevenDay[position].time,
            Utility.DATE_FORMAT_HH_MM_SSS,
            Utility.DATE_FORMAT_h_small_aa
        )
        holder.tvTimeWeather.text = dateHH
    }

    override fun getItemCount(): Int {
        return nextSevenDay.size
    }
}