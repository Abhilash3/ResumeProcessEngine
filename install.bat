@echo off

echo ###################################################################
echo #                                                                 #
echo # Performing initial setup.......                                 #
echo # This is a one time setup and need not be triggered again.       #
echo #                                                                 #
echo ###################################################################

set /p skipJ="Is Java 8 already installed: "
if /i %skipJ% == y      goto:skipJavaInstall
if /i %skipJ% == yes    goto:skipJavaInstall

Powershell.exe -executionpolicy remotesigned Invoke-WebRequest -OutFile javaInstaller.exe http://javadl.oracle.com/webapps/download/AutoDL?BundleId=230511_2f38c3b165be4555a1fa6e98c45e0808
Powershell.exe -executionpolicy remotesigned ./javaInstaller.exe

echo Java installation started.......

:skipJavaInstall

echo If you don't need local MongoDb server, say yes.
set /p skipM="Is MongoDb already installed: "
if /i %skipM% == y      goto:skipMongodbInstall
if /i %skipM% == yes    goto:skipMongodbInstall

Powershell.exe -executionpolicy remotesigned Invoke-WebRequest -OutFile mongodbInstaller.msi https://fastdl.mongodb.org/win32/mongodb-win32-x86_64-2008plus-ssl-3.6.2-signed.msi
Powershell.exe -executionpolicy remotesigned ./mongodbInstaller.msi

echo MongoDB installation started.......

:skipMongodbInstall
echo ###################################################################
echo #                                                                 #
echo # Creating application.properties.......                          #
echo # Please provide required configuration details.                  #
echo # You can edit them but that would take affect after app restart. #
echo #                                                                 #
echo ###################################################################

set /p hostM="MongoDB host: "
set /p portM="MongoDB port: "
set /p dbM="MongoDB db: "
set /p user="MongoDB username: "
set /p pass="MongoDB password: "
set /p path="Resume base path: "

echo spring.data.mongodb.uri=mongodb://%hostM%:%portM%/%dbM%> "application.properties"

if not defined user goto:skipCred
if not defined pass goto:skipCred

echo spring.data.mongodb.username=%user%>> "application.properties"
echo spring.data.mongodb.password=%pass%>> "application.properties"

:skipCred
echo application.resume.location=%path%>> "application.properties"

echo ###################################################################
echo #                                                                 #
echo # Creating start script.                                          #
echo #                                                                 #
echo ###################################################################

echo @echo off> "start.bat"

if /i %skipM% == y      goto:skipMongodbSteps
if /i %skipM% == yes    goto:skipMongodbSteps

echo mkdir ./data
set /p mongoLoc="MongoDB main dir: "
echo @start cmd /k "%mongoLoc%/Server/3.6/bin/mongod.exe" --dbpath ./data>> "start.bat"

:skipMongodbSteps

set /p javaLoc="Java8 jdk dir: "
echo @start cmd /k "%javaLoc%/bin/java" -jar ./resume_engine.jar>> "start.bat"

echo ###################################################################
echo #                                                                 #
echo # Script created.                                                 #
echo # To start application, run start.bat                             #
echo # To stop application, close command prompts started by start.bat #
echo #                                                                 #
echo ###################################################################

pause
