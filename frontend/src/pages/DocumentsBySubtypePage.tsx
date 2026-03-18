import OpenInNewRoundedIcon from "@mui/icons-material/OpenInNewRounded";
import { Alert, Box, Button, CircularProgress, Stack, Typography } from "@mui/material";
import { useState } from "react";
import { useSearchParams } from "react-router-dom";
import { CaptchaVerificationModal } from "../components/CaptchaVerificationModal";
import { DocumentList } from "../components/DocumentList";
import { SectionCard } from "../components/SectionCard";
import { useDocumentsBySubtype } from "../hooks/useDocumentsBySubtype";
import { fetchDocumentViewerUrl } from "../services/searchService";
import type { DocumentListItem } from "../types/api";

export function DocumentsBySubtypePage() {
  const [searchParams] = useSearchParams();
  const ssl = searchParams.get("ssl") ?? "";
  const subtype = searchParams.get("subtype") ?? "";
  const [viewerLoadingId, setViewerLoadingId] = useState<string | null>(null);
  const [viewerError, setViewerError] = useState<string | null>(null);
  const [lastViewerUrl, setLastViewerUrl] = useState<string | null>(null);
  const [selectedDocument, setSelectedDocument] = useState<DocumentListItem | null>(null);
  const [captchaOpen, setCaptchaOpen] = useState(false);

  const { data, isLoading, error } = useDocumentsBySubtype(ssl, subtype);

  const handleOpenDocument = async (item: DocumentListItem) => {
    setSelectedDocument(item);
    setCaptchaOpen(true);
  };

  const handleVerifiedOpen = async () => {
    if (!selectedDocument) {
      return;
    }

    setViewerLoadingId(selectedDocument.documentId);
    setViewerError(null);
    try {
      const response = await fetchDocumentViewerUrl(selectedDocument.documentId);
      setLastViewerUrl(response.viewerUrl);
      window.open(response.viewerUrl, "_blank", "noopener,noreferrer");
    } catch (err) {
      setViewerError("Unable to fetch the viewer URL for the selected document.");
    } finally {
      setViewerLoadingId(null);
    }
  };

  return (
    <Stack spacing={3}>
      <SectionCard
        title="Documents By Subtype"
        subtitle="Subtype-based document results with direct viewer launch. Comment actions have been removed."
      >
        <Stack spacing={1}>
          <Typography variant="body2" color="text.secondary">
            SSL
          </Typography>
          <Typography variant="h6">{ssl || "Not provided"}</Typography>
          <Typography variant="body2" color="text.secondary">
            Subtype
          </Typography>
          <Typography variant="h6">{subtype || "Not provided"}</Typography>
        </Stack>
      </SectionCard>

      {isLoading ? (
        <Box sx={{ display: "flex", justifyContent: "center", py: 4 }}>
          <CircularProgress />
        </Box>
      ) : null}

      {error ? <Alert severity="error">{error}</Alert> : null}

      {!isLoading && !error ? (
        <SectionCard title="Results" subtitle="Select a document to retrieve its viewer URL.">
          <DocumentList
            items={data?.items ?? []}
            onView={handleOpenDocument}
            viewerLoadingId={viewerLoadingId}
            viewerError={viewerError}
          />
        </SectionCard>
      ) : null}

      {lastViewerUrl ? (
        <SectionCard title="Last Viewer Launch">
          <Button
            href={lastViewerUrl}
            target="_blank"
            rel="noreferrer"
            variant="outlined"
            startIcon={<OpenInNewRoundedIcon />}
          >
            Open Last Viewer URL Again
          </Button>
        </SectionCard>
      ) : null}

      <CaptchaVerificationModal
        open={captchaOpen}
        documentTitle={selectedDocument?.title}
        onClose={() => {
          setCaptchaOpen(false);
          setSelectedDocument(null);
        }}
        onVerified={handleVerifiedOpen}
      />
    </Stack>
  );
}
