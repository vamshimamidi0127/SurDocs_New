import { apiClient } from "./apiClient";
import type {
  DocumentCountResponse,
  DocumentListResponse,
  LotListResponse,
  DocumentViewerUrlResponse,
  QuickSearchOptionsResponse
} from "../types/api";

export async function fetchQuickSearchOptions(square: string): Promise<QuickSearchOptionsResponse> {
  const response = await apiClient.get<QuickSearchOptionsResponse>("/search/ssl/options", {
    params: { square }
  });
  return response.data;
}

export async function fetchDocumentCounts(ssl: string): Promise<DocumentCountResponse> {
  const response = await apiClient.get<DocumentCountResponse>("/search/ssl/documents", {
    params: { ssl }
  });
  return response.data;
}

export async function fetchLotOptions(square: string, suffix: string): Promise<LotListResponse> {
  const response = await apiClient.get<LotListResponse>("/search/ssl/lots", {
    params: { square, suffix }
  });
  return response.data;
}

export async function fetchDocumentsBySubtype(ssl: string, subtype: string): Promise<DocumentListResponse> {
  const response = await apiClient.get<DocumentListResponse>("/search/ssl/document-list", {
    params: { ssl, subtype }
  });
  return response.data;
}

export async function fetchDocumentViewerUrl(documentId: string): Promise<DocumentViewerUrlResponse> {
  const response = await apiClient.get<DocumentViewerUrlResponse>(`/documents/${documentId}/viewer-url`);
  return response.data;
}
