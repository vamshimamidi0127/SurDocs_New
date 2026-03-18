package gov.dc.surdocs.dao.sqlserver;

import gov.dc.surdocs.model.dto.search.DocumentCountDto;
import gov.dc.surdocs.model.dto.search.DocumentSubtypeCountDto;
import gov.dc.surdocs.model.dto.search.LotOptionDto;
import gov.dc.surdocs.model.dto.search.MapQueryResultDto;
import gov.dc.surdocs.model.dto.search.SearchDocumentDetailDto;
import gov.dc.surdocs.model.dto.search.SingleDocumentDetailDto;
import gov.dc.surdocs.model.dto.search.SuffixOptionDto;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcLegacySearchDao implements LegacySearchDao {

    private static final String SUFFIX_VALUES_SQL =
            "select distinct substring(cast(ls.element_value as varchar(12)), 1, 4) as square, "
                    + "substring(cast(ls.element_value as varchar(12)), 5, 4) as suffix "
                    + "from ListOfString ls "
                    + "where ls.element_value like ? "
                    + "order by suffix";

    private static final String LOT_LIST_SQL =
            "select distinct ? as ssl_prefix, "
                    + "substring(cast(ls.element_value as varchar(12)), 9, 4) as lot "
                    + "from ListOfString ls "
                    + "where ls.element_value like ? "
                    + "order by lot";

    private static final String DOC_COUNT_SQL =
            "select count(f.obj) as doc_count, f.name as doc_type "
                    + "from ( "
                    + "  select distinct dv.object_id as obj, cd.symbolic_name as name "
                    + "  from DocVersion dv "
                    + "  join ClassDefinition cd on dv.object_class_id = cd.object_id "
                    + "  join ListOfString ls on ls.parent_id = dv.object_id "
                    + "  where ls.element_value like ? "
                    + ") f "
                    + "group by f.name "
                    + "order by f.name";

    private static final String DOC_SUB_CNTS_BOOK_SQL =
            "select dv.u4388_booktype as subtype, count(distinct dv.object_id) as subtype_count "
                    + "from DocVersion dv "
                    + "join ListOfString ls on ls.parent_id = dv.object_id "
                    + "join ClassDefinition cd on dv.object_class_id = cd.object_id "
                    + "where ls.element_value like ? "
                    + "and cd.symbolic_name = ? "
                    + "group by dv.u4388_booktype "
                    + "order by dv.u4388_booktype";

    private static final String DOC_SUB_CNTS_PAPER_SQL =
            "select dv.u64f8_papertype as subtype, count(distinct dv.object_id) as subtype_count "
                    + "from DocVersion dv "
                    + "join ListOfString ls on ls.parent_id = dv.object_id "
                    + "join ClassDefinition cd on dv.object_class_id = cd.object_id "
                    + "where ls.element_value like ? "
                    + "and cd.symbolic_name = ? "
                    + "group by dv.u64f8_papertype "
                    + "order by dv.u64f8_papertype";

    private static final String DOC_SUB_CNTS_MAP_SQL =
            "select dv.ue6d8_maptype as subtype, count(distinct dv.object_id) as subtype_count "
                    + "from DocVersion dv "
                    + "join ListOfString ls on ls.parent_id = dv.object_id "
                    + "join ClassDefinition cd on dv.object_class_id = cd.object_id "
                    + "where ls.element_value like ? "
                    + "and cd.symbolic_name = ? "
                    + "group by dv.ue6d8_maptype "
                    + "order by dv.ue6d8_maptype";

    private static final String DOC_SUB_CNTS_INDEX_SQL =
            "select dv.u04d8_cardtype as subtype, count(distinct dv.object_id) as subtype_count "
                    + "from DocVersion dv "
                    + "join ListOfString ls on ls.parent_id = dv.object_id "
                    + "join ClassDefinition cd on dv.object_class_id = cd.object_id "
                    + "where ls.element_value like ? "
                    + "and cd.symbolic_name = ? "
                    + "group by dv.u04d8_cardtype "
                    + "order by dv.u04d8_cardtype";

    private static final String DOC_SUB_DETAIL_BOOK_SQL =
            "select distinct cast(? as varchar(64)) as ssl, cd.symbolic_name as document_class, dv.object_id as document_id, "
                    + "dv.u4388_booktype as subtype, dv.u1d78_pagenumber as page_number, dv.u1708_documenttitle as title, "
                    + "dv.version_series_id as version_series_id, dv.object_class_id as object_class_id, "
                    + "dv.mime_type as mime_type, dv.uc718_surveypapercategory as category "
                    + "from DocVersion dv "
                    + "join ListOfString ls on ls.parent_id = dv.object_id "
                    + "join ClassDefinition cd on dv.object_class_id = cd.object_id "
                    + "where dv.is_current = 1 and ls.element_value like ? and dv.u4388_booktype = ? "
                    + "order by dv.uc718_surveypapercategory, dv.u1708_documenttitle";

    private static final String DOC_SUB_DETAIL_PAPER_SQL =
            "select distinct cast(? as varchar(64)) as ssl, cd.symbolic_name as document_class, dv.object_id as document_id, "
                    + "dv.u64f8_papertype as subtype, dv.u1d78_pagenumber as page_number, dv.u1708_documenttitle as title, "
                    + "dv.version_series_id as version_series_id, dv.object_class_id as object_class_id, "
                    + "dv.mime_type as mime_type, dv.uc718_surveypapercategory as category "
                    + "from DocVersion dv "
                    + "join ListOfString ls on ls.parent_id = dv.object_id "
                    + "join ClassDefinition cd on dv.object_class_id = cd.object_id "
                    + "where dv.is_current = 1 and ls.element_value like ? and dv.u64f8_papertype = ? "
                    + "order by dv.uc718_surveypapercategory, dv.u1708_documenttitle";

    private static final String DOC_SUB_DETAIL_MAP_SQL =
            "select distinct cast(? as varchar(64)) as ssl, cd.symbolic_name as document_class, dv.object_id as document_id, "
                    + "dv.ue6d8_maptype as subtype, dv.u1d78_pagenumber as page_number, dv.u1708_documenttitle as title, "
                    + "dv.version_series_id as version_series_id, dv.object_class_id as object_class_id, "
                    + "dv.mime_type as mime_type, dv.uc718_surveypapercategory as category "
                    + "from DocVersion dv "
                    + "join ListOfString ls on ls.parent_id = dv.object_id "
                    + "join ClassDefinition cd on dv.object_class_id = cd.object_id "
                    + "where dv.is_current = 1 and ls.element_value like ? and dv.ue6d8_maptype = ? "
                    + "order by dv.uc718_surveypapercategory, dv.u1708_documenttitle";

    private static final String DOC_SUB_DETAIL_INDEX_SQL =
            "select distinct cast(? as varchar(64)) as ssl, cd.symbolic_name as document_class, dv.object_id as document_id, "
                    + "dv.u04d8_cardtype as subtype, dv.u1d78_pagenumber as page_number, dv.u1708_documenttitle as title, "
                    + "dv.version_series_id as version_series_id, dv.object_class_id as object_class_id, "
                    + "dv.mime_type as mime_type, dv.uc718_surveypapercategory as category "
                    + "from DocVersion dv "
                    + "join ListOfString ls on ls.parent_id = dv.object_id "
                    + "join ClassDefinition cd on dv.object_class_id = cd.object_id "
                    + "where dv.is_current = 1 and ls.element_value like ? and dv.u04d8_cardtype = ? "
                    + "order by dv.uc718_surveypapercategory, dv.u1708_documenttitle";

    private static final String MAP_QUERY_SQL =
            "select dv.object_id as document_id, dv.ue6d8_maptype as map_type, dv.u1d78_pagenumber as page_number, "
                    + "dv.u1708_documenttitle as title, dv.version_series_id as version_series_id, "
                    + "dv.object_class_id as object_class_id, dv.u13f8_mapnumber as map_number "
                    + "from DocVersion dv "
                    + "where dv.u13f8_mapnumber = ? "
                    + "and dv.is_current = 1 "
                    + "order by dv.major_version_number desc";

    private static final String SINGLE_DOC_DETAIL_SQL =
            "select cd.symbolic_name as document_type, dv.object_id as document_id, dv.u4388_booktype as subtype, "
                    + "dv.u1d78_pagenumber as page_number, dv.u1708_documenttitle as title, "
                    + "dv.version_series_id as version_series_id, dv.object_class_id as object_class_id, "
                    + "dv.mime_type as mime_type "
                    + "from DocVersion dv "
                    + "join ClassDefinition cd on dv.object_class_id = cd.object_id "
                    + "where dv.object_id = ? and dv.is_current = 1";

    private final JdbcTemplate jdbcTemplate;

    public JdbcLegacySearchDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<SuffixOptionDto> suffixValues(String square) {
        return jdbcTemplate.query(SUFFIX_VALUES_SQL, new Object[]{square + "%"}, new SuffixOptionRowMapper());
    }

    @Override
    public List<LotOptionDto> lotList(String sslPrefix) {
        return jdbcTemplate.query(LOT_LIST_SQL, new Object[]{sslPrefix, sslPrefix + "%"}, new LotOptionRowMapper());
    }

    @Override
    public List<DocumentCountDto> docCount(String ssl) {
        return jdbcTemplate.query(DOC_COUNT_SQL, new Object[]{ssl.trim() + "%"}, new DocumentCountRowMapper());
    }

    @Override
    public List<DocumentSubtypeCountDto> docSubCnts(String ssl, String documentType) {
        return jdbcTemplate.query(resolveSubtypeCountSql(documentType),
                new Object[]{ssl.trim() + "%", documentType},
                new DocumentSubtypeCountRowMapper(documentType));
    }

    @Override
    public List<SearchDocumentDetailDto> docSubDetail(String documentClass, String subtype, String ssl) {
        return jdbcTemplate.query(resolveSubtypeDetailSql(documentClass),
                new Object[]{ssl, ssl.trim() + "%", subtype},
                new SearchDocumentDetailRowMapper());
    }

    @Override
    public List<MapQueryResultDto> mapQuery(String mapNumber) {
        return jdbcTemplate.query(MAP_QUERY_SQL, new Object[]{mapNumber}, new MapQueryResultRowMapper());
    }

    @Override
    public SingleDocumentDetailDto singleDocDetail(String documentId) {
        List<SingleDocumentDetailDto> results = jdbcTemplate.query(
                SINGLE_DOC_DETAIL_SQL,
                new Object[]{documentId},
                new SingleDocumentDetailRowMapper());
        return results.isEmpty() ? null : results.get(0);
    }

    private String resolveSubtypeCountSql(String documentType) {
        if ("Book".equalsIgnoreCase(documentType)) {
            return DOC_SUB_CNTS_BOOK_SQL;
        }
        if ("Paper".equalsIgnoreCase(documentType)) {
            return DOC_SUB_CNTS_PAPER_SQL;
        }
        if ("Map".equalsIgnoreCase(documentType)) {
            return DOC_SUB_CNTS_MAP_SQL;
        }
        if ("IndexCards".equalsIgnoreCase(documentType) || "Index Cards".equalsIgnoreCase(documentType)) {
            return DOC_SUB_CNTS_INDEX_SQL;
        }
        throw new IllegalArgumentException("Unsupported documentType for docSubCnts: " + documentType);
    }

    private String resolveSubtypeDetailSql(String documentClass) {
        if ("Book".equalsIgnoreCase(documentClass)) {
            return DOC_SUB_DETAIL_BOOK_SQL;
        }
        if ("Paper".equalsIgnoreCase(documentClass)) {
            return DOC_SUB_DETAIL_PAPER_SQL;
        }
        if ("Map".equalsIgnoreCase(documentClass)) {
            return DOC_SUB_DETAIL_MAP_SQL;
        }
        if ("IndexCards".equalsIgnoreCase(documentClass) || "Index Cards".equalsIgnoreCase(documentClass)) {
            return DOC_SUB_DETAIL_INDEX_SQL;
        }
        throw new IllegalArgumentException("Unsupported documentClass for docSubDetail: " + documentClass);
    }

    private static class SuffixOptionRowMapper implements RowMapper<SuffixOptionDto> {
        @Override
        public SuffixOptionDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            SuffixOptionDto dto = new SuffixOptionDto();
            dto.setSquare(rs.getString("square"));
            dto.setSuffix(rs.getString("suffix"));
            return dto;
        }
    }

    private static class LotOptionRowMapper implements RowMapper<LotOptionDto> {
        @Override
        public LotOptionDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            LotOptionDto dto = new LotOptionDto();
            dto.setSslPrefix(rs.getString("ssl_prefix"));
            dto.setLot(rs.getString("lot"));
            return dto;
        }
    }

    private static class DocumentCountRowMapper implements RowMapper<DocumentCountDto> {
        @Override
        public DocumentCountDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            DocumentCountDto dto = new DocumentCountDto();
            dto.setType(rs.getString("doc_type"));
            dto.setCount(rs.getInt("doc_count"));
            return dto;
        }
    }

    private static class DocumentSubtypeCountRowMapper implements RowMapper<DocumentSubtypeCountDto> {

        private final String documentType;

        private DocumentSubtypeCountRowMapper(String documentType) {
            this.documentType = documentType;
        }

        @Override
        public DocumentSubtypeCountDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            DocumentSubtypeCountDto dto = new DocumentSubtypeCountDto();
            dto.setDocumentType(documentType);
            dto.setSubtype(rs.getString("subtype"));
            dto.setCount(rs.getInt("subtype_count"));
            return dto;
        }
    }

    private static class SearchDocumentDetailRowMapper implements RowMapper<SearchDocumentDetailDto> {
        @Override
        public SearchDocumentDetailDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            SearchDocumentDetailDto dto = new SearchDocumentDetailDto();
            dto.setSsl(rs.getString("ssl"));
            dto.setDocumentClass(rs.getString("document_class"));
            dto.setDocumentId(rs.getString("document_id"));
            dto.setSubtype(rs.getString("subtype"));
            dto.setPageNumber(rs.getString("page_number"));
            dto.setTitle(rs.getString("title"));
            dto.setVersionSeriesId(rs.getString("version_series_id"));
            dto.setObjectClassId(rs.getString("object_class_id"));
            dto.setMimeType(rs.getString("mime_type"));
            dto.setCategory(rs.getString("category"));
            return dto;
        }
    }

    private static class MapQueryResultRowMapper implements RowMapper<MapQueryResultDto> {
        @Override
        public MapQueryResultDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            MapQueryResultDto dto = new MapQueryResultDto();
            dto.setDocumentId(rs.getString("document_id"));
            dto.setMapType(rs.getString("map_type"));
            dto.setPageNumber(rs.getString("page_number"));
            dto.setTitle(rs.getString("title"));
            dto.setVersionSeriesId(rs.getString("version_series_id"));
            dto.setObjectClassId(rs.getString("object_class_id"));
            dto.setMapNumber(rs.getString("map_number"));
            return dto;
        }
    }

    private static class SingleDocumentDetailRowMapper implements RowMapper<SingleDocumentDetailDto> {
        @Override
        public SingleDocumentDetailDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            SingleDocumentDetailDto dto = new SingleDocumentDetailDto();
            dto.setDocumentType(rs.getString("document_type"));
            dto.setDocumentId(rs.getString("document_id"));
            dto.setSubtype(rs.getString("subtype"));
            dto.setPageNumber(rs.getString("page_number"));
            dto.setTitle(rs.getString("title"));
            dto.setVersionSeriesId(rs.getString("version_series_id"));
            dto.setObjectClassId(rs.getString("object_class_id"));
            dto.setMimeType(rs.getString("mime_type"));
            return dto;
        }
    }
}
