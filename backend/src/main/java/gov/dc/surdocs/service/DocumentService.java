package gov.dc.surdocs.service;

import gov.dc.surdocs.filenet.FileNetDocumentGateway;
import gov.dc.surdocs.model.dto.DocumentDetailDto;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    private final FileNetDocumentGateway fileNetDocumentGateway;

    public DocumentService(FileNetDocumentGateway fileNetDocumentGateway) {
        this.fileNetDocumentGateway = fileNetDocumentGateway;
    }

    public DocumentDetailDto getDocument(String documentId) {
        return fileNetDocumentGateway.getDocumentMetadata(documentId);
    }
}
