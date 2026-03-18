import { useCallback, useState } from "react";
import { fetchQuickSearchOptions } from "../services/searchService";
import type { QuickSearchOptionsResponse } from "../types/api";

export function useQuickSearchOptions() {
  const [data, setData] = useState<QuickSearchOptionsResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchOptions = useCallback(async (square: string) => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await fetchQuickSearchOptions(square);
      setData(response);
    } catch (err) {
      setError("Unable to load quick search options.");
    } finally {
      setIsLoading(false);
    }
  }, []);

  const reset = useCallback(() => {
    setData(null);
    setError(null);
    setIsLoading(false);
  }, []);

  return { data, isLoading, error, fetchOptions, reset };
}
