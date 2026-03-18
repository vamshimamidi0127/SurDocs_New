package gov.dc.surdocs.model.dto.api;

import gov.dc.surdocs.model.dto.search.DocumentSubtypeCountDto;
import java.util.List;

public class SubtypeResponse {

    private String ssl;
    private String documentType;
    private List<DocumentSubtypeCountDto> items;

    public String getSsl() {
        return ssl;
    }

    public void setSsl(String ssl) {
        this.ssl = ssl;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public List<DocumentSubtypeCountDto> getItems() {
        return items;
    }

    public void setItems(List<DocumentSubtypeCountDto> items) {
        this.items = items;
    }
}
