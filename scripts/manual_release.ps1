# Manual GitHub Release Guide (Alternative to automation)
Write-Host "📋 Manual Release Creation Guide" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "🎯 OPTION 1: Direct GitHub Web Upload (Fastest)" -ForegroundColor Green
Write-Host "1. Go to: https://github.com/djtaylor333/bahai-resource-library/releases/new" -ForegroundColor White
Write-Host "2. Tag: v0.3.1 (select existing)" -ForegroundColor White
Write-Host "3. Title: Bahai Resource Library v0.3.1 - Android Release" -ForegroundColor White
Write-Host "4. Drag & drop APK: releases\v0.3.1\bahai-resource-library-v0.3.1.apk" -ForegroundColor White
Write-Host "5. Copy/paste release notes from: releases\v0.3.1\RELEASE_NOTES.md" -ForegroundColor White
Write-Host "6. Click 'Publish Release'" -ForegroundColor White

Write-Host ""
Write-Host "🎯 OPTION 2: Copy Release Notes" -ForegroundColor Blue
$releaseNotes = Get-Content "releases\v0.3.1\RELEASE_NOTES.md" -Raw
$releaseNotes | Set-Clipboard
Write-Host "✅ Release notes copied to clipboard!" -ForegroundColor Green

Write-Host ""
Write-Host "📂 File to upload:" -ForegroundColor Yellow
$apkPath = "releases\v0.3.1\bahai-resource-library-v0.3.1.apk"
if (Test-Path $apkPath) {
    $apkSize = [math]::Round((Get-Item $apkPath).Length / 1KB, 0)
    Write-Host "   📱 $apkPath ($apkSize KB)" -ForegroundColor White
    Write-Host "   ✅ File exists and ready to upload" -ForegroundColor Green
} else {
    Write-Host "   ❌ APK file not found!" -ForegroundColor Red
}

Write-Host ""
Write-Host "🌐 Opening GitHub release creation page..." -ForegroundColor Cyan
Start-Process "https://github.com/djtaylor333/bahai-resource-library/releases/new"

Write-Host ""
Write-Host "⏱️  Manual process: ~2 minutes" -ForegroundColor Gray
Write-Host "🤖 For automation: Use scripts\quick_release.bat" -ForegroundColor Gray