package gov.dc.surdocs.model.dto;

public class DocumentSummaryDto {

    private String documentType;
    private int count;

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
