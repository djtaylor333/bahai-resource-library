# Build Script for Bahá'í Resource Library Releases
# Usage: .\build-release.ps1 -Version "v0.8.0"
# Example: .\build-release.ps1 -Version "v0.8.0"

param(
    [Parameter(Mandatory=$true)]
    [string]$Version
)

$BuildDir = "android-app"
$ReleasesDir = "android-app\releases\$Version"
$APKName = "bahai-resource-library-$Version-release.apk"

Write-Host "🚀 Building Bahá'í Resource Library $Version" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green

# Navigate to build directory
Set-Location $BuildDir

# Clean and build release APK
Write-Host "📦 Building release APK..." -ForegroundColor Yellow
& .\gradlew clean assembleRelease

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Build failed!" -ForegroundColor Red
    exit 1
}

# Create release directory
Write-Host "📁 Creating release directory..." -ForegroundColor Yellow
New-Item -ItemType Directory -Force -Path "releases\$Version" | Out-Null

# Copy APK to releases
Write-Host "📱 Copying APK to releases..." -ForegroundColor Yellow
Copy-Item "app\build\outputs\apk\release\app-release-unsigned.apk" "releases\$Version\$APKName"

# Get APK size for release notes
$APKSize = (Get-Item "releases\$Version\$APKName").Length
$APKSizeMB = [math]::Round($APKSize / 1MB, 1)

Write-Host "✅ Build Complete!" -ForegroundColor Green
Write-Host "===================" -ForegroundColor Green
Write-Host "📱 APK: releases\$Version\$APKName" -ForegroundColor Cyan
Write-Host "📏 Size: $APKSizeMB MB" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Test the APK on Android device"
Write-Host "2. Create release notes in releases\$Version\RELEASE_NOTES.md"
Write-Host "3. Commit and tag the release:"
Write-Host "   git add releases\$Version\"
Write-Host "   git commit -m \`"Release $Version with APK\`""
Write-Host "   git tag -a $Version -m \`"$Version Release\`""
Write-Host "   git push origin main --tags"
Write-Host ""
Write-Host "🎉 Ready for release!" -ForegroundColor Green

# Return to original directory
Set-Location ..