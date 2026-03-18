import { useEffect, useState } from "react";
import { fetchDocumentsBySubtype } from "../services/searchService";
import type { DocumentListResponse } from "../types/api";

export function useDocumentsBySubtype(ssl: string, subtype: string) {
  const [data, setData] = useState<DocumentListResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function load() {
      if (!ssl || !subtype) {
        setData(null);
        return;
      }

      setIsLoading(true);
      setError(null);
      try {
        const response = await fetchDocumentsBySubtype(ssl, subtype);
        setData(response);
      } catch (err) {
        setError("Unable to load documents for the selected subtype.");
      } finally {
        setIsLoading(false);
      }
    }

    void load();
  }, [ssl, subtype]);

  return { data, isLoading, error };
}
