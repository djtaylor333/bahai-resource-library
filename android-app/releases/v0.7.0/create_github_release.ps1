# GitHub Release Creation Script for Bahá'í Resource Library v0.7.0

$VERSION = "v0.7.0"
$RELEASE_NAME = "Bahá'í Resource Library v0.7.0 - Major Feature Update"
$APK_FILE = "bahai-resource-library-v0.7.0-release.apk"
$REPO = "djtaylor333/bahai-resource-library"

Write-Host "🚀 Creating GitHub Release for $VERSION" -ForegroundColor Green

# Check if gh CLI is installed
if (-not (Get-Command "gh" -ErrorAction SilentlyContinue)) {
    Write-Host "❌ GitHub CLI (gh) is not installed. Please install it first:" -ForegroundColor Red
    Write-Host "   Visit: https://cli.github.com/" -ForegroundColor Yellow
    Write-Host "   Or run: winget install GitHub.CLI" -ForegroundColor Yellow
    exit 1
}

# Check if APK exists
if (!(Test-Path $APK_FILE)) {
    Write-Host "❌ APK file not found: $APK_FILE" -ForegroundColor Red
    Write-Host "   Please ensure the APK is built and copied to this directory" -ForegroundColor Yellow
    exit 1
}

# Check if release notes exist
if (!(Test-Path "RELEASE_NOTES.md")) {
    Write-Host "❌ RELEASE_NOTES.md not found" -ForegroundColor Red
    exit 1
}

Write-Host "📦 Found APK: $APK_FILE" -ForegroundColor Green
Write-Host "📝 Found Release Notes: RELEASE_NOTES.md" -ForegroundColor Green

# Get APK size
$APK_SIZE = [math]::Round((Get-Item $APK_FILE).Length / 1MB, 1)
Write-Host "📏 APK Size: $APK_SIZE MB" -ForegroundColor Cyan

# Create the release
Write-Host "🏗️ Creating release..." -ForegroundColor Cyan

$command = @(
    "gh", "release", "create", $VERSION,
    "--repo", $REPO,
    "--title", $RELEASE_NAME,
    "--notes-file", "RELEASE_NOTES.md",
    "--target", "main",
    $APK_FILE
)

Write-Host "Running: $($command -join ' ')" -ForegroundColor Gray

try {
    & gh release create $VERSION --repo $REPO --title $RELEASE_NAME --notes-file "RELEASE_NOTES.md" --target main $APK_FILE
    
    Write-Host ""
    Write-Host "✅ Successfully created release $VERSION" -ForegroundColor Green
    Write-Host "🔗 Release URL: https://github.com/$REPO/releases/tag/$VERSION" -ForegroundColor Blue
    Write-Host ""
    Write-Host "📱 Users can now download the APK from:" -ForegroundColor Cyan
    Write-Host "   https://github.com/$REPO/releases/download/$VERSION/$APK_FILE" -ForegroundColor Blue
    Write-Host ""
    Write-Host "🎉 Release is live and ready for download!" -ForegroundColor Green
}
catch {
    Write-Host "❌ Failed to create release: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}