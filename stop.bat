@echo off
set /p springPidValue= < ./pid.file
taskkill /f /pid %springPidValue%
taskkill /f /im mongod.exe
echo > ./pid.file