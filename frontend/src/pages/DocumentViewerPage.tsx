import { Alert } from "@mui/material";
import { useParams } from "react-router-dom";
import { FileNetNavigatorViewer } from "../components/FileNetNavigatorViewer";
import { SectionCard } from "../components/SectionCard";

export function DocumentViewerPage() {
  const { id } = useParams<{ id: string }>();
  const documentId = id ?? "";

  return (
    <SectionCard
      title="Document Viewer"
      subtitle="Uses the backend-provided viewer URL to open FileNet Navigator without streaming document content through this application."
    >
      {!documentId ? (
        <Alert severity="error">A document id is required to launch FileNet Navigator.</Alert>
      ) : (
        <FileNetNavigatorViewer documentId={documentId} />
      )}
    </SectionCard>
  );
}
