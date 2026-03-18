import { useCallback, useState } from "react";
import { fetchDocumentCounts } from "../services/searchService";
import type { DocumentCountResponse } from "../types/api";

export function useDocumentCounts() {
  const [data, setData] = useState<DocumentCountResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchCounts = useCallback(async (ssl: string) => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await fetchDocumentCounts(ssl);
      setData(response);
    } catch (err) {
      setError("Unable to load document counts.");
    } finally {
      setIsLoading(false);
    }
  }, []);

  const reset = useCallback(() => {
    setData(null);
    setError(null);
    setIsLoading(false);
  }, []);

  return { data, isLoading, error, fetchDocumentCounts: fetchCounts, reset };
}
