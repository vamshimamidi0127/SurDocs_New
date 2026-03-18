package gov.dc.surdocs.controller;

import gov.dc.surdocs.model.dto.api.DocumentCountResponse;
import gov.dc.surdocs.model.dto.api.DocumentListResponse;
import gov.dc.surdocs.model.dto.api.LotListResponse;
import gov.dc.surdocs.model.dto.api.QuickSearchOptionsResponse;
import gov.dc.surdocs.service.SearchApplicationService;
import javax.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/search/ssl")
public class SearchController {

    private final SearchApplicationService searchApplicationService;

    public SearchController(SearchApplicationService searchApplicationService) {
        this.searchApplicationService = searchApplicationService;
    }

    @GetMapping("/options")
    public QuickSearchOptionsResponse getQuickSearchOptions(
            @RequestParam("square") @NotBlank(message = "square is required") String square) {
        return searchApplicationService.getQuickSearchOptions(square.trim());
    }

    @GetMapping("/lots")
    public LotListResponse getLots(
            @RequestParam("square") @NotBlank(message = "square is required") String square,
            @RequestParam(value = "suffix", required = false) String suffix) {
        return searchApplicationService.getLots(square.trim(), suffix == null ? "" : suffix.trim());
    }

    @GetMapping("/documents")
    public DocumentCountResponse getDocumentCounts(
            @RequestParam("ssl") @NotBlank(message = "ssl is required") String ssl) {
        return searchApplicationService.getDocumentCounts(ssl.trim());
    }

    @GetMapping("/document-list")
    public DocumentListResponse getDocumentList(
            @RequestParam("ssl") @NotBlank(message = "ssl is required") String ssl,
            @RequestParam("subtype") @NotBlank(message = "subtype is required") String subtype,
            @RequestParam(value = "documentType", required = false) String documentType) {
        if (documentType == null || documentType.trim().isEmpty()) {
            return searchApplicationService.getDocuments(ssl.trim(), subtype.trim());
        }
        return searchApplicationService.getDocuments(ssl.trim(), documentType.trim(), subtype.trim());
    }
}
