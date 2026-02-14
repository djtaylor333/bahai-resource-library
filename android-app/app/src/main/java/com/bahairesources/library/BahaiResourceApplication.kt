package com.bahairesources.library

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Bahai Resource Library
 * Initializes Hilt for dependency injection
 */
@HiltAndroidApp
class BahaiResourceApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize PdfBox for Android
        com.tom_roush.pdfbox.android.PDFBoxResourceLoader.init(applicationContext)
    }
}