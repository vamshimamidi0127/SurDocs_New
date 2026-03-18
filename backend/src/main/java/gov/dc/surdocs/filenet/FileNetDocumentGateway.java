package gov.dc.surdocs.filenet;

import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.Id;
import gov.dc.surdocs.model.dto.DocumentDetailDto;

public class FileNetDocumentGateway {

    private final FileNetObjectStoreProvider objectStoreProvider;

    public FileNetDocumentGateway(FileNetObjectStoreProvider objectStoreProvider) {
        this.objectStoreProvider = objectStoreProvider;
    }

    public DocumentDetailDto getDocumentMetadata(String documentId) {
        ObjectStore objectStore = objectStoreProvider.getObjectStore();
        Document document = Factory.Document.fetchInstance(objectStore, new Id(documentId), null);

        DocumentDetailDto dto = new DocumentDetailDto();
        dto.setDocumentId(document.get_Id().toString());
        dto.setDocumentTitle(document.get_Name());
        dto.setMimeType(document.get_MimeType());
        return dto;
    }
}
