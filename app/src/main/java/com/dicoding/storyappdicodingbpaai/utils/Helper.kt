package com.dicoding.storyappdicodingbpaai.utils


import android.annotation.SuppressLint
import android.app.Application
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.dicoding.storyappdicodingbpaai.R
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object Helper {


    /*
    *  DATE FORMAT
    * */
    private const val timestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private const val simpleDateFormat = "dd MMM yyyy HH.mm"

    /*
    *  FILE SIZE
    * */
    private const val MAXIMAL_SIZE = 1000000

    /*
    * DATE INSTANCE
    * */
    private var defaultDate = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())

    @SuppressLint("ConstantLocale")
    val simpleDate = SimpleDateFormat(simpleDateFormat, Locale.getDefault())

    // curent date in date
    private fun getCurrentDate(): Date {
        return Date()
    }

    // curent date in string
    fun getCurrentDateString(): String = defaultDate.format(getCurrentDate())

    @SuppressLint("ConstantLocale")
    val currentTimestamp: String = SimpleDateFormat(
        "ddMMyySSSSS",
        Locale.getDefault()
    ).format(System.currentTimeMillis())

    // string simpleDate (unformatted) to date
    private fun parseSimpleDate(dateValue: String): Date {
        return defaultDate.parse(dateValue) as Date
    }

    // simpleDate (Date) to string
    private fun getSimpleDate(date: Date): String = simpleDate.format(date)

    // string to string
    fun getSimpleDateString(dateValue: String): String = getSimpleDate(parseSimpleDate(dateValue))


    // string UTC format to date
    private fun parseUTCDate(timestamp: String): Date {
        return try {
            val formatter = SimpleDateFormat(timestampFormat, Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            formatter.parse(timestamp) as Date
        } catch (e: ParseException) {
            getCurrentDate()
        }
    }

    fun getTimelineUpload(context: Context, timestamp: String): String {
        val currentTime = getCurrentDate()
        val uploadTime = parseUTCDate(timestamp)
        val diff: Long = currentTime.time - uploadTime.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val label = when (minutes.toInt()) {
            0 -> "$seconds ${context.getString(R.string.const_text_seconds_ago)}"
            in 1..59 -> "$minutes ${context.getString(R.string.const_text_minutes_ago)}"
            in 60..1440 -> "$hours ${context.getString(R.string.const_text_hours_ago)}"
            else -> "$days ${context.getString(R.string.const_text_days_ago)}"
        }
        return label
    }

    fun getUploadStoryTime(timestamp: String): String {
        val date: Date = parseUTCDate(timestamp)
        return getSimpleDate(date)
    }


    /*
    * CAMERA INSTANCE HELPER
    * */

    private fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(currentTimestamp, ".jpg", storageDir)
    }

    fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createCustomTempFile(context)
        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()
        return myFile
    }

    fun createFile(application: Application): File {
        val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
            File(it, "story").apply { mkdirs() }
        }
        val outputDirectory = if (
            mediaDir != null && mediaDir.exists()
        ) mediaDir else application.filesDir

        return File(outputDirectory, "STORY-$currentTimestamp.jpg")
    }

    fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
        val matrix = Matrix()
        return if (isBackCamera) {
            matrix.postRotate(90f)
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        } else {
            matrix.postRotate(-90f)
            matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        }
    }

    fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)

        var compressQuality = 100
        var streamLength: Int

        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > MAXIMAL_SIZE)

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

        return file
    }
}