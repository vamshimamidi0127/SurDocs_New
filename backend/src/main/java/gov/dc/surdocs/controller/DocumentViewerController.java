package gov.dc.surdocs.controller;

import gov.dc.surdocs.model.dto.api.DisplayDocumentResponse;
import gov.dc.surdocs.model.dto.api.DocumentViewerUrlResponse;
import gov.dc.surdocs.service.SearchApplicationService;
import javax.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/documents")
public class DocumentViewerController {

    private final SearchApplicationService searchApplicationService;

    public DocumentViewerController(SearchApplicationService searchApplicationService) {
        this.searchApplicationService = searchApplicationService;
    }

    @GetMapping("/{id}/viewer-url")
    public DocumentViewerUrlResponse getDocumentViewerUrl(
            @PathVariable("id") @NotBlank(message = "document id is required") String documentId) {
        return searchApplicationService.getDocumentViewerUrl(documentId.trim());
    }

    @GetMapping("/{id}/display")
    public DisplayDocumentResponse getDisplayDocument(
            @PathVariable("id") @NotBlank(message = "document id is required") String documentId) {
        return searchApplicationService.getDisplayDocument(documentId.trim());
    }
}
