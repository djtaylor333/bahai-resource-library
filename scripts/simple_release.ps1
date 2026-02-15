# Simple GitHub Release Creation for v0.3.1
Write-Host "Creating GitHub Release v0.3.1..." -ForegroundColor Green

# Check if authenticated
try {
    gh auth status 2>$null | Out-Null
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Authentication required. Starting login..." -ForegroundColor Yellow
        gh auth login --web --git-protocol https
    }
}
catch {
    Write-Host "Starting authentication..." -ForegroundColor Yellow
    gh auth login --web --git-protocol https
}

# Create the release
Write-Host "Creating release with APK..." -ForegroundColor Cyan
try {
    gh release create v0.3.1 --title "Bahai Resource Library v0.3.1 - Android Release" --notes-file "releases\v0.3.1\RELEASE_NOTES.md" "releases\v0.3.1\bahai-resource-library-v0.3.1.apk"
    
    Write-Host ""
    Write-Host "SUCCESS! GitHub Release Created" -ForegroundColor Green -BackgroundColor Black
    Write-Host "Download URL: https://github.com/djtaylor333/bahai-resource-library/releases/tag/v0.3.1" -ForegroundColor Cyan
}
catch {
    Write-Host "Error creating release: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Manual link: https://github.com/djtaylor333/bahai-resource-library/releases/new" -ForegroundColor Yellow
}

Read-Host "Press Enter to continue"