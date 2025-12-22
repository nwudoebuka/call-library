package com.appcapital.call_library.utils
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.location.Geocoder
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.ContactsContract.PhoneLookup
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


class Utils {
    companion object {
        fun formatMillisecondsToTime(milliseconds: Long): String {
            val minutes = (milliseconds / 1000) / 60
            val seconds = (milliseconds / 1000) % 60

            return String.format("%02d:%02d", minutes, seconds)
        }
        fun fahrenheitToCelsius(fahrenheit: Double): Double {
            val celsius =  (fahrenheit - 32) * 5 / 9
            return String.format("%.1f", celsius).toDouble()
        }
        fun getCountryCode(countryName: String): String? {
            val locales = Locale.getAvailableLocales()
            for (locale in locales) {
                if (locale.displayCountry.equals(countryName, ignoreCase = true)) {
                    return locale.country
                }
            }
            return null
        }
        fun getCityAndCountry(context: Context,latitude: Double, longitude: Double): Pair<String?, String?> {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            addresses?.let {
                return if (it.isNotEmpty()) {
                    val address = addresses[0]
                    val city = address.locality
                    val country = address.countryName
                    Pair(city, country)
                } else {
                    Pair(null, null)
                }
            }
          return Pair(null, null)
        }
        fun getCurrentDate(): String {
            // Get the current date
            val currentDate = LocalDate.now()

            // Define the date format
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            // Format the date
            return currentDate.format(formatter)
        }
        fun getContactsName(context: Context, phoneNumber: String?): String? {
            val cr = context.contentResolver
            val uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
            val cursor =
                cr.query(uri, arrayOf(PhoneLookup.DISPLAY_NAME), null, null, null)
                    ?: return null
            var contactName: String? = null
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME))
            }
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            return contactName
        }

        fun getContactName(context: Context, phoneNumber: String): String? {
            val contentResolver: ContentResolver = context.contentResolver
            val uri: Uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
            val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

            var contactName: String? = phoneNumber
            val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
            cursor.use {
                if (it != null && it.moveToFirst()) {
                    contactName = it.getString(it.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME))
                }
            }
            cursor?.close()
            return contactName
        }
        @SuppressLint("MissingPermission")
        fun getLastCallDuration(context: Context): Long {

            val projection = arrayOf(
                CallLog.Calls.DURATION
            )

            val sortOrder = "${CallLog.Calls.DATE} DESC"

            context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    return cursor.getLong(
                        cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION)
                    )
                }
            }

            return 0L
        }


        fun getLastCallDate(context: Context) : String? {
            val contentResolver: ContentResolver = context.contentResolver
            var callDate : String? = "Unknown"
            val cursor: Cursor? = contentResolver.query( CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                CallLog.Calls.DATE + " DESC")
            cursor.use {
                if (it != null && it.moveToFirst()) {
                    callDate = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.DATE))
                }
            }
            cursor?.close()
            return callDate
        }

        fun getContactNumber(context: Context) : String? {
            val contentResolver: ContentResolver = context.contentResolver
            var callDate : String? = "Unknown"
            val cursor: Cursor? = contentResolver.query( CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                CallLog.Calls.NUMBER + " DESC")
            cursor.use {
                if (it != null && it.moveToFirst()) {
                    callDate = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.NUMBER))
                }
            }
            cursor?.close()
            return callDate
        }

        fun getLastPhoneNumber(context: Context): String? {
            val projection = arrayOf(
                CallLog.Calls.NUMBER
            )

            val sortOrder = "${CallLog.Calls.DATE} DESC"

            context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection,
                null,           // no selection, we want all numbers
                null,
                sortOrder
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    return cursor.getString(
                        cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
                    )
                }
            }

            return null
        }

        @SuppressLint("MissingPermission")
        fun getContactPhotoUri(
            context: Context,
            phoneNumber: String
        ): Uri? {

            val uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
            )

            val projection = arrayOf(
                ContactsContract.PhoneLookup.PHOTO_URI
            )

            context.contentResolver.query(uri, projection, null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        return cursor.getString(0)?.let { Uri.parse(it) }
                    }
                }

            return null
        }


        fun formatDuration(seconds: Long): String {
            if (seconds <= 0) return "0 sec"

            val minutes = seconds / 60
            val remainingSeconds = seconds % 60

            return when {
                minutes > 0 && remainingSeconds > 0 ->
                    "$minutes min $remainingSeconds sec"

                minutes > 0 ->
                    "$minutes min"

                else ->
                    "$remainingSeconds sec"
            }
        }


        fun getFormattedDateTime(
            timestamp: Long = System.currentTimeMillis()
        ): String {

            val now = Calendar.getInstance()
            val inputTime = Calendar.getInstance().apply {
                timeInMillis = timestamp
            }

            val timeFormat = SimpleDateFormat("h:mma", Locale.getDefault())
            val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())

            val time = timeFormat.format(Date(timestamp)).lowercase()

            return when {
                isSameDay(now, inputTime) -> {
                    "Today, $time"
                }

                isYesterday(now, inputTime) -> {
                    "Yesterday, $time"
                }

                else -> {
                    "${dateFormat.format(Date(timestamp))}, $time"
                }
            }
        }

        private fun isSameDay(c1: Calendar, c2: Calendar): Boolean {
            return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                    c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
        }

        private fun isYesterday(now: Calendar, input: Calendar): Boolean {
            val yesterday = now.clone() as Calendar
            yesterday.add(Calendar.DAY_OF_YEAR, -1)
            return isSameDay(yesterday, input)
        }
    }


}