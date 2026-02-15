# GitHub Release Creation Script for Bahai Resource Library v0.3.1
# PowerShell version with enhanced error handling

Write-Host "=========================================================" -ForegroundColor Cyan
Write-Host "        Bahai Resource Library v0.3.1 Release Creator" -ForegroundColor Green
Write-Host "=========================================================" -ForegroundColor Cyan
Write-Host ""

# Check if GitHub CLI is installed
try {
    $ghVersion = gh --version 2>$null
    Write-Host "✅ GitHub CLI found: $($ghVersion[0])" -ForegroundColor Green
} catch {
    Write-Host "❌ GitHub CLI not found. Installing..." -ForegroundColor Yellow
    try {
        winget install --id GitHub.cli --silent --accept-package-agreements --accept-source-agreements
        Write-Host "✅ GitHub CLI installed successfully!" -ForegroundColor Green
        Write-Host "Please run this script again." -ForegroundColor Yellow
        Read-Host "Press Enter to exit"
        exit
    } catch {
        Write-Host "❌ Failed to install GitHub CLI" -ForegroundColor Red
        Write-Host "Please install manually: https://cli.github.com/" -ForegroundColor Cyan
        Read-Host "Press Enter to exit"  
        exit
    }
}

# Check authentication
try {
    $user = gh api user --jq '.login' 2>$null
    Write-Host "✅ Authenticated as: $user" -ForegroundColor Green
} catch {
    Write-Host "⚠️  Not authenticated with GitHub" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Starting GitHub authentication..." -ForegroundColor Cyan
    Write-Host "A browser window will open for authentication." -ForegroundColor White
    
    try {
        gh auth login --web --git-protocol https
        Write-Host "✅ Authentication completed!" -ForegroundColor Green
    } catch {
        Write-Host "❌ Authentication failed" -ForegroundColor Red
        Write-Host "Please run: gh auth login --web" -ForegroundColor Yellow
        Read-Host "Press Enter to exit"
        exit
    }
}

# Verify APK file exists
$apkPath = "releases\v0.3.1\bahai-resource-library-v0.3.1.apk"
if (-not (Test-Path $apkPath)) {
    Write-Host "❌ APK file not found: $apkPath" -ForegroundColor Red
    Write-Host "Please ensure the APK has been built and is in the correct location." -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit
}

$apkSize = [math]::Round((Get-Item $apkPath).Length / 1KB, 0)
Write-Host "📱 APK found: $apkSize KB" -ForegroundColor Blue

# Create GitHub Release
Write-Host ""
Write-Host "Creating GitHub Release v0.3.1..." -ForegroundColor Cyan

try {
    gh release create v0.3.1 `
        --title "Bahai Resource Library v0.3.1 - Android Release" `
        --notes-file "releases\v0.3.1\RELEASE_NOTES.md" `
        "releases\v0.3.1\bahai-resource-library-v0.3.1.apk#bahai-resource-library-v0.3.1.apk"
    
    Write-Host ""
    Write-Host "=========================================================" -ForegroundColor Green
    Write-Host "🎉 SUCCESS! GitHub Release Created" -ForegroundColor Green -BackgroundColor Black
    Write-Host "=========================================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "📱 APK Download URL:" -ForegroundColor Cyan
    Write-Host "https://github.com/djtaylor333/bahai-resource-library/releases/tag/v0.3.1" -ForegroundColor White
    Write-Host ""
    Write-Host "🎯 Users can now download the APK directly from GitHub!" -ForegroundColor Green
    Write-Host "   File: bahai-resource-library-v0.3.1.apk ($apkSize KB)" -ForegroundColor White
    
} catch {
    Write-Host ""
    Write-Host "❌ Failed to create GitHub release" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "Manual alternative:" -ForegroundColor Yellow
    Write-Host "https://github.com/djtaylor333/bahai-resource-library/releases/new" -ForegroundColor Cyan
}

Write-Host ""
Read-Host "Press Enter to exit"