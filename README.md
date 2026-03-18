# SURDOCS_EXTERNAL

Modernized SURDOCS_EXTERNAL project structure with:

- Spring Boot backend packaged as a WAR for WebSphere
- React + TypeScript frontend using Vite
- architecture, API, setup, and optimization docs
- root-level FileNet library location for enterprise builds

## Project Layout

```text
SURDOCS_EXTERNAL/
  backend/
    pom.xml
    src/
      main/
        java/
        resources/
        webapp/
  frontend/
    package.json
    src/
  docs/
    README-mac-setup.md
    README-windows-websphere-setup.md
    sql-optimization-guide.md
    surdocs-modernization-architecture.md
    surdocs-rest-openapi.yaml
    surdocs-technical-mapping.md
  lib/
    filenet/
      README.md
```

## Modules

### Backend

- Location: [backend](/Users/vamshimamidi/Downloads/SurDocs_External/backend)
- Stack:
  - Java 8
  - Spring Boot
  - WAR packaging
  - SQL Server JDBC
  - FileNet WSI Java APIs
- Purpose:
  - expose application APIs for the React frontend
  - query SQL Server for search/count flows
  - connect to FileNet Content Engine via WSI when needed
  - generate FileNet Navigator viewer URLs

### Frontend

- Location: [frontend](/Users/vamshimamidi/Downloads/SurDocs_External/frontend)
- Stack:
  - React
  - TypeScript
  - Vite
  - Material UI
  - React Router
  - Axios
- Purpose:
  - replace legacy JSP/JSF screens
  - provide responsive UI for quick search, document listing, CAPTCHA, and viewer launch

### Docs

- Location: [docs](/Users/vamshimamidi/Downloads/SurDocs_External/docs)
- Includes:
  - legacy mapping
  - modernization architecture
  - OpenAPI contract
  - Mac setup guide
  - Windows + WebSphere setup guide
  - SQL optimization guide

### FileNet Libraries

- Location: [lib/filenet](/Users/vamshimamidi/Downloads/SurDocs_External/lib/filenet)
- Purpose:
  - holds proprietary FileNet jars required for backend compilation/runtime

Expected jars:

- `Jace.jar`

## Getting Started

### Backend

From [backend](/Users/vamshimamidi/Downloads/SurDocs_External/backend):

```bash
mvn clean package
```

### Frontend

From [frontend](/Users/vamshimamidi/Downloads/SurDocs_External/frontend):

```bash
npm install
npm run dev
```

## Setup Guides

- Mac: [README-mac-setup.md](/Users/vamshimamidi/Downloads/SurDocs_External/docs/README-mac-setup.md)
- Windows + WebSphere: [README-windows-websphere-setup.md](/Users/vamshimamidi/Downloads/SurDocs_External/docs/README-windows-websphere-setup.md)

## Notes

- Legacy JSP/JSF application artifacts have been removed.
- WebSphere deployment compatibility is preserved in the backend WAR structure.
- The backend build requires proprietary FileNet jars in [lib/filenet](/Users/vamshimamidi/Downloads/SurDocs_External/lib/filenet).
- Frontend and backend are now cleanly separated.
