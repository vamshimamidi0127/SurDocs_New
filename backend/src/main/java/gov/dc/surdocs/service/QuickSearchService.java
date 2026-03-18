package gov.dc.surdocs.service;

import gov.dc.surdocs.dao.sqlserver.DocumentCountDao;
import gov.dc.surdocs.model.dto.DocumentSummaryDto;
import gov.dc.surdocs.model.dto.QuickSearchResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class QuickSearchService {

    private final DocumentCountDao documentCountDao;

    public QuickSearchService(DocumentCountDao documentCountDao) {
        this.documentCountDao = documentCountDao;
    }

    public QuickSearchResponse getDocumentCounts(String ssl) {
        List<DocumentSummaryDto> counts = documentCountDao.findDocumentCounts(ssl);
        QuickSearchResponse response = new QuickSearchResponse();
        response.setSsl(ssl);
        response.setDocuments(counts);
        return response;
    }
}
