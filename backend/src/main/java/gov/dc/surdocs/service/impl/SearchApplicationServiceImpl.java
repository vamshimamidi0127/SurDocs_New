package gov.dc.surdocs.service.impl;

import gov.dc.surdocs.dao.sqlserver.LegacySearchDao;
import gov.dc.surdocs.model.dto.DocumentDetailDto;
import gov.dc.surdocs.model.dto.api.DocumentCountResponse;
import gov.dc.surdocs.model.dto.api.DisplayDocumentResponse;
import gov.dc.surdocs.model.dto.api.DocumentListResponse;
import gov.dc.surdocs.model.dto.api.DocumentViewerUrlResponse;
import gov.dc.surdocs.model.dto.api.LotListResponse;
import gov.dc.surdocs.model.dto.api.QuickSearchOptionsResponse;
import gov.dc.surdocs.model.dto.api.SubtypeResponse;
import gov.dc.surdocs.model.dto.search.DocumentSubtypeCountDto;
import gov.dc.surdocs.model.dto.search.SearchDocumentDetailDto;
import gov.dc.surdocs.model.dto.search.SingleDocumentDetailDto;
import gov.dc.surdocs.service.DocumentService;
import gov.dc.surdocs.service.SearchApplicationService;
import gov.dc.surdocs.service.ViewerUrlBuilderService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SearchApplicationServiceImpl implements SearchApplicationService {

    private static final List<String> DOCUMENT_TYPE_SEARCH_ORDER =
            Arrays.asList("Book", "Paper", "Map", "IndexCards");

    private final LegacySearchDao legacySearchDao;
    private final DocumentService documentService;
    private final ViewerUrlBuilderService viewerUrlBuilderService;

    public SearchApplicationServiceImpl(LegacySearchDao legacySearchDao,
                                        DocumentService documentService,
                                        ViewerUrlBuilderService viewerUrlBuilderService) {
        this.legacySearchDao = legacySearchDao;
        this.documentService = documentService;
        this.viewerUrlBuilderService = viewerUrlBuilderService;
    }

    @Override
    public QuickSearchOptionsResponse getQuickSearchOptions(String square) {
        QuickSearchOptionsResponse response = new QuickSearchOptionsResponse();
        response.setSquare(square);
        response.setSuffixes(legacySearchDao.suffixValues(square));
        return response;
    }

    @Override
    public LotListResponse getLots(String square, String suffix) {
        String normalizedSuffix = suffix == null ? "" : suffix;
        String sslPrefix = square + normalizedSuffix;

        LotListResponse response = new LotListResponse();
        response.setSquare(square);
        response.setSuffix(normalizedSuffix);
        response.setSslPrefix(sslPrefix);
        response.setLots(legacySearchDao.lotList(sslPrefix));
        return response;
    }

    @Override
    public DocumentCountResponse getDocumentCounts(String ssl) {
        DocumentCountResponse response = new DocumentCountResponse();
        response.setSsl(ssl);
        response.setItems(legacySearchDao.docCount(ssl));
        return response;
    }

    @Override
    public SubtypeResponse getSubtypes(String ssl, String docType) {
        List<DocumentSubtypeCountDto> subtypes = legacySearchDao.docSubCnts(ssl, docType);
        SubtypeResponse response = new SubtypeResponse();
        response.setSsl(ssl);
        response.setDocumentType(docType);
        response.setItems(subtypes);
        return response;
    }

    @Override
    public DocumentListResponse getDocuments(String ssl, String subtype) {
        if (!StringUtils.hasText(subtype)) {
            DocumentListResponse empty = new DocumentListResponse();
            empty.setSsl(ssl);
            empty.setSubtype(subtype);
            empty.setItems(Collections.<SearchDocumentDetailDto>emptyList());
            return empty;
        }

        for (String documentType : DOCUMENT_TYPE_SEARCH_ORDER) {
            List<SearchDocumentDetailDto> documents = legacySearchDao.docSubDetail(documentType, subtype, ssl);
            if (!documents.isEmpty()) {
                DocumentListResponse response = new DocumentListResponse();
                response.setSsl(ssl);
                response.setSubtype(subtype);
                response.setDocumentType(documentType);
                response.setItems(documents);
                return response;
            }
        }

        DocumentListResponse empty = new DocumentListResponse();
        empty.setSsl(ssl);
        empty.setSubtype(subtype);
        empty.setItems(Collections.<SearchDocumentDetailDto>emptyList());
        return empty;
    }

    @Override
    public DocumentListResponse getDocuments(String ssl, String docType, String subtype) {
        DocumentListResponse response = new DocumentListResponse();
        response.setSsl(ssl);
        response.setSubtype(subtype);
        response.setDocumentType(docType);

        if (!StringUtils.hasText(ssl) || !StringUtils.hasText(subtype) || !StringUtils.hasText(docType)) {
            response.setItems(Collections.<SearchDocumentDetailDto>emptyList());
            return response;
        }

        response.setItems(legacySearchDao.docSubDetail(docType, subtype, ssl));
        return response;
    }

    @Override
    public DocumentViewerUrlResponse getDocumentViewerUrl(String docId) {
        DocumentDetailDto documentDetail = documentService.getDocument(docId);

        DocumentViewerUrlResponse response = new DocumentViewerUrlResponse();
        response.setDocumentId(documentDetail.getDocumentId());
        response.setDocumentTitle(documentDetail.getDocumentTitle());
        response.setMimeType(documentDetail.getMimeType());
        response.setViewerUrl(viewerUrlBuilderService.buildViewerUrl(docId));
        return response;
    }

    @Override
    public DisplayDocumentResponse getDisplayDocument(String docId) {
        SingleDocumentDetailDto detail = legacySearchDao.singleDocDetail(docId);
        if (detail == null) {
            throw new IllegalArgumentException("Document not found for id: " + docId);
        }

        DisplayDocumentResponse response = new DisplayDocumentResponse();
        response.setDocumentId(detail.getDocumentId());
        response.setDocumentType(detail.getDocumentType());
        response.setSubtype(detail.getSubtype());
        response.setTitle(detail.getTitle());
        response.setPageNumber(detail.getPageNumber());
        response.setVersionSeriesId(detail.getVersionSeriesId());
        response.setObjectClassId(detail.getObjectClassId());
        response.setMimeType(detail.getMimeType());
        response.setViewerUrl(viewerUrlBuilderService.buildViewerUrl(docId));
        return response;
    }
}
