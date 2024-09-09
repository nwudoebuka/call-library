package com.appcapital.call_library.utils
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.location.Geocoder
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.PhoneLookup
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

            var contactName: String? = null
            val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
            cursor.use {
                if (it != null && it.moveToFirst()) {
                    contactName = it.getString(it.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME))
                }
            }
            cursor?.close()
            return contactName
        }
    }
}