package gov.dc.surdocs.dao.sqlserver;

import gov.dc.surdocs.model.dto.DocumentSummaryDto;
import java.util.List;

public interface DocumentCountDao {

    List<DocumentSummaryDto> findDocumentCounts(String ssl);
}
