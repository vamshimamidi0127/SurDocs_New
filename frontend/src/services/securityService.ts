import { apiClient } from "./apiClient";
import type { RecaptchaVerifyResponse } from "../types/api";

export async function verifyRecaptcha(recaptchaToken: string): Promise<RecaptchaVerifyResponse> {
  const response = await apiClient.post<RecaptchaVerifyResponse>("/security/recaptcha/verify", {
    recaptchaToken
  });
  return response.data;
}
