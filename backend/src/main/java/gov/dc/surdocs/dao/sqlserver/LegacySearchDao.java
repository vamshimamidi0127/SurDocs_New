package gov.dc.surdocs.dao.sqlserver;

import gov.dc.surdocs.model.dto.search.DocumentCountDto;
import gov.dc.surdocs.model.dto.search.DocumentSubtypeCountDto;
import gov.dc.surdocs.model.dto.search.LotOptionDto;
import gov.dc.surdocs.model.dto.search.MapQueryResultDto;
import gov.dc.surdocs.model.dto.search.SearchDocumentDetailDto;
import gov.dc.surdocs.model.dto.search.SingleDocumentDetailDto;
import gov.dc.surdocs.model.dto.search.SuffixOptionDto;
import java.util.List;

public interface LegacySearchDao {

    List<SuffixOptionDto> suffixValues(String square);

    List<LotOptionDto> lotList(String sslPrefix);

    List<DocumentCountDto> docCount(String ssl);

    List<DocumentSubtypeCountDto> docSubCnts(String ssl, String documentType);

    List<SearchDocumentDetailDto> docSubDetail(String documentClass, String subtype, String ssl);

    List<MapQueryResultDto> mapQuery(String mapNumber);

    SingleDocumentDetailDto singleDocDetail(String documentId);
}
