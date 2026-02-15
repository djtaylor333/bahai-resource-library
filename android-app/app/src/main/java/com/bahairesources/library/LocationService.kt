package com.bahairesources.library

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.math.*

object LocationService {
    private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    
    data class SunTimes(
        val sunrise: String,
        val sunset: String,
        val location: String = "Unknown Location"
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
        val location = getCurrentLocation(context)
        
        return if (location != null) {
            calculateSunTimes(location.latitude, location.longitude, date)
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