package com.bahairesources.library

import android.content.Context

object ReligiousHolidaysData {
    
    data class Holiday(
        val name: String,
        val description: String,
        val religion: String,
        val type: String = "Religious Holiday"
    )
    
    // Christian Holidays (2026)
    private val christianHolidays = mapOf(
        "2026-01-06" to Holiday("Epiphany", "Celebration of the visit of the Magi to Jesus", "Christianity"),
        "2026-02-17" to Holiday("Ash Wednesday", "Beginning of Lent", "Christianity"),
        "2026-03-29" to Holiday("Palm Sunday", "Jesus' triumphal entry into Jerusalem", "Christianity"),
        "2026-04-02" to Holiday("Maundy Thursday", "Last Supper commemoration", "Christianity"),
        "2026-04-03" to Holiday("Good Friday", "Crucifixion of Jesus Christ", "Christianity"),
        "2026-04-05" to Holiday("Easter Sunday", "Resurrection of Jesus Christ", "Christianity"),
        "2026-05-14" to Holiday("Ascension Day", "Ascension of Jesus to Heaven", "Christianity"),
        "2026-05-24" to Holiday("Pentecost", "Descent of the Holy Spirit", "Christianity"),
        "2026-12-25" to Holiday("Christmas Day", "Birth of Jesus Christ", "Christianity"),
        "2026-12-26" to Holiday("Boxing Day", "St. Stephen's Day", "Christianity")
    )
    
    // Islamic Holidays (2026 - approximate lunar dates)
    private val islamicHolidays = mapOf(
        "2026-01-05" to Holiday("Mawlid an-Nabi", "Birthday of Prophet Muhammad", "Islam"),
        "2026-03-11" to Holiday("Ramadan Begins", "Start of holy month of fasting", "Islam"),
        "2026-04-09" to Holiday("Eid al-Fitr", "End of Ramadan celebration", "Islam"),
        "2026-06-16" to Holiday("Eid al-Adha", "Festival of Sacrifice", "Islam"),
        "2026-07-07" to Holiday("Islamic New Year", "First day of Muharram", "Islam"),
        "2026-07-16" to Holiday("Day of Ashura", "Martyrdom of Hussein", "Islam"),
        "2026-09-14" to Holiday("Mawlid an-Nabi", "Birthday of Prophet Muhammad", "Islam")
    )
    
    // Jewish Holidays (2026)
    private val jewishHolidays = mapOf(
        "2026-03-13" to Holiday("Purim", "Celebration of Esther saving the Jews", "Judaism"),
        "2026-04-13" to Holiday("Passover Begins", "Exodus from Egypt", "Judaism"),
        "2026-04-20" to Holiday("Passover Ends", "End of Passover week", "Judaism"),
        "2026-05-03" to Holiday("Yom HaShoah", "Holocaust Remembrance Day", "Judaism"),
        "2026-05-25" to Holiday("Shavuot", "Festival of Weeks", "Judaism"),
        "2026-09-15" to Holiday("Rosh Hashanah", "Jewish New Year", "Judaism"),
        "2026-09-24" to Holiday("Yom Kippur", "Day of Atonement", "Judaism"),
        "2026-09-29" to Holiday("Sukkot Begins", "Festival of Tabernacles", "Judaism"),
        "2026-10-06" to Holiday("Simchat Torah", "Rejoicing with the Torah", "Judaism"),
        "2026-12-16" to Holiday("Hanukkah Begins", "Festival of Lights", "Judaism")
    )
    
    // Hindu Holidays (2026)
    private val hinduHolidays = mapOf(
        "2026-01-14" to Holiday("Makar Sankranti", "Harvest festival", "Hinduism"),
        "2026-03-13" to Holiday("Holi", "Festival of Colors", "Hinduism"),
        "2026-04-02" to Holiday("Ram Navami", "Birthday of Lord Rama", "Hinduism"),
        "2026-08-31" to Holiday("Janmashtami", "Birthday of Lord Krishna", "Hinduism"),
        "2026-09-05" to Holiday("Ganesh Chaturthi", "Birthday of Lord Ganesha", "Hinduism"),
        "2026-10-17" to Holiday("Navaratri Begins", "Nine nights of the Goddess", "Hinduism"),
        "2026-10-25" to Holiday("Dussehra", "Victory of good over evil", "Hinduism"),
        "2026-11-14" to Holiday("Diwali", "Festival of Lights", "Hinduism"),
        "2026-11-15" to Holiday("Govardhan Puja", "Mountain lifting celebration", "Hinduism")
    )
    
    // Buddhist Holidays (2026)
    private val buddhistHolidays = mapOf(
        "2026-02-11" to Holiday("Chinese New Year", "Lunar New Year (Buddhist traditions)", "Buddhism"),
        "2026-05-11" to Holiday("Vesak Day", "Buddha's Birth, Enlightenment & Death", "Buddhism"),
        "2026-07-19" to Holiday("Dharma Day", "First teaching of Buddha", "Buddhism"),
        "2026-09-14" to Holiday("Sangha Day", "Celebration of the Buddhist community", "Buddhism"),
        "2026-12-08" to Holiday("Bodhi Day", "Buddha's Enlightenment", "Buddhism")
    )
    
    // Secular/National Holidays (2026 - US focused, can be expanded)
    private val secularHolidays = mapOf(
        "2026-01-01" to Holiday("New Year's Day", "Beginning of the calendar year", "Secular"),
        "2026-01-20" to Holiday("Martin Luther King Jr. Day", "Civil rights leader commemoration", "Secular"),
        "2026-02-16" to Holiday("Presidents Day", "US Presidents commemoration", "Secular"),
        "2026-04-22" to Holiday("Earth Day", "Environmental awareness day", "Secular"),
        "2026-05-25" to Holiday("Memorial Day", "Remembering fallen soldiers", "Secular"),
        "2026-07-04" to Holiday("Independence Day", "US Independence celebration", "Secular"),
        "2026-09-07" to Holiday("Labor Day", "Workers' rights celebration", "Secular"),
        "2026-10-12" to Holiday("Columbus Day", "Explorer commemoration", "Secular"),
        "2026-11-11" to Holiday("Veterans Day", "Military veterans honor", "Secular"),
        "2026-11-26" to Holiday("Thanksgiving", "Gratitude and harvest celebration", "Secular")
    )
    
    fun getAllEnabledHolidays(context: Context): Map<String, Holiday> {
        val enabledHolidays = mutableMapOf<String, Holiday>()
        
        if (SettingsManager.getShowChristianHolidays(context)) {
            enabledHolidays.putAll(christianHolidays)
        }
        
        if (SettingsManager.getShowIslamicHolidays(context)) {
            enabledHolidays.putAll(islamicHolidays)
        }
        
        if (SettingsManager.getShowJewishHolidays(context)) {
            enabledHolidays.putAll(jewishHolidays)
        }
        
        if (SettingsManager.getShowHinduHolidays(context)) {
            enabledHolidays.putAll(hinduHolidays)
        }
        
        if (SettingsManager.getShowBuddhistHolidays(context)) {
            enabledHolidays.putAll(buddhistHolidays)
        }
        
        if (SettingsManager.getShowSecularHolidays(context)) {
            enabledHolidays.putAll(secularHolidays)
        }
        
        return enabledHolidays
    }
    
    fun getHolidaysByReligion(religion: String): Map<String, Holiday> {
        return when (religion.lowercase()) {
            "christianity" -> christianHolidays
            "islam" -> islamicHolidays
            "judaism" -> jewishHolidays
            "hinduism" -> hinduHolidays
            "buddhism" -> buddhistHolidays
            "secular" -> secularHolidays
            else -> emptyMap()
        }
    }
    
    fun getEnabledReligionsCount(context: Context): Int {
        var count = 0
        if (SettingsManager.getShowChristianHolidays(context)) count++
        if (SettingsManager.getShowIslamicHolidays(context)) count++
        if (SettingsManager.getShowJewishHolidays(context)) count++
        if (SettingsManager.getShowHinduHolidays(context)) count++
        if (SettingsManager.getShowBuddhistHolidays(context)) count++
        if (SettingsManager.getShowSecularHolidays(context)) count++
        return count
    }
    
    fun getReligionColors(): Map<String, String> {
        return mapOf(
            "Christianity" to "#8BC34A",      // Green
            "Islam" to "#4CAF50",             // Darker Green
            "Judaism" to "#2196F3",           // Blue
            "Hinduism" to "#FF9800",          // Orange
            "Buddhism" to "#9C27B0",          // Purple
            "Secular" to "#607D8B"            // Blue Gray
        )
    }
}