package gov.dc.surdocs.model.dto;

import java.util.List;

public class QuickSearchResponse {

    private String ssl;
    private List<DocumentSummaryDto> documents;

    public String getSsl() {
        return ssl;
    }

    public void setSsl(String ssl) {
        this.ssl = ssl;
    }

    public List<DocumentSummaryDto> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentSummaryDto> documents) {
        this.documents = documents;
    }
}
