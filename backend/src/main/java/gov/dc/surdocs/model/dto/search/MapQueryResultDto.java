package gov.dc.surdocs.model.dto.search;

public class MapQueryResultDto {

    private String documentId;
    private String mapType;
    private String pageNumber;
    private String title;
    private String versionSeriesId;
    private String objectClassId;
    private String mapNumber;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getMapType() {
        return mapType;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersionSeriesId() {
        return versionSeriesId;
    }

    public void setVersionSeriesId(String versionSeriesId) {
        this.versionSeriesId = versionSeriesId;
    }

    public String getObjectClassId() {
        return objectClassId;
    }

    public void setObjectClassId(String objectClassId) {
        this.objectClassId = objectClassId;
    }

    public String getMapNumber() {
        return mapNumber;
    }

    public void setMapNumber(String mapNumber) {
        this.mapNumber = mapNumber;
    }
}
