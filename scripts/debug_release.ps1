# Debug GitHub Release Creation
$Token = $args[0]
if (-not $Token) {
    Write-Host "Usage: debug_release.ps1 YOUR_GITHUB_TOKEN" -ForegroundColor Yellow
    exit 1
}
$headers = @{
    "Authorization" = "token $Token"
    "Accept" = "application/vnd.github.v3+json"
    "User-Agent" = "PowerShell-Release-Script"
}

Write-Host "Testing GitHub API access..." -ForegroundColor Cyan
try {
    $user = Invoke-RestMethod -Uri "https://api.github.com/user" -Headers $headers
    Write-Host "✅ Authenticated as: $($user.login)" -ForegroundColor Green
} catch {
    Write-Host "❌ Authentication failed" -ForegroundColor Red
    Write-Host $_.Exception.Message
    exit 1
}

Write-Host "Checking repository access..." -ForegroundColor Cyan
try {
    $repo = Invoke-RestMethod -Uri "https://api.github.com/repos/djtaylor333/bahai-resource-library" -Headers $headers
    Write-Host "✅ Repository access confirmed" -ForegroundColor Green
} catch {
    Write-Host "❌ Repository access failed" -ForegroundColor Red  
    Write-Host $_.Exception.Message
    exit 1
}

Write-Host "Verifying tag exists..." -ForegroundColor Cyan
try {
    $tag = Invoke-RestMethod -Uri "https://api.github.com/repos/djtaylor333/bahai-resource-library/git/refs/tags/v0.3.1" -Headers $headers
    Write-Host "✅ Tag v0.3.1 found: $($tag.object.sha)" -ForegroundColor Green
} catch {
    Write-Host "❌ Tag not found" -ForegroundColor Red
    Write-Host $_.Exception.Message
    exit 1
}

$releaseNotes = Get-Content "releases\v0.3.1\RELEASE_NOTES.md" -Raw -ErrorAction SilentlyCatch
if (-not $releaseNotes) {
    $releaseNotes = "Bahai Resource Library v0.3.1 Android Release"
    Write-Host "⚠️  Using default release notes" -ForegroundColor Yellow
}

$releaseData = @{
    tag_name = "v0.3.1"
    target_commitish = "main"
    name = "Bahai Resource Library v0.3.1 - Android Release"
    body = $releaseNotes
    draft = $false
    prerelease = $false
}

Write-Host "Creating release..." -ForegroundColor Cyan
Write-Host "Request data:" -ForegroundColor Gray
Write-Host ($releaseData | ConvertTo-Json) -ForegroundColor Gray

try {
    $releaseResponse = Invoke-RestMethod -Uri "https://api.github.com/repos/djtaylor333/bahai-resource-library/releases" -Method Post -Headers $headers -Body ($releaseData | ConvertTo-Json) -ContentType "application/json"
    Write-Host "✅ Release created successfully!" -ForegroundColor Green
    Write-Host "Release URL: $($releaseResponse.html_url)" -ForegroundColor Cyan
    
    # Now upload APK
    $apkPath = "releases\v0.3.1\bahai-resource-library-v0.3.1.apk"
    if (Test-Path $apkPath) {
        Write-Host "Uploading APK..." -ForegroundColor Cyan
        $uploadUrl = $releaseResponse.upload_url -replace '\{\?name,label\}', "?name=bahai-resource-library-v0.3.1.apk"
        $apkBytes = [System.IO.File]::ReadAllBytes((Resolve-Path $apkPath).Path)
        
        $uploadHeaders = $headers.Clone()
        $uploadHeaders["Content-Type"] = "application/vnd.android.package-archive"
        
        $assetResponse = Invoke-RestMethod -Uri $uploadUrl -Method Post -Headers $uploadHeaders -Body $apkBytes
        Write-Host "✅ APK uploaded!" -ForegroundColor Green
        Write-Host "Direct download: $($assetResponse.browser_download_url)" -ForegroundColor Cyan
    }
    
} catch {
    Write-Host "❌ Release creation failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $stream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response body: $responseBody" -ForegroundColor Yellow
    }
}