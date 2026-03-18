package gov.dc.surdocs.model.dto.api;

import gov.dc.surdocs.model.dto.search.SuffixOptionDto;
import java.util.List;

public class QuickSearchOptionsResponse {

    private String square;
    private List<SuffixOptionDto> suffixes;

    public String getSquare() {
        return square;
    }

    public void setSquare(String square) {
        this.square = square;
    }

    public List<SuffixOptionDto> getSuffixes() {
        return suffixes;
    }

    public void setSuffixes(List<SuffixOptionDto> suffixes) {
        this.suffixes = suffixes;
    }
}
