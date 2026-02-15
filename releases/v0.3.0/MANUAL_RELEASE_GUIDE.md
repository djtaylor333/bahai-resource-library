# 📝 Manual GitHub Release Creation Guide

## 🎯 Release Information
- **Tag**: `v0.3.0`  
- **Release Title**: `🌟 Bahá'í Resource Library v0.3.0 - Light of Knowledge`
- **Target Branch**: `main`

## 📋 Step-by-Step Instructions

### 1. Navigate to GitHub Releases
1. Go to https://github.com/djtaylor333/bahai-resource-library
2. Click on **"Releases"** in the sidebar (or `/releases` path)
3. Click **"Create a new release"** button

### 2. Configure Release Details
1. **Tag version**: Enter `v0.3.0`
2. **Target**: Select `main` branch  
3. **Release title**: Copy and paste:
   ```
   🌟 Bahá'í Resource Library v0.3.0 - Light of Knowledge
   ```

### 3. Add Release Description
1. In the **"Describe this release"** text area
2. Copy the entire content from `releases/v0.3.0/RELEASE_NOTES.md`
3. Paste it into the description field

### 4. Upload Asset Files
Click **"Attach binaries by dropping them here or selecting them"** and upload:

#### 📱 APK Files
- `bahai-resource-library-v0.3.0-release.apk` 
  - Label: "📱 Release APK (Android 5.0+)"
- `bahai-resource-library-v0.3.0-debug.apk`
  - Label: "🔧 Debug APK (Development)"  
- `bahai-resource-library-v0.3.0-release.aab`
  - Label: "📦 App Bundle (Play Store Ready)"

#### 📚 Documentation
- `INSTALLATION_GUIDE.md`
  - Label: "📖 Installation Guide"
- `apk_metadata.json`
  - Label: "📋 APK Metadata"

### 5. Release Settings
- ❌ **"Set as a pre-release"** - Leave unchecked (this is a full release)
- ❌ **"Set as the latest release"** - Check this box
- ✅ **"Create a discussion for this release"** - Optional but recommended

### 6. Publish Release
1. Click **"Publish release"** button
2. Wait for the release to be created
3. Verify all files uploaded correctly

## 🎉 After Publication

### Share the Release
1. **Copy release URL**: https://github.com/djtaylor333/bahai-resource-library/releases/tag/v0.3.0
2. **Share with community**: Use the content from `announcement.md`
3. **Social media**: Post announcement on relevant platforms

### Verify Installation
1. **Test APK download** on an Android device
2. **Verify installation** process works smoothly  
3. **Check app functionality** with the 28 included documents

## 📋 Asset Checklist

Before publishing, ensure these files are uploaded:

- [ ] `bahai-resource-library-v0.3.0-release.apk` (Main APK file)
- [ ] `bahai-resource-library-v0.3.0-debug.apk` (Debug version)  
- [ ] `bahai-resource-library-v0.3.0-release.aab` (App Bundle)
- [ ] `INSTALLATION_GUIDE.md` (User instructions)
- [ ] `apk_metadata.json` (Technical details)

## 🌟 Community Announcement

After creating the release, share this message:

---

🌟 **Exciting News**: Bahá'í Resource Library v0.3.0 is now available! 

This Android app brings 28 official Bahá'í texts to your device with:
- 🔍 Intelligent search across all documents  
- 📚 Complete Central Figures collection (Bahá'u'lláh, The Báb, 'Abdu'l-Bahá)
- 🎓 Full Ruhi Institute main sequence (Books 1-7)
- 📖 Advanced PDF reader with bookmarks and annotations
- 🎨 Beautiful Material 3 design with nine-pointed star branding

**Download**: https://github.com/djtaylor333/bahai-resource-library/releases/tag/v0.3.0

*"So powerful is the light of unity that it can illuminate the whole earth."* - Bahá'u'lláh

Share with your local Bahá'í community and study circles!

---

## 🔧 Troubleshooting

### If Upload Fails
- Check file sizes (GitHub has 100MB limit per file)
- Ensure stable internet connection
- Try uploading files one at a time

### If Release Notes Don't Format
- Use GitHub's preview tab to check formatting
- Ensure markdown syntax is correct
- Copy-paste in smaller sections if needed

### Need to Update Release
- Edit the release after publishing
- Add missing files or update descriptions
- Use "Save draft" to preview changes

---

*Created: February 15, 2026 at 08:48 AM*
*Version: 0.3.0*
