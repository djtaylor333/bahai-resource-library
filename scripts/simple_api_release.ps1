# Simple GitHub Release Creator with API Token
param([string]$Token = "")

if (-not $Token) {
    Write-Host "Error: Token required" -ForegroundColor Red
    exit 1
}

$apkPath = "releases\v0.3.1\bahai-resource-library-v0.3.1.apk"
$notesPath = "releases\v0.3.1\RELEASE_NOTES.md"

if (-not (Test-Path $apkPath)) {
    Write-Host "Error: APK not found at $apkPath" -ForegroundColor Red
    exit 1
}

if (-not (Test-Path $notesPath)) {
    Write-Host "Error: Release notes not found at $notesPath" -ForegroundColor Red  
    exit 1
}

$apkSize = [math]::Round((Get-Item $apkPath).Length / 1KB, 0)
$releaseNotes = Get-Content $notesPath -Raw

Write-Host "Creating GitHub Release v0.3.1..." -ForegroundColor Green
Write-Host "APK Size: $apkSize KB" -ForegroundColor Blue

$headers = @{
    "Authorization" = "token $Token"
    "Accept" = "application/vnd.github.v3+json"
    "User-Agent" = "PowerShell-Release-Script"
}

$releaseData = @{
    tag_name = "v0.3.1"
    target_commitish = "main" 
    name = "Bahai Resource Library v0.3.1 - Android Release"
    body = $releaseNotes
    draft = $false
    prerelease = $false
} | ConvertTo-Json

try {
    Write-Host "Step 1: Creating release..." -ForegroundColor Cyan
    $releaseResponse = Invoke-RestMethod -Uri "https://api.github.com/repos/djtaylor333/bahai-resource-library/releases" -Method Post -Headers $headers -Body $releaseData -ContentType "application/json"
    
    Write-Host "Success! Release created with ID: $($releaseResponse.id)" -ForegroundColor Green
    Write-Host "Release URL: $($releaseResponse.html_url)" -ForegroundColor Cyan
    
    Write-Host "Step 2: Uploading APK..." -ForegroundColor Cyan
    $uploadUrl = $releaseResponse.upload_url -replace '\{\?name,label\}', "?name=bahai-resource-library-v0.3.1.apk"
    $apkBytes = [System.IO.File]::ReadAllBytes((Resolve-Path $apkPath).Path)
    
    $uploadHeaders = $headers.Clone()
    $uploadHeaders["Content-Type"] = "application/vnd.android.package-archive"
    
    $assetResponse = Invoke-RestMethod -Uri $uploadUrl -Method Post -Headers $uploadHeaders -Body $apkBytes
    
    Write-Host ""
    Write-Host "SUCCESS! APK Release Complete!" -ForegroundColor Green -BackgroundColor Black
    Write-Host "Download APK from: $($releaseResponse.html_url)" -ForegroundColor White
    Write-Host "Direct APK link: $($assetResponse.browser_download_url)" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Your Bahai Resource Library APK is now available on GitHub!" -ForegroundColor Green

} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response: $responseBody" -ForegroundColor Yellow
    }
    exit 1
}