package gov.dc.surdocs.dao.sqlserver;

import gov.dc.surdocs.model.dto.DocumentSummaryDto;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class SqlServerDocumentCountDao implements DocumentCountDao {

    private static final String DOCUMENT_COUNT_SQL =
            "select count(f.obj) as doc_count, f.name as doc_type "
                    + "from ( "
                    + "  select distinct a.object_id as obj, a.object_class_id as class, b.symbolic_name as name "
                    + "  from docversion a, classdefinition b, listofstring c "
                    + "  where c.parent_id = a.object_id "
                    + "    and a.object_class_id = b.object_id "
                    + "    and c.element_value like ? "
                    + ") as f "
                    + "group by f.name "
                    + "order by f.name";

    private final JdbcTemplate jdbcTemplate;

    public SqlServerDocumentCountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<DocumentSummaryDto> findDocumentCounts(String ssl) {
        return jdbcTemplate.query(DOCUMENT_COUNT_SQL, new Object[]{ssl.trim() + "%"}, new DocumentSummaryRowMapper());
    }

    private static class DocumentSummaryRowMapper implements RowMapper<DocumentSummaryDto> {
        @Override
        public DocumentSummaryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            DocumentSummaryDto dto = new DocumentSummaryDto();
            dto.setDocumentType(rs.getString("doc_type"));
            dto.setCount(rs.getInt("doc_count"));
            return dto;
        }
    }
}
