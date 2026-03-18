# SURDOCS_EXTERNAL Windows Setup and WebSphere Deployment Guide

This guide explains how to build the project on Windows and deploy the backend WAR to WebSphere.

## 1. Clone the project from GitHub

Open PowerShell or Git Bash:

```powershell
git clone https://github.com/your-org/SURDOCS_EXTERNAL.git
cd SURDOCS_EXTERNAL
```

Main modules:

- [backend](/Users/vamshimamidi/Downloads/SurDocs_External/backend)
- [frontend](/Users/vamshimamidi/Downloads/SurDocs_External/frontend)

## 2. Install required software

Install:

- Java 8 JDK
- Maven 3.8+
- Node.js LTS
- Git for Windows

Recommended JDK:

- Eclipse Temurin 8

Verify installation:

```powershell
java -version
mvn -version
node -v
npm -v
git --version
```

## 3. Configure JAVA_HOME

Example:

```powershell
setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-8.x.x.x-hotspot"
```

Ensure `%JAVA_HOME%\bin` is on `Path`.

Open a new terminal and verify:

```powershell
echo $env:JAVA_HOME
java -version
```

## 4. Configure backend properties

Edit:

- [application.properties](/Users/vamshimamidi/Downloads/SurDocs_External/backend/src/main/resources/application.properties)

Set values for:

```properties
spring.datasource.url=jdbc:sqlserver://YOUR_SQLSERVER_HOST:1433;databaseName=YOUR_DATABASE;encrypt=false
spring.datasource.username=YOUR_SQLSERVER_USERNAME
spring.datasource.password=YOUR_SQLSERVER_PASSWORD

filenet.uri=http://YOUR_FILENET_CE_URI/wsi/FNCEWS40MTOM/
filenet.username=YOUR_FILENET_USERNAME
filenet.password=YOUR_FILENET_PASSWORD
filenet.object-store-name=YOUR_OBJECT_STORE_NAME

google.recaptcha.secret=YOUR_RECAPTCHA_SECRET
```

## 5. FileNet JAR setup

Place proprietary FileNet jars in:

- [backend/lib/filenet](/Users/vamshimamidi/Downloads/SurDocs_External/backend/lib/filenet)

Required:

- `Jace.jar`

Windows example:

```text
C:\dev\SURDOCS_EXTERNAL\backend\lib\filenet\Jace.jar
```

Reference:

- [README.md](/Users/vamshimamidi/Downloads/SurDocs_External/backend/lib/filenet/README.md)

These jars are required for local build because the backend `pom.xml` uses `systemPath`.

## 6. Build the backend WAR

From:

- [backend](/Users/vamshimamidi/Downloads/SurDocs_External/backend)

Run:

```powershell
mvn clean package
```

Expected WAR output:

- [surdocs-external-backend.war](/Users/vamshimamidi/Downloads/SurDocs_External/backend/target/surdocs-external-backend.war)

Optional local run:

```powershell
mvn spring-boot:run
```

or

```powershell
java -jar target\surdocs-external-backend.war
```

## 7. Configure frontend

Create:

- `frontend/.env`

Add:

```properties
VITE_RECAPTCHA_SITE_KEY=YOUR_RECAPTCHA_SITE_KEY
```

From:

- [frontend](/Users/vamshimamidi/Downloads/SurDocs_External/frontend)

Run:

```powershell
npm install
npm run build
npm run dev
```

Default frontend URL:

```text
http://localhost:5173
```

## 8. Deploy WAR to WebSphere

Deploy:

- [surdocs-external-backend.war](/Users/vamshimamidi/Downloads/SurDocs_External/backend/target/surdocs-external-backend.war)

In WebSphere Admin Console:

1. Go to `Applications` -> `New Application` -> `New Enterprise Application`
2. Upload the WAR
3. Select target server or cluster
4. Confirm context root
5. Save
6. Synchronize nodes if needed
7. Start the application

Descriptors included:

- [web.xml](/Users/vamshimamidi/Downloads/SurDocs_External/backend/src/main/webapp/WEB-INF/web.xml)
- [ibm-web-ext.xml](/Users/vamshimamidi/Downloads/SurDocs_External/backend/src/main/webapp/WEB-INF/ibm-web-ext.xml)
- [ibm-web-bnd.xml](/Users/vamshimamidi/Downloads/SurDocs_External/backend/src/main/webapp/WEB-INF/ibm-web-bnd.xml)

## 9. WebSphere FileNet shared library setup

Recommended approach: configure FileNet jars as a WebSphere shared library.

Copy jars to a stable server path, for example:

```text
C:\IBM\SharedLibs\FileNet\Jace.jar
```

In WebSphere:

1. `Environment` -> `Shared Libraries`
2. Create library, for example `FileNetWSILib`
3. Set classpath to:

```text
C:\IBM\SharedLibs\FileNet\Jace.jar
```

4. Go to `Servers` -> `Application Servers` -> your server
5. Open `Class loader`
6. Add shared library reference
7. Save and restart server

Notes:

- local compilation uses jars under `backend/lib/filenet`
- WebSphere runtime uses the shared library path
- add any additional FileNet jars required by your environment

## 10. WebSphere DataSource setup

Recommended production approach: configure SQL Server in WebSphere.

In WebSphere:

1. Create J2C auth alias for DB credentials
2. Go to `Resources` -> `JDBC` -> `JDBC Providers`
3. Create or select Microsoft SQL Server JDBC provider
4. Go to `Data sources` -> `New`
5. Create a datasource, e.g.:
   - Name: `SurdocsDS`
   - JNDI: `jdbc/SurdocsDS`
6. Bind the J2C alias
7. Configure:
   - server name
   - port
   - database name
8. Test connection
9. Save configuration

Important note:

The current Spring Boot app uses `spring.datasource.*` properties directly. If you want true WebSphere-managed DB usage, the app should be updated to use JNDI datasource lookup.

## 11. Recommended WebSphere settings

### Class loading

Start with:

- `parent first`

Only change to `parent last` if you hit jar conflicts.

### Context root

Match:

```text
/surdocs-external
```

### Security

If using enterprise SSO or container-managed auth later, align Spring and WebSphere security configuration.

### Network access

Ensure the WebSphere server can reach:

- SQL Server
- FileNet CE WSI endpoint
- Google reCAPTCHA verification endpoint

## 12. Optional Windows environment setup

Temporary session:

```powershell
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-8.x.x.x-hotspot"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

Persistent:

```powershell
setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-8.x.x.x-hotspot"
```

## 13. Validation checklist

Before build:

- Java 8 installed
- Maven installed
- Node installed
- Git installed
- FileNet jars copied locally
- backend properties updated
- frontend `.env` created

Before deployment:

- WAR builds successfully
- WebSphere shared library created for FileNet jars
- datasource test succeeds
- target server/cluster selected
- context root confirmed

After deployment:

- application starts without classloading errors
- FileNet classes resolve
- SQL connection works
- `/search/ssl/options` responds
- viewer URL flow works
- CAPTCHA verification works

## 14. Common issues

### `ClassNotFoundException` for FileNet classes

Check:

- WebSphere shared library path
- jars actually present on server
- shared library attached to application/server

### SQL connection test fails

Check:

- SQL Server host/port
- JDBC provider version
- credentials
- firewall/network route

### CAPTCHA verification fails

Check:

- `google.recaptcha.secret`
- `VITE_RECAPTCHA_SITE_KEY`
- outbound access to Google verify endpoint

### App starts locally but not in WebSphere

Check:

- Java level
- classloader mode
- shared library mapping
- app context root
