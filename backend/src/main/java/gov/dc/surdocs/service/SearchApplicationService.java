package gov.dc.surdocs.service;

import gov.dc.surdocs.model.dto.api.DocumentCountResponse;
import gov.dc.surdocs.model.dto.api.DisplayDocumentResponse;
import gov.dc.surdocs.model.dto.api.DocumentListResponse;
import gov.dc.surdocs.model.dto.api.DocumentViewerUrlResponse;
import gov.dc.surdocs.model.dto.api.LotListResponse;
import gov.dc.surdocs.model.dto.api.QuickSearchOptionsResponse;
import gov.dc.surdocs.model.dto.api.SubtypeResponse;

public interface SearchApplicationService {

    QuickSearchOptionsResponse getQuickSearchOptions(String square);

    LotListResponse getLots(String square, String suffix);

    DocumentCountResponse getDocumentCounts(String ssl);

    SubtypeResponse getSubtypes(String ssl, String docType);

    DocumentListResponse getDocuments(String ssl, String subtype);

    DocumentListResponse getDocuments(String ssl, String docType, String subtype);

    DocumentViewerUrlResponse getDocumentViewerUrl(String docId);

    DisplayDocumentResponse getDisplayDocument(String docId);
}
