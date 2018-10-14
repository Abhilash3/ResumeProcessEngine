# Resume Process Engine
This is to Process Bulk resumes and presnt them in searchable format.

#### Instructions to start
1. Download and install mongodb using link
```
https://docs.mongodb.com/manual/installation/
```
2. Open command prompt and start mongodb
```
<mongodb installation dir>/Server/<version>/bin/mongod.exe --dbpath <dir to store mongodb databases>
```
3. Download and install maven using link
```
https://maven.apache.org/download.cgi
```
4. Download and install git using link and setup 
```
https://git-scm.com/downloads
```
5. Open another command prompt and run following steps one by one
```
git clone https://github.com/Abhilash3/ResumeProcessEngine.git
cd ResumeProcessEngine
maven package
```
6. Copy target/resume_engine-*.jar to dir of your choice
7. Create application.properties in same dir with required props
```
spring.data.mongodb.uri -> mongodb connection url (mongodb://localhost:27017/<db name>)
application.resume.loader.cron -> spring cron pattern for loading resumes
application.resume.location -> base dir containing resumes
application.resume.level -> how deep do we go in base dir
```
8. Start application with
```
"<Java installation dir>/bin/java" -jar <dir>/resume_engine-*.jar
```
