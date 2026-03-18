package gov.dc.surdocs.controller;

import gov.dc.surdocs.model.dto.api.RecaptchaVerifyRequest;
import gov.dc.surdocs.model.dto.api.RecaptchaVerifyResponse;
import gov.dc.surdocs.service.RecaptchaVerificationService;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security")
public class SecurityController {

    private final RecaptchaVerificationService recaptchaVerificationService;

    public SecurityController(RecaptchaVerificationService recaptchaVerificationService) {
        this.recaptchaVerificationService = recaptchaVerificationService;
    }

    @PostMapping("/recaptcha/verify")
    public RecaptchaVerifyResponse verifyRecaptcha(@Valid @RequestBody RecaptchaVerifyRequest request) {
        return recaptchaVerificationService.verifyToken(request.getRecaptchaToken().trim());
    }
}
