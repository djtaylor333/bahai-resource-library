# Bahá'í Resource Library v0.3.1 - Android Release

## 📱 Working Android Application Build

This release marks the successful compilation and packaging of the Bahá'í Resource Library as a working Android application (.APK) that can be installed on Android devices.

## ✅ What's New in v0.3.1

### Android App Features
- **Working APK Build**: Successfully compiles both debug (6.34 MB) and release (2.04 MB) APK files
- **Simple, Clean Interface**: MainActivity displays library information and welcome message
- **Document Collection Display**: Shows info about 28 Bahá'í texts organized in 6 categories
- **Stable Foundation**: Simplified codebase provides reliable base for future development

### Technical Improvements
- **Build System Integration**: Full Android SDK and Gradle build system working correctly
- **Dependency Cleanup**: Removed complex external libraries causing build conflicts
- **Resource Optimization**: Cleaned XML layouts and removed problematic resource references
- **Kotlin Compilation**: All source files now compile successfully without errors

## 📦 Installation

### Option 1: Download APK (Recommended)
1. Download `app-release.apk` (2.04 MB) from the Assets below
2. Enable "Install from Unknown Sources" in your Android device settings
3. Install the APK file on your Android device

### Option 2: Build from Source
```bash
# Clone the repository
git clone https://github.com/djtaylor333/bahai-resource-library.git
cd bahai-resource-library/android-app

# Build the APK
./gradlew assembleRelease

# Find your APK in: app/build/outputs/apk/release/app-release.apk
```

## 🔧 System Requirements

- **Android Version**: 7.0 (API level 24) or higher
- **Storage**: ~5 MB free space
- **Permissions**: Internet access for future online features

## 📚 Document Collection

The app includes references to 28 official Bahá'í texts:

### Central Figures (10 documents)
- Kitab-i-Iqan, Kitab-i-Aqdas, Hidden Words, Seven Valleys, and more

### Administrative Writings (6 documents) 
- Will and Testament, Dispensation of Bahá'u'lláh, World Order Letters

### Ruhi Institute Books (7 documents)
- Complete series of Books 1-7 covering spiritual education

### Devotional Materials (2 collections)
- Prayers and devotional texts for daily use

### Study Materials (1 guide)
- Core curriculum guidelines for deepening study

### Compilations (2 documents)
- Thematic compilations on Bahá'í principles

## 🚀 Future Development

This stable v0.3.1 foundation enables the planned implementation of:

- **PDF Reader Integration**: Full text viewing with annotations
- **Advanced Search**: Intelligent search across all 28 texts
- **Bookmarks & Notes**: Personal study features
- **Material 3 Design**: Modern UI with nine-pointed star theme
- **Offline Access**: Complete text storage for offline reading

## 🔧 For Developers

### Build Environment
- **Android SDK**: API 34 with build-tools 34.0.0
- **Gradle**: 8.4 with Android Gradle Plugin 8.1.4
- **Java**: JDK 11+ (uses Android Studio JBR)
- **Kotlin**: 1.9.10 with coroutines support

### Key Changes from v0.3.0
- Removed Hilt dependency injection for simplified build
- Removed PDF rendering libraries (PdfBox, PdfJS) causing conflicts
- Simplified XML layouts removing custom resource references
- Cleaned data models and search engine classes
- Streamlined to single MainActivity for stable foundation

## 🙏 Acknowledgments

Built with ❤️ for the Bahá'í community worldwide

---

**Installation Note**: When installing the APK, your device may show a security warning because this is not from an official app store. This is normal for sideloaded applications. The APK is safe to install.