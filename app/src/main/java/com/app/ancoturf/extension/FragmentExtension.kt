package com.app.ancoturf.extension

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.app.ancoturf.utils.Boast


fun Fragment.shortToast(message: String?) {
    Boast.showText(activity, message, Toast.LENGTH_SHORT)
}

fun Fragment.longToast(message: String?) {
    Boast.showText(activity, message, Toast.LENGTH_LONG)
}

fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun AppCompatActivity.hideFragment(fragment: Fragment?) {
    if (fragment == null) return

    supportFragmentManager.inTransaction {
        hide(fragment)
    }
}

fun AppCompatActivity.showFragment(fragment: Fragment?) {
    if (fragment == null) return
    supportFragmentManager.inTransaction {
        setCustomAnimations(
            android.R.anim.fade_in,
            android.R.anim.fade_out,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
        show(fragment)
    }
    hideKeyboard()
}

fun AppCompatActivity.pushFragment(
    destinationFragment: Fragment,
    addToBackStack: Boolean = false,
    ignoreIfCurrent: Boolean,
    justAdd: Boolean = true,
    fragmentContainerId: Int
) {
    hideKeyboard()
    val currentFragment = supportFragmentManager.findFragmentById(fragmentContainerId)
    if (ignoreIfCurrent && currentFragment != null) {
        if (destinationFragment.javaClass.canonicalName.equals(currentFragment.tag, false)) {
            return
        }
    }
    supportFragmentManager.inTransaction {
        setCustomAnimations(
            android.R.anim.fade_in,
            android.R.anim.fade_out,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
//        if (currentFragment != null) {
//            hide(currentFragment)
//        }
        if (addToBackStack) {
            addToBackStack(destinationFragment.javaClass.canonicalName)
        }
        if (justAdd) {
            add(
                fragmentContainerId,
                destinationFragment,
                destinationFragment.javaClass.canonicalName
            )
        } else {
            replace(
                fragmentContainerId,
                destinationFragment,
                destinationFragment.javaClass.canonicalName
            )
        }
    }
    hideKeyboard()
}


fun AppCompatActivity.hideKeyboard() {
    window!!.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    val view = currentFocus
    if (view != null) {
        val imm =
            getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun AppCompatActivity.hideKeyboard(view: View?) {
    window!!.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    if (view != null) {
        val imm =
            getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun AppCompatActivity.showKeyboard() {
    window!!.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    val view = currentFocus
    if (view != null) {
        val imm =
            getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun Fragment.openBrowser(link: String) {
    try {
        val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        requireActivity().startActivity(myIntent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            requireActivity(),
            "No application can handle this request." + " Please install a webbrowser",
            Toast.LENGTH_LONG
        ).show()
        e.printStackTrace()
    }
}

