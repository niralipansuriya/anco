package com.app.ancoturf.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import com.app.ancoturf.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtil
{

    public static final int REQUEST_PICK_PICTURE = 1001;
    public static final int REQUEST_CAPTURE_PICTURE = 1002;

    private static OnImageChosen onImageChoosen;
    private static File file;

    /**
     * Method for return file path of Gallery image
     *
     * @param context
     * @param uri
     * @return path of the selected image file from gallery
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri)
    {

        // check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri))
        {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri))
            {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type))
                {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri))
            {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri))
            {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type))
                {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type))
                {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type))
                {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme()))
        {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme()))
        {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for MediaStore Uris, and other file-based
     * ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs)
    {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try
        {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst())
            {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally
        {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri)
    {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri)
    {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri)
    {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri)
    {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static void takePicture(Context context, OnImageChosen imageChoosen, boolean isFrontFace)
    {
        onImageChoosen = imageChoosen;

        Uri outputFileUri = null;
        try
        {
//            file = createImageFile(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                outputFileUri = FileProvider.getUriForFile(context,
                        BuildConfig.APPLICATION_ID + ".provider",
                        file);
            else
                outputFileUri = Uri.fromFile(file);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        if (isFrontFace)
            cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        else
            cameraIntent.putExtra("android.intent.extras.CAMERA_FACING",
                    Camera.CameraInfo.CAMERA_FACING_BACK);
        //if (context instanceof MainFragmentActivity)
        //    ((MainFragmentActivity) context).startActivityForResult(cameraIntent, REQUEST_CAPTURE_PICTURE);
    }

    public static void choosePicture(Context context, OnImageChosen imageChoosen)
    {
        onImageChoosen = imageChoosen;
        try
        {
            Intent cameraIntent = new Intent(Intent.ACTION_GET_CONTENT);
            cameraIntent.setType("image/*");

        } catch (ActivityNotFoundException var2)
        {
            var2.printStackTrace();
        }
    }

    public static void onActivityResult(Context context, int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            String path = null;
            if (requestCode == REQUEST_CAPTURE_PICTURE)
            {
                try
                {
                    path = file.getAbsolutePath();

                } catch (NullPointerException e)
                {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_PICK_PICTURE)
            {
                try
                {
                    path = getPath(context, data.getData());

                } catch (NullPointerException e)
                {
                    e.printStackTrace();
                }
            }

            if (onImageChoosen != null)
            {
                onImageChoosen.onImageChosen(path);
            }

        } else if (requestCode == REQUEST_CAPTURE_PICTURE)
//            Utility.log("Image path : canecl by user");

        onImageChoosen = null;
        file = null;
    }

    public interface OnImageChosen
    {
        public void onImageChosen(final String path);
    }

    /**
     * create image file
     *
     * @return File
     * @throws IOException
     */
//    public static File createImageFile(Context context) throws IOException
//    {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        return File.createTempFile(imageFileName,  / prefix /
//                ".jpg",         / suffix /
//            storageDir      / directory /);
//
//    }

}