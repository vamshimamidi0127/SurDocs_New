package gov.dc.surdocs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ViewerUrlBuilderService {

    @Value("${surdocs.viewer.protocol}")
    private String protocol;

    @Value("${surdocs.viewer.host}")
    private String host;

    @Value("${surdocs.viewer.port}")
    private String port;

    @Value("${surdocs.viewer.desktop}")
    private String desktop;

    public String buildViewerUrl(String documentId) {
        return protocol + host + ":" + port + "/navigator/?desktop=" + desktop + "&docId=" + documentId;
    }
}
