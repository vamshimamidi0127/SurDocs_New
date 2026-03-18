package gov.dc.surdocs.model.dto.api;

import javax.validation.constraints.NotBlank;

public class RecaptchaVerifyRequest {

    @NotBlank(message = "recaptchaToken is required")
    private String recaptchaToken;

    public String getRecaptchaToken() {
        return recaptchaToken;
    }

    public void setRecaptchaToken(String recaptchaToken) {
        this.recaptchaToken = recaptchaToken;
    }
}
