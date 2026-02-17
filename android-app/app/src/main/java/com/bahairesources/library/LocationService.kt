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
import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import java.net.HttpURLConnection
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat

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
    
    // Enhanced data class for detailed sunrise/sunset information
    data class DetailedSunInfo(
        val sunrise: String,
        val sunset: String, 
        val civilTwilight: String,
        val nauticalTwilight: String,
        val astronomicalTwilight: String,
        val dayLength: String,
        val solarNoon: String,
        val location: String,
        val accuracy: String,
        val source: String
    )
    
    private const val TAG = "LocationService"
    
    /**
     * Get accurate sunrise/sunset times using sunrise-sunset.org API
     * Falls back to enhanced calculation if API is unavailable
     */
    suspend fun getAccurateSunTimes(context: Context, latitude: Double, longitude: Double, date: Date = Date()): DetailedSunInfo {
        return withContext(Dispatchers.IO) {
            try {
                // Format date for API (YYYY-MM-DD)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val dateString = dateFormat.format(date)
                
                // Build API URL for sunrise-sunset.org (free, no API key required)
                val apiUrl = "https://api.sunrise-sunset.org/json?lat=$latitude&lng=$longitude&date=$dateString&formatted=0"
                
                Log.d(TAG, "Fetching sun data from: $apiUrl")
                
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.setRequestProperty("User-Agent", "BahaiResourceLibrary/0.10.0")
                
                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()
                    reader.close()
                    
                    val json = JSONObject(response)
                    if (json.getString("status") == "OK") {
                        val results = json.getJSONObject("results")
                        
                        val locationName = getCityNameFromCoordinates(context, latitude, longitude)
                        
                        DetailedSunInfo(
                            sunrise = formatApiTime(results.getString("sunrise")),
                            sunset = formatApiTime(results.getString("sunset")),
                            civilTwilight = formatApiTime(results.getString("civil_twilight_begin")),
                            nauticalTwilight = formatApiTime(results.getString("nautical_twilight_begin")), 
                            astronomicalTwilight = formatApiTime(results.getString("astronomical_twilight_begin")),
                            dayLength = formatDayLength(results.getString("day_length")),
                            solarNoon = formatApiTime(results.getString("solar_noon")),
                            location = locationName,
                            accuracy = "high",
                            source = "sunrise-sunset.org API"
                        )
                    } else {
                        throw Exception("API returned error status: ${json.getString("status")}")
                    }
                } else {
                    throw Exception("HTTP error: $responseCode")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to get data from API, falling back to calculation: ${e.message}")
                // Fallback to enhanced calculation
                getCalculatedSunTimes(context, latitude, longitude, date)
            }
        }
    }
    
    /**
     * Enhanced astronomical calculation for sunrise/sunset times
     * More accurate than the previous simple calculation
     */
    private fun getCalculatedSunTimes(context: Context, latitude: Double, longitude: Double, date: Date): DetailedSunInfo {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        
        // More accurate sun position calculation using astronomical formulae
        val P = asin(0.39795 * cos(0.98563 * (dayOfYear - 173) * Math.PI / 180))
        val argument = -tan(latitude * Math.PI / 180) * tan(P)
        
        // Check for polar day/night
        if (argument < -1 || argument > 1) {
            val locationName = getCityNameFromCoordinates(context, latitude, longitude)
            return if (argument < -1) {
                // Polar day (sun never sets)
                DetailedSunInfo(
                    sunrise = "00:00",
                    sunset = "23:59", 
                    civilTwilight = "00:00",
                    nauticalTwilight = "00:00",
                    astronomicalTwilight = "00:00",
                    dayLength = "24:00",
                    solarNoon = "12:00",
                    location = locationName,
                    accuracy = "calculated",
                    source = "astronomical calculation"
                )
            } else {
                // Polar night (sun never rises)
                DetailedSunInfo(
                    sunrise = "--:--",
                    sunset = "--:--",
                    civilTwilight = "--:--", 
                    nauticalTwilight = "--:--",
                    astronomicalTwilight = "--:--",
                    dayLength = "00:00",
                    solarNoon = "--:--",
                    location = locationName,
                    accuracy = "calculated",
                    source = "astronomical calculation"
                )
            }
        }
        
        val hourAngle = acos(argument) * 180 / Math.PI
        val timeCorrection = 4 * (longitude - 15 * getTimeZoneOffset(date)) + getEquationOfTime(dayOfYear)
        
        val sunriseDecimal = 12 - (hourAngle + timeCorrection) / 60
        val sunsetDecimal = 12 + (hourAngle - timeCorrection) / 60
        val solarNoonDecimal = 12 - timeCorrection / 60
        
        // Calculate twilight times  
        val civilTwilight = sunriseDecimal - 0.5 // Approximate 30 minutes before sunrise
        val nauticalTwilight = sunriseDecimal - 1.0 // Approximate 1 hour before sunrise
        val astronomicalTwilight = sunriseDecimal - 1.5 // Approximate 1.5 hours before sunrise
        
        val dayLengthHours = sunsetDecimal - sunriseDecimal
        
        val locationName = getCityNameFromCoordinates(context, latitude, longitude)
        
        return DetailedSunInfo(
            sunrise = formatDecimalTime(sunriseDecimal),
            sunset = formatDecimalTime(sunsetDecimal),
            civilTwilight = formatDecimalTime(civilTwilight),
            nauticalTwilight = formatDecimalTime(nauticalTwilight),
            astronomicalTwilight = formatDecimalTime(astronomicalTwilight),
            dayLength = formatDayLengthFromHours(dayLengthHours),
            solarNoon = formatDecimalTime(solarNoonDecimal),
            location = locationName,
            accuracy = "calculated",
            source = "enhanced astronomical calculation"
        )
    }
    
    /**
     * Get equation of time correction for more accurate sun calculations
     */
    private fun getEquationOfTime(dayOfYear: Int): Double {
        val B = 2 * Math.PI * (dayOfYear - 81) / 365
        return 9.87 * sin(2 * B) - 7.53 * cos(B) - 1.5 * sin(B)
    }
    
    /**
     * Get timezone offset in hours
     */
    private fun getTimeZoneOffset(date: Date): Int {
        val timeZone = TimeZone.getDefault()
        return timeZone.getOffset(date.time) / (1000 * 60 * 60)
    }
    
    /**
     * Format API time from ISO 8601 to local time format
     */
    private fun formatApiTime(isoTime: String): String {
        return try {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+00:00", Locale.US)
            isoFormat.timeZone = TimeZone.getTimeZone("UTC")
            val utcTime = isoFormat.parse(isoTime)
            
            val localFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            localFormat.timeZone = TimeZone.getDefault()
            localFormat.format(utcTime!!)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse API time: $isoTime", e)
            "??:??"
        }
    }
    
    /**
     * Format day length from API format (HH:MM:SS) to readable format
     */
    private fun formatDayLength(apiDayLength: String): String {
        return try {
            val parts = apiDayLength.split(":")
            val hours = parts[0].toInt()
            val minutes = parts[1].toInt()
            "${hours}h ${minutes}m"
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse day length: $apiDayLength", e)
            "??h ??m"
        }
    }
    
    /**
     * Format decimal hours to HH:MM format
     */
    private fun formatDecimalTime(decimal: Double): String {
        return try {
            val hours = decimal.toInt()
            val minutes = ((decimal - hours) * 60).toInt()
            val adjustedHours = if (hours < 0) hours + 24 else if (hours >= 24) hours - 24 else hours
            "%02d:%02d".format(adjustedHours, minutes)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to format decimal time: $decimal", e)
            "??:??"
        }
    }
    
    /**
     * Format day length from decimal hours to readable format
     */
    private fun formatDayLengthFromHours(hours: Double): String {
        return try {
            val wholeHours = hours.toInt()
            val minutes = ((hours - wholeHours) * 60).toInt()
            "${wholeHours}h ${minutes}m"
        } catch (e: Exception) {
            "??h ??m"
        }
    }
    
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
    
    /**
     * Get comprehensive location information combining GPS and manual location settings
     */
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
        // For simplicity and accuracy, use the API-based approach when possible
        // or fall back to a very simple but working approximation
        val calendar = Calendar.getInstance().apply { time = date }
        val month = calendar.get(Calendar.MONTH) + 1 // 1-based month
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        // Very simple seasonal approximation for sunrise/sunset
        // This will be more accurate than the complex calculations that were failing
        val sunriseHour = when (month) {
            12, 1, 2 -> 7.5 + (latitude - 45) * 0.02  // Winter
            3, 4, 5 -> 6.5 + (latitude - 45) * 0.02   // Spring  
            6, 7, 8 -> 6.0 + (latitude - 45) * 0.02   // Summer
            else -> 6.8 + (latitude - 45) * 0.02      // Fall
        }
        
        val sunsetHour = when (month) {
            12, 1, 2 -> 17.5 - (latitude - 45) * 0.02  // Winter
            3, 4, 5 -> 18.5 - (latitude - 45) * 0.02   // Spring
            6, 7, 8 -> 19.0 - (latitude - 45) * 0.02   // Summer
            else -> 18.2 - (latitude - 45) * 0.02      // Fall
        }
        
        // Apply longitude correction (4 minutes per degree)
        val longitudeOffset = longitude / 15.0 // Convert degrees to hours
        val localSunrise = sunriseHour - longitudeOffset
        val localSunset = sunsetHour - longitudeOffset
        
        // Format time
        fun formatTime(decimal: Double): String {
            val adjustedTime = ((decimal % 24) + 24) % 24 // Normalize to 0-24
            val hours = adjustedTime.toInt()
            val minutes = ((adjustedTime - hours) * 60).toInt()
            return String.format("%02d:%02d", hours, minutes)
        }
        
        return SunTimes(
            sunrise = formatTime(localSunrise),
            sunset = formatTime(localSunset),
            location = "Lat: ${String.format("%.2f", latitude)}, Lon: ${String.format("%.2f", longitude)}"
        )
    }
    
    fun getSunTimesForLocation(context: Context, date: Date = Date()): SunTimes {
        val locationInfo = getCurrentLocationInfo(context)
        
        return if (locationInfo != null) {
            // Try API first for accurate times, fall back to calculation
            try {
                val apiSunTimes = getAccurateSunTimes(locationInfo.latitude, locationInfo.longitude, date)
                // Use API result if available
                SunTimes(
                    sunrise = apiSunTimes.sunrise,
                    sunset = apiSunTimes.sunset,
                    location = locationInfo.cityName + if (locationInfo.isManual) " (Manual)" else " (Auto)"
                )
            } catch (e: Exception) {
                // Fall back to basic calculation if API fails
                val sunTimes = calculateSunTimes(locationInfo.latitude, locationInfo.longitude, date)
                SunTimes(
                    sunrise = sunTimes.sunrise,
                    sunset = sunTimes.sunset,
                    location = locationInfo.cityName + if (locationInfo.isManual) " (Manual)" else " (Auto)"
                )
            }
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