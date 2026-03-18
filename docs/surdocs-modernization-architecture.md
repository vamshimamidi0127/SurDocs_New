# SURDOCS_EXTERNAL Modernization Architecture

## 1. High-Level Architecture Diagram

```text
Users
  |
  v
[React SPA]
  - Responsive UI
  - React Router
  - Search pages
  - Document results/detail pages
  - Viewer launch flow
  |
  | HTTPS / JSON
  v
[Spring Boot Web App]
  - Controllers
  - Application Services
  - Search Orchestration
  - Viewer URL Service
  - Security / Session Context
  |
  +------------------------------+
  |                              |
  v                              v
[SQL Server]                 [FileNet WSI Java APIs]
  - Search SQL                 - ObjectStore connection
  - Counts                     - Document metadata retrieval
  - Subtype drilldowns         - Document/openable object resolution
  - Map/book/page lookups
  |
  v
[Existing FileNet CE schema views/tables]

Document Viewing Path:
React -> Spring Boot -> Viewer URL Builder -> Redirect/launch -> FileNet Navigator / approved viewer URL
```

## 2. Target Folder Structure

```text
SURDOCS_EXTERNAL/
  docs/
    surdocs-modernization-architecture.md
  frontend/
    package.json
    src/
      app/
        router/
          AppRouter.tsx
          routes.tsx
        layout/
          AppShell.tsx
          Header.tsx
          Footer.tsx
          Breadcrumbs.tsx
      components/
        common/
          LoadingSpinner.tsx
          ErrorBanner.tsx
          EmptyState.tsx
          DataTable.tsx
          ResponsiveCardList.tsx
        search/
          SearchModeSelector.tsx
          QuickSearchForm.tsx
          AdvancedSearchMenu.tsx
          SquareSuffixLotSelector.tsx
          DocumentTypeCountList.tsx
          SubtypeCountList.tsx
          BookSelector.tsx
          PageSelector.tsx
          MapNumberSearch.tsx
      features/
        bootstrap/
          bootstrapApi.ts
          bootstrapStore.ts
        quick-search/
          quickSearchApi.ts
          quickSearchPage.tsx
          quickSearchResultsPage.tsx
        advanced-search/
          advancedSearchPage.tsx
          bookPageSearchPage.tsx
          mapSearchPage.tsx
          indexCardSearchPage.tsx
        documents/
          documentApi.ts
          documentListPage.tsx
          documentDetailPage.tsx
          documentViewerLauncher.tsx
      services/
        apiClient.ts
        authSession.ts
      hooks/
        useBootstrap.ts
        useQuickSearch.ts
        useAdvancedSearch.ts
      types/
        api.ts
        search.ts
        document.ts
      styles/
        theme.css
        layout.css
      main.tsx
  backend/
    pom.xml
    src/main/java/gov/dc/surdocs/
      config/
        WebConfig.java
        JacksonConfig.java
        SecurityConfig.java
        SessionConfig.java
        DataSourceConfig.java
        FileNetConfig.java
        WebSphereCompatibilityConfig.java
      controller/
        BootstrapController.java
        QuickSearchController.java
        AdvancedSearchController.java
        DocumentController.java
      service/
        BootstrapService.java
        QuickSearchService.java
        AdvancedSearchService.java
        DocumentService.java
        ViewerLaunchService.java
        FileNetSessionService.java
      dao/
        sqlserver/
          SearchQueryDao.java
          DocumentCountDao.java
          SubtypeQueryDao.java
          BookPageDao.java
          MapQueryDao.java
      filenet/
        FileNetConnectionManager.java
        FileNetDocumentGateway.java
        FileNetObjectStoreProvider.java
      model/
        dto/
          BootstrapResponse.java
          QuickSearchRequest.java
          QuickSearchResponse.java
          DocumentSummaryDto.java
          DocumentDetailDto.java
          ViewerLaunchResponse.java
        domain/
          SearchCriteria.java
          DocumentSummary.java
          DocumentMetadata.java
      mapper/
        SearchResponseMapper.java
        DocumentMapper.java
      exception/
        BusinessException.java
        NotFoundException.java
        IntegrationException.java
      util/
        SslFormatter.java
        MapNumberFormatter.java
        NavigationRules.java
    src/main/resources/
      application.yml
      application-local.yml
      application-dev.yml
      application-qa.yml
      application-prod.yml
      sql/
        search/
          suffix-options.sql
          lot-options.sql
          document-counts.sql
          subtype-counts.sql
          document-list.sql
          book-types.sql
          map-types.sql
          index-card-types.sql
          page-list.sql
          map-lookup.sql
```

## 3. Component Breakdown

### React components

#### App shell

- `AppShell`
  - responsive layout, global navigation, breadcrumb container
- `AppRouter`
  - route definitions replacing JSP/JSF navigation outcomes

#### Search components

- `SearchModeSelector`
  - replaces `t1.jsp`
- `QuickSearchForm`
  - replaces `t2.jsp`
- `DocumentTypeCountList`
  - replaces `t3.jsp`
- `SubtypeCountList`
  - replaces `t5.jsp`
- `AdvancedSearchMenu`
  - replaces `Level103Menu.jsp`
- `BookSelector`
  - replaces book/subtype dropdown portions of `Level106BookTypes.jsp` and `Level107DocSubTypeSel.jsp`
- `PageSelector`
  - replaces page dropdown logic in `Level107DocSubTypeSel.jsp`
- `MapNumberSearch`
  - replaces `Level106MapTypes.jsp`

#### Document components

- `DocumentListPage`
  - replaces `DocDetailListDisplayServlet`
- `DocumentDetailPage`
  - optional metadata preview screen before launch
- `DocumentViewerLauncher`
  - launches navigator/viewer URL in a controlled way

#### Shared UX components

- `LoadingSpinner`
- `ErrorBanner`
- `EmptyState`
- `DataTable`
- `ResponsiveCardList`

### Backend services

#### Controller layer

- `BootstrapController`
  - app initialization data
- `QuickSearchController`
  - square/suffix/lot flows
- `AdvancedSearchController`
  - book/map/index-card flows
- `DocumentController`
  - document detail and viewer launch

#### Service layer

- `BootstrapService`
  - preload dropdown/reference data
- `QuickSearchService`
  - orchestrates quick-search flow
- `AdvancedSearchService`
  - orchestrates advanced search flow
- `DocumentService`
  - combines SQL detail lookup with FileNet metadata lookup when needed
- `ViewerLaunchService`
  - builds approved FileNet Navigator/viewer URL
- `FileNetSessionService`
  - manages WSI login/session/ObjectStore access

#### DAO layer

- `SearchQueryDao`
  - suffix, lot, SSL search queries
- `DocumentCountDao`
  - document type counts and subtype counts
- `SubtypeQueryDao`
  - document detail rows for selected type/subtype
- `BookPageDao`
  - book types, books, pages
- `MapQueryDao`
  - map number queries

#### FileNet integration layer

- `FileNetConnectionManager`
  - central WSI connection handling
- `FileNetObjectStoreProvider`
  - resolves ObjectStore
- `FileNetDocumentGateway`
  - fetches document metadata or validates document existence

## 4. Routing Model

```text
/                     -> landing/start
/search               -> search mode selection
/search/quick         -> quick search form
/search/quick/results -> document type counts
/search/quick/subtypes -> subtype selection/results
/search/advanced      -> advanced menu
/search/advanced/books
/search/advanced/maps
/search/advanced/index-cards
/documents            -> document list
/documents/:id        -> document detail
/documents/:id/view   -> viewer launch handoff
```

This replaces JSF outcome-based navigation with explicit frontend routes.

## 5. Data Flow

### A. Quick search

```text
User enters Square / Parcel / Reservation / Appropriation
  -> React QuickSearchForm validates input
  -> React calls Spring Boot quick-search endpoints
  -> QuickSearchController
  -> QuickSearchService
  -> SearchQueryDao / DocumentCountDao
  -> SQL Server queries against FileNet CE schema
  -> Service assembles response DTO
  -> React renders counts and subtype options
```

Detailed steps:

1. User selects query type and enters square.
2. Frontend requests suffix options.
3. Frontend requests lot options when needed.
4. Frontend requests document counts for the computed SSL.
5. User selects a document class.
6. Frontend requests subtype counts.
7. User selects a subtype.
8. Frontend requests matching document list.

### B. Advanced search

```text
User chooses Books / Maps / Index Cards
  -> React advanced search page
  -> Spring Boot AdvancedSearchController
  -> AdvancedSearchService
  -> DAO layer queries SQL Server
  -> Results returned for subtype/book/page selection
  -> Optional document detail lookup
```

Detailed variants:

- Books:
  1. Load book types.
  2. Select subtype.
  3. Load books/folders.
  4. Select book.
  5. Load page/document options.
  6. Select document.

- Maps:
  1. Enter map number.
  2. Normalize format server-side.
  3. Query matching map documents in SQL Server.
  4. Return either direct document match or subtype/book drilldown choices.

- Index cards:
  1. Load index card types.
  2. Select subtype.
  3. Load books/pages or document list.
  4. Preserve existing business rules for pre/post split if still required.

### C. Document viewing

```text
User clicks View
  -> React calls document launch endpoint
  -> DocumentController
  -> DocumentService gets SQL-backed detail
  -> FileNetSessionService gets ObjectStore via WSI
  -> FileNetDocumentGateway optionally validates/fetches metadata
  -> ViewerLaunchService builds FileNet Navigator/viewer URL
  -> Backend returns launch URL
  -> React opens viewer in new tab/window
```

Design rule:

- SQL remains the system of record for search/navigation.
- FileNet WSI is used at the document boundary:
  - ObjectStore connection
  - Document existence/metadata retrieval
  - any future document-level operations

## 6. Enterprise Separation of Concerns

### Frontend responsibilities

- Routing and responsive rendering
- Form validation and state management
- Calling backend APIs
- Presenting search results and viewer launch actions

### Backend responsibilities

- Business orchestration
- Input normalization and validation
- SQL query execution
- FileNet WSI integration
- Session handling and security
- Error handling and audit logging

### DAO responsibilities

- Only database access
- No business logic
- SQL externalized in resource files where practical

### FileNet gateway responsibilities

- Encapsulate WSI API usage
- Hide ObjectStore/session details from services
- Provide typed methods like:
  - `connectObjectStore()`
  - `getDocumentMetadata(documentId)`
  - `validateDocumentAccess(documentId)`

## 7. Recommended Backend API Surface

Even though FileNet itself is not accessed through REST, the React frontend should still call Spring Boot JSON endpoints.

Recommended internal application endpoints:

- `GET /api/bootstrap`
- `GET /api/search/quick/suffixes`
- `GET /api/search/quick/lots`
- `GET /api/search/quick/document-counts`
- `GET /api/search/quick/subtype-counts`
- `GET /api/search/advanced/book-types`
- `GET /api/search/advanced/map-types`
- `GET /api/search/advanced/index-card-types`
- `GET /api/search/books`
- `GET /api/search/pages`
- `GET /api/search/maps`
- `GET /api/documents`
- `GET /api/documents/{id}`
- `POST /api/documents/{id}/launch`

Removed from target scope:

- User comment endpoints and UI

## 8. Security and Session Strategy

- Use enterprise SSO if available in WebSphere.
- Keep Spring Security minimal and compatible with container-managed auth.
- Store no FileNet credentials in the browser.
- Backend manages FileNet WSI connectivity and session lifecycle.
- Use HTTPS only.
- Add server-side audit logging for:
  - search requests
  - document launch requests
  - integration failures

## 9. Responsive React UX Strategy

- Desktop:
  - multi-column search/filter layout
  - tables for document counts and result lists
- Tablet:
  - stacked filters with condensed results
- Mobile:
  - step-based search flow
  - card layout for counts/results
  - prominent document launch action

Recommended frontend patterns:

- React Router for navigation
- TanStack Query or equivalent for async data fetching
- Typed DTOs generated from backend contracts if possible
- Centralized API client and error handling

## 10. Deployment Approach

### A. Local Mac development

Frontend:

- Node.js LTS
- `npm install`
- `npm run dev`

Backend:

- Java 8 or 11 based on target WebSphere version compatibility
- Maven build
- Run Spring Boot locally with `application-local.yml`
- Connect to lower environment SQL Server/FileNet or approved dev endpoints

Recommended local setup:

- Frontend on `http://localhost:5173` or similar
- Backend on `http://localhost:8080`
- CORS enabled only for local profile

### B. Windows setup

Developer workstation:

- Install JDK matching WebSphere target
- Install Maven
- Install Node.js LTS
- Configure SQL Server JDBC access
- Configure FileNet WSI client jars and environment-specific properties

Recommended Windows conventions:

- Externalize all environment values in property files or WebSphere JNDI
- Avoid hard-coded paths
- Provide one-click scripts:
  - `start-frontend.bat`
  - `start-backend.bat`
  - `build-war.bat`

### C. WAR deployment on WebSphere

Backend packaging:

- Package Spring Boot backend as WAR, not executable JAR
- Extend `SpringBootServletInitializer`
- Use WebSphere-compatible servlet container settings

Deployment model:

1. Build frontend static assets.
2. Copy frontend build output into Spring Boot static resources or serve from enterprise web tier.
3. Package backend as WAR.
4. Deploy WAR to WebSphere.
5. Configure:
   - JDBC datasource or encrypted DB properties
   - FileNet WSI connection properties
   - logging
   - security realm / SSO

Recommended enterprise deployment options:

- Option 1: Single deployable WAR
  - React build bundled into Spring Boot WAR
  - simplest operations model

- Option 2: Split deploy
  - React served by enterprise web server/CDN
  - Spring Boot WAR deployed separately on WebSphere
  - better frontend release independence

For WebSphere-first organizations, Option 1 is usually the lowest-friction starting point.

## 11. Business Logic Preservation Rules

- Preserve all SQL-driven search rules exactly during phase 1.
- Preserve map number normalization.
- Preserve existing document type/subtype drilldown behavior.
- Preserve current viewer URL strategy unless governance requires a new viewer.
- Remove all user comment code, routes, menu items, and backend processing.
- Do not reimplement inactive servlets unless a business owner confirms they are needed.

## 12. Recommended Migration Phases

### Phase 1

- Build Spring Boot backend facade over existing SQL logic
- Add FileNet WSI connection module
- Build React shell and quick search

### Phase 2

- Implement advanced search flows
- Implement document launch flow
- Remove JSP/JSF screens from active use

### Phase 3

- Harden security, logging, and monitoring
- Optimize SQL and connection pooling
- Retire unused legacy servlets and pages

## 13. Final Recommendation

The cleanest modernization path is:

- React SPA for all user interaction
- Spring Boot WAR on WebSphere
- SQL Server retained for search/navigation queries
- FileNet WSI retained for ObjectStore/document-level operations
- FileNet Navigator/viewer URLs retained for launch
- User Comment feature removed entirely

This gives you a modern UI and maintainable service architecture without forcing a risky rewrite of the existing search model.
