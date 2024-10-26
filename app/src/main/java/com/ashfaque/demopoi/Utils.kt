package com.ashfaque.demopoi

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Utils {

    private const val TAG = "Ashu"

    fun logDebug(message: String) {
        Log.d(TAG, message)
    }

    val tagNames = listOf("School","Hospital","Hotel", "Petrol Pump", "Govt Bldg", "Theatre", "Mall", "Park","Other")
    val radiusOption = listOf(5,10,15,20)


    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun getCurrentDate(pattern:String): String {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

     fun arePointsWithinRadius(
        lat1: Double, lon1: Double, lat2: Double, lon2: Double, radiusInMeters: Float
    ): Boolean {
        val result = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, result)
        return result[0] <= radiusInMeters
    }

    // Function to validate date format
     fun isValidDate(date: String): Boolean {

        if (!date.matches(Regex("""\d{2}/\d{2}/\d{4}"""))) {
            return false
        }

        // Parse the date
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormat.isLenient = false // Ensure strict parsing

        return try {
            val inputDate = dateFormat.parse(date)
            val calendar = Calendar.getInstance()

            // Check if the date is in the past
            val currentDate = calendar.time
            if (inputDate.after(currentDate)) return false

            // Check if the year is 1900 or later
            calendar.time = inputDate
            val year = calendar.get(Calendar.YEAR)

            year >= 1900
        } catch (e: Exception) {
            false // Return false if parsing fails
        }

    }

    fun stringToLatLng(latLngString: String): LatLng? {
        return try {
            val parts = latLngString.split(",")
            if (parts.size == 2) {
                val latitude = parts[0].toDouble()
                val longitude = parts[1].toDouble()
                LatLng(latitude, longitude)
            } else {
                logDebug("stringToLatLng try Invalid format")
                null
            }
        } catch (e: NumberFormatException) {
            logDebug("stringToLatLng catch $e")
            null
        }
    }


}
