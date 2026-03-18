import { Navigate, Route, Routes } from "react-router-dom";
import { AppLayout } from "../components/AppLayout";
import { DocumentViewerPage } from "../pages/DocumentViewerPage";
import { DocumentsBySubtypePage } from "../pages/DocumentsBySubtypePage";
import { HomePage } from "../pages/HomePage";
import { QuickSearchPage } from "../pages/QuickSearchPage";

export function AppRoutes() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/search/quick" element={<QuickSearchPage />} />
        <Route path="/documents/by-subtype" element={<DocumentsBySubtypePage />} />
        <Route path="/documents/:id/viewer-url" element={<DocumentViewerPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
}
