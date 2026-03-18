import LaunchRoundedIcon from "@mui/icons-material/LaunchRounded";
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Pagination,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
  useMediaQuery
} from "@mui/material";
import { useMemo, useState } from "react";
import { useTheme } from "@mui/material/styles";
import type { DocumentListItem } from "../types/api";

type DocumentListProps = {
  items: DocumentListItem[];
  onView: (item: DocumentListItem) => Promise<void> | void;
  viewerLoadingId?: string | null;
  viewerError?: string | null;
  pageSize?: number;
};

export function DocumentList({
  items,
  onView,
  viewerLoadingId,
  viewerError,
  pageSize = 10
}: DocumentListProps) {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("md"));
  const [page, setPage] = useState(1);

  const pageCount = Math.max(1, Math.ceil(items.length / pageSize));
  const pagedItems = useMemo(() => {
    const startIndex = (page - 1) * pageSize;
    return items.slice(startIndex, startIndex + pageSize);
  }, [items, page, pageSize]);

  if (items.length === 0) {
    return <Typography color="text.secondary">No documents found for the selected subtype.</Typography>;
  }

  return (
    <Stack spacing={2.5}>
      {viewerError ? <Alert severity="error">{viewerError}</Alert> : null}

      {isMobile ? (
        <Stack spacing={2}>
          {pagedItems.map((item) => (
            <Card key={item.documentId} variant="outlined">
              <CardContent>
                <Stack spacing={1.5}>
                  <Box>
                    <Typography variant="overline" color="text.secondary">
                      Document Type
                    </Typography>
                    <Typography variant="body1" fontWeight={700}>
                      {item.documentClass}
                    </Typography>
                  </Box>
                  <Box>
                    <Typography variant="overline" color="text.secondary">
                      Subtype
                    </Typography>
                    <Typography variant="body1">{item.subtype || "N/A"}</Typography>
                  </Box>
                  <Box>
                    <Typography variant="overline" color="text.secondary">
                      Count
                    </Typography>
                    <Chip label={item.count ?? 1} size="small" color="primary" variant="outlined" />
                  </Box>
                  <Box>
                    <Typography variant="overline" color="text.secondary">
                      Title
                    </Typography>
                    <Typography variant="body2">{item.title || "Untitled Document"}</Typography>
                  </Box>
                  <Button
                    variant="contained"
                    startIcon={<LaunchRoundedIcon />}
                    onClick={() => onView(item)}
                    disabled={viewerLoadingId === item.documentId}
                  >
                    {viewerLoadingId === item.documentId ? "Loading..." : "Open Viewer"}
                  </Button>
                </Stack>
              </CardContent>
            </Card>
          ))}
        </Stack>
      ) : (
        <TableContainer component={Paper} variant="outlined">
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Document Type</TableCell>
                <TableCell>Subtype</TableCell>
                <TableCell>Count</TableCell>
                <TableCell>Title</TableCell>
                <TableCell align="right">Action</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {pagedItems.map((item) => (
                <TableRow key={item.documentId} hover>
                  <TableCell>{item.documentClass}</TableCell>
                  <TableCell>{item.subtype || "N/A"}</TableCell>
                  <TableCell>{item.count ?? 1}</TableCell>
                  <TableCell>{item.title || "Untitled Document"}</TableCell>
                  <TableCell align="right">
                    <Button
                      variant="contained"
                      size="small"
                      startIcon={<LaunchRoundedIcon />}
                      onClick={() => onView(item)}
                      disabled={viewerLoadingId === item.documentId}
                    >
                      {viewerLoadingId === item.documentId ? "Loading..." : "Open Viewer"}
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {pageCount > 1 ? (
        <Box sx={{ display: "flex", justifyContent: "center" }}>
          <Pagination count={pageCount} page={page} onChange={(_event, value) => setPage(value)} color="primary" />
        </Box>
      ) : null}
    </Stack>
  );
}
