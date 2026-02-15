@echo off
REM GitHub Release Creation Script for Bahai Resource Library v0.3.1
REM This script creates a GitHub release with the APK as a downloadable asset

echo =========================================================
echo         Bahai Resource Library v0.3.1 Release Creator
echo =========================================================
echo.

REM Check if GitHub CLI is installed
gh --version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: GitHub CLI not found
    echo.
    echo Installing GitHub CLI...
    winget install --id GitHub.cli --silent --accept-package-agreements --accept-source-agreements
    echo GitHub CLI installed. Please restart this script.
    pause
    exit /b 1
)

REM Check if authenticated
gh api user >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo WARNING: Not authenticated with GitHub
    echo.
    echo Please authenticate first:
    echo   gh auth login --web
    echo.
    echo Then run this script again.
    pause
    exit /b 1
)

echo ✅ GitHub CLI found and authenticated
echo.
echo Creating release v0.3.1...

REM Create the release with APK upload
gh release create v0.3.1 ^
    --title "Bahá'í Resource Library v0.3.1 - Android Release" ^
    --notes-file "releases\v0.3.1\RELEASE_NOTES.md" ^
    "releases\v0.3.1\bahai-resource-library-v0.3.1.apk#bahai-resource-library-v0.3.1.apk"

if %ERRORLEVEL% equ 0 (
    echo.
    echo =========================================================  
    echo ✅ SUCCESS! GitHub Release Created
    echo =========================================================
    echo.
    echo 📱 APK Download URL:
    echo https://github.com/djtaylor333/bahai-resource-library/releases/tag/v0.3.1
    echo.
    echo 🎯 The APK can now be downloaded directly from GitHub!
    echo    File: bahai-resource-library-v0.3.1.apk (2,090 KB)
    echo.
) else (
    echo.
    echo ❌ ERROR: Failed to create GitHub release
    echo Check if tag v0.3.1 already exists or try manually:
    echo https://github.com/djtaylor333/bahai-resource-library/releases/new
)

echo.
echo Press any key to exit...
pause