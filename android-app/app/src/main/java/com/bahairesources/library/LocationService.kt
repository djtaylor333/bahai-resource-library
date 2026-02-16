package com.bahairesources.library

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.Geocoder
import android.location.Address
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.math.*

object LocationService {
    private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private const val LOCATION_PREFS = "location_preferences"
    private const val PREF_SAVED_LOCATION = "saved_location"
    private const val PREF_SAVED_CITY = "saved_city"
    private const val PREF_SAVED_LAT = "saved_latitude"
    private const val PREF_SAVED_LON = "saved_longitude"
    private const val PREF_USE_MANUAL_LOCATION = "use_manual_location"
    
    data class SunTimes(
        val sunrise: String,
        val sunset: String,
        val location: String = "Unknown Location"
    )
    
    data class LocationInfo(
        val cityName: String,
        val latitude: Double,
        val longitude: Double,
        val isManual: Boolean = false
    )
    
    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    fun requestLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
    
    fun getCurrentLocation(context: Context): Location? {
        if (!hasLocationPermission(context)) {
            return null
        }
        
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        
        try {
            // Try GPS first, then network
            val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            
            return when {
                gpsLocation != null -> gpsLocation
                networkLocation != null -> networkLocation
                else -> null
            }
        } catch (e: SecurityException) {
            return null
        }
    }
    
    fun getCityNameFromCoordinates(context: Context, latitude: Double, longitude: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val cityName = when {
                    !address.locality.isNullOrBlank() -> address.locality
                    !address.subAdminArea.isNullOrBlank() -> address.subAdminArea
                    !address.adminArea.isNullOrBlank() -> address.adminArea
                    !address.countryName.isNullOrBlank() -> address.countryName
                    else -> "Unknown Location"
                }
                
                val countryCode = if (!address.countryName.isNullOrBlank()) ", ${address.countryName}" else ""
                "$cityName$countryCode"
            } else {
                "Lat: ${String.format("%.2f", latitude)}, Lon: ${String.format("%.2f", longitude)}"
            }
        } catch (e: Exception) {
            "Lat: ${String.format("%.2f", latitude)}, Lon: ${String.format("%.2f", longitude)}"
        }
    }
    
    fun getCurrentLocationInfo(context: Context): LocationInfo? {
        // Check if user has set a manual location
        val prefs = context.getSharedPreferences(LOCATION_PREFS, Context.MODE_PRIVATE)
        val useManual = prefs.getBoolean(PREF_USE_MANUAL_LOCATION, false)
        
        if (useManual) {
            val savedCity = prefs.getString(PREF_SAVED_CITY, null)
            val savedLat = prefs.getFloat(PREF_SAVED_LAT, Float.NaN)
            val savedLon = prefs.getFloat(PREF_SAVED_LON, Float.NaN)
            
            if (savedCity != null && !savedLat.isNaN() && !savedLon.isNaN()) {
                return LocationInfo(savedCity, savedLat.toDouble(), savedLon.toDouble(), true)
            }
        }
        
        // Try to get GPS location
        val location = getCurrentLocation(context) ?: return null
        val cityName = getCityNameFromCoordinates(context, location.latitude, location.longitude)
        
        return LocationInfo(cityName, location.latitude, location.longitude, false)
    }
    
    fun saveManualLocation(context: Context, cityName: String, latitude: Double, longitude: Double) {
        val prefs = context.getSharedPreferences(LOCATION_PREFS, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString(PREF_SAVED_CITY, cityName)
            putFloat(PREF_SAVED_LAT, latitude.toFloat())
            putFloat(PREF_SAVED_LON, longitude.toFloat())
            putBoolean(PREF_USE_MANUAL_LOCATION, true)
            apply()
        }
    }
    
    fun clearManualLocation(context: Context) {
        val prefs = context.getSharedPreferences(LOCATION_PREFS, Context.MODE_PRIVATE)
        prefs.edit().apply {
            remove(PREF_SAVED_CITY)
            remove(PREF_SAVED_LAT)
            remove(PREF_SAVED_LON)
            putBoolean(PREF_USE_MANUAL_LOCATION, false)
            apply()
        }
    }
    
    fun calculateSunTimes(latitude: Double, longitude: Double, date: Date = Date()): SunTimes {
        val calendar = Calendar.getInstance().apply { time = date }
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        
        // Simplified sunrise/sunset calculation using the sunrise equation
        val P = asin(0.39795 * cos(0.98563 * (dayOfYear - 173) * PI / 180))
        val argument = -tan(latitude * PI / 180) * tan(P)
        val timeCorrection = 12 * acos(argument) / PI
        
        // Calculate sunrise and sunset times
        val sunriseDecimal = 12 - timeCorrection - longitude / 15
        val sunsetDecimal = 12 + timeCorrection - longitude / 15
        
        // Convert to hours and minutes
        fun formatTime(decimal: Double): String {
            val hours = decimal.toInt()
            val minutes = ((decimal - hours) * 60).toInt()
            val adjustedHours = if (hours < 0) hours + 24 else if (hours >= 24) hours - 24 else hours
            return String.format("%02d:%02d", adjustedHours, minutes)
        }
        
        return SunTimes(
            sunrise = formatTime(sunriseDecimal),
            sunset = formatTime(sunsetDecimal),
            location = "Lat: ${String.format("%.2f", latitude)}, Lon: ${String.format("%.2f", longitude)}"
        )
    }
    
    fun getSunTimesForLocation(context: Context, date: Date = Date()): SunTimes {
        val locationInfo = getCurrentLocationInfo(context)
        
        return if (locationInfo != null) {
            val sunTimes = calculateSunTimes(locationInfo.latitude, locationInfo.longitude, date)
            // Replace the generic lat/lon with the city name
            SunTimes(
                sunrise = sunTimes.sunrise,
                sunset = sunTimes.sunset,
                location = locationInfo.cityName + if (locationInfo.isManual) " (Manual)" else " (Auto)"
            )
        } else {
            // Default times if location is not available
            SunTimes(
                sunrise = "06:30",
                sunset = "18:30",
                location = "Location unavailable - using default times"
            )
        }
    }
    
    fun getFastTimes(context: Context, date: Date = Date()): SunTimes {
        return getSunTimesForLocation(context, date)
    }
    
    fun getFeastDaySunset(context: Context, date: Date): String {
        val sunTimes = getSunTimesForLocation(context, date)
        return sunTimes.sunset
    }
}