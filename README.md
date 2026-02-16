# Bahai Resource Library

A comprehensive Android application providing offline access to Bahá'í texts, prayers, calendar, and resources for study, devotion, and community engagement.

## ✨ Features

### 📚 Document Library
- **Offline Access**: Complete collection of official Bahá'í texts available without internet connection
- **Advanced Search**: Full-text search with context-aware results and highlighting
- **Enhanced Reader**: In-document search, bookmarking, and font size customization
- **Category Browsing**: Organized access to writings by author and topic

### 🙏 Prayer Collection
- **80+ Prayers**: Comprehensive collection across 14 categories
- **Categories Include**: Daily Prayers, Healing, Protection, Gratitude, Guidance, Unity, Service, Departed Souls, Children, Youth, Families, Communities, Tests & Difficulties, and Special Occasions
- **Easy Navigation**: Browse by category or search specific prayers

### 📅 Bahá'í Calendar
- **Holy Days & Feast Days**: Complete 2026 Bahá'í calendar with accurate dates
- **Fast Period Information**: Detailed guidance for the 19-day Fast
- **Location-Based Times**: Sunrise/sunset times for proper observance
- **Interactive Calendar**: Click any date for detailed information about observances

### 🔖 Personal Tools
- **Bookmarks**: Save and organize favorite passages with advanced search
- **Notes**: Digital note-taking with document references
- **Favorites**: Quick access to frequently used content

### ⚙️ Customization & Settings
- **Universal Dark Mode**: Complete dark theme across all activities
- **Font Size Options**: Small, Medium, Large, and Extra Large text sizing
- **Theme Preferences**: Light and dark mode with consistent styling
- **Persistent Settings**: Preferences saved across app sessions

### 🌐 Resources & Links
- **Official Links**: Direct access to 15 official Bahá'í websites and resources
- **Feast Resources**: Information for all 19 Bahá'í months with descriptions
- **About Section**: App information and developer details

## 📱 Version History

### **v0.7.0** (February 2026) - **Major Update**
- ✅ **Universal Dark Mode**: Complete dark theme implementation across all activities
- ✅ **Comprehensive Settings System**: Centralized preferences with theme and font controls
- ✅ **Enhanced Calendar**: Accurate Bahá'í dates, Fast information, and location-based times
- ✅ **Advanced Document Reader**: In-document search with context results and highlighting
- ✅ **Navigation Improvements**: Settings access and consistent navigation across all screens
- ✅ **Vastly Expanded Prayers**: 80+ prayers across 14 comprehensive categories
- ✅ **Links Section**: 15 official Bahá'í websites and resources
- ✅ **About Section**: App information and contact details
- ✅ **9-Pointed Star Icon**: Updated app icon to reflect Bahá'í symbolism
- ✅ **Feast Resources**: Detailed information for all 19 Bahá'í months
- ✅ **Bookmark Search**: Advanced search functionality for saved bookmarks
- ✅ **Font Size Settings**: Four font size options with dynamic scaling
- ✅ **Text Formatting**: Cleaned up display formatting throughout the app

### v0.6.0 (Previous Release)
- Document search functionality
- Basic bookmarking system
- Initial prayer collection
- Basic calendar features

## 🛠️ Technical Specifications

### Requirements
- **Android API Level**: 21+ (Android 5.0 Lollipop)
- **Target SDK**: 34 (Android 14)
- **Architecture**: Modern Android app architecture with Activities and Services
- **Storage**: Local SQLite database for bookmarks and settings
- **Permissions**: Location (optional, for accurate prayer times)

### Development Environment
- **IDE**: Android Studio Hedgehog (2023.1.1) or newer
- **Language**: Kotlin with Android Extensions
- **Build System**: Gradle with Android Gradle Plugin 8.1+
- **Minimum SDK**: API 21 (covers 99%+ of active Android devices)

## 📋 Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/bahai-resource-library.git
   ```

2. Open the `android-app` directory in Android Studio

3. Sync project with Gradle files

4. Build and run on your device or emulator

## 📁 Project Structure

```
android-app/
├── app/
│   ├── src/main/java/com/bahairesources/library/
│   │   ├── MainActivity.kt              # App entry point
│   │   ├── SettingsManager.kt          # Centralized settings system
│   │   ├── DocumentReaderActivity.kt   # Enhanced document reader
│   │   ├── BrowseActivity.kt           # Category browsing
│   │   ├── SearchActivity.kt           # Global search
│   │   ├── PrayersActivity.kt          # Prayer collection
│   │   ├── CalendarActivity.kt         # Bahá'í calendar
│   │   ├── BookmarksActivity.kt        # Bookmark management
│   │   ├── LinksActivity.kt           # Official links
│   │   ├── AboutActivity.kt           # App information
│   │   └── FeastResourcesActivity.kt  # Feast information
│   └── res/
│       ├── drawable/                   # App icons and images
│       ├── layout/                     # XML layouts
│       ├── values/                     # Themes, colors, strings
│       └── mipmap/                     # App launcher icons
└── documents/
    ├── confirmed-official/             # Verified official texts
    └── pending-permissions/            # Texts requiring verification
```

## 🎨 Design Philosophy

This app follows Material Design principles with:
- **Consistent Navigation**: Settings access from every screen
- **Accessibility**: High contrast ratios, scalable fonts, clear visual hierarchy
- **User Experience**: Intuitive navigation with logical information architecture
- **Professional Polish**: Attention to detail in animations, spacing, and interactions

## 📄 Document Collections

The app includes carefully curated Bahá'í texts from official sources:
- **Primary Sources**: Bahá'u'lláh, the Báb, 'Abdu'l-Bahá
- **Institutional Guidance**: Universal House of Justice, International Teaching Centre
- **Educational Materials**: Study guides and compilation materials
- **Prayer Collections**: Comprehensive devotional content

All texts are included for non-commercial educational use with proper attribution to official Bahá'í sources.

## 🤝 Contributing

This project aims to serve the global Bahá'í community. Contributions are welcome:

1. **Content Contributions**: Help expand the document library with verified official texts
2. **Translation Support**: Assist with internationalization for global accessibility  
3. **Feature Suggestions**: Propose improvements that serve community needs
4. **Bug Reports**: Help identify and resolve issues for better user experience

## 📞 Contact & Support

**Developer**: David Taylor  
**Email**: djtaylor333@gmail.com  
**Purpose**: Educational tool for Bahá'í community worldwide

For technical support, feature requests, or content suggestions, please reach out via email.

## 📃 License & Attribution

This application is developed for the Bahá'í community as an educational resource. All Bahá'í texts included are used with respect for official sources and are available through:

- [Bahá'í Reference Library](https://www.bahai.org/library/)
- Local Bahá'í centers and communities  
- Official Bahá'í publishing organizations

The application code is available for community use and improvement.

---

*"The betterment of the world can be accomplished through pure and goodly deeds, through commendable and seemly conduct."* - Bahá'u'lláh