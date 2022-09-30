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
import com.app.ancoturf.utils.Utility
import kotlinx.android.synthetic.main.fragment_weather.*
import java.lang.Exception
import java.text.DecimalFormat
import java.text.NumberFormat

class WeatherRowAdapter(
    private val activity: AppCompatActivity,
    var nextSevenDayAdap: List<WeatherResponse.NextSevenDay>
): RecyclerView.Adapter<WeatherRowAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDays = itemView
            .findViewById(R.id.tvDays) as AppCompatTextView
        val ivWeatherRow = itemView
            .findViewById(R.id.ivWeatherRow) as ImageView
        val lineWeather = itemView
            .findViewById(R.id.lineWeather) as View

        val tvWeatherType = itemView
            .findViewById(R.id.tvWeatherType) as AppCompatTextView
        val tvNightTempreture = itemView
            .findViewById(R.id.tvNightTempreture) as AppCompatTextView
        val tvDayTempreture = itemView
            .findViewById(R.id.tvDayTempreture) as AppCompatTextView


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return WeatherRowAdapter.ViewHolder(
            LayoutInflater.from(activity as Context?)
                .inflate(R.layout.item_forecast_weather_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dateHH = Utility.changeDateFormat(
            nextSevenDayAdap[position].date,
            Utility.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS,
            Utility.DATE_FORMAT_eeee
        )
        /*if(nextSevenDayAdap[position].precis!!.substring(nextSevenDayAdap[position].precis!!.length-1).equals(".")){
                var strtmp =nextSevenDayAdap[position].precis!!.subSequence(0,nextSevenDayAdap[position].precis!!.length-1)
            holder.tvWeatherType.text = strtmp
        }else{
            holder.tvWeatherType.text = nextSevenDayAdap[position].precis
        }*/





        if (position == nextSevenDayAdap.size -1 ){
            holder.lineWeather.visibility = View.GONE
        }else{
            holder.lineWeather.visibility = View.VISIBLE
        }
        var precisCode =  nextSevenDayAdap[position].forecastIconCode

        var forcastType = Utility.getDisplaynamefromforecasticon(precisCode!!.toString())


        holder.tvWeatherType.text = forcastType

        try{
            val imagename = Utility.getImageFromForecastIcon(precisCode!!.toString())
            val res =
                activity!!.resources.getIdentifier(imagename, "drawable", activity!!.packageName)
            holder.ivWeatherRow.setImageResource(res)
        }catch (e : Exception){
            e.printStackTrace()
        }
        holder.tvDays.text = dateHH
        holder.tvDayTempreture.text = nextSevenDayAdap[position].airTemperatureMaximum + "°"
        holder.tvNightTempreture.text = nextSevenDayAdap[position].airTemperatureMinimum + "°"
//        holder.tvWeatherType.text = nextSevenDayAdap[position].precis
    }

    override fun getItemCount(): Int {
        return nextSevenDayAdap.size
    }

}