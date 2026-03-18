package gov.dc.surdocs.model.dto.search;

public class LotOptionDto {

    private String sslPrefix;
    private String lot;

    public String getSslPrefix() {
        return sslPrefix;
    }

    public void setSslPrefix(String sslPrefix) {
        this.sslPrefix = sslPrefix;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }
}
