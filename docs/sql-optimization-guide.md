# SURDOCS_EXTERNAL SQL Optimization Guide

This guide focuses on optimizing the highest-volume legacy SQL patterns for large datasets:

- `docCount`
- subtype counts
- document lists

Assumptions:

- SQL Server
- millions of rows in `DocVersion`, `ListOfString`, `ClassDefinition`
- current search pattern is driven by `SSL`-like prefix matching
- existing logic must be preserved

## 1. Current Performance Risks

The legacy query style has a few common issues:

- wide scans on `ListOfString.element_value like ?`
- repeated joins to `DocVersion` and `ClassDefinition`
- repeated round trips for:
  - counts
  - subtype counts
  - document detail
- `distinct` used heavily to deduplicate joins
- document subtype logic split into multiple similar queries

On large datasets, the dominant cost will usually be:

- lookup of matching `ListOfString.parent_id`
- joining those parent ids to `DocVersion`
- sorting/grouping for counts and document lists

## 2. Recommended Query Strategy

General rule:

1. Filter `ListOfString` first using a prefix-friendly predicate.
2. Project only `parent_id` from that filtered set.
3. Join to `DocVersion` on `object_id`.
4. Join to `ClassDefinition` only when symbolic name is needed.
5. Group or project as late as possible.

For very large datasets, prefer this pattern:

```sql
with matching_docs as (
    select distinct ls.parent_id
    from ListOfString ls
    where ls.element_value >= ?
      and ls.element_value < ?
)
select ...
from matching_docs md
join DocVersion dv on dv.object_id = md.parent_id
...
```

This is often better than `LIKE 'prefix%'` because SQL Server can use a tighter range seek if indexing supports it.

Example range logic:

- if prefix = `0564`
- lower bound = `0564`
- upper bound = next lexicographic prefix

If your application layer cannot easily compute the upper bound, `LIKE ? + '%'` is still acceptable, but range predicates are usually better for very large tables.

## 3. Optimized docCount

### Current intent

Count documents by document class for a given SSL prefix.

### Recommended query

```sql
with matching_docs as (
    select distinct ls.parent_id
    from ListOfString ls
    where ls.element_value like ?
)
select
    cd.symbolic_name as doc_type,
    count_big(*) as doc_count
from matching_docs md
join DocVersion dv
    on dv.object_id = md.parent_id
join ClassDefinition cd
    on cd.object_id = dv.object_class_id
group by cd.symbolic_name
order by cd.symbolic_name;
```

### Why this is better

- deduplicates `parent_id` before joining to large document rows
- removes nested subquery over wide join set
- keeps grouping over a smaller rowset

### Additional optimization

If `ListOfString` contains multiple rows per doc for many properties, restrict to the SSL property if possible:

```sql
where ls.element_value like ?
  and ls.parent_prop_id = ?
```

This can reduce join fan-out dramatically.

## 4. Optimized subtype counts

### Current intent

Count documents by subtype within a selected document class.

### Recommended pattern

Use the same filtered document CTE, then group by the correct subtype column.

#### Book subtype counts

```sql
with matching_docs as (
    select distinct ls.parent_id
    from ListOfString ls
    where ls.element_value like ?
)
select
    dv.u4388_booktype as subtype,
    count_big(*) as subtype_count
from matching_docs md
join DocVersion dv
    on dv.object_id = md.parent_id
join ClassDefinition cd
    on cd.object_id = dv.object_class_id
where cd.symbolic_name = ?
group by dv.u4388_booktype
order by dv.u4388_booktype;
```

#### Map subtype counts

```sql
with matching_docs as (
    select distinct ls.parent_id
    from ListOfString ls
    where ls.element_value like ?
)
select
    dv.ue6d8_maptype as subtype,
    count_big(*) as subtype_count
from matching_docs md
join DocVersion dv
    on dv.object_id = md.parent_id
join ClassDefinition cd
    on cd.object_id = dv.object_class_id
where cd.symbolic_name = ?
group by dv.ue6d8_maptype
order by dv.ue6d8_maptype;
```

### Why this is better

- one filtered working set per request
- avoids joining all rows before deduplication
- indexable join path from `ListOfString.parent_id -> DocVersion.object_id`

## 5. Optimized document list query

### Current intent

Return document details for a selected subtype and SSL prefix.

### Recommended query

```sql
with matching_docs as (
    select distinct ls.parent_id
    from ListOfString ls
    where ls.element_value like ?
)
select
    cd.symbolic_name as document_class,
    dv.object_id as document_id,
    dv.u4388_booktype as subtype,
    dv.u1d78_pagenumber as page_number,
    dv.u1708_documenttitle as title,
    dv.version_series_id,
    dv.object_class_id,
    dv.mime_type,
    dv.uc718_surveypapercategory as category
from matching_docs md
join DocVersion dv
    on dv.object_id = md.parent_id
join ClassDefinition cd
    on cd.object_id = dv.object_class_id
where dv.is_current = 1
  and dv.u4388_booktype = ?
order by
    dv.uc718_surveypapercategory,
    dv.u1708_documenttitle;
```

### Key changes

- no `select distinct` across the full wide rowset unless truly required
- deduplicate only on `parent_id` in the CTE
- sort after filtering to subtype/current documents

### If detail rows still duplicate

That usually means `ListOfString` still introduces duplicate parent ids due to multiple property rows. Keep the `matching_docs` CTE with `distinct parent_id`; do not apply `distinct` to the full final projection unless necessary.

## 6. Index Recommendations

These are the highest-value indexes for the current access pattern.

## A. `ListOfString`

This table is the primary driver for SSL prefix filtering.

Recommended nonclustered index:

```sql
create nonclustered index IX_ListOfString_ElementValue_ParentId
on ListOfString (element_value, parent_id);
```

If property filtering is possible:

```sql
create nonclustered index IX_ListOfString_Prop_ElementValue_ParentId
on ListOfString (parent_prop_id, element_value, parent_id);
```

Why:

- supports prefix search on `element_value`
- supports quick retrieval of `parent_id`
- avoids bookmark lookups for the filtered doc id set

## B. `DocVersion`

Likely already clustered or keyed by `object_id`, but subtype filters and ordering need help.

Recommended indexes:

```sql
create nonclustered index IX_DocVersion_ObjectClass_Current
on DocVersion (object_class_id, is_current, object_id);
```

For book subtype detail:

```sql
create nonclustered index IX_DocVersion_BookType_Current
on DocVersion (u4388_booktype, is_current, object_id)
include (u1d78_pagenumber, u1708_documenttitle, version_series_id, mime_type, uc718_surveypapercategory, object_class_id);
```

For paper subtype detail:

```sql
create nonclustered index IX_DocVersion_PaperType_Current
on DocVersion (u64f8_papertype, is_current, object_id)
include (u1d78_pagenumber, u1708_documenttitle, version_series_id, mime_type, uc718_surveypapercategory, object_class_id);
```

For map subtype detail:

```sql
create nonclustered index IX_DocVersion_MapType_Current
on DocVersion (ue6d8_maptype, is_current, object_id)
include (u1d78_pagenumber, u1708_documenttitle, version_series_id, mime_type, uc718_surveypapercategory, object_class_id, u13f8_mapnumber);
```

For index card subtype detail:

```sql
create nonclustered index IX_DocVersion_CardType_Current
on DocVersion (u04d8_cardtype, is_current, object_id)
include (u1d78_pagenumber, u1708_documenttitle, version_series_id, mime_type, uc718_surveypapercategory, object_class_id);
```

For map number lookup:

```sql
create nonclustered index IX_DocVersion_MapNumber_Current
on DocVersion (u13f8_mapnumber, is_current, object_id)
include (ue6d8_maptype, u1d78_pagenumber, u1708_documenttitle, version_series_id, object_class_id);
```

## C. `ClassDefinition`

Usually small, but if not indexed:

```sql
create nonclustered index IX_ClassDefinition_ObjectId_SymbolicName
on ClassDefinition (object_id, symbolic_name);
```

## 7. Filtered Index Opportunities

If `is_current = 1` is heavily used and current rows are a subset, filtered indexes can help a lot.

Example:

```sql
create nonclustered index IX_DocVersion_Current_MapNumber
on DocVersion (u13f8_mapnumber, object_id)
include (ue6d8_maptype, u1d78_pagenumber, u1708_documenttitle, version_series_id, object_class_id)
where is_current = 1;
```

Use filtered indexes only if:

- `is_current = 1` is a stable predicate
- query plans consistently benefit in your environment

## 8. Caching Strategy

For millions of records, caching should be selective and based on query volatility.

### Cache candidates

Good cache candidates:

- suffix values by square
- lot list by square+suffix
- document counts by SSL
- subtype counts by SSL + docType

Less ideal to cache long-term:

- full document lists
- viewer URLs
- per-document dynamic metadata

### Recommended application cache

Use Spring Cache with Caffeine.

Suggested TTLs:

- suffix values: 30 to 60 minutes
- lot list: 15 to 30 minutes
- document counts: 5 to 15 minutes
- subtype counts: 5 to 15 minutes
- document lists: 1 to 5 minutes if needed

### Cache key examples

- `suffix:{square}`
- `lots:{square}:{suffix}`
- `doccount:{ssl}`
- `subtypes:{ssl}:{docType}`
- `doclist:{ssl}:{docType}:{subtype}`

### Cache invalidation

If underlying CE tables are near-real-time and frequently updated, prefer short TTL over manual invalidation.

## 9. Batch Query Strategy

### Problem

Current flows often trigger multiple sequential requests:

1. suffix lookup
2. lot lookup
3. document counts
4. subtype counts
5. document list

For large-scale systems, reducing round trips matters.

### Recommended batching options

## A. Combined search summary endpoint

Create a summary query or service call that returns:

- suffixes
- lots
- doc counts

for a single search state when possible.

## B. Multi-result service orchestration

At the service layer:

- issue independent DAO queries in parallel when safe
- combine into one response object

Example:

- `getQuickSearchState(square, suffix, lot, queryType)`

returns:

- suffixes
- lots
- counts

### C. SQL temp-table batching

If you must run several related aggregations for the same matching document set:

1. materialize matching doc ids into a temp table
2. run counts and detail queries against that temp table

Example:

```sql
select distinct ls.parent_id
into #matching_docs
from ListOfString ls
where ls.element_value like @sslPattern;

select cd.symbolic_name, count_big(*)
from #matching_docs md
join DocVersion dv on dv.object_id = md.parent_id
join ClassDefinition cd on cd.object_id = dv.object_class_id
group by cd.symbolic_name;

select dv.u4388_booktype, count_big(*)
from #matching_docs md
join DocVersion dv on dv.object_id = md.parent_id
join ClassDefinition cd on cd.object_id = dv.object_class_id
where cd.symbolic_name = 'Book'
group by dv.u4388_booktype;
```

This is often valuable when:

- the same filtered document set drives several queries
- the filtered set is much smaller than the base tables

## 10. Pagination Strategy for Document Lists

Never return huge result sets unbounded.

Use:

```sql
order by dv.uc718_surveypapercategory, dv.u1708_documenttitle
offset ? rows fetch next ? rows only;
```

Also add a paired count query when needed for UI pagination.

Recommended defaults:

- page size: 25 or 50
- maximum page size: 100

## 11. Materialized Summary Options

If counts become a hotspot under heavy load, consider pre-aggregation.

Options:

- indexed view for current document counts by class
- scheduled summary table by SSL prefix and document type
- nightly or incremental refresh depending on data latency requirements

Use this only after validating that indexing + caching is insufficient.

## 12. DAO Implementation Recommendations

For Spring JDBC:

- keep SQL externalized if it grows further
- use `NamedParameterJdbcTemplate`
- add explicit pagination support
- avoid returning unused columns
- log slow queries with threshold-based monitoring

Example enhancements:

- `docCount(ssl, propertyId)`
- `docSubCnts(ssl, docType, currentOnly)`
- `docSubDetail(ssl, docType, subtype, offset, limit)`

## 13. Recommended Next Changes in This Codebase

1. Rewrite DAO queries to use a shared `matching_docs` pattern.
2. Add pagination to document list APIs.
3. Add Spring Cache for:
   - suffix values
   - lot list
   - doc counts
   - subtype counts
4. Add SQL Server indexes beginning with:
   - `IX_ListOfString_ElementValue_ParentId`
   - `IX_DocVersion_MapNumber_Current`
   - subtype-specific `DocVersion` indexes
5. Benchmark using real execution plans before and after changes.

## 14. Validation Checklist

Before applying indexes:

- capture baseline execution plans
- capture logical reads and duration
- test with representative prefixes and map numbers

After applying indexes:

- verify seeks instead of scans where expected
- check sort/hash spill reduction
- verify write overhead is acceptable
- confirm no regression for ingestion/update workloads
