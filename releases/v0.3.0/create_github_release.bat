@echo off
REM GitHub Release Creation Script for Bahai Resource Library v0.3.0
REM Requires GitHub CLI (gh) to be installed and authenticated

set REPO=djtaylor333/bahai-resource-library
set TAG=v0.3.0
set TITLE=🌟 Bahai Resource Library v0.3.0 - Light of Knowledge

echo 🚀 Creating GitHub Release v0.3.0...

REM Create the release with notes
gh release create "%TAG%" ^
  --repo "%REPO%" ^
  --title "%TITLE%" ^
  --notes-file "releases\v0.3.0\RELEASE_NOTES.md" ^
  --prerelease=false ^
  --generate-notes=false

REM Upload APK files
echo 📦 Uploading APK files...

gh release upload "%TAG%" ^
  --repo "%REPO%" ^
  "releases\v0.3.0\bahai-resource-library-v0.3.0-release.apk#📱 Release APK (Android 5.0+)" ^
  "releases\v0.3.0\bahai-resource-library-v0.3.0-debug.apk#🔧 Debug APK (Development)" ^
  "releases\v0.3.0\bahai-resource-library-v0.3.0-release.aab#📦 App Bundle (Play Store Ready)"

REM Upload documentation  
echo 📚 Uploading documentation...

gh release upload "%TAG%" ^
  --repo "%REPO%" ^
  "releases\v0.3.0\INSTALLATION_GUIDE.md#📖 Installation Guide" ^
  "releases\v0.3.0\apk_metadata.json#📋 APK Metadata"

echo ✅ Release v0.3.0 created successfully!
echo 🌐 View release: https://github.com/%REPO%/releases/tag/%TAG%

echo 📝 Creating announcement...
echo 🌟 **New Release**: Bahai Resource Library v0.3.0 is now available! > releases\v0.3.0\announcement.md
echo. >> releases\v0.3.0\announcement.md
echo 📱 **Download APK**: Ready for Android 5.0+ devices >> releases\v0.3.0\announcement.md
echo 📚 **28 Bahai Texts**: Complete document library with intelligent search >> releases\v0.3.0\announcement.md
echo 🎨 **Material 3 UI**: Beautiful design with nine-pointed star branding >> releases\v0.3.0\announcement.md
echo 🔍 **Smart Search**: Find passages with fuzzy matching and full-text search >> releases\v0.3.0\announcement.md
echo. >> releases\v0.3.0\announcement.md
echo Download from GitHub Releases and share with your Bahai community! >> releases\v0.3.0\announcement.md
echo *"The earth is but one country, and mankind its citizens."* - Bahaullah >> releases\v0.3.0\announcement.md

echo 📝 Announcement saved to releases\v0.3.0\announcement.md
echo 🎯 Share this announcement with the Bahai community!

pause
