package gov.dc.surdocs.model.dto;

public class ViewerLaunchResponse {

    private String documentId;
    private String launchUrl;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getLaunchUrl() {
        return launchUrl;
    }

    public void setLaunchUrl(String launchUrl) {
        this.launchUrl = launchUrl;
    }
}
