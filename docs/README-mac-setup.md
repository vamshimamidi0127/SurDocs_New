# SURDOCS_EXTERNAL Mac Setup Guide

This guide explains how to run the modernized SURDOCS_EXTERNAL project on macOS.

## 1. Clone the project

```bash
git clone https://github.com/your-org/SURDOCS_EXTERNAL.git
cd SURDOCS_EXTERNAL
```

Main modules:

- [backend](/Users/vamshimamidi/Downloads/SurDocs_External/backend)
- [frontend](/Users/vamshimamidi/Downloads/SurDocs_External/frontend)

## 2. Install Java 8

Recommended: Eclipse Temurin 8 via Homebrew.

```bash
brew install --cask temurin8
/usr/libexec/java_home -V
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
export PATH="$JAVA_HOME/bin:$PATH"
java -version
```

Expected result: Java 8 is active.

## 3. Install Maven

```bash
brew install maven
mvn -version
```

## 4. Install Node.js

```bash
brew install node
node -v
npm -v
```

## 5. Configure backend properties

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

## 6. FileNet JAR setup

Place proprietary FileNet jars here:

- [backend/lib/filenet](/Users/vamshimamidi/Downloads/SurDocs_External/backend/lib/filenet)

Required jars:

- `Jace.jar`

Reference:

- [README.md](/Users/vamshimamidi/Downloads/SurDocs_External/backend/lib/filenet/README.md)

Without these jars, the backend will not compile.
Without `Jace.jar`, the backend will not compile.

## 7. Optional shell environment variables

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
export PATH="$JAVA_HOME/bin:$PATH"
```

## 8. Build and run backend

From:

- [backend](/Users/vamshimamidi/Downloads/SurDocs_External/backend)

Run:

```bash
mvn clean package
mvn spring-boot:run
```

If needed:

```bash
java -jar target/surdocs-external-backend.war
```

Backend URL:

```text
http://localhost:8080/surdocs-external
```

## 9. Configure frontend

Create:

- `frontend/.env`

Add:

```properties
VITE_RECAPTCHA_SITE_KEY=YOUR_RECAPTCHA_SITE_KEY
```

The frontend API base URL is currently defined in:

- [apiClient.ts](/Users/vamshimamidi/Downloads/SurDocs_External/frontend/src/services/apiClient.ts)

Default:

```ts
baseURL: "http://localhost:8080/surdocs-external"
```

## 10. Build and run frontend

From:

- [frontend](/Users/vamshimamidi/Downloads/SurDocs_External/frontend)

Run:

```bash
npm install
npm run dev
```

Frontend URL:

```text
http://localhost:5173
```

## 11. Recommended startup order

1. Start backend
2. Start frontend
3. Open `http://localhost:5173`

## 12. Quick verification

Backend endpoint check:

```bash
curl "http://localhost:8080/surdocs-external/search/ssl/options?square=0564"
```

Frontend checks:

- home page loads
- quick search page loads
- lot/suffix requests work
- CAPTCHA modal appears during document open flow

## 13. Common issues

### Java version mismatch

Check:

```bash
java -version
```

It must show Java 8.

### Backend build fails

Most likely cause:

- missing FileNet jars in `backend/lib/filenet`

### SQL connection failure

Check:

- SQL Server host
- username/password
- firewall/network access

### FileNet connection failure

Check:

- `filenet.uri`
- credentials
- Object Store name
- network access to CE WSI endpoint

### CAPTCHA not rendering

Check:

- `frontend/.env`
- `VITE_RECAPTCHA_SITE_KEY`

### Frontend cannot reach backend

Check:

- backend is running on `8080`
- frontend API base URL is correct
- CORS settings match localhost

## 14. Build output

WAR file is created at:

- [surdocs-external-backend.war](/Users/vamshimamidi/Downloads/SurDocs_External/backend/target/surdocs-external-backend.war)
