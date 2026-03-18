export type SuffixOption = {
  square: string;
  suffix: string;
};

export type QuickSearchOptionsResponse = {
  square: string;
  suffixes: SuffixOption[];
};

export type LotOption = {
  sslPrefix: string;
  lot: string;
};

export type LotListResponse = {
  square: string;
  suffix: string;
  sslPrefix: string;
  lots: LotOption[];
};

export type DocumentCountItem = {
  type: string;
  count: number;
};

export type DocumentCountResponse = {
  ssl: string;
  items: DocumentCountItem[];
};

export type DocumentListItem = {
  ssl: string;
  documentClass: string;
  documentId: string;
  subtype: string;
  pageNumber: string;
  title: string;
  versionSeriesId: string;
  objectClassId: string;
  mimeType: string;
  category: string;
  count?: number;
};

export type DocumentListResponse = {
  ssl: string;
  subtype: string;
  documentType?: string;
  items: DocumentListItem[];
};

export type DocumentViewerUrlResponse = {
  documentId: string;
  documentTitle: string;
  mimeType: string;
  viewerUrl: string;
};

export type RecaptchaVerifyResponse = {
  success: boolean;
  score?: number;
  action?: string;
  challengeTs?: string;
  hostname?: string;
  errorCodes?: string[];
};
