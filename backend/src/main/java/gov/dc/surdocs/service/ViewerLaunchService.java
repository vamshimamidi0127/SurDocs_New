package gov.dc.surdocs.service;

import gov.dc.surdocs.model.dto.ViewerLaunchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ViewerLaunchService {

    @Value("${surdocs.viewer.protocol}")
    private String protocol;

    @Value("${surdocs.viewer.host}")
    private String host;

    @Value("${surdocs.viewer.port}")
    private String port;

    @Value("${surdocs.viewer.desktop}")
    private String desktop;

    public ViewerLaunchResponse buildLaunchResponse(String documentId) {
        ViewerLaunchResponse response = new ViewerLaunchResponse();
        response.setDocumentId(documentId);
        response.setLaunchUrl(protocol + host + ":" + port + "/navigator/?desktop=" + desktop + "&docId=" + documentId);
        return response;
    }
}
