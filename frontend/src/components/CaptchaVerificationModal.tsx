import VerifiedUserRoundedIcon from "@mui/icons-material/VerifiedUserRounded";
import {
  Alert,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Stack,
  Typography
} from "@mui/material";
import { useState } from "react";
import ReCAPTCHA from "react-google-recaptcha";
import { verifyRecaptcha } from "../services/securityService";

type CaptchaVerificationModalProps = {
  open: boolean;
  documentTitle?: string;
  onClose: () => void;
  onVerified: () => Promise<void> | void;
};

export function CaptchaVerificationModal({
  open,
  documentTitle,
  onClose,
  onVerified
}: CaptchaVerificationModalProps) {
  const [token, setToken] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const siteKey = import.meta.env.VITE_RECAPTCHA_SITE_KEY as string | undefined;

  const handleVerify = async () => {
    if (!token) {
      setError("Complete the CAPTCHA challenge before continuing.");
      return;
    }

    setIsSubmitting(true);
    setError(null);
    try {
      const response = await verifyRecaptcha(token);
      if (!response.success) {
        setError("CAPTCHA verification failed. Please try again.");
        return;
      }
      await onVerified();
      onClose();
      setToken(null);
    } catch (err) {
      setError("Unable to verify CAPTCHA at this time.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Verify Before Opening Document</DialogTitle>
      <DialogContent>
        <Stack spacing={2.5} sx={{ pt: 1 }}>
          <Typography variant="body1">
            Confirm that you are a real user before the application opens FileNet Navigator.
          </Typography>
          {documentTitle ? (
            <Alert severity="info" icon={<VerifiedUserRoundedIcon fontSize="inherit" />}>
              Document: {documentTitle}
            </Alert>
          ) : null}
          {siteKey ? (
            <Box sx={{ display: "flex", justifyContent: "center" }}>
              <ReCAPTCHA sitekey={siteKey} onChange={(value) => setToken(value)} />
            </Box>
          ) : (
            <Alert severity="warning">
              `VITE_RECAPTCHA_SITE_KEY` is not configured. Add it to your frontend environment to enable CAPTCHA.
            </Alert>
          )}
          {error ? <Alert severity="error">{error}</Alert> : null}
        </Stack>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 3 }}>
        <Button onClick={onClose} color="inherit">
          Cancel
        </Button>
        <Button onClick={handleVerify} variant="contained" disabled={isSubmitting || !siteKey}>
          {isSubmitting ? "Verifying..." : "Verify and Open"}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
