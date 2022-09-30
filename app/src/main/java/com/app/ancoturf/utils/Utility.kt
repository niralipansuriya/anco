package com.app.ancoturf.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.ancoturf.R
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class Utility {
    companion object {

        val DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
        val DATE_FORMAT_MMMM_D_YYYY = "MMMM d, yyyy"
        val DATE_FORMAT_DD_MM_YY = "dd-MM-yy"
        val DATE_FORMAT_D_M_YYYY = "d-M-yyyy"
        val DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd"
        val DATE_FORMAT_D_MMMM_YYYY = "d MMMM yyyy"
        val DATE_FORMAT_D_MM_YYYY = "d MMM yyyy"
        val DATE_FORMAT_D_MM_YY = "d MMM yy"
        val DATE_FORMAT_dd_MMM_YYYY_HH_MM_AA = "dd MMM yyyy, hh:mm aa"
        val DATE_FORMAT_dd_MM_hh_mm_aa= "dd/MM, hh:mm a"
        val DATE_FORMAT_hh= "HH"
        val DATE_FORMAT_mm= "m"
//        val DATE_FORMAT_hh_small_a= "hh a"
        val DATE_FORMAT_h_small_aa= "h aa"
        val DATE_FORMAT_hh_mm_ss= "hh:mm:ss"
        val DATE_FORMAT_HH_MM_SSS= "HH:mm:ss"
        val DATE_FORMAT_eeee= "EEEE"


        private var dialog: ProgressDialog? = null

        fun isTablet(context: Context): Boolean {
            return context.getResources().getConfiguration().screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
        }

        fun showProgress(context: Context, message: String, cancellable: Boolean) {
            try {
                if (context == null)
                    return;

                if (dialog != null)
                    cancelProgress();
                dialog = ProgressDialog(context);
                dialog?.show();
                dialog?.setCancelable(false);
                dialog?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
                var inflater: LayoutInflater =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                var layout: View =
                    inflater.inflate(com.app.ancoturf.R.layout.defult_progress_dialoge, null);
                layout.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                dialog?.setContentView(layout);
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun cancelProgress() {
            Logger.log("cancelProgress")
            if (checkProgressOpen()) {
                try {
                    dialog?.dismiss()
                } catch (e: Exception) {
                }
                dialog = null
            }
        }

        fun checkProgressOpen(): Boolean {
            return if (dialog != null && dialog!!.isShowing())
                true
            else
                false
        }

        fun getDateFromMillis(millis: Long, dateFormat: String): String {
            val date = Date(millis)
            val sfd = SimpleDateFormat(dateFormat)
            return sfd.format(date)
        }

        fun getMillis(date: String): Long {
            //        9/19/2016 12:00:00 AM
            val sdf = SimpleDateFormat(DATE_FORMAT_YYYY_MM_DD_HH_MM_SS)
            var timeInMilliseconds: Long = 0
            try {
                val mDate = sdf.parse(date)
                timeInMilliseconds = mDate.time
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return timeInMilliseconds
        }

        fun formatNumber(number: Long): String {
            val formatter = DecimalFormat("#,###,###,###,###")
            var formattednum = formatter.format(number)
            return formattednum
        }

        fun formatNumber(number: Float): String {
            val formatter = DecimalFormat("#,###,###,###,###.##")
            var formattednum = formatter.format(number)
            var numString = formattednum.split(".")
            if (numString.size > 1 && numString[1].length < 2) {
                formattednum += "0"
            } else if (numString.size == 1) {
                formattednum += ".00"
            }
            return formattednum
        }

        private fun getNavigationBarHeight(context: Activity?, orientation: Int): Int {
            val resources = context?.resources

            val id = resources?.getIdentifier(
                if (orientation == Configuration.ORIENTATION_PORTRAIT) "navigation_bar_height" else "navigation_bar_height_landscape",
                "dimen",
                "android"
            )
            if (id != null) {
                return if (id > 0) {
                    resources.getDimensionPixelSize(id)
                } else 0
            } else
                return 0
        }

        fun getVersion(activity: Activity): String? {
            val pInfo: PackageInfo
            var version: String? = null
            try {
                pInfo = activity.packageManager.getPackageInfo(activity.packageName, 0)
//                version = "Version " + pInfo.versionName + "- " + pInfo.versionCode
                version = "Version " + pInfo.versionName + "- " + "2"
            } catch (e: PackageManager.NameNotFoundException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            return version
        }


        fun getScreenWidth(context: Context): Int {
            val metrics = context.getResources().getDisplayMetrics()
            return metrics.widthPixels
        }

        //convert dp to px
        fun dpToPx(dp: Float): Int {
            return (dp * Resources.getSystem().displayMetrics.density).toInt()
        }

        fun changeDateFormat(dateString: String?, oldFormat: String, newFormat: String): String {
            if (isValueNull(dateString))
                return ""
            val originalFormat = SimpleDateFormat(oldFormat)
            val targetFormat = SimpleDateFormat(newFormat)
            var date: Date? = null
            try {
                date = originalFormat.parse(dateString)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
//        Utility.log("End Date->" , dateString + " formated Date :->" + formattedDate);
            return targetFormat.format(date)
        }

        fun shareUrl(activity: AppCompatActivity, url: String) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, url)
            sendIntent.type = "text/plain"
            activity.startActivity(
                Intent.createChooser(
                    sendIntent,
                    activity.getString(R.string.chooser_title)
                )
            )
        }


        fun roundTwoDecimals(value: Float): String {
            return String.format("%.2f", value)
        }

        fun roundTwoDecimals(value: Double): String {
            return String.format("%.2f", value)
        }

        fun isValueNull(input: String?): Boolean {
            return !(input != null && !input.toString().trim().equals(
                "null",
                true
            ) && !input.toString().trim().equals(""))
        }

        fun getImageName(strName: String,isNight:Boolean) :String{
            var imgName = ""
            if(strName == "fine") {
                imgName = if(isNight)  "fine_night" else "fine_day"
            } else if(strName == "mostly-fine") {
                imgName = if(isNight) "mostly_fine_night" else "mostly_fine_day"
            }
            else if(strName == "cloudy") {
                imgName = if(isNight) "cloudy" else "cloudy"
            }
            else if(strName == "partly-cloudy") {
                imgName = if(isNight) "partly_cloudy_night" else "partly_cloudy_day"
            }
            else if(strName == "dust") {
                imgName = if(isNight) "dusty" else "dusty"
            }
            else if(strName == "fog") {
                imgName = if(isNight) "fog" else "fog"
            }
            else if(strName == "high-cloud") {
                imgName = if(isNight) "high_cloud_night" else "high_cloud_day"
            }
            else if(strName == "mostly-cloudy") {
                imgName = if(isNight) "mostly_cloudy_night" else "mostly_cloudy_day"
            }
            else if(strName == "overcast") {
                imgName = if(isNight) "overcast" else "overcast"
            }
            else if(strName == "shower-or-two") {
                    imgName = if(isNight) "shower_or_two_night" else "shower_or_two_day"
            }
            else if(strName == "chance-shower-fine") {
                imgName = if(isNight) "chance_shower_fine_night" else "chance_shower_fine_day"
            }
            else if(strName == "few-showers") {
                imgName = if(isNight) "few_shower" else "few_shower"
            }
            else if(strName == "chance-shower-cloud") {
                imgName = if(isNight) "chance_shower_cloud" else "chance_shower_cloud"
            }
            else if(strName == "drizzle") {
                imgName = if(isNight) "drizzle" else "drizzle"
            }
            else if(strName == "showers-rain") {
                imgName = if(isNight) "shower_rain" else "shower_rain"
            }
            else if(strName == "heavy-showers-rain") {
                imgName = if(isNight) "heavy_shower_rain" else "heavy_shower_rain"
            }
            else if(strName == "chance-thunderstorm-fine") {
                imgName = if(isNight) "chance_thunderstorm_fine_night" else "chance_thunderstorm_fine_day"
            }
            else if(strName == "thunderstorm") {
                imgName = if(isNight) "thunderstorm" else "thunderstorm"
            }
            else if(strName == "chance-thunderstorm-cloud") {
                imgName = if(isNight) "chance_thunderstorm_cloud" else "chance_thunderstorm_cloud"
            }
            else if(strName == "chance-thunderstorm-showers") {
                imgName = if(isNight) "chance_thunderstorm_shower" else "chance_thunderstorm_shower"
            }
            else if(strName == "chance-snow-fine") {
                imgName = if(isNight) "chance_snow_fine_night" else "chance_snow_fine_day"
            }
            else if(strName == "light-snow") {
                imgName = if(isNight) "light_snow" else "light_snow"
            }
            else if(strName == "snow") {
                imgName = if(isNight) "snow" else "snow"
            }
            else if(strName == "heavy-snow") {
                imgName = if(isNight) "heavy_snow" else "heavy_snow"
            }
            else if(strName == "wind") {
                imgName = if(isNight) "wind" else "wind"
            }
            else if(strName == "frost") {
                imgName = if(isNight) "frost" else "frost"
            }
            else if(strName == "chance-snow-cloud") {
                imgName = if(isNight) "chance_snow_cloud" else "chance_snow_cloud"
            }
            else if(strName == "hail") {
                imgName = if(isNight) "hail" else "hail"
            }
            else if(strName == "snow-and-rain") {
                imgName = if(isNight) "snow_rain" else "snow_rain"
            }
            else {
                imgName = if(isNight) "fine_night" else "fine_day"
            }

            return imgName
        }

        fun getDisplaynamefromforecasticon(strName : String) : String{
            var imgName = ""
            if(strName == "1") {
                imgName = "Sunny"
            }
            else if(strName == "2") {
                imgName = "Clear"
            }
            else if(strName == "3") {
//                imgName = "mostly_cloudy_day"
                imgName = "Partly cloudy"
            }
            else if(strName == "4") {
                imgName = "Cloudy"
            }
            else if(strName == "6") {
                imgName =  "Hazy"
            }
            else if(strName == "8") {
                imgName = "Light rain"
            }
            else if(strName == "9") {
                imgName = "Windy"
            }
            else if(strName == "10") {
                imgName = "Fog"
            }
            else if(strName == "11") {
//                imgName = "shower"
                imgName = "Shower"
            }
            else if(strName == "12") {
                imgName = "Rain"
            }
            else if(strName == "13") {
                imgName = "Dusty"
            }
            else if(strName == "14") {
                imgName = "Frost"
            }
            else if(strName == "15") {
                imgName = "Snow"
            }
            else if(strName == "16") {
                imgName = "Storm"
            }
            else if(strName == "17") {
                imgName = "Light shower"
            }
            else if(strName == "18") {
                imgName = "Heavy shower"
            }
            else if(strName == "19") {
                imgName = "Cyclone"
            }
            else {
                imgName = "Sunny"
            }
            return imgName
        }

        fun getImageFromForecastIcon(strName : String) : String{
            var imgName = ""
            if(strName == "1") {
                imgName = "fine_day"
            }
            else if(strName == "2") {
                imgName = "fine_night"
            }
            else if(strName == "3") {
//                imgName = "mostly_cloudy_day"
                imgName = "partly_cloudy_daily_weather"
            }
            else if(strName == "4") {
                imgName = "cloudy"
            }
            else if(strName == "6") {
                imgName =  "hazy"
            }
            else if(strName == "8") {
                imgName = "light_rain"
            }
            else if(strName == "9") {
                imgName = "wind"
            }
            else if(strName == "10") {
                imgName = "fog_daily_weather"
            }
            else if(strName == "11") {
//                imgName = "shower"
                imgName = "shower_day"
            }
            else if(strName == "12") {
                imgName = "rain"
            }
            else if(strName == "13") {
                imgName = "dusty"
            }
            else if(strName == "14") {
                imgName = "frost"
            }
            else if(strName == "15") {
                imgName = "snow_daily_weather"
            }
            else if(strName == "16") {
                imgName = "thunderstorm"
            }
            else if(strName == "17") {
                imgName = "light_shower_daily_weather"
            }
            else if(strName == "18") {
                imgName = "heavy_shower_daily_weather"
            }
            else if(strName == "19") {
                imgName = "cyclone"
            }
            else {
                imgName = "fine_day"
            }
            return imgName
        }

    }
}

