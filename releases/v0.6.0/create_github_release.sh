#!/bin/bash

# GitHub Release Creation Script for Bahá'í Resource Library v0.6.0
# This script creates a GitHub release with the APK attached

VERSION="v0.6.0"
RELEASE_NAME="Bahá'í Resource Library v0.6.0 - Complete Feature Release" 
APK_FILE="bahai-resource-library-v0.6.0.apk"
REPO="djtaylor333/bahai-resource-library"

echo "🚀 Creating GitHub Release for $VERSION"

# Check if gh CLI is installed
if ! command -v gh &> /dev/null; then
    echo "❌ GitHub CLI (gh) is not installed. Please install it first:"
    echo "   Visit: https://cli.github.com/"
    exit 1
fi

# Check if APK exists
if [ ! -f "$APK_FILE" ]; then
    echo "❌ APK file not found: $APK_FILE"
    echo "   Please ensure the APK is built and copied to this directory"
    exit 1
fi

echo "📦 Found APK: $APK_FILE"

# Create the release
echo "🏗️  Creating release..."

gh release create "$VERSION" \
    --repo "$REPO" \
    --title "$RELEASE_NAME" \
    --notes-file "RELEASE_NOTES.md" \
    --target main \
    "$APK_FILE"

if [ $? -eq 0 ]; then
    echo "✅ Successfully created release $VERSION"
    echo "🔗 Release URL: https://github.com/$REPO/releases/tag/$VERSION"
    echo ""
    echo "📱 Users can now download the APK from:"
    echo "   https://github.com/$REPO/releases/download/$VERSION/$APK_FILE"
    echo ""
    echo "🎉 Release is live and ready for download!"
else
    echo "❌ Failed to create release"
    exit 1
fi