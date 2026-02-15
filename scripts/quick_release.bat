@echo off
REM Quick GitHub Release Setup with Personal Access Token
REM Uses GitHub API directly (no GitHub CLI OAuth needed)

echo ============================================
echo   Quick GitHub Release Creator Setup
echo ============================================
echo.

REM Check if token is set as environment variable
if defined GITHUB_TOKEN (
    echo ✅ Using GITHUB_TOKEN environment variable
    powershell -ExecutionPolicy Bypass -File "scripts\create_release_api.ps1" -Token "%GITHUB_TOKEN%"
    goto :end
)

REM Check if token provided as parameter
if not "%1"=="" (
    echo ✅ Using provided token
    powershell -ExecutionPolicy Bypass -File "scripts\create_release_api.ps1" -Token "%1"
    goto :end
)

REM No token found - show instructions
echo ⚠️  GitHub Personal Access Token needed for automation
echo.
echo 🔧 QUICK SETUP (2 minutes):
echo 1. Browser opened: https://github.com/settings/tokens/new
echo 2. Name: "Bahai App Release"
echo 3. Expiration: 90 days
echo 4. Check: "repo" (full repository access)
echo 5. Click "Generate token"
echo 6. Copy the token
echo.
echo 🚀 THEN RUN ONE OF:
echo   quick_release.bat YOUR_TOKEN_HERE
echo   OR
echo   set GITHUB_TOKEN=YOUR_TOKEN_HERE
echo   quick_release.bat
echo.
start https://github.com/settings/tokens/new

:end
pause