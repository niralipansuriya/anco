package com.app.ancoturf.utils;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.app.ancoturf.R;

/*
    Handle Toast Messages
 */
public class Boast
{
    private volatile static Boast globalBoast = null;

    private Toast internalToast;

    private Boast(Toast toast)
    {
        // null check
        if (toast == null)
        {
            throw new NullPointerException("Boast.Boast(Toast) requires a non-null parameter.");
        }
        internalToast = toast;
    }

    public static Boast makeText(@NonNull Context context, CharSequence text, int duration) throws Resources.NotFoundException,NullPointerException
    {
          return new Boast(Toast.makeText(context, text, duration));
    }

    public static Boast makeText(@NonNull Context context, int resId, int duration) throws Resources.NotFoundException,NullPointerException
    {
        return new Boast(Toast.makeText(context, resId, duration));
    }

    public static Boast makeText(@NonNull Context context, CharSequence text) throws Resources.NotFoundException,NullPointerException
    {
        return new Boast(Toast.makeText(context, text, Toast.LENGTH_SHORT));
    }

    public static Boast makeText(@NonNull Context context, int resId) throws Resources.NotFoundException,NullPointerException
    {
        return new Boast(Toast.makeText(context, resId, Toast.LENGTH_SHORT));
    }

    public static void showText(Context context, CharSequence text, int duration) throws Resources.NotFoundException
    {
        Boast.makeText(context, text, duration).show();
    }

    public static void showText(Context context, int resId, int duration) throws Resources.NotFoundException
    {
        Boast.makeText(context, resId, duration).show();
    }

    public static void showText(Context context, CharSequence text) throws Resources.NotFoundException
    {
        Boast.makeText(context, "" + text, Toast.LENGTH_SHORT).show();
    }

    public static void showText(boolean showDialog, Context context, CharSequence text) throws Resources.NotFoundException
    {
//        if (showDialog)
//        {
//            Utility.openAlertDialogOkCancel(context, text.toString(), 0, true, context.getString(R.string.app_name));
//        }
//        else
//        {
            Boast.makeText(context, "" + text, Toast.LENGTH_SHORT).show();
//        }
    }

    public static void showText(boolean showDialog, Context context, int text) throws Resources.NotFoundException
    {
//        if (showDialog)
//        {
//            Utility.openAlertDialogOkCancel(context, context.getResources().getString(text), 0, true, context.getString(R.string.app_name));
//        }
//        else
//        {
            Boast.makeText(context, "" + text, Toast.LENGTH_SHORT).show();
//        }
    }

    public static void showText(Context context, int resId) throws Resources.NotFoundException
    {
        Boast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public void cancel()
    {
        internalToast.cancel();
    }

    public void show()
    {
        show(true);
    }

    public void show(boolean cancelCurrent)
    {
        // cancel current
        if (cancelCurrent && (globalBoast != null))
        {
            globalBoast.cancel();
        }

        // save an instance of this current notification
        globalBoast = this;
        //internalToast.setGravity(Gravity.CENTER, 0, 0);
        internalToast.show();
    }

}