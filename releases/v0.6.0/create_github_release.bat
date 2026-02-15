# GitHub Release Creation Script for Bahá'í Resource Library v0.6.0
# PowerShell version for Windows users

$VERSION = "v0.6.0"
$RELEASE_NAME = "Bahá'í Resource Library v0.6.0 - Complete Feature Release"
$APK_FILE = "bahai-resource-library-v0.6.0.apk"
$REPO = "djtaylor333/bahai-resource-library"

Write-Host "🚀 Creating GitHub Release for $VERSION" -ForegroundColor Green

# Check if gh CLI is installed
try {
    gh --version | Out-Null
} catch {
    Write-Host "❌ GitHub CLI (gh) is not installed. Please install it first:" -ForegroundColor Red
    Write-Host "   Visit: https://cli.github.com/" -ForegroundColor Yellow
    exit 1
}

# Check if APK exists
if (!(Test-Path $APK_FILE)) {
    Write-Host "❌ APK file not found: $APK_FILE" -ForegroundColor Red
    Write-Host "   Please ensure the APK is built and copied to this directory" -ForegroundColor Yellow
    exit 1
}

Write-Host "📦 Found APK: $APK_FILE" -ForegroundColor Green

# Create the release
Write-Host "🏗️  Creating release..." -ForegroundColor Cyan

try {
    gh release create $VERSION `
        --repo $REPO `
        --title $RELEASE_NAME `
        --notes-file "RELEASE_NOTES.md" `
        --target main `
        $APK_FILE

    Write-Host "✅ Successfully created release $VERSION" -ForegroundColor Green
    Write-Host "🔗 Release URL: https://github.com/$REPO/releases/tag/$VERSION" -ForegroundColor Blue
    Write-Host ""
    Write-Host "📱 Users can now download the APK from:" -ForegroundColor Cyan
    Write-Host "   https://github.com/$REPO/releases/download/$VERSION/$APK_FILE" -ForegroundColor Blue
    Write-Host ""
    Write-Host "🎉 Release is live and ready for download!" -ForegroundColor Green
} catch {
    Write-Host "❌ Failed to create release: $_" -ForegroundColor Red
    exit 1
}