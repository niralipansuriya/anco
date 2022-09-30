package com.app.ancoturf.utils

import android.net.Uri
import android.text.TextUtils
import android.webkit.MimeTypeMap
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import com.bumptech.glide.load.ImageHeaderParser
import com.bumptech.glide.load.resource.bitmap.DefaultImageHeaderParser
import java.io.FileInputStream
import java.io.IOException

class MimeTypeFileProvider : FileProvider() {

    private val mImageHeaderParser = DefaultImageHeaderParser()

    override fun getType(@NonNull uri: Uri): String? {

        var type = super.getType(uri)
        if (!TextUtils.equals(type, "application/octet-stream")) {
            return type
        }
        try {
            val parcelFileDescriptor = openFile(uri, "r")
            if (parcelFileDescriptor != null) {

                try {
                    val fileInputStream = FileInputStream(
                            parcelFileDescriptor.fileDescriptor)
                    try {
                        val imageType = mImageHeaderParser.getType(
                                fileInputStream)
                        type?.let {
                            type = getTypeFromImageType(imageType, it)
                        }
                    } finally {
                        try {
                            fileInputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                } finally {
                    try {
                        parcelFileDescriptor.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return type
    }

    private fun getTypeFromImageType(imageType: ImageHeaderParser.ImageType, defaultType: String): String? {
        val extension = when (imageType) {
            ImageHeaderParser.ImageType.GIF -> "gif"
            ImageHeaderParser.ImageType.JPEG -> "jpg"
            ImageHeaderParser.ImageType.PNG_A, ImageHeaderParser.ImageType.PNG -> "png"
            ImageHeaderParser.ImageType.WEBP_A, ImageHeaderParser.ImageType.WEBP -> "webp"
            else -> return defaultType
        }
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
}