package com.app.ancoturf.extension

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.view.WindowManager
import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.ancoturf.R
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.presentation.login.LoginActivity
import com.app.ancoturf.utils.Boast
import com.app.ancoturf.utils.Utility
import com.app.ancoturf.utils.interfces.OnDialogButtonClick


fun AppCompatActivity.shortToast(message: String?) {
    Boast.showText(this , message , Toast.LENGTH_SHORT)
}

fun AppCompatActivity.longToast(message: String?) {
    Boast.showText(this , message , Toast.LENGTH_LONG)
}

fun AppCompatActivity.showAlert(message: String) {
    AlertDialog.Builder(this)
        .setTitle(R.string.app_name)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(android.R.string.ok, { dialog, which -> dialog.dismiss() })
        .show()
}


fun AppCompatActivity.openAlertDialogWithOneClick(
    title: String?,
    message: String?,
    positiveButton: String?,
    onDialogButtonClick: OnDialogButtonClick
) {
    if (Utility.isValueNull(message)) {
        return
    }
    val alertDialogBuilder = AlertDialog.Builder(this)
    val alertDialog = alertDialogBuilder.setTitle(title).setMessage(message).setCancelable(false)
        .setPositiveButton(positiveButton) { dialog, which ->
            dialog.dismiss()
            onDialogButtonClick.onPositiveButtonClick()
        }.create()
    alertDialog.setCanceledOnTouchOutside(false)
    alertDialog.show()
}


fun AppCompatActivity.openAlertDialogWithTwoClick(
    title: String,
    message: String,
    positiveButton: String,
    nagetiveButton: String,
    onDialogButtonClick: OnDialogButtonClick
) {
    if (Utility.isValueNull(message)) {
        return
    }
    val alertDialogBuilder = AlertDialog.Builder(this)
    val alertDialog = alertDialogBuilder.setTitle(title).setMessage(message).setCancelable(false)
        .setPositiveButton(positiveButton) { dialog, which ->
            dialog.dismiss()
            onDialogButtonClick.onPositiveButtonClick()
        }
        .setNegativeButton(nagetiveButton) { dialog, i ->
            dialog.dismiss()
            onDialogButtonClick.onNegativeButtonClick()
        }.create()
    alertDialog.setCanceledOnTouchOutside(false)
    alertDialog.show()
}

fun AppCompatActivity.openLogoutAlertDialog(sharedPrefs : SharedPrefs) {
    val alertDialogBuilder = AlertDialog.Builder(this)
    val alertDialog = alertDialogBuilder.setTitle(getString(R.string.app_name)).setMessage(getString(R.string.unauthorized_message)).setCancelable(false)
        .setPositiveButton(getString(android.R.string.ok)) { dialog, which ->
            dialog.dismiss()
            var fcmToken = sharedPrefs.fcmToken
            sharedPrefs.clear()
            sharedPrefs.isWelcomeVisited = true
            sharedPrefs.fcmToken = fcmToken
//            sharedPrefs.isLogged = false
//            sharedPrefs.accessToken = ""
//            sharedPrefs.userId = 0
//            sharedPrefs.availableCredit = 0
//            sharedPrefs.userEmail = ""
//            sharedPrefs.userName = ""
//            sharedPrefs.totalProductsInCart = 0
//            Handler().postDelayed({
                var intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finishAffinity()
//            }, 1000)

        }.create()
    alertDialog.setCanceledOnTouchOutside(false)
    alertDialog.show()
}

fun AppCompatActivity.openBrowser(link: String) {
    try {
        val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(myIntent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            this,
            "No application can handle this request." + " Please install a webbrowser",
            Toast.LENGTH_LONG
        ).show()
        e.printStackTrace()
    }
}


