import { useEffect, useState } from "react";
import { fetchDocumentViewerUrl } from "../services/searchService";
import type { DocumentViewerUrlResponse } from "../types/api";

export function useDocumentViewerUrl(documentId: string) {
  const [data, setData] = useState<DocumentViewerUrlResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function load() {
      if (!documentId) {
        return;
      }
      setIsLoading(true);
      setError(null);
      try {
        const response = await fetchDocumentViewerUrl(documentId);
        setData(response);
      } catch (err) {
        setError("Unable to load document viewer URL.");
      } finally {
        setIsLoading(false);
      }
    }

    void load();
  }, [documentId]);

  return { data, isLoading, error };
}
