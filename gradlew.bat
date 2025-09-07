@echo off
REM Gradle start script for Windows

set DIRNAME=%~dp0
cd %DIRNAME%
java -jar gradle\wrapper\gradle-wrapper.jar %*
