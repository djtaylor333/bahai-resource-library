package com.bahairesources.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.graphics.Color
import androidx.cardview.widget.CardView
import android.view.View
import java.util.*
import java.text.SimpleDateFormat

class CalendarActivity : AppCompatActivity() {
    
    private lateinit var monthYearText: TextView
    private lateinit var calendarLayout: LinearLayout
    private lateinit var holyDaysList: LinearLayout
    private var currentDate = Calendar.getInstance()
    private var showOtherReligions = false
    private var isDarkMode = false
    private var currentFontSize = SettingsManager.FONT_MEDIUM
    
    private lateinit var locationService: LocationService
    
    // Expanded Bahá'í calendar data for 2026 (BE 183)
    private val holydaysData = mapOf(
        // Major Holy Days - work suspended
        "2026-03-21" to BahaiDate("Naw-Rúz", "Bahá'í New Year (BE 183)", "Major Holy Day", "Sunset March 20", true),
        "2026-04-21" to BahaiDate("Riḍván 1st Day", "Declaration of Bahá'u'lláh", "Major Holy Day", "Sunset April 20", true),
        "2026-05-02" to BahaiDate("Riḍván 12th Day", "End of Festival of Riḍván", "Major Holy Day", "Sunset May 1", true),
        "2026-05-23" to BahaiDate("Declaration of the Báb", "1844 CE", "Major Holy Day", "Sunset May 22", true),
        "2026-05-29" to BahaiDate("Ascension of Bahá'u'lláh", "1892 CE at 3:00 AM", "Major Holy Day", "", true),
        "2026-07-09" to BahaiDate("Martyrdom of the Báb", "1850 CE at Noon", "Major Holy Day", "", true),
        "2026-10-20" to BahaiDate("Birth of the Báb", "1819 CE", "Major Holy Day", "Sunset October 19", true),
        "2026-11-12" to BahaiDate("Birth of Bahá'u'lláh", "1817 CE", "Major Holy Day", "Sunset November 11", true),
        
        // Other Holy Days
        "2026-04-23" to BahaiDate("Riḍván 9th Day", "Middle day of Festival", "Holy Day", "Sunset April 22", false),
        "2026-11-26" to BahaiDate("Day of the Covenant", "Celebration of 'Abdu'l-Bahá", "Holy Day", "", false),
        "2026-11-28" to BahaiDate("Ascension of 'Abdu'l-Bahá", "1921 CE at 1:00 AM", "Holy Day", "", false),
        
        // Bahá'í Fast Period (March 2-20, 2026)
        "2026-03-02" to BahaiDate("Fast Begins", "19-day Bahá'í Fast commencing", "Fast Period", "Sunset March 1", false),
        "2026-03-20" to BahaiDate("Fast Ends", "Fast concludes at sunset", "Fast Period", "Sunset March 19", false),
        
        // Ayyám-i-Há (Intercalary Days) - February 26 to March 1, 2026
        "2026-02-26" to BahaiDate("Ayyám-i-Há Begins", "Intercalary Days (4 days)", "Special Period", "Sunset February 25", false),
        "2026-03-01" to BahaiDate("Ayyám-i-Há Ends", "Last day of Intercalary Days", "Special Period", "Sunset February 28", false),
        
        // 19-Day Feast Days (first day of each Bahá'í month) - sample for current period
        "2026-02-07" to BahaiDate("Feast of Mulk", "Dominion - 18th Bahá'í month begins", "Feast Day", "Sunset February 6", false),
        "2026-02-26" to BahaiDate("Feast of 'Alá'", "Loftiness - 19th Bahá'í month begins", "Feast Day", "Sunset February 25", false),
        "2026-03-21" to BahaiDate("Feast of Bahá", "Splendor - 1st Bahá'í month begins", "Feast Day", "Sunset March 20", false),
        "2026-04-09" to BahaiDate("Feast of Jalál", "Glory - 2nd Bahá'í month begins", "Feast Day", "Sunset April 8", false),
        "2026-04-28" to BahaiDate("Feast of Jamál", "Beauty - 3rd Bahá'í month begins", "Feast Day", "Sunset April 27", false),
        "2026-05-17" to BahaiDate("Feast of 'Aẓamat", "Grandeur - 4th Bahá'í month begins", "Feast Day", "Sunset May 16", false),
        "2026-06-05" to BahaiDate("Feast of Núr", "Light - 5th Bahá'í month begins", "Feast Day", "Sunset June 4", false),
        "2026-06-24" to BahaiDate("Feast of Raḥmat", "Mercy - 6th Bahá'í month begins", "Feast Day", "Sunset June 23", false),
        "2026-07-13" to BahaiDate("Feast of Kalimát", "Words - 7th Bahá'í month begins", "Feast Day", "Sunset July 12", false),
        "2026-08-01" to BahaiDate("Feast of Kamál", "Perfection - 8th Bahá'í month begins", "Feast Day", "Sunset July 31", false),
        "2026-08-20" to BahaiDate("Feast of Asmá'", "Names - 9th Bahá'í month begins", "Feast Day", "Sunset August 19", false),
        "2026-09-08" to BahaiDate("Feast of 'Izzat", "Might - 10th Bahá'í month begins", "Feast Day", "Sunset September 7", false)
    )
    
    private val otherReligiousHolidays = mapOf(
        // Sample other religious holidays
        "2026-01-01" to "New Year's Day",
        "2026-03-29" to "Good Friday (Christian)",
        "2026-03-31" to "Easter Sunday (Christian)", 
        "2026-04-13" to "Ram Navami (Hindu)",
        "2026-04-22" to "Earth Day",
        "2026-12-25" to "Christmas (Christian)"
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize settings
        isDarkMode = SettingsManager.isDarkMode(this)
        currentFontSize = SettingsManager.getFontSize(this)
        
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 30, 20, 30)
            setBackgroundColor(if (isDarkMode) Color.parseColor("#121212") else Color.parseColor("#F8F9FA"))
        }
        
        // Header
        val headerLayout = createHeader()
        
        // Calendar controls
        val controlsLayout = createCalendarControls()
        
        // Month/Year display
        monthYearText = TextView(this).apply {
            textSize = 20f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            gravity = android.view.Gravity.CENTER
            setPadding(0, 20, 0, 20)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
        
        // Calendar grid container
        val calendarCard = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.WHITE)
        }
        
        calendarLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        calendarCard.addView(calendarLayout)
        
        // Holy days list
        val holyDaysCard = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#E8F5E8"))
        }
        
        val holyDaysHeader = TextView(this).apply {
            text = "📅 Upcoming Bahá'í Holy Days & Feast Days"
            textSize = 16f
            setTextColor(if (isDarkMode) Color.parseColor("#81C784") else Color.parseColor("#2E7D32"))
            setPadding(20, 20, 20, 10)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
        
        holyDaysList = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 10, 20, 20)
        }
        
        val holyDaysLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        holyDaysLayout.addView(holyDaysHeader)
        holyDaysLayout.addView(holyDaysList)
        holyDaysCard.addView(holyDaysLayout)
        
        mainLayout.addView(headerLayout)
        mainLayout.addView(controlsLayout)
        mainLayout.addView(monthYearText)
        mainLayout.addView(calendarCard)
        mainLayout.addView(createSpacing(20))
        mainLayout.addView(holyDaysCard)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
        
        // Initialize calendar display
        updateCalendarDisplay()
    }
    
    private fun createHeader(): LinearLayout {
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(10, 10, 10, 30)
        }
        
        val backButton = Button(this).apply {
            text = "← Back"
            setBackgroundColor(Color.parseColor("#1976D2"))
            setTextColor(Color.WHITE)
            setPadding(20, 10, 20, 10)
            setOnClickListener { finish() }
        }
        
        val titleText = TextView(this).apply {
            text = "📅 Bahá'í Calendar"
            textSize = 20f
            setTextColor(Color.parseColor("#1976D2"))
            setPadding(30, 15, 0, 0)
        }
        
        headerLayout.addView(backButton)
        headerLayout.addView(titleText)
        
        return headerLayout
    }
    
    private fun createCalendarControls(): CardView {
        val controlsCard = CardView(this).apply {
            radius = 8f
            cardElevation = 3f
            setCardBackgroundColor(Color.parseColor("#FFF3E0"))
        }
        
        val controlsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 15, 20, 15)
        }
        
        // Navigation row
        val navRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER
        }
        
        val prevButton = Button(this).apply {
            text = "◀ Previous"
            setBackgroundColor(Color.parseColor("#FF9800"))
            setTextColor(Color.WHITE)
            setPadding(20, 10, 20, 10)
            textSize = 14f
            setOnClickListener { changeMonth(-1) }
        }
        
        val todayButton = Button(this).apply {
            text = "Today"
            setBackgroundColor(Color.parseColor("#4CAF50"))
            setTextColor(Color.WHITE)
            setPadding(20, 10, 20, 10)
            textSize = 14f
            setOnClickListener { goToToday() }
        }
        
        val nextButton = Button(this).apply {
            text = "Next ▶"
            setBackgroundColor(Color.parseColor("#FF9800"))
            setTextColor(Color.WHITE)
            setPadding(20, 10, 20, 10)
            textSize = 14f
            setOnClickListener { changeMonth(1) }
        }
        
        navRow.addView(prevButton)
        navRow.addView(View(this).apply { layoutParams = LinearLayout.LayoutParams(20, 0) })
        navRow.addView(todayButton)
        navRow.addView(View(this).apply { layoutParams = LinearLayout.LayoutParams(20, 0) })
        navRow.addView(nextButton)
        
        // Options row
        val optionsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 15, 0, 0)
            gravity = android.view.Gravity.CENTER
        }
        
        val otherReligionsToggle = Button(this).apply {
            text = if (showOtherReligions) "✓ Other Religions" else "☐ Other Religions"
            setBackgroundColor(if (showOtherReligions) Color.parseColor("#9C27B0") else Color.parseColor("#CCCCCC"))
            setTextColor(Color.WHITE)
            setPadding(20, 10, 20, 10)
            textSize = 12f
            setOnClickListener { toggleOtherReligions() }
        }
        
        val locationButton = Button(this).apply {
            text = "📍 Get Location Times"
            setBackgroundColor(Color.parseColor("#2196F3"))
            setTextColor(Color.WHITE)
            setPadding(20, 10, 20, 10)
            textSize = 12f
            setOnClickListener { requestLocationAndUpdateTimes() }
        }
        
        optionsRow.addView(otherReligionsToggle)
        optionsRow.addView(View(this).apply { layoutParams = LinearLayout.LayoutParams(20, 0) })
        optionsRow.addView(locationButton)
        
        controlsLayout.addView(navRow)
        controlsLayout.addView(optionsRow)
        controlsCard.addView(controlsLayout)
        
        return controlsCard
    }
    
    private fun updateCalendarDisplay() {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthYearText.text = dateFormat.format(currentDate.time)
        
        // Clear existing calendar
        calendarLayout.removeAllViews()
        
        // Add day headers
        val daysHeader = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 0, 0, 10)
        }
        
        val dayNames = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        dayNames.forEach { day ->
            val dayView = TextView(this).apply {
                text = day
                textSize = 12f
                setTextColor(Color.parseColor("#666666"))
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            }
            daysHeader.addView(dayView)
        }
        calendarLayout.addView(daysHeader)
        
        // Generate calendar grid
        val calendar = Calendar.getInstance()
        calendar.time = currentDate.time
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        
        val startDay = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        var dayCounter = 1
        
        // Create weeks
        for (week in 0..5) {
            if (dayCounter > daysInMonth) break
            
            val weekRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 5, 0, 5)
            }
            
            // Create days in week
            for (dayOfWeek in 0..6) {
                val dayView = createDayView(week, dayOfWeek, startDay, dayCounter, daysInMonth)
                weekRow.addView(dayView)
                
                if (week == 0 && dayOfWeek >= startDay) dayCounter++
                else if (week > 0) dayCounter++
            }
            
            calendarLayout.addView(weekRow)
        }
        
        // Update holy days list
        updateHolyDaysList()
    }
    
    private fun createDayView(week: Int, dayOfWeek: Int, startDay: Int, dayCounter: Int, daysInMonth: Int): TextView {
        val dayNum = when {
            week == 0 && dayOfWeek < startDay -> 0
            dayCounter > daysInMonth -> 0
            else -> if (week == 0) dayOfWeek - startDay + 1 else dayCounter - 7 * week + (7 - startDay)
        }
        
        val dayView = TextView(this).apply {
            text = if (dayNum > 0 && dayNum <= daysInMonth) dayNum.toString() else ""
            textSize = 14f
            gravity = android.view.Gravity.CENTER
            setPadding(8, 12, 8, 12)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            
            // Check if this day has a holy day
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                Calendar.getInstance().apply {
                    time = currentDate.time
                    set(Calendar.DAY_OF_MONTH, if (dayNum > 0 && dayNum <= daysInMonth) dayNum else 1)
                }.time
            )
            
            when {
                holydaysData.containsKey(dateStr) -> {
                    setTextColor(Color.WHITE)
                    setBackgroundColor(Color.parseColor("#1976D2"))
                }
                showOtherReligions && otherReligiousHolidays.containsKey(dateStr) -> {
                    setTextColor(Color.WHITE)
                    setBackgroundColor(Color.parseColor("#9C27B0"))
                }
                else -> {
                    setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
                    setBackgroundColor(Color.TRANSPARENT)
                }
            }
            
            // Add click listener to show date details
            if (dayNum > 0 && dayNum <= daysInMonth) {
                setOnClickListener { showDateDetails(dateStr) }
            }
        }
        
        return dayView
    }
    
    private fun showDateDetails(date: String) {
        val bahaiEvent = holydaysData[date]
        
        val displayText = if (bahaiEvent != null) {
            buildString {
                append("📅 ${bahaiEvent.name}\n")
                append("${bahaiEvent.type}")
                if (bahaiEvent.workSuspended) append(" ⭐")
                append("\n\n")
                if (bahaiEvent.description.isNotEmpty()) {
                    append("${bahaiEvent.description}\n\n")
                }
                if (bahaiEvent.time.isNotEmpty()) {
                    append("⏰ Observance: ${bahaiEvent.time}\n\n")
                }
                if (bahaiEvent.workSuspended) {
                    append("🛠️ Work is suspended on this holy day\n\n")
                }
                
                // Special information for Fast period
                when (bahaiEvent.name) {
                    "Fast Begins" -> {
                        append("The Bahá'í Fast is a 19-day period of spiritual preparation leading to Naw-Rúz. ")
                        append("During this time, Bahá'ís aged 15-70 abstain from food and drink from sunrise to sunset. ")
                        append("The Fast is a time for prayer, meditation, and spiritual renewal.\n\n")
                        append("Exemptions include children, elderly, pregnant/nursing mothers, the sick, and travelers.")
                    }
                    "Fast Ends" -> {
                        append("The 19-day Fast concludes this evening at sunset, followed immediately by Naw-Rúz celebrations.")
                    }
                    "Ayyám-i-Há Begins" -> {
                        append("The Intercalary Days (Ayyám-i-Há) are four or five days of celebration, gift-giving, ")
                        append("charity, and preparation for the Fast. These days fall outside the regular 19-day month structure.")
                    }
                    else -> {
                        // Standard holy day information
                        if (bahaiEvent.type == "Major Holy Day") {
                            append("This is one of the nine major Bahá'í holy days. ")
                        }
                        if (bahaiEvent.type == "Feast Day") {
                            append("Nineteen Day Feast - a community gathering for worship, consultation, and fellowship.")
                        }
                    }
                }
            }
        } else {
            val dayOfMonth = try {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)?.let { dateObj ->
                    SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(dateObj)
                }
            } catch (e: Exception) {
                date
            }
            
            "📅 $dayOfMonth\n\nNo Bahá'í observances on this date.\n\nFor the most accurate Bahá'í calendar dates in your location, please consult your Local Spiritual Assembly or official Bahá'í calendar resources."
        }
        
        TextView(this).apply {
            text = displayText
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#212121"))
            setPadding(32, 32, 32, 32)
            setBackgroundColor(if (isDarkMode) Color.parseColor("#121212") else Color.WHITE)
            gravity = android.view.Gravity.START
        }.also { textView ->
            AlertDialog.Builder(this, if (isDarkMode) R.style.DarkDialogTheme else R.style.AppTheme)
                .setTitle("Bahá'í Calendar")
                .setView(textView)
                .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }
    
    private fun updateHolyDaysList() {
        holyDaysList.removeAllViews()
        
        val today = Calendar.getInstance()
        val upcomingDates = holydaysData.entries
            .filter {
                val entryDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.key)
                entryDate?.after(today.time) == true
            }
            .sortedBy { it.key }
            .take(6) // Show next 6 holy days
        
        upcomingDates.forEach { (date, bahaDate) ->
            val holyDayView = createHolyDayView(date, bahaDate)
            holyDaysList.addView(holyDayView)
            holyDaysList.addView(createSpacing(10))
        }
    }
    
    private fun createHolyDayView(dateStr: String, bahaDate: BahaiDate): LinearLayout {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(if (isDarkMode) Color.parseColor("#2E2E2E") else Color.WHITE)
            setPadding(15, 15, 15, 15)
        }
        
        val titleView = TextView(this).apply {
            text = bahaDate.name
            textSize = 16f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
        
        val dateView = TextView(this).apply {
            text = "📅 $dateStr"
            textSize = 12f
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
            setPadding(0, 5, 0, 5)
        }
        
        val detailsView = TextView(this).apply {
            text = buildString {
                if (bahaDate.description.isNotEmpty()) append("${bahaDate.description}\n")
                append("Type: ${bahaDate.type}\n")
                if (bahaDate.timing.isNotEmpty()) {
                    append("Time: ${getLocationBasedTiming(bahaDate, dateStr)}")
                } else {
                    append("Time: ${bahaDate.timing}")
                }
            }.replace("\\n", "\n")
            textSize = 12f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
        }
        
        layout.addView(titleView)
        layout.addView(dateView)
        layout.addView(detailsView)
        
        return layout
    }
    
    private fun changeMonth(delta: Int) {
        currentDate.add(Calendar.MONTH, delta)
        updateCalendarDisplay()
    }
    
    private fun goToToday() {
        currentDate = Calendar.getInstance()
        updateCalendarDisplay()
    }
    
    private fun toggleOtherReligions() {
        showOtherReligions = !showOtherReligions
        updateCalendarDisplay()
        Toast.makeText(this, 
            if (showOtherReligions) "Showing other religious holidays" else "Showing only Bahá'í dates", 
            Toast.LENGTH_SHORT).show()
    }
    
    private fun requestLocationAndUpdateTimes() {
        if (!LocationService.hasLocationPermission(this)) {
            LocationService.requestLocationPermission(this)
        } else {
            showLocationBasedTimes()
        }
    }
    
    private fun showLocationBasedTimes() {
        val sunTimes = LocationService.getSunTimesForLocation(this)
        
        val message = buildString {
            append("🌅 Current Location Times:\n\n")
            append("📍 ${sunTimes.location}\n\n")
            append("🌅 Sunrise: ${sunTimes.sunrise}\n")
            append("🌅 Sunset: ${sunTimes.sunset}\n\n")
            
            // Add Fast information if currently in Fast period
            val calendar = Calendar.getInstance()
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            
            if ((month == 3 && day >= 2 && day <= 20) || (month == 2 && day >= 26)) {
                append("⏰ Fast Period Active:\n")
                append("• Begin fast at sunrise: ${sunTimes.sunrise}\n")
                append("• Break fast at sunset: ${sunTimes.sunset}\n\n")
            }
            
            append("These times will be used for Feast days and holy day observances.")
        }
        
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("📍 Location-Based Times")
            .setMessage(message)
            .setPositiveButton("Update Calendar") { dialog, _ -> 
                updateHolyDaysList() // Refresh with location-based times
                dialog.dismiss() 
            }
            .setNegativeButton("Close") { dialog, _ -> dialog.dismiss() }
            .create()
        
        dialog.show()
    }
    
    private fun getLocationBasedTiming(bahaDate: BahaiDate, dateStr: String): String {
        return when {
            bahaDate.timing.contains("Sunset") -> {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)
                if (date != null && LocationService.hasLocationPermission(this)) {
                    val sunTimes = LocationService.getSunTimesForLocation(this, date)
                    "Sunset ${sunTimes.sunset} (${sunTimes.location})"
                } else {
                    bahaDate.timing
                }
            }
            bahaDate.name.contains("Fast") -> {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)
                if (date != null && LocationService.hasLocationPermission(this)) {
                    val sunTimes = LocationService.getSunTimesForLocation(this, date)
                    "Sunrise: ${sunTimes.sunrise}, Sunset: ${sunTimes.sunset}"
                } else {
                    "Sunrise to Sunset (location required for exact times)"
                }
            }
            else -> bahaDate.timing
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int, 
        permissions: Array<String>, 
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && 
            grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            showLocationBasedTimes()
        }
    }
    
    private fun createSpacing(height: Int): View {
        return View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                height
            )
        }
    }
}

data class BahaiDate(
    val name: String,
    val description: String,
    val type: String,
    val time: String,
    val workSuspended: Boolean = false
)