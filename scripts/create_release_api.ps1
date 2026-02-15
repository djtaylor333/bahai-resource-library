# GitHub Release Automation with Personal Access Token
# This script creates releases using GitHub API directly (no GitHub CLI OAuth needed)

param(
    [string]$Token,
    [string]$Version = "v0.3.1",
    [string]$ReleaseName = "Bahai Resource Library v0.3.1 - Android Release",
    [string]$ApkPath = "releases\v0.3.1\bahai-resource-library-v0.3.1.apk",
    [string]$NotesPath = "releases\v0.3.1\RELEASE_NOTES.md"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "GitHub Release Creator with API Token" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if token is provided
if (-not $Token) {
    Write-Host "❌ GitHub Personal Access Token required" -ForegroundColor Red
    Write-Host ""
    Write-Host "🔧 TO CREATE A TOKEN:" -ForegroundColor Yellow
    Write-Host "1. Go to: https://github.com/settings/tokens/new" -ForegroundColor White
    Write-Host "2. Name: 'Bahai App Release'" -ForegroundColor White  
    Write-Host "3. Expiration: 90 days (or custom)" -ForegroundColor White
    Write-Host "4. Scopes: Check 'repo' (full repository access)" -ForegroundColor White
    Write-Host "5. Click 'Generate token'" -ForegroundColor White
    Write-Host "6. Copy the token and run:" -ForegroundColor White
    Write-Host "   .\scripts\create_release_api.ps1 -Token 'YOUR_TOKEN_HERE'" -ForegroundColor Green
    Write-Host ""
    Write-Host "🔒 The token will be used once and not stored" -ForegroundColor Gray
    exit 1
}

# Verify files exist
if (-not (Test-Path $ApkPath)) {
    Write-Host "❌ APK file not found: $ApkPath" -ForegroundColor Red
    exit 1
}

if (-not (Test-Path $NotesPath)) {
    Write-Host "❌ Release notes not found: $NotesPath" -ForegroundColor Red
    exit 1
}

$apkSize = [math]::Round((Get-Item $ApkPath).Length / 1KB, 0)
$releaseNotes = Get-Content $NotesPath -Raw

Write-Host "📋 Release Details:" -ForegroundColor Blue
Write-Host "   Version: $Version" -ForegroundColor White
Write-Host "   APK: $ApkPath ($apkSize KB)" -ForegroundColor White
Write-Host "   Notes: $NotesPath" -ForegroundColor White
Write-Host ""

$repo = "djtaylor333/bahai-resource-library"
$baseUrl = "https://api.github.com"
$headers = @{
    "Authorization" = "token $Token"
    "Accept" = "application/vnd.github.v3+json"
    "User-Agent" = "PowerShell-Release-Script"
}

try {
    Write-Host "🚀 Creating GitHub Release..." -ForegroundColor Cyan

    # Create the release
    $releaseData = @{
        tag_name = $Version
        target_commitish = "main"
        name = $ReleaseName
        body = $releaseNotes
        draft = $false
        prerelease = $false
    } | ConvertTo-Json

    $releaseResponse = Invoke-RestMethod -Uri "$baseUrl/repos/$repo/releases" -Method Post -Headers $headers -Body $releaseData -ContentType "application/json"
    
    Write-Host "✅ Release created successfully!" -ForegroundColor Green
    Write-Host "   Release ID: $($releaseResponse.id)" -ForegroundColor White
    Write-Host "   URL: $($releaseResponse.html_url)" -ForegroundColor Cyan

    # Upload APK asset
    Write-Host ""
    Write-Host "📤 Uploading APK asset..." -ForegroundColor Cyan
    
    $uploadUrl = $releaseResponse.upload_url -replace '\{\?name,label\}', "?name=bahai-resource-library-v0.3.1.apk"
    $apkBytes = [System.IO.File]::ReadAllBytes((Resolve-Path $ApkPath).Path)
    
    $uploadHeaders = $headers.Clone()
    $uploadHeaders["Content-Type"] = "application/vnd.android.package-archive"
    
    $assetResponse = Invoke-RestMethod -Uri $uploadUrl -Method Post -Headers $uploadHeaders -Body $apkBytes

    Write-Host "✅ APK uploaded successfully!" -ForegroundColor Green
    Write-Host "   Asset ID: $($assetResponse.id)" -ForegroundColor White
    Write-Host "   Download URL: $($assetResponse.browser_download_url)" -ForegroundColor Cyan

    Write-Host ""
    Write-Host "🎉 SUCCESS! Release is live with APK download!" -ForegroundColor Green -BackgroundColor Black
    Write-Host "📱 Users can now download: $($releaseResponse.html_url)" -ForegroundColor White

} catch {
    Write-Host ""
    Write-Host "❌ Error creating release:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response: $responseBody" -ForegroundColor Yellow
    }
    
    Write-Host ""
    Write-Host "💡 Common issues:" -ForegroundColor Yellow
    Write-Host "   - Token expired or invalid" -ForegroundColor White
    Write-Host "   - Insufficient permissions (needs 'repo' scope)" -ForegroundColor White
    Write-Host "   - Release already exists (delete it first)" -ForegroundColor White
    exit 1
}

Write-Host ""
Write-Host "🎯 Release automation complete!" -ForegroundColor Blue