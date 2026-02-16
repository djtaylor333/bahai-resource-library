#!/bin/bash
# Build Script for Bahá'í Resource Library Releases
# Usage: ./build-release.sh <version>
# Example: ./build-release.sh v0.8.0

if [ -z "$1" ]; then
    echo "Usage: ./build-release.sh <version>"
    echo "Example: ./build-release.sh v0.8.0"
    exit 1
fi

VERSION=$1
BUILD_DIR="android-app"
RELEASES_DIR="android-app/releases/$VERSION"
APK_NAME="bahai-resource-library-$VERSION-release.apk"

echo "🚀 Building Bahá'í Resource Library $VERSION"
echo "============================================"

# Navigate to build directory
cd $BUILD_DIR

# Clean and build release APK
echo "📦 Building release APK..."
./gradlew clean assembleRelease

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

# Create release directory
echo "📁 Creating release directory..."
mkdir -p "releases/$VERSION"

# Copy APK to releases
echo "📱 Copying APK to releases..."
cp "app/build/outputs/apk/release/app-release-unsigned.apk" "releases/$VERSION/$APK_NAME"

# Get APK size for release notes
APK_SIZE=$(du -sh "releases/$VERSION/$APK_NAME" | cut -f1)

echo "✅ Build Complete!"
echo "==================="
echo "📱 APK: releases/$VERSION/$APK_NAME"
echo "📏 Size: $APK_SIZE"
echo ""
echo "Next steps:"
echo "1. Test the APK on Android device"
echo "2. Create release notes in releases/$VERSION/RELEASE_NOTES.md"
echo "3. Commit and tag the release:"
echo "   git add releases/$VERSION/"
echo "   git commit -m \"Release $VERSION with APK\""
echo "   git tag -a $VERSION -m \"$VERSION Release\""
echo "   git push origin main --tags"
echo ""
echo "🎉 Ready for release!"