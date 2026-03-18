# SURDOCS_EXTERNAL Legacy Route and Backend Mapping

## Scope

This document maps the legacy `SURDOCS_EXTERNAL` JSP/JSF application to:

- Current JSP routes and navigation paths
- Servlet endpoints from `WEB-INF/web.xml`
- Backend query and FileNet operations triggered by each screen
- A proposed REST replacement surface

## Key Findings

- The application uses JSP + legacy JSF 1.x managed beans for most navigation.
- `WEB-INF/web.xml` declares servlets, but no servlet filters are configured.
- Most search/read operations do **not** use FileNet Java APIs directly. They query the FileNet Content Engine database tables through `queries.MSSqlDB`.
- FileNet Java APIs are used primarily for:
  - Object Store login/bootstrap
  - Creating `UserComment` custom objects
  - Filing those comment objects into FileNet folders
- Document viewing is handled by generating external FileNet Navigator / Watermark URLs rather than streaming document content from this app.

## Servlet / Filter Inventory

### Configured servlets

| Servlet name | URL pattern | Class | Role |
|---|---|---|---|
| `Faces Servlet` | `/faces/*` | `javax.faces.webapp.FacesServlet` | Handles all JSP/JSF views |
| `DocDisplayServlet` | `/DocDisplayServlet` | `servlets.DocDisplayServlet` | Legacy/mostly stubbed display servlet |
| `DisplayOneServlet` | `/DisplayOneServlet` | `servlets.DisplayOneServlet` | Builds a single document access link |
| `DisplayEightServlet` | `/DisplayEightServlet` | `servlets.DisplayEightServlet` | Legacy viewer for multi-doc display |
| `StartServlet` | `/StartServlet` | `servlets.StartServlet` | Placeholder servlet; not part of active JSF flow |
| `DocTypeDispatchServlet` | `/DocTypeDispatchServlet` | `servlets.DocTypeDispatchServlet` | Legacy/incomplete dispatch servlet |
| `DocDetailListDisplayServlet` | `/DocDetailListDisplayServlet` | `servlets.DocDetailListDisplayServlet` | Renders list of document links for subtype selection |
| `CaptchaVeify` | `/CaptchaVeify` | `servlets.CaptchaVeify` | Verifies reCAPTCHA and redirects to document URL |
| `VerifyRecaptcha` | `/VerifyRecaptcha` | `servlets.VerifyRecaptcha` | Utility class with Google verification logic; servlet mapping exists but request handling is not implemented as a usable endpoint |

### Configured filters

None. `WEB-INF/web.xml` does not define any `<filter>` or `<filter-mapping>` entries.

## Current JSP Routes

### Entry and top-level navigation

| Route | Backing bean / action | Next step |
|---|---|---|
| `/faces/t0.jsp` | `T0.startAction` | Initializes app state, FileNet/Object Store config, preloads doc types, goes to `/faces/t1.jsp` |
| `/faces/t1.jsp` | `T1.quickSearch` | Goes to `/faces/t2.jsp` |
| `/faces/t1.jsp` | `T1.advancedSearch` | Goes to `/faces/Level103Menu.jsp` |
| `/faces/t1.jsp` | `T1.comment` | Goes to `/faces/UserCommentPage.jsp` |

### Quick search flow

| Route | Action | Backend operation | Next step |
|---|---|---|---|
| `/faces/t2.jsp` | Query type change (`Square`, `Parcel`, `Reservation`, `Appropriation`) | Updates session state only | Stays on page |
| `/faces/t2.jsp` | `square_processValueChange` | `MSSqlDB.suffixValues(square)`, `MSSqlDB.lotList(square/suffix)`, `MSSqlDB.docCount(ssl)` | Stays on page with dynamic fields/results |
| `/faces/t2.jsp` | `suffix_processValueChange` | `MSSqlDB.lotList(sqsuffix)`, `MSSqlDB.docCount(ssl)` | Stays on page |
| `/faces/t2.jsp` | `lot_processValueChange` | `MSSqlDB.docCount(ssl)` | Stays on page |
| `/faces/t2.jsp` | `T2.btnSSLLookup_action()` | Uses current session `queryType` outcome | Goes to `/faces/t3.jsp` |
| `/faces/t3.jsp` | `T3.docSelectAction` | `DocQueries.docSubCnts(ssl, docType)` and optionally `NewSurveyorCards.getChanges(ssl)` for index cards | Goes to `/faces/t5.jsp` |
| `/faces/t5.jsp` | `T5.docSelectedaction1` | Sets selected subtype row only | Goes to `/DocDetailListDisplayServlet` |
| `/faces/t5.jsp` | `T5.docSelectedaction2` | Filters in-memory `newCardListHold` by book type | Goes to `/faces/Level102NewCardDisplay.jsp` |
| `/faces/Level102NewCardDisplay.jsp` | Display only | No backend call in backing bean | End of visible flow in this codebase |

### Advanced search flow

| Route | Action | Backend operation | Next step |
|---|---|---|---|
| `/faces/Level103Menu.jsp` | `T6.buttonBookPage_action` | `DocQueries.getBookTypes()` | `/faces/Level106BookTypes.jsp` |
| `/faces/Level103Menu.jsp` | `T6.buttonMapNumber_action` | `DocQueries.getDocTypes("Maps")` | `/faces/Level106MapTypes.jsp` |
| `/faces/Level103Menu.jsp` | `T6.buttonIndexCards_action` | `DocQueries.getDocTypes("Index Cards")` | `/faces/Level106IndexCards.jsp` |
| `/faces/Level106BookTypes.jsp` | `Level106BookTypes.docSelectionButton_action` | `DocQueries.getBookList(path)` | `/faces/Level107DocSubTypeSel.jsp` |
| `/faces/Level106MapTypes.jsp` | Map number validator / change | `DocQueries.mapQuery(mapNumber)` | Stays on page |
| `/faces/Level106MapTypes.jsp` | `Level106MapTypes.lookupMapbtn_action` | Uses selected doc id from prior map query | `/DisplayOneServlet` |
| `/faces/Level106MapTypes.jsp` | `Level106MapTypes.mapSelectBtn_action` | `DocQueries.getBookList(path)` | `/faces/Level107DocSubTypeSel.jsp` |
| `/faces/Level106IndexCards.jsp` | `Level106IndexCards.docSelectionButton_action` | `DocQueries.getBookList(path)` | `/faces/Level107DocSubTypeSel.jsp` |
| `/faces/Level107DocSubTypeSel.jsp` | Book dropdown change | `DocQueries.getPageList(bookFolder)` | Stays on page |
| `/faces/Level107DocSubTypeSel.jsp` | Page dropdown change | `DocQueries.SingleDocDetail(docId)` then `NavigatorURLs.buildUrl()` | Stays on page with computed URL |
| `/faces/Level107DocSubTypeSel.jsp` | Navigation outcome `next` | Uses previously selected doc id | `/DisplayOneServlet` |

### Utility / support routes

| Route | Purpose | Backend operation |
|---|---|---|
| `/faces/UserCommentPage.jsp` | Comment form | `UserCommentPage.btnSubmit()` creates FileNet `CustomObject` and files it into `/SurDocs Comments/yyyy/MM` |
| `/faces/captcha.jsp` | CAPTCHA gate before opening a document | Redirects immediately if session says captcha already passed; otherwise posts to `/CaptchaVeify` |
| `/process.jsp` | Legacy redirect helper for navigator/bookmark links | Redirect-only utility |
| `/About.jsp` | Informational page | None |
| `/BookResults.jsp` | Present in web root but not wired into main navigation | No active backend action found |
| `/errorPage.jsp` | Global error view | None |

## Backend Endpoint Mapping

### Active backend endpoints used by the UI

| Endpoint | Called from | What it does |
|---|---|---|
| `/faces/*` | All JSF JSP pages | Dispatches to JSP views and invokes managed-bean actions |
| `/DocDetailListDisplayServlet` | `t5.jsp` when user selects a non-new-card subtype | Queries document details for a subtype and renders clickable document links |
| `/DisplayOneServlet` | `Level106MapTypes.jsp`, `Level107DocSubTypeSel.jsp` | Resolves a single document and renders a clickable document link |
| `/CaptchaVeify` | `captcha.jsp` form POST | Calls Google reCAPTCHA verification and redirects to final document viewer URL |

### Configured but effectively inactive / legacy endpoints

| Endpoint | Status | Notes |
|---|---|---|
| `/DocDisplayServlet` | Inactive | Main logic is commented out |
| `/DisplayEightServlet` | Legacy | Depends on session `displayDocs` and `SurveyorViewer`; not in main active route path |
| `/StartServlet` | Placeholder | Writes sample HTML only |
| `/DocTypeDispatchServlet` | Incomplete | Core logic is commented out |
| `/VerifyRecaptcha` | Not usable as HTTP endpoint | Verification logic is static utility method; request handling throws unsupported operation |

## Legacy Backend Operations by Screen

### 1. Application bootstrap

**Trigger**

- `/faces/t0.jsp` -> `T0.startAction`

**Operations**

- Loads configuration from `EZMoveParameters.getInstance()`
- Sets Object Store metadata in session/application beans
- Preloads:
  - `DocQueries.getBookTypes()`
  - `DocQueries.getDocTypes("Maps")`

**FileNet API usage**

- Indirect bootstrap through application bean `A1`, which uses:
  - `OSLogon`
  - `Factory.Connection.getConnection(...)`
  - `Factory.Domain.fetchInstance(...)`
  - `Factory.ObjectStore.fetchInstance(...)`

### 2. Quick search by SSL / square / parcel / reservation / appropriation

**Trigger**

- `/faces/t2.jsp`

**Operations**

- Fetch suffix values:
  - `MSSqlDB.suffixValues(square)`
  - SQL against `listofstring`
- Fetch lot list:
  - `MSSqlDB.lotList(sqSuffix)`
  - SQL against `listofstring`
- Fetch document counts:
  - `MSSqlDB.docCount(ssl)`
  - SQL against `docversion`, `classdefinition`, `listofstring`

**FileNet API usage**

- None in this flow
- Reads are against FileNet CE database tables, not CE Java APIs

### 3. Quick search subtype selection

**Trigger**

- `/faces/t3.jsp` -> `T3.docSelectAction`

**Operations**

- Fetch subtype counts for chosen document class:
  - `DocQueries.docSubCnts(ssl, docType)`
- For index cards, fetch pre-2003 card changes:
  - `NewSurveyorCards.getChanges(ssl)`

**FileNet API usage**

- None direct
- SQL only

### 4. Quick search document list

**Trigger**

- `/faces/t5.jsp` -> `/DocDetailListDisplayServlet`

**Operations**

- Fetch subtype-specific document detail rows:
  - `DocQueries.docSubDetail(docClassName, docSubType, ssl)`
- Fetch SSL values per selected document:
  - `DocQueries.sslList(docGuid)`
- Build downstream viewer URL:
  - `NavigatorURLs.documentURL(...)`
- Determine content size for display strategy:
  - `DocQueries.contentsize(guid)`

**FileNet API usage**

- None direct for query
- Final generated URL targets:
  - FileNet Navigator `bookmark.jsp`
  - or Watermark `ViewOne.jsp`

### 5. Advanced search by book / page

**Trigger**

- `/faces/Level103Menu.jsp`
- `/faces/Level106BookTypes.jsp`
- `/faces/Level107DocSubTypeSel.jsp`

**Operations**

- Fetch available book subtypes:
  - `DocQueries.getBookTypes()`
- Fetch child book folders:
  - `DocQueries.getBookList(path)`
- Fetch pages/documents under selected book folder:
  - `DocQueries.getPageList(folder)`
- Fetch single document detail:
  - `DocQueries.SingleDocDetail(docId)`
- Build external viewer URL:
  - `NavigatorURLs.buildUrl()`
  - `NavigatorURLs.documentURL(...)`

**FileNet API usage**

- None for search retrieval
- Downstream viewing is externalized through Navigator/Watermark URLs

### 6. Advanced search by map number

**Trigger**

- `/faces/Level106MapTypes.jsp`

**Operations**

- Validate and normalize map number:
  - `SrvyMapNumber.reformMapNumber(mapNumber)`
- Query matching map document ids:
  - `DocQueries.mapQuery(mapNumber)`
- For subtype drill-in:
  - `DocQueries.getBookList(path)`
- For direct display:
  - `/DisplayOneServlet`
  - which calls `DocQueries.SingleDocDetail(docId)` and `NavigatorURLs.documentURL(...)`

**FileNet API usage**

- None for search
- View URL generation only

### 7. Advanced search by index cards

**Trigger**

- `/faces/Level106IndexCards.jsp`
- `/faces/Level107DocSubTypeSel.jsp`

**Operations**

- Fetch index card doc types:
  - `DocQueries.getDocTypes("Index Cards")`
- Fetch folders/books for chosen subtype:
  - `DocQueries.getBookList(path)`
- Fetch pages/documents:
  - `DocQueries.getPageList(folder)`
- Fetch single document detail:
  - `DocQueries.SingleDocDetail(docId)`

**FileNet API usage**

- None for search
- View URL generation only

### 8. Comment submission

**Trigger**

- `/faces/UserCommentPage.jsp` -> `UserCommentPage.btnSubmit()`

**Operations**

- Create FileNet custom object:
  - `Factory.CustomObject.createInstance(os, "UserComment")`
- Set properties:
  - `ucName`
  - `ucComment`
- Save object:
  - `co.save(RefreshMode.REFRESH)`
- Resolve filing folder:
  - `FolderUtils.GetFolder("/SurDocs Comments/yyyy/MM", os)`
- File object into folder:
  - `fld.file(co, AutoUniqueName.NOT_AUTO_UNIQUE, null, DefineSecurityParentage.DEFINE_SECURITY_PARENTAGE)`
- Save containment relationship:
  - `rcr.save(RefreshMode.REFRESH)`

**FileNet API usage**

- Direct CE Java API usage for metadata create + filing

### 9. CAPTCHA-protected document open

**Trigger**

- `/faces/captcha.jsp` -> POST `/CaptchaVeify`

**Operations**

- Google verification:
  - `VerifyRecaptcha.verify(gRecaptchaResponse)`
  - POSTs to `https://www.google.com/recaptcha/api/siteverify`
- On success:
  - Redirects to Navigator/Watermark target URL with repository/document parameters

**FileNet API usage**

- None in this app tier
- Final redirect lands on FileNet-facing viewer URLs

## Actual FileNet API Call Inventory

### Used directly in source

| Class | FileNet API calls |
|---|---|
| `beans.A1` | Uses `OSLogon` to obtain `ObjectStore` |
| `com.docsysinc.filenet.utils.OSLogon` | `Factory.Connection.getConnection`, `Factory.Domain.fetchInstance`, `Factory.ObjectStore.fetchInstance`, `Factory.Document.fetchInstance` |
| `com.docsysinc.filenet.utils.CEConnection` | `Factory.Connection.getConnection`, `Factory.Domain.fetchInstance`, `Factory.ObjectStore.fetchInstance` |
| `beans.UserCommentPage` | `Factory.CustomObject.createInstance`, `CustomObject.getProperties().putValue`, `CustomObject.save`, `Folder.file`, `ReferentialContainmentRelationship.save` |
| `com.docsysinc.filenet.utils.FolderUtils` | `Factory.Folder.fetchInstance`, `Folder.createSubFolder`, `Folder.save` |

### Not currently used for main search flows

The following search/read flows operate via SQL against CE schema tables instead of FileNet CE Java search APIs:

- SSL suffix lookup
- Lot lookup
- Document type counts
- Document subtype counts
- Book/page folder drill-down
- Map number lookup
- Single document detail lookup
- Content size lookup

## Data Sources Behind the Legacy UI

### SQL-backed tables observed in queries

- `DocVersion`
- `ListOfString`
- `ClassDefinition`
- `Container`
- `Relationship`
- `GlobalPropertyDef`

### FileNet object model observed in API usage

- `Domain`
- `ObjectStore`
- `Document`
- `CustomObject` (`UserComment`)
- `Folder`
- `ReferentialContainmentRelationship`

## REST Replacement Mapping

### Recommended design principles

- Replace JSF screen outcomes with resource-oriented APIs
- Stop querying Content Engine database tables directly from the web tier
- Encapsulate FileNet CE access in service classes
- Return document metadata from REST, and expose a separate action to obtain a viewer/download URL
- Make CAPTCHA an explicit document-access gate if still required

### Legacy-to-REST mapping

| Legacy screen/action | Proposed REST endpoint |
|---|---|
| Start page initialization | `GET /api/v1/bootstrap` |
| Search options | handled by UI; no dedicated backend required beyond bootstrap |
| SSL suffix lookup | `GET /api/v1/search/ssl/options` |
| Lot list lookup | `GET /api/v1/search/ssl/lots` |
| Document counts by SSL | `GET /api/v1/search/ssl/document-counts` |
| Document subtype counts | `GET /api/v1/search/ssl/document-types/{documentType}/subtypes` |
| Document list for subtype | `GET /api/v1/search/documents` |
| Book type list | `GET /api/v1/book-types` |
| Map type list | `GET /api/v1/map-types` |
| Index card type list | `GET /api/v1/index-card-types` |
| Book folders for subtype | `GET /api/v1/document-subtypes/{subtype}/books` |
| Pages/documents for book folder | `GET /api/v1/books/{bookName}/pages` |
| Map number lookup | `GET /api/v1/maps/{mapNumber}` |
| Single document detail | `GET /api/v1/documents/{documentId}` |
| Open/view document | `POST /api/v1/documents/{documentId}/access-url` |
| Submit comment | `POST /api/v1/comments` |
| CAPTCHA verification | `POST /api/v1/security/recaptcha/verify` |

## Migration Notes

- `DocDisplayServlet`, `DocTypeDispatchServlet`, and `StartServlet` can likely be retired rather than ported as-is.
- `DisplayOneServlet` and `DocDetailListDisplayServlet` should become JSON APIs plus a frontend route.
- `captcha.jsp` should become an API + frontend modal/page instead of a redirecting JSP.
- Comment submission is the clearest existing write operation and should be preserved first.
- The current code mixes three concerns:
  - JSF page navigation
  - SQL queries against CE schema
  - FileNet CE API object operations
  These should be separated in the replacement service layer.
