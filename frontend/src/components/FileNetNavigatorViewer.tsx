import LaunchRoundedIcon from "@mui/icons-material/LaunchRounded";
import OpenInBrowserRoundedIcon from "@mui/icons-material/OpenInBrowserRounded";
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Stack,
  ToggleButton,
  ToggleButtonGroup,
  Typography
} from "@mui/material";
import { useEffect, useMemo, useState } from "react";
import { fetchDocumentViewerUrl } from "../services/searchService";

type FileNetNavigatorViewerProps = {
  documentId: string;
  defaultMode?: "tab" | "iframe";
};

export function FileNetNavigatorViewer({
  documentId,
  defaultMode = "tab"
}: FileNetNavigatorViewerProps) {
  const [mode, setMode] = useState<"tab" | "iframe">(defaultMode);
  const [viewerUrl, setViewerUrl] = useState<string | null>(null);
  const [documentTitle, setDocumentTitle] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [iframeLoading, setIframeLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const iframeSupported = useMemo(() => mode === "iframe" && Boolean(viewerUrl), [mode, viewerUrl]);

  useEffect(() => {
    async function loadViewerUrl() {
      if (!documentId) {
        return;
      }

      setIsLoading(true);
      setError(null);
      try {
        const response = await fetchDocumentViewerUrl(documentId);
        setViewerUrl(response.viewerUrl);
        setDocumentTitle(response.documentTitle);
        if (defaultMode === "tab") {
          window.open(response.viewerUrl, "_blank", "noopener,noreferrer");
        } else {
          setIframeLoading(true);
        }
      } catch (err) {
        setError("Unable to retrieve the FileNet Navigator URL.");
      } finally {
        setIsLoading(false);
      }
    }

    void loadViewerUrl();
  }, [defaultMode, documentId]);

  const handleModeChange = (_event: React.MouseEvent<HTMLElement>, nextMode: "tab" | "iframe" | null) => {
    if (!nextMode) {
      return;
    }
    setMode(nextMode);
    if (nextMode === "tab" && viewerUrl) {
      window.open(viewerUrl, "_blank", "noopener,noreferrer");
    }
    if (nextMode === "iframe" && viewerUrl) {
      setIframeLoading(true);
    }
  };

  const handleOpenInTab = () => {
    if (!viewerUrl) {
      return;
    }
    window.open(viewerUrl, "_blank", "noopener,noreferrer");
  };

  return (
    <Stack spacing={2.5}>
      <Stack
        direction={{ xs: "column", sm: "row" }}
        spacing={2}
        justifyContent="space-between"
        alignItems={{ xs: "stretch", sm: "center" }}
      >
        <Box>
          <Typography variant="h6">FileNet Navigator Viewer</Typography>
          <Typography variant="body2" color="text.secondary">
            {documentTitle || "Loading document metadata..."}
          </Typography>
        </Box>
        <ToggleButtonGroup
          value={mode}
          exclusive
          onChange={handleModeChange}
          size="small"
          color="primary"
        >
          <ToggleButton value="tab">New Tab</ToggleButton>
          <ToggleButton value="iframe">Iframe</ToggleButton>
        </ToggleButtonGroup>
      </Stack>

      {isLoading ? (
        <Box sx={{ display: "flex", justifyContent: "center", py: 4 }}>
          <CircularProgress />
        </Box>
      ) : null}

      {error ? <Alert severity="error">{error}</Alert> : null}

      {!isLoading && !error && viewerUrl ? (
        <Stack spacing={2}>
          <Button
            variant="contained"
            startIcon={<LaunchRoundedIcon />}
            onClick={handleOpenInTab}
            sx={{ alignSelf: "flex-start" }}
          >
            Open FileNet Navigator
          </Button>

          {iframeSupported ? (
            <Box
              sx={{
                position: "relative",
                minHeight: { xs: 420, md: 640 },
                border: "1px solid #d9e2ec",
                borderRadius: 2,
                overflow: "hidden",
                backgroundColor: "#fff"
              }}
            >
              {iframeLoading ? (
                <Box
                  sx={{
                    position: "absolute",
                    inset: 0,
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    backgroundColor: "rgba(255,255,255,0.8)",
                    zIndex: 1
                  }}
                >
                  <Stack spacing={1} alignItems="center">
                    <CircularProgress />
                    <Typography variant="body2" color="text.secondary">
                      Loading FileNet Navigator...
                    </Typography>
                  </Stack>
                </Box>
              ) : null}

              <iframe
                title="FileNet Navigator"
                src={viewerUrl}
                style={{ width: "100%", height: "100%", minHeight: "inherit", border: 0 }}
                onLoad={() => setIframeLoading(false)}
                onError={() => {
                  setIframeLoading(false);
                  setError("FileNet Navigator could not be displayed in an iframe. Try opening in a new tab.");
                }}
              />
            </Box>
          ) : (
            <Alert severity="info" icon={<OpenInBrowserRoundedIcon fontSize="inherit" />}>
              The viewer URL is ready. Open it in a new tab, or switch to iframe mode.
            </Alert>
          )}
        </Stack>
      ) : null}
    </Stack>
  );
}
