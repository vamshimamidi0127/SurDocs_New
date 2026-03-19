import SearchRoundedIcon from "@mui/icons-material/SearchRounded";
import MapRoundedIcon from "@mui/icons-material/MapRounded";
import TuneRoundedIcon from "@mui/icons-material/TuneRounded";
import { Box, Button, Grid, Stack, Typography } from "@mui/material";
import { Link as RouterLink } from "react-router-dom";
import { SectionCard } from "../components/SectionCard";

export function HomePage() {
return (
<SectionCard
title="Surveyor Records Search"
subtitle="Modern React frontend scaffold for quick search and document launch flows."
>
<Stack spacing={3}>
<Typography variant="body1">
Choose a search experience to navigate surveyor records through the modernized application.
</Typography>
<Grid container spacing={2}>
<Grid item xs={12} md={4}>
<Box>
<Button
component={RouterLink}
to="/search/quick"
variant="contained"
size="large"
startIcon={<SearchRoundedIcon />}
fullWidth
>
Quick Search
</Button>
</Box>
</Grid>
<Grid item xs={12} md={4}>
<Box>
<Button
component={RouterLink}
to="/search/advanced"
variant="outlined"
size="large"
startIcon={<TuneRoundedIcon />}
fullWidth
>
Advance Search
</Button>
</Box>
</Grid>
<Grid item xs={12} md={4}>
<Box>
<Button
component={RouterLink}
to="/search/gis-map"
variant="outlined"
size="large"
startIcon={<MapRoundedIcon />}
fullWidth
>
GIS Map Search
</Button>
</Box>
</Grid>
</Grid>
</Stack>
</SectionCard>
);
}
