package gov.dc.surdocs.model.dto.api;

import gov.dc.surdocs.model.dto.search.SearchDocumentDetailDto;
import java.util.List;

public class DocumentListResponse {

    private String ssl;
    private String subtype;
    private String documentType;
    private List<SearchDocumentDetailDto> items;

    public String getSsl() {
        return ssl;
    }

    public void setSsl(String ssl) {
        this.ssl = ssl;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public List<SearchDocumentDetailDto> getItems() {
        return items;
    }

    public void setItems(List<SearchDocumentDetailDto> items) {
        this.items = items;
    }
}
