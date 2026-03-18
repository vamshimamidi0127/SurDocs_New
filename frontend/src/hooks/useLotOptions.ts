import { useCallback, useState } from "react";
import { fetchLotOptions } from "../services/searchService";
import type { LotListResponse } from "../types/api";

export function useLotOptions() {
  const [data, setData] = useState<LotListResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchLots = useCallback(async (square: string, suffix: string) => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await fetchLotOptions(square, suffix);
      setData(response);
    } catch (err) {
      setError("Unable to load lot options.");
    } finally {
      setIsLoading(false);
    }
  }, []);

  const reset = useCallback(() => {
    setData(null);
    setError(null);
    setIsLoading(false);
  }, []);

  return { data, isLoading, error, fetchLots, reset };
}
