#!/bin/bash

# GitHub Release Creation Script for Bahá'í Resource Library v0.3.0
# Requires GitHub CLI (gh) to be installed and authenticated

set -e

REPO="djtaylor333/bahai-resource-library"
TAG="v0.3.0"
TITLE="🌟 Bahá'í Resource Library v0.3.0 - Light of Knowledge"

echo "🚀 Creating GitHub Release v0.3.0..."

# Create the release with notes
gh release create "$TAG" \
  --repo "$REPO" \
  --title "$TITLE" \
  --notes-file "releases/v0.3.0/RELEASE_NOTES.md" \
  --prerelease=false \
  --generate-notes=false

# Upload APK files
echo "📦 Uploading APK files..."

gh release upload "$TAG" \
  --repo "$REPO" \
  "releases/v0.3.0/bahai-resource-library-v0.3.0-release.apk#📱 Release APK (Android 5.0+)" \
  "releases/v0.3.0/bahai-resource-library-v0.3.0-debug.apk#🔧 Debug APK (Development)" \
  "releases/v0.3.0/bahai-resource-library-v0.3.0-release.aab#📦 App Bundle (Play Store Ready)"

# Upload documentation
echo "📚 Uploading documentation..."

gh release upload "$TAG" \
  --repo "$REPO" \
  "releases/v0.3.0/INSTALLATION_GUIDE.md#📖 Installation Guide" \
  "releases/v0.3.0/apk_metadata.json#📋 APK Metadata"

echo "✅ Release v0.3.0 created successfully!"
echo "🌐 View release: https://github.com/$REPO/releases/tag/$TAG"

# Optional: Create announcement
echo "📢 Creating release announcement..."
cat << 'EOF' > releases/v0.3.0/announcement.md
🌟 **New Release**: Bahá'í Resource Library v0.3.0 is now available!

📱 **Download APK**: Ready for Android 5.0+ devices
📚 **28 Bahá'í Texts**: Complete document library with intelligent search  
🎨 **Material 3 UI**: Beautiful design with nine-pointed star branding
🔍 **Smart Search**: Find passages with fuzzy matching and full-text search

Download from GitHub Releases and share with your Bahá'í community!
*"The earth is but one country, and mankind its citizens."* - Bahá'u'lláh

#BahaiResources #Android #BahaiApp #SpiritualEducation
EOF

echo "📝 Announcement saved to releases/v0.3.0/announcement.md"
echo "🎯 Share this announcement with the Bahá'í community!"
