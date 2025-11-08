@echo off
REM Launch script for Bisaya++ IDE
echo Starting Bisaya++ IDE...
cd /d "%~dp0"
call gradlew.bat :app:runIDE --console=plain
