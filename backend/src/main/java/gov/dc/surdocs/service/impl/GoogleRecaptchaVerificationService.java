package gov.dc.surdocs.service.impl;

import gov.dc.surdocs.config.RecaptchaProperties;
import gov.dc.surdocs.model.dto.api.RecaptchaVerifyResponse;
import gov.dc.surdocs.service.RecaptchaVerificationService;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleRecaptchaVerificationService implements RecaptchaVerificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleRecaptchaVerificationService.class);

    private final RestTemplate restTemplate;
    private final RecaptchaProperties recaptchaProperties;

    public GoogleRecaptchaVerificationService(RestTemplate restTemplate, RecaptchaProperties recaptchaProperties) {
        this.restTemplate = restTemplate;
        this.recaptchaProperties = recaptchaProperties;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecaptchaVerifyResponse verifyToken(String recaptchaToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        form.add("secret", recaptchaProperties.getSecret());
        form.add("response", recaptchaToken);

        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(
                recaptchaProperties.getVerifyUrl(),
                form,
                Map.class);

        Map<String, Object> body = responseEntity.getBody();
        if (body == null) {
            LOGGER.warn("Received empty response from Google reCAPTCHA verification endpoint");
            RecaptchaVerifyResponse response = new RecaptchaVerifyResponse();
            response.setSuccess(false);
            response.setErrorCodes(Collections.singletonList("empty-response"));
            return response;
        }

        LOGGER.debug("reCAPTCHA verification completed with success={}", body.get("success"));

        RecaptchaVerifyResponse response = new RecaptchaVerifyResponse();
        response.setSuccess(Boolean.TRUE.equals(body.get("success")));
        response.setScore(body.get("score") instanceof Number ? ((Number) body.get("score")).doubleValue() : null);
        response.setAction((String) body.get("action"));
        response.setChallengeTs((String) body.get("challenge_ts"));
        response.setHostname((String) body.get("hostname"));
        response.setErrorCodes((java.util.List<String>) body.getOrDefault("error-codes", Collections.emptyList()));
        return response;
    }
}
