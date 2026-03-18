import SearchRoundedIcon from "@mui/icons-material/SearchRounded";
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  FormControl,
  Grid,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
  Stack,
  TextField,
  Typography
} from "@mui/material";
import { useEffect, useMemo, useState } from "react";
import { DocumentCountList } from "./DocumentCountList";
import { SectionCard } from "./SectionCard";
import { useDocumentCounts } from "../hooks/useDocumentCounts";
import { useLotOptions } from "../hooks/useLotOptions";
import { useQuickSearchOptions } from "../hooks/useQuickSearchOptions";

const QUERY_TYPES = ["Square", "Parcel", "Reservation", "Appropriation"] as const;

export function QuickSearchForm() {
  const [queryType, setQueryType] = useState<(typeof QUERY_TYPES)[number]>("Square");
  const [square, setSquare] = useState("");
  const [selectedSuffix, setSelectedSuffix] = useState("");
  const [selectedLot, setSelectedLot] = useState("");
  const [submittedSsl, setSubmittedSsl] = useState("");

  const {
    data: suffixData,
    isLoading: suffixLoading,
    error: suffixError,
    fetchOptions,
    reset: resetSuffixes
  } = useQuickSearchOptions();

  const {
    data: lotData,
    isLoading: lotLoading,
    error: lotError,
    fetchLots,
    reset: resetLots
  } = useLotOptions();

  const {
    data: documentCounts,
    isLoading: countLoading,
    error: countError,
    fetchDocumentCounts,
    reset: resetCounts
  } = useDocumentCounts();

  const normalizedSquare = useMemo(() => square.trim(), [square]);
  const computedSsl = useMemo(
    () => `${normalizedSquare}${selectedSuffix}${selectedLot}`.trim(),
    [normalizedSquare, selectedSuffix, selectedLot]
  );

  useEffect(() => {
    setSelectedSuffix("");
    setSelectedLot("");
    setSubmittedSsl("");
    resetSuffixes();
    resetLots();
    resetCounts();
  }, [queryType, resetCounts, resetLots, resetSuffixes]);

  useEffect(() => {
    if (!normalizedSquare || queryType !== "Square") {
      setSelectedSuffix("");
      resetSuffixes();
      return;
    }
    void fetchOptions(normalizedSquare);
  }, [normalizedSquare, queryType, fetchOptions, resetSuffixes]);

  useEffect(() => {
    if (!normalizedSquare) {
      setSelectedLot("");
      resetLots();
      return;
    }

    if (queryType === "Square") {
      if (!selectedSuffix) {
        setSelectedLot("");
        resetLots();
        return;
      }
      void fetchLots(normalizedSquare, selectedSuffix);
      return;
    }

    void fetchLots(normalizedSquare, "");
  }, [normalizedSquare, queryType, selectedSuffix, fetchLots, resetLots]);

  const handleQueryTypeChange = (event: SelectChangeEvent) => {
    setQueryType(event.target.value as (typeof QUERY_TYPES)[number]);
    setSquare("");
  };

  const handleSuffixChange = (event: SelectChangeEvent) => {
    setSelectedSuffix(event.target.value);
    setSelectedLot("");
    resetCounts();
  };

  const handleLotChange = (event: SelectChangeEvent) => {
    setSelectedLot(event.target.value);
    resetCounts();
  };

  const handleSearch = async () => {
    if (!computedSsl) {
      return;
    }
    setSubmittedSsl(computedSsl);
    await fetchDocumentCounts(computedSsl);
  };

  const isSearchDisabled =
    !normalizedSquare ||
    (queryType === "Square" && !selectedSuffix) ||
    (lotData?.lots.length ?? 0) > 0 && !selectedLot;

  return (
    <Stack spacing={3}>
      <SectionCard
        title="Quick Search"
        subtitle="Search surveyor documents using square, suffix, lot, and query type."
      >
        <Grid container spacing={2.5}>
          <Grid item xs={12} md={6}>
            <FormControl fullWidth>
              <InputLabel id="query-type-label">Query Type</InputLabel>
              <Select
                labelId="query-type-label"
                value={queryType}
                label="Query Type"
                onChange={handleQueryTypeChange}
              >
                {QUERY_TYPES.map((type) => (
                  <MenuItem key={type} value={type}>
                    {type}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>

          <Grid item xs={12} md={6}>
            <TextField
              label="Square"
              value={square}
              onChange={(event) => setSquare(event.target.value)}
              fullWidth
              required
              helperText="Enter the square or base identifier for the selected query type."
            />
          </Grid>

          <Grid item xs={12} md={6}>
            <FormControl fullWidth disabled={queryType !== "Square" || suffixLoading || !suffixData?.suffixes.length}>
              <InputLabel id="suffix-label">Suffix</InputLabel>
              <Select
                labelId="suffix-label"
                value={selectedSuffix}
                label="Suffix"
                onChange={handleSuffixChange}
              >
                {suffixData?.suffixes.map((option) => (
                  <MenuItem key={`${option.square}-${option.suffix}`} value={option.suffix}>
                    {option.suffix || "No Suffix"}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>

          <Grid item xs={12} md={6}>
            <FormControl fullWidth disabled={lotLoading || !lotData?.lots.length}>
              <InputLabel id="lot-label">Lot</InputLabel>
              <Select labelId="lot-label" value={selectedLot} label="Lot" onChange={handleLotChange}>
                {lotData?.lots.map((option) => (
                  <MenuItem key={`${option.sslPrefix}-${option.lot}`} value={option.lot}>
                    {option.lot}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
        </Grid>

        <Stack
          direction={{ xs: "column", sm: "row" }}
          spacing={2}
          alignItems={{ xs: "stretch", sm: "center" }}
          justifyContent="space-between"
          sx={{ mt: 3 }}
        >
          <Box>
            <Typography variant="body2" color="text.secondary">
              Computed SSL
            </Typography>
            <Typography variant="h6">{computedSsl || "Waiting for input"}</Typography>
          </Box>
          <Button
            variant="contained"
            size="large"
            startIcon={<SearchRoundedIcon />}
            onClick={handleSearch}
            disabled={isSearchDisabled || countLoading}
          >
            Search Documents
          </Button>
        </Stack>
      </SectionCard>

      {suffixLoading || lotLoading || countLoading ? (
        <Box sx={{ display: "flex", justifyContent: "center", py: 2 }}>
          <CircularProgress />
        </Box>
      ) : null}

      {suffixError ? <Alert severity="error">{suffixError}</Alert> : null}
      {lotError ? <Alert severity="error">{lotError}</Alert> : null}
      {countError ? <Alert severity="error">{countError}</Alert> : null}

      <SectionCard
        title="Document Counts"
        subtitle={submittedSsl ? `Results for SSL ${submittedSsl}` : "Run a quick search to load document counts."}
      >
        <DocumentCountList items={documentCounts?.items ?? []} />
      </SectionCard>
    </Stack>
  );
}
