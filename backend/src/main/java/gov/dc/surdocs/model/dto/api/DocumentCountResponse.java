package gov.dc.surdocs.model.dto.api;

import gov.dc.surdocs.model.dto.search.DocumentCountDto;
import java.util.List;

public class DocumentCountResponse {

    private String ssl;
    private List<DocumentCountDto> items;

    public String getSsl() {
        return ssl;
    }

    public void setSsl(String ssl) {
        this.ssl = ssl;
    }

    public List<DocumentCountDto> getItems() {
        return items;
    }

    public void setItems(List<DocumentCountDto> items) {
        this.items = items;
    }
}
