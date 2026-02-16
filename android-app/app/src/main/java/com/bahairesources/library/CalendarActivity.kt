package com.bahairesources.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.graphics.Color
import androidx.cardview.widget.CardView
import android.view.View
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import java.util.*
import java.text.SimpleDateFormat
import kotlinx.coroutines.*
import android.util.Log

class CalendarActivity : AppCompatActivity() {
    
    private lateinit var monthYearText: TextView
    private lateinit var calendarLayout: LinearLayout
    private lateinit var holyDaysList: LinearLayout
    private var currentDate = Calendar.getInstance()
    private var isDarkMode = false
    private var currentFontSize = SettingsManager.FONT_MEDIUM
    
    // Coroutine scope for API calls
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var isBahaiCalendarMode = false // New: Calendar mode toggle
    
    private lateinit var locationService: LocationService
    
    // Bahá'í Calendar Structure
    private val bahaiMonths = listOf(
        "Bahá" to "Splendor",       // March 21 - April 8
        "Jalál" to "Glory",         // April 9 - April 27
        "Jamál" to "Beauty",        // April 28 - May 16
        "'Aẓamat" to "Grandeur",    // May 17 - June 4
        "Núr" to "Light",           // June 5 - June 23
        "Raḥmat" to "Mercy",        // June 24 - July 12
        "Kalimát" to "Words",       // July 13 - July 31
        "Kamál" to "Perfection",    // August 1 - August 19
        "Asmá'" to "Names",         // August 20 - September 7
        "'Izzat" to "Might",        // September 8 - September 26
        "Mashíyyat" to "Will",      // September 27 - October 15
        "'Ilm" to "Knowledge",      // October 16 - November 3
        "Qudrat" to "Power",        // November 4 - November 22
        "Qawl" to "Speech",         // November 23 - December 11
        "Masá'il" to "Questions",   // December 12 - December 30
        "Sharaf" to "Honor",        // December 31 - January 18
        "Sulṭán" to "Sovereignty",  // January 19 - February 6
        "Mulk" to "Dominion",       // February 7 - February 25
        "Ayyám-i-Há" to "Intercalary Days", // February 26 - March 1 (4 days)
        "'Alá'" to "Loftiness"      // March 2 - March 20 (Fast period)
    )
    
    // Current Bahá'í date calculation variables
    private var currentBahaiYear = 183 // BE 183 (2026 CE)
    private var currentBahaiMonthIndex = 0
    
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
        
        // Initialize current Bahá'í month
        calculateCurrentBahaiMonth()
        
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
        
        val calendarModeToggle = Button(this).apply {
            text = if (isBahaiCalendarMode) "🌙 Bahá'í Calendar" else "📅 Gregorian Calendar"
            setBackgroundColor(if (isBahaiCalendarMode) Color.parseColor("#1976D2") else Color.parseColor("#4CAF50"))
            setTextColor(Color.WHITE)
            setPadding(20, 10, 20, 10)
            textSize = 12f
            setOnClickListener { toggleCalendarMode() }
        }
        
        val locationButton = Button(this).apply {
            text = "📍 Location & Times"
            setBackgroundColor(Color.parseColor("#2196F3"))
            setTextColor(Color.WHITE)
            setPadding(20, 10, 20, 10)
            textSize = 12f
            setOnClickListener { showLocationMenu() }
        }
        
        optionsRow.addView(calendarModeToggle)
        optionsRow.addView(View(this).apply { layoutParams = LinearLayout.LayoutParams(20, 0) })
        optionsRow.addView(locationButton)
        
        controlsLayout.addView(navRow)
        controlsLayout.addView(optionsRow)
        controlsCard.addView(controlsLayout)
        
        return controlsCard
    }
    
    private fun updateCalendarDisplay() {
        // Clear existing calendar
        calendarLayout.removeAllViews()
        
        if (isBahaiCalendarMode) {
            updateBahaiCalendarDisplay()
        } else {
            updateGregorianCalendarDisplay()
        }
        
        // Update holy days list
        updateHolyDaysList()
    }
    
    private fun updateGregorianCalendarDisplay() {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthYearText.text = dateFormat.format(currentDate.time)
        
        // Add day headers with better styling
        val daysHeader = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(8, 8, 8, 16)
            setBackgroundColor(if (isDarkMode) Color.parseColor("#323232") else Color.parseColor("#F5F5F5"))
        }
        
        val dayNames = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        dayNames.forEach { day ->
            val dayView = TextView(this).apply {
                text = day
                textSize = 13f
                setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#424242"))
                gravity = android.view.Gravity.CENTER
                setPadding(4, 8, 4, 8)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            }
            daysHeader.addView(dayView)
        }
        calendarLayout.addView(daysHeader)
        
        // Generate calendar grid with proper logic
        val calendar = Calendar.getInstance()
        calendar.time = currentDate.time
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        
        val startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        // Create calendar grid (6 weeks max)
        var dayNumber = 1
        
        for (week in 0..5) {
            val weekRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(4, 2, 4, 2)
            }
            
            for (dayOfWeek in 0..6) {
                val dayView = TextView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    gravity = android.view.Gravity.CENTER
                    setPadding(8, 16, 8, 16)
                    textSize = 15f
                    minHeight = 80
                    
                    val shouldShowDay = if (week == 0) {
                        dayOfWeek >= startDayOfWeek && dayNumber <= daysInMonth
                    } else {
                        dayNumber <= daysInMonth
                    }
                    
                    if (shouldShowDay) {
                        text = dayNumber.toString()
                        
                        // Create calendar instance for this specific day
                        val dayCalendar = Calendar.getInstance()
                        dayCalendar.time = currentDate.time
                        dayCalendar.set(Calendar.DAY_OF_MONTH, dayNumber)
                        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dayCalendar.time)
                        
                        // Check for holidays and special days
                        val enabledHolidays = ReligiousHolidaysData.getAllEnabledHolidays(this@CalendarActivity)
                        val today = Calendar.getInstance()
                        val isToday = dayCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                                     dayCalendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                        
                        when {
                            isToday -> {
                                setTextColor(Color.WHITE)
                                setBackgroundColor(if (isDarkMode) Color.parseColor("#FF6F00") else Color.parseColor("#FF9800"))
                                background.alpha = 200
                            }
                            holydaysData.containsKey(dateStr) -> {
                                setTextColor(Color.WHITE)
                                setBackgroundColor(if (isDarkMode) Color.parseColor("#1565C0") else Color.parseColor("#1976D2"))
                                background.alpha = 220
                            }
                            enabledHolidays.containsKey(dateStr) -> {
                                setTextColor(Color.WHITE)
                                setBackgroundColor(if (isDarkMode) Color.parseColor("#388E3C") else Color.parseColor("#4CAF50"))
                                background.alpha = 200
                            }
                            isFastingDay(dateStr) != null -> {
                                setTextColor(if (isDarkMode) Color.WHITE else Color.parseColor("#4A148C"))
                                setBackgroundColor(if (isDarkMode) Color.parseColor("#4A148C") else Color.parseColor("#E1BEE7"))
                                background.alpha = 180
                            }
                            else -> {
                                setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
                                setBackgroundColor(Color.TRANSPARENT)
                            }
                        }
                        
                        // Add click listener for day details
                        isClickable = true
                        setOnClickListener { showDateDetails(dateStr) }
                        
                        dayNumber++
                    } else {
                        text = ""
                        setTextColor(Color.TRANSPARENT)
                        setBackgroundColor(Color.TRANSPARENT)
                        isClickable = false
                    }
                }
                
                weekRow.addView(dayView)
            }
            
            calendarLayout.addView(weekRow)
            
            // Break if we've displayed all days
            if (dayNumber > daysInMonth) break
        }
    }
    
    private fun updateBahaiCalendarDisplay() {
        // Display Bahá'í month and year
        val (monthName, monthTranslation) = bahaiMonths[currentBahaiMonthIndex]
        monthYearText.text = "$monthName ($monthTranslation) - BE $currentBahaiYear"
        
        // Add day number headers (1-19 for regular months, fewer for Ayyám-i-Há)
        val daysHeader = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 0, 0, 10)
        }
        
        val daysInBahaiMonth = if (currentBahaiMonthIndex == 18) 4 else 19 // Ayyám-i-Há has 4 days
        
        // Show day numbers in a grid format
        for (i in 1..daysInBahaiMonth) {
            val dayView = TextView(this).apply {
                text = i.toString()
                textSize = 12f
                setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                setPadding(4, 8, 4, 8)
            }
            daysHeader.addView(dayView)
        }
        calendarLayout.addView(daysHeader)
        
        // Create Bahá'í calendar grid (show days 1-19 in rows)
        var dayCounter = 1
        val daysPerRow = 7 // Keep 7 days per row for consistency
        
        while (dayCounter <= daysInBahaiMonth) {
            val weekRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 5, 0, 5)
            }
            
            // Create up to 7 days per row
            for (dayOfWeek in 0 until daysPerRow) {
                val dayView = if (dayCounter <= daysInBahaiMonth) {
                    createBahaiDayView(dayCounter)
                } else {
                    // Empty view for remaining slots
                    TextView(this).apply {
                        text = ""
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                        setPadding(8, 12, 8, 12)
                    }
                }
                weekRow.addView(dayView)
                dayCounter++
            }
            
            calendarLayout.addView(weekRow)
        }
    }
    
    private fun createBahaiDayView(dayNum: Int): TextView {
        val dayView = TextView(this).apply {
            text = dayNum.toString()
            textSize = 14f
            gravity = android.view.Gravity.CENTER
            setPadding(8, 12, 8, 12)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            
            // Calculate the Gregorian date for this Bahá'í day
            val gregorianDate = getBahaiDayGregorianDate(currentBahaiMonthIndex, dayNum)
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(gregorianDate)
            
            val enabledHolidays = ReligiousHolidaysData.getAllEnabledHolidays(this@CalendarActivity)
            
            when {
                holydaysData.containsKey(dateStr) -> {
                    setTextColor(Color.WHITE)
                    setBackgroundColor(Color.parseColor("#1976D2")) // Bahá'í holidays in blue
                }
                enabledHolidays.containsKey(dateStr) -> {
                    val holiday = enabledHolidays[dateStr]!!
                    val religionColors = ReligiousHolidaysData.getReligionColors()
                    val holidayColor = religionColors[holiday.religion] ?: "#9C27B0"
                    setTextColor(Color.WHITE)
                    setBackgroundColor(Color.parseColor(holidayColor))
                }
                else -> {
                    setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#333333"))
                    setBackgroundColor(Color.TRANSPARENT)
                }
            }
            
            // Add click listener to show date details
            setOnClickListener { showDateDetails(dateStr) }
        }
        
        return dayView
    }
    
    private fun getBahaiDayGregorianDate(monthIndex: Int, day: Int): Date {
        val calendar = Calendar.getInstance()
        
        // Calculate the starting Gregorian date for each Bahá'í month
        when (monthIndex) {
            0 -> calendar.set(2026, 2, 21 + day - 1)  // Bahá: March 21-April 8
            1 -> calendar.set(2026, 3, 9 + day - 1)   // Jalál: April 9-27
            2 -> if (day <= 13) calendar.set(2026, 3, 28 + day - 1) else calendar.set(2026, 4, day - 13)  // Jamál: April 28-May 16
            3 -> if (day <= 15) calendar.set(2026, 4, 17 + day - 1) else calendar.set(2026, 5, day - 15)  // 'Aẓamat: May 17-June 4
            4 -> calendar.set(2026, 5, 5 + day - 1)   // Núr: June 5-23
            5 -> if (day <= 7) calendar.set(2026, 5, 24 + day - 1) else calendar.set(2026, 6, day - 7)   // Raḥmat: June 24-July 12
            6 -> calendar.set(2026, 6, 13 + day - 1)  // Kalimát: July 13-31
            7 -> calendar.set(2026, 7, 1 + day - 1)   // Kamál: August 1-19
            8 -> if (day <= 12) calendar.set(2026, 7, 20 + day - 1) else calendar.set(2026, 8, day - 12)  // Asmá': August 20-September 7
            9 -> if (day <= 11) calendar.set(2026, 8, 8 + day - 1) else calendar.set(2026, 8, 27 + day - 11) // 'Izzat: September 8-26
            10 -> if (day <= 4) calendar.set(2026, 8, 27 + day - 1) else calendar.set(2026, 9, day - 4)   // Mashíyyat: September 27-October 15
            11 -> if (day <= 16) calendar.set(2026, 9, 16 + day - 1) else calendar.set(2026, 10, day - 16) // 'Ilm: October 16-November 3
            12 -> calendar.set(2026, 10, 4 + day - 1)  // Qudrat: November 4-22
            13 -> if (day <= 8) calendar.set(2026, 10, 23 + day - 1) else calendar.set(2026, 11, day - 8)  // Qawl: November 23-December 11
            14 -> calendar.set(2026, 11, 12 + day - 1) // Masá'il: December 12-30
            15 -> if (day <= 1) calendar.set(2026, 11, 31 + day - 1) else calendar.set(2027, 0, day - 1) // Sharaf: December 31-January 18
            16 -> if (day <= 13) calendar.set(2027, 0, 19 + day - 1) else calendar.set(2027, 1, day - 13) // Sulṭán: January 19-February 6
            17 -> calendar.set(2027, 1, 7 + day - 1)   // Mulk: February 7-25
            18 -> calendar.set(2026, 1, 26 + day - 1)  // Ayyám-i-Há: February 26-March 1 (4 days)
            19 -> calendar.set(2026, 2, 2 + day - 1)   // 'Alá': March 2-20
            else -> calendar.set(2026, 2, 21)          // Default to Naw-Rúz
        }
        
        return calendar.time
    }
    
    private fun showDateDetails(date: String) {
        val bahaiEvent = holydaysData[date]
        val enabledHolidays = ReligiousHolidaysData.getAllEnabledHolidays(this)
        val religiousHoliday = enabledHolidays[date]
        
        val displayText = when {
            bahaiEvent != null -> {
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
                    
                    // Check if this date is during the Fast period (March 2-20, 2026)
                    val fastPeriod = isFastingDay(date)
                    if (fastPeriod != null) {
                        val sunTimes = LocationService.getSunTimesForLocation(this@CalendarActivity)
                        append("\n⏰ Fast Times for Today:\n")
                        append("🌅 Begin Fast: ${sunTimes.sunrise}\n")
                        append("🌆 Break Fast: ${sunTimes.sunset}\n\n")
                        
                        when (fastPeriod) {
                            "first" -> {
                                append("📿 First Day of the Fast:\n")
                                append("Today begins the 19-day Bahá'í Fast, a period of spiritual preparation leading to Naw-Rúz. ")
                                append("During this time, Bahá'ís aged 15-70 abstain from food and drink from sunrise to sunset.\n\n")
                                append("🙏 The Fast is a time for prayer, meditation, and spiritual renewal.\n\n")
                                append("Exemptions include children, elderly, pregnant/nursing mothers, the sick, and travelers.")
                            }
                            "last" -> {
                                append("📿 Final Day of the Fast:\n")
                                append("The 19-day Fast concludes this evening at sunset, followed immediately by Naw-Rúz celebrations.\n\n")
                                append("🎉 After sunset tonight, the new Bahá'í year begins!")
                            }
                            "active" -> {
                                val fastDayNumber = getFastDayNumber(date)
                                append("📿 Day $fastDayNumber of the 19-Day Fast:\n")
                                append("Continue the spiritual discipline of fasting from sunrise to sunset. ")
                                append("Use this time for prayer, meditation, and spiritual reflection.\n\n")
                                append("🌱 The Fast brings spiritual purification and preparation for the new year.")
                            }
                        }
                    }
                    
                    // Special information for specific holy days
                    when (bahaiEvent.name) {
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
                    
                    // Add religious holiday if present on same date
                    if (religiousHoliday != null) {
                        append("\n\n━━━━━━━━━━━━━━━━━━━━\n\n")
                        append("🌍 ${religiousHoliday.name}\n")
                        append("${religiousHoliday.religion} ${religiousHoliday.type}\n\n")
                        append("${religiousHoliday.description}")
                    }
                }
            }
            religiousHoliday != null -> {
                buildString {
                    append("🌍 ${religiousHoliday.name}\n")
                    append("${religiousHoliday.religion} ${religiousHoliday.type}\n\n")
                    append("${religiousHoliday.description}\n\n")
                    
                    // Check if this date is during the Fast period
                    val fastPeriod = isFastingDay(date)
                    if (fastPeriod != null) {
                        append("││││││││││││││││││││\n\n")
                        val sunTimes = LocationService.getSunTimesForLocation(this@CalendarActivity)
                        append("⏰ Fast Times for Today:\n")
                    append("Sunrise: ${sunTimes.sunrise}\n")
                    append("Sunset: ${sunTimes.sunset}\n\n")
                        when (fastPeriod) {
                            "first" -> append("📿 First Day of the 19-Day Fast begins today.")
                            "last" -> append("📿 Final Day of the Fast - Naw-Rúz begins at sunset!")
                            "active" -> {
                                val fastDayNumber = getFastDayNumber(date)
                                append("📿 Day $fastDayNumber of the 19-Day Fast continues.")
                            }
                        }
                        append("\n\n")
                    }
                    
                    append("Note: You can adjust which religious holidays are shown in Settings > Religious Holidays Display.")
                }
            }
            else -> {
                val dayOfMonth = try {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)?.let { dateObj ->
                        SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(dateObj)
                    }
                } catch (e: Exception) {
                    date
                }
                
                buildString {
                    append("📅 $dayOfMonth\n\n")
                    
                    // Check if this date is during the Fast period
                    val fastPeriod = isFastingDay(date)
                    if (fastPeriod != null) {
                        val sunTimes = LocationService.getSunTimesForLocation(this@CalendarActivity)
                        append("⏰ Fast Times for Today:\n")
                        append("Sunrise: ${sunTimes.sunrise}\n")
                        append("Sunset: ${sunTimes.sunset}\n\n")
                        
                        when (fastPeriod) {
                            "first" -> {
                                append("📿 First Day of the Fast:\n")
                                append("Today begins the 19-day Bahá'í Fast, a period of spiritual preparation leading to Naw-Rúz.\n\n")
                            }
                            "last" -> {
                                append("📿 Final Day of the Fast:\n")
                                append("The Fast concludes at sunset tonight, followed by Naw-Rúz celebrations!\n\n")
                            }
                            "active" -> {
                                val fastDayNumber = getFastDayNumber(date)
                                append("📿 Day $fastDayNumber of the 19-Day Fast:\n")
                                append("Continue the spiritual discipline of fasting from sunrise to sunset.\n\n")
                            }
                        }
                    } else {
                        append("No Bahá'í observances on this date.\n\n")
                    }
                    
                    val enabledReligionsCount = ReligiousHolidaysData.getEnabledReligionsCount(this@CalendarActivity)
                    if (enabledReligionsCount == 0 && fastPeriod == null) {
                        append("\n💡 Tip: You can enable holidays from other religions in Settings > Religious Holidays Display to see a more complete calendar view.")
                    }
                    
                    append("\n\nFor the most accurate Bahá'í calendar dates in your location, please consult your Local Spiritual Assembly or official Bahá'í calendar resources.")
                }
            }
        }
        
        TextView(this).apply {
            text = displayText
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#212121"))
            setPadding(32, 32, 32, 32)
            setBackgroundColor(if (isDarkMode) Color.parseColor("#121212") else Color.WHITE)
            gravity = android.view.Gravity.START
        }.also { textView ->
            AlertDialog.Builder(this)
                .setTitle("Calendar Details")
                .setView(textView)
                .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
                .setNeutralButton("Settings") { _, _ ->
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
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
                if (bahaDate.time.isNotEmpty()) {
                    append("Time: ${getLocationBasedTiming(bahaDate, dateStr)}")
                } else {
                    append("Time: ${bahaDate.time}")
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
        if (isBahaiCalendarMode) {
            // Navigate Bahá'í months
            currentBahaiMonthIndex += delta
            if (currentBahaiMonthIndex < 0) {
                currentBahaiMonthIndex = 19 // Wrap to last month (19th month)
                currentBahaiYear--
            } else if (currentBahaiMonthIndex > 19) {
                currentBahaiMonthIndex = 0  // Wrap to first month
                currentBahaiYear++
            }
        } else {
            // Navigate Gregorian months
            currentDate.add(Calendar.MONTH, delta)
        }
        updateCalendarDisplay()
    }
    
    private fun goToToday() {
        currentDate = Calendar.getInstance()
        if (isBahaiCalendarMode) {
            calculateCurrentBahaiMonth()
        }
        updateCalendarDisplay()
    }
    
    private fun toggleCalendarMode() {
        isBahaiCalendarMode = !isBahaiCalendarMode
        if (isBahaiCalendarMode) {
            // Switch to current Bahá'í month when switching to Bahá'í mode
            calculateCurrentBahaiMonth()
        }
        updateCalendarDisplay()
        // Reduced notification - only show mode without extra details
        Toast.makeText(this, 
            if (isBahaiCalendarMode) "Bahá'í Calendar" else "Gregorian Calendar", 
            Toast.LENGTH_SHORT).show()
    }
    
    private fun calculateCurrentBahaiMonth() {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1 // 1-based month
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        // Calculate current Bahá'í month based on Gregorian date
        // Naw-Rúz (Bahá'í New Year) typically falls on March 21
        currentBahaiMonthIndex = when {
            month == 3 && day >= 21 -> 0  // Bahá
            month == 4 && day <= 8 -> 0   // Bahá continues
            month == 4 && day >= 9 && day <= 27 -> 1  // Jalál
            month == 4 && day >= 28 -> 2  // Jamál
            month == 5 && day <= 16 -> 2  // Jamál continues
            month == 5 && day >= 17 -> 3  // 'Aẓamat
            month == 6 && day <= 4 -> 3   // 'Aẓamat continues
            month == 6 && day >= 5 && day <= 23 -> 4  // Núr
            month == 6 && day >= 24 -> 5  // Raḥmat
            month == 7 && day <= 12 -> 5  // Raḥmat continues
            month == 7 && day >= 13 && day <= 31 -> 6  // Kalimát
            month == 8 && day >= 1 && day <= 19 -> 7  // Kamál
            month == 8 && day >= 20 -> 8  // Asmá'
            month == 9 && day <= 7 -> 8   // Asmá' continues
            month == 9 && day >= 8 && day <= 26 -> 9  // 'Izzat
            month == 9 && day >= 27 -> 10 // Mashíyyat
            month == 10 && day <= 15 -> 10 // Mashíyyat continues
            month == 10 && day >= 16 -> 11 // 'Ilm
            month == 11 && day <= 3 -> 11  // 'Ilm continues
            month == 11 && day >= 4 && day <= 22 -> 12 // Qudrat
            month == 11 && day >= 23 -> 13 // Qawl
            month == 12 && day <= 11 -> 13 // Qawl continues
            month == 12 && day >= 12 && day <= 30 -> 14 // Masá'il
            month == 12 && day >= 31 -> 15 // Sharaf
            month == 1 && day <= 18 -> 15  // Sharaf continues
            month == 1 && day >= 19 -> 16  // Sulṭán
            month == 2 && day <= 6 -> 16   // Sulṭán continues
            month == 2 && day >= 7 && day <= 25 -> 17 // Mulk
            month == 2 && day >= 26 -> 18  // Ayyám-i-Há
            month == 3 && day <= 1 -> 18   // Ayyám-i-Há continues
            month == 3 && day >= 2 && day <= 20 -> 19 // 'Alá' (Fast)
            else -> 0 // Default to first month
        }
    }
    
    private fun showLocationMenu() {
        val locationInfo = LocationService.getCurrentLocationInfo(this)
        
        val currentLocation = if (locationInfo != null) {
            "${locationInfo.cityName} ${if (locationInfo.isManual) "(Manual)" else "(Auto-detected)"}"
        } else {
            "No location detected"
        }
        
        val options = arrayOf(
            "📍 Current Location: $currentLocation",
            "🔄 Auto-detect Location",
            "✏️ Set Manual Location",
            "🌍 Choose from City List",
            "🌅 Show Sunrise/Sunset Times"
        )
        
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("📍 Location & Prayer Times")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showCurrentLocationDetails()
                    1 -> autoDetectLocation()
                    2 -> showManualLocationDialog()
                    3 -> showCitySelectionDialog()
                    4 -> showLocationBasedTimes()
                }
            }
            .setNegativeButton("Close") { dialog, _ -> dialog.dismiss() }
            .create()
        
        dialog.show()
    }
    
    private fun showCurrentLocationDetails() {
        val locationInfo = LocationService.getCurrentLocationInfo(this)
        
        if (locationInfo != null) {
            val sunTimes = LocationService.calculateSunTimes(locationInfo.latitude, locationInfo.longitude)
            
            val message = buildString {
                append("📍 Current Location:\n")
                append("${locationInfo.cityName}\n")
                append("${if (locationInfo.isManual) "(Manually set)" else "(Auto-detected)"}\n\n")
                append("📍 Coordinates:\n")
                append("Latitude: ${String.format("%.4f", locationInfo.latitude)}\n")
                append("Longitude: ${String.format("%.4f", locationInfo.longitude)}\n\n")
                append("🌅 Today's Times:\n")
                append("Sunrise: ${sunTimes.sunrise}\n")
                append("Sunset: ${sunTimes.sunset}\n\n")
                if (locationInfo.isManual) {
                    append("Tap 'Reset to Auto' to use GPS location detection.")
                } else {
                    append("Tap 'Set Manual' to override with a custom location.")
                }
            }
            
            val dialog = android.app.AlertDialog.Builder(this)
                .setTitle("📍 Location Details")
                .setMessage(message)
                .setPositiveButton(if (locationInfo.isManual) "Reset to Auto" else "Set Manual") { _, _ ->
                    if (locationInfo.isManual) {
                        LocationService.clearManualLocation(this)
                        updateCalendarDisplay()
                        Toast.makeText(this, "Switched to auto-detection", Toast.LENGTH_SHORT).show()
                    } else {
                        showManualLocationDialog()
                    }
                }
                .setNegativeButton("Close") { dialog, _ -> dialog.dismiss() }
                .create()
            
            dialog.show()
        } else {
            Toast.makeText(this, "No location available. Please enable location services or set manually.", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun autoDetectLocation() {
        if (!LocationService.hasLocationPermission(this)) {
            LocationService.requestLocationPermission(this)
        } else {
            val location = LocationService.getCurrentLocation(this)
            if (location != null) {
                val cityName = LocationService.getCityNameFromCoordinates(this, location.latitude, location.longitude)
                LocationService.clearManualLocation(this) // Clear any manual override
                updateCalendarDisplay()
                Toast.makeText(this, "Location set to: $cityName", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Unable to detect location. Please check GPS settings.", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun showManualLocationDialog() {
        val input = android.widget.EditText(this).apply {
            hint = "Enter city name (e.g., Paris, Tokyo, New York)"
            setPadding(50, 20, 50, 20)
        }
        
        // Add some common search suggestions
        val suggestionsText = TextView(this).apply {
            text = "💡 Try: Tokyo, London, Sydney, Cairo, Mumbai, São Paulo, etc."
            textSize = 12f
            setTextColor(if (isDarkMode) Color.parseColor("#888888") else Color.parseColor("#666666"))
            setPadding(50, 10, 50, 10)
            setTypeface(typeface, android.graphics.Typeface.ITALIC)
        }
        
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(input)
            addView(suggestionsText)
        }
        
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("✏️ Set Manual Location")
            .setMessage("Enter your city name for accurate sunrise/sunset times.\nThe search will find the best match:")
            .setView(container)
            .setPositiveButton("Search & Set") { _, _ ->
                val cityName = input.text.toString().trim()
                if (cityName.isNotEmpty()) {
                    searchAndSetLocation(cityName)
                }
            }
            .setNeutralButton("Quick Search") { _, _ ->
                val cityName = input.text.toString().trim()
                if (cityName.isNotEmpty()) {
                    showQuickSearchResults(cityName)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
        
        dialog.show()
    }
    
    private fun searchAndSetLocation(cityName: String) {
        // Searching silently
        
        try {
            val geocoder = android.location.Geocoder(this, Locale.getDefault())
            // Try multiple variations of the city name for better results
            val searchQueries = listOf(
                cityName,
                "$cityName, ${getCountryFromInput(cityName)}",
                cityName.split(",")[0].trim() // Just city name without country
            )
            
            var addresses: List<android.location.Address>? = null
            
            // Try each search variation until we get results
            for (query in searchQueries) {
                try {
                    addresses = geocoder.getFromLocationName(query, 5)
                    if (!addresses.isNullOrEmpty()) break
                } catch (e: Exception) {
                    continue
                }
            }
            
            if (!addresses.isNullOrEmpty()) {
                // Use the first (best) result
                val address = addresses[0]
                val fullCityName = LocationService.getCityNameFromCoordinates(this, address.latitude, address.longitude)
                LocationService.saveManualLocation(this, fullCityName, address.latitude, address.longitude)
                updateCalendarDisplay()
                // Location updated silently
            } else {
                // Search failed, show quick search options
                showQuickSearchResults(cityName)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Search error. Showing quick options...", Toast.LENGTH_SHORT).show()
            showQuickSearchResults(cityName)
        }
    }
    
    private fun showQuickSearchResults(searchQuery: String) {
        val worldCities = listOf(
            "New York, USA" to Pair(40.7128, -74.0060),
            "Los Angeles, USA" to Pair(34.0522, -118.2437),
            "Chicago, USA" to Pair(41.8781, -87.6298),
            "London, UK" to Pair(51.5074, -0.1278),
            "Paris, France" to Pair(48.8566, 2.3522),
            "Berlin, Germany" to Pair(52.5200, 13.4050),
            "Tokyo, Japan" to Pair(35.6762, 139.6503),
            "Sydney, Australia" to Pair(-33.8688, 151.2093),
            "Toronto, Canada" to Pair(43.6532, -79.3832),
            "Mumbai, India" to Pair(19.0760, 72.8777),
            "Cairo, Egypt" to Pair(30.0444, 31.2357),
            "São Paulo, Brazil" to Pair(-23.5505, -46.6333),
            "Mexico City, Mexico" to Pair(19.4326, -99.1332),
            "Istanbul, Turkey" to Pair(41.0082, 28.9784),
            "Moscow, Russia" to Pair(55.7558, 37.6176),
            "Try search again..." to Pair(0.0, 0.0)
        )
        
        // Filter cities that match the search query (fuzzy matching)
        val filteredCities = worldCities.filter { (cityName, _) ->
            cityName.contains(searchQuery, ignoreCase = true) ||
                    searchQuery.contains(cityName.split(",")[0], ignoreCase = true) ||
                    cityName.split(",")[0].contains(searchQuery, ignoreCase = true)
        }.ifEmpty { worldCities } // If no matches, show all options
        
        val cityNames = filteredCities.map { it.first }.toTypedArray()
        
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("🔍 Quick Location Search")
            .setMessage("\"$searchQuery\" not found. Choose from these options:")
            .setItems(cityNames) { _, which ->
                val selectedCity = filteredCities[which]
                if (selectedCity.first == "Try search again...") {
                    showManualLocationDialog()
                } else {
                    val (cityName, coords) = selectedCity
                    LocationService.saveManualLocation(this, cityName, coords.first, coords.second)
                    updateCalendarDisplay()
                    // Location updated silently
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
        
        dialog.show()
    }
    
    private fun getCountryFromInput(input: String): String {
        return when {
            input.contains("usa", ignoreCase = true) || input.contains("america", ignoreCase = true) -> "USA"
            input.contains("uk", ignoreCase = true) || input.contains("britain", ignoreCase = true) -> "UK"
            input.contains("canada", ignoreCase = true) -> "Canada"
            input.contains("australia", ignoreCase = true) -> "Australia"
            input.contains("france", ignoreCase = true) -> "France"
            input.contains("germany", ignoreCase = true) -> "Germany"
            input.contains("japan", ignoreCase = true) -> "Japan"
            input.contains("india", ignoreCase = true) -> "India"
            else -> ""
        }
    }
    
    private fun searchForCity(cityName: String) {
        try {
            val geocoder = android.location.Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocationName(cityName, 5)
            
            if (!addresses.isNullOrEmpty()) {
                if (addresses.size == 1) {
                    // Only one result, use it directly
                    val address = addresses[0]
                    val fullCityName = LocationService.getCityNameFromCoordinates(this, address.latitude, address.longitude)
                    LocationService.saveManualLocation(this, fullCityName, address.latitude, address.longitude)
                    updateCalendarDisplay()
                    // Location updated silently
                } else {
                    // Multiple results, let user choose
                    showCityChoiceDialog(addresses)
                }
            } else {
                Toast.makeText(this, "City not found. Please try a different name.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error searching for city. Please try again.", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun showCityChoiceDialog(addresses: List<android.location.Address>) {
        val cityOptions = addresses.map { address ->
            LocationService.getCityNameFromCoordinates(this, address.latitude, address.longitude)
        }.toTypedArray()
        
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Choose Your City")
            .setItems(cityOptions) { _, which ->
                val selectedAddress = addresses[which]
                val cityName = cityOptions[which]
                LocationService.saveManualLocation(this, cityName, selectedAddress.latitude, selectedAddress.longitude)
                updateCalendarDisplay()
                Toast.makeText(this, "Location set to: $cityName", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
        
        dialog.show()
    }
    
    private fun showCitySelectionDialog() {
        val majorCities = listOf(
            "New York, NY" to Pair(40.7128, -74.0060),
            "Los Angeles, CA" to Pair(34.0522, -118.2437),
            "Chicago, IL" to Pair(41.8781, -87.6298),
            "Houston, TX" to Pair(29.7604, -95.3698),
            "Phoenix, AZ" to Pair(33.4484, -112.0740),
            "Philadelphia, PA" to Pair(39.9526, -75.1652),
            "San Antonio, TX" to Pair(29.4241, -98.4936),
            "San Diego, CA" to Pair(32.7157, -117.1611),
            "Dallas, TX" to Pair(32.7767, -96.7970),
            "San Jose, CA" to Pair(37.3382, -121.8863),
            "Toronto, Canada" to Pair(43.6532, -79.3832),
            "Vancouver, Canada" to Pair(49.2827, -123.1207),
            "London, UK" to Pair(51.5074, -0.1278),
            "Paris, France" to Pair(48.8566, 2.3522),
            "Sydney, Australia" to Pair(-33.8688, 151.2093),
            "Other..." to Pair(0.0, 0.0)
        )
        
        val cityNames = majorCities.map { it.first }.toTypedArray()
        
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("🌍 Choose Your City")
            .setItems(cityNames) { _, which ->
                if (which == majorCities.size - 1) {
                    // "Other..." was selected
                    showManualLocationDialog()
                } else {
                    val (cityName, coords) = majorCities[which]
                    LocationService.saveManualLocation(this, cityName, coords.first, coords.second)
                    updateCalendarDisplay()
                    // Location updated silently
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
        
        dialog.show()
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
        val locationInfo = LocationService.getCurrentLocationInfo(this)
        
        val message = buildString {
            append("🌅 Prayer & Fast Times:\n\n")
            append("📍 Location: ${sunTimes.location}\n\n")
            
            if (locationInfo != null) {
                append("📍 Coordinates: ${String.format("%.4f", locationInfo.latitude)}, ${String.format("%.4f", locationInfo.longitude)}\n\n")
            }
            
            append("Sunrise: ${sunTimes.sunrise}\n")
            append("Sunset: ${sunTimes.sunset}\n\n")
            
            // Add Fast information if currently in Fast period
            val calendar = Calendar.getInstance()
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            
            if ((month == 3 && day >= 2 && day <= 20) || (month == 2 && day >= 26)) {
                append("⏰ Fast Period Active:\n")
                append("• Begin fast at sunrise: ${sunTimes.sunrise}\n")
                append("• Break fast at sunset: ${sunTimes.sunset}\n\n")
                append("The Bahá'í Fast is observed from sunrise to sunset during the month of 'Alá' (March 2-20).\n\n")
            }
            
            append("These times are calculated for your specific location and will be used for Feast days and holy day observances.\n\n")
            
            if (locationInfo?.isManual == true) {
                append("💡 You're using a manually set location. Tap 'Change Location' to modify it.")
            } else {
                append("💡 Location auto-detected from GPS. Tap 'Change Location' to set manually.")
            }
        }
        
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("📍 Location-Based Times")
            .setMessage(message)
            .setPositiveButton("Change Location") { _, _ -> 
                showLocationMenu()
            }
            .setNeutralButton("Update Calendar") { _, _ -> 
                updateHolyDaysList() // Refresh with location-based times
                Toast.makeText(this, "Calendar updated with current location times", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Close") { dialog, _ -> dialog.dismiss() }
            .create()
        
        dialog.show()
    }
    
    private fun getLocationBasedTiming(bahaDate: BahaiDate, dateStr: String): String {
        return when {
            bahaDate.time.contains("Sunset") -> {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)
                if (date != null && LocationService.hasLocationPermission(this)) {
                    // Use enhanced sun times with API integration
                    loadAccurateSunTimes(date) { detailedSunInfo ->
                        "Sunset ${detailedSunInfo.sunset} (${detailedSunInfo.location})\nSource: ${detailedSunInfo.source}"
                    }
                    "Loading accurate sunset time..." // Placeholder while loading
                } else {
                    bahaDate.time
                }
            }
            bahaDate.name.contains("Fast") -> {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)
                if (date != null && LocationService.hasLocationPermission(this)) {
                    // Use enhanced sun times with API integration
                    loadAccurateSunTimes(date) { detailedSunInfo ->
                        "🌅 Sunrise: ${detailedSunInfo.sunrise} | 🌅 Sunset: ${detailedSunInfo.sunset}\n📍 ${detailedSunInfo.location} (${detailedSunInfo.accuracy} accuracy)"
                    }
                    "Loading accurate fast times..." // Placeholder while loading
                } else {
                    "Sunrise to Sunset (location required for exact times)"
                }
            }
            else -> bahaDate.time
        }
    }
    
    /**
     * Load accurate sun times using the new API with coroutines
     */
    private fun loadAccurateSunTimes(date: Date, callback: (LocationService.DetailedSunInfo) -> String) {
        coroutineScope.launch {
            try {
                val locationInfo = LocationService.getCurrentLocationInfo(this@CalendarActivity)
                if (locationInfo != null) {
                    val detailedSunInfo = LocationService.getAccurateSunTimes(
                        this@CalendarActivity, 
                        locationInfo.latitude, 
                        locationInfo.longitude, 
                        date
                    )
                    
                    // Update UI with accurate data - no notification spam
                    runOnUiThread {
                        updateCalendarDisplay() // Refresh calendar with new data
                        // Removed excessive API update notifications
                    }
                } else {
                    Log.w("CalendarActivity", "No location available for accurate sun times")
                }
            } catch (e: Exception) {
                Log.e("CalendarActivity", "Failed to load accurate sun times", e)
                // Remove excessive network failure notifications
                runOnUiThread {
                    // Using calculated times silently - no notification spam
                }
            }
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
    
    private fun isFastingDay(date: String): String? {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val inputDate = sdf.parse(date) ?: return null
            val calendar = Calendar.getInstance().apply { time = inputDate }
            
            val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-based
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            
            when {
                month == 3 && day >= 2 && day <= 20 -> {
                    when (day) {
                        2 -> "first"
                        20 -> "last"
                        else -> "active"
                    }
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getFastDayNumber(date: String): Int {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val inputDate = sdf.parse(date) ?: return 1
            val calendar = Calendar.getInstance().apply { time = inputDate }
            
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            
            // Fast starts March 2, so March 2 = Day 1, March 3 = Day 2, etc.
            if (calendar.get(Calendar.MONTH) + 1 == 3 && day >= 2 && day <= 20) {
                day - 1 // March 2 becomes Day 1
            } else {
                1
            }
        } catch (e: Exception) {
            1
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // Clean up coroutines
    }
}

data class BahaiDate(
    val name: String,
    val description: String,
    val type: String,
    val time: String,
    val workSuspended: Boolean = false
)