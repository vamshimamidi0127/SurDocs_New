package gov.dc.surdocs.service;

import gov.dc.surdocs.model.dto.api.RecaptchaVerifyResponse;

public interface RecaptchaVerificationService {

    RecaptchaVerifyResponse verifyToken(String recaptchaToken);
}
