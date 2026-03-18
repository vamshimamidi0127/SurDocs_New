package gov.dc.surdocs.model.dto.api;

import gov.dc.surdocs.model.dto.search.LotOptionDto;
import java.util.List;

public class LotListResponse {

    private String square;
    private String suffix;
    private String sslPrefix;
    private List<LotOptionDto> lots;

    public String getSquare() {
        return square;
    }

    public void setSquare(String square) {
        this.square = square;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getSslPrefix() {
        return sslPrefix;
    }

    public void setSslPrefix(String sslPrefix) {
        this.sslPrefix = sslPrefix;
    }

    public List<LotOptionDto> getLots() {
        return lots;
    }

    public void setLots(List<LotOptionDto> lots) {
        this.lots = lots;
    }
}
