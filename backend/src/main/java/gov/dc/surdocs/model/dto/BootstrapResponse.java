package gov.dc.surdocs.model.dto;

import java.util.List;

public class BootstrapResponse {

    private List<String> queryTypes;

    public List<String> getQueryTypes() {
        return queryTypes;
    }

    public void setQueryTypes(List<String> queryTypes) {
        this.queryTypes = queryTypes;
    }
}
