import PublicRoundedIcon from "@mui/icons-material/PublicRounded";
import { Alert, Stack, Typography } from "@mui/material";
import { SectionCard } from "../components/SectionCard";

export function GisMapSearchPage() {
return (
<Stack spacing={3}>
<SectionCard
title="GIS Map Search"
subtitle="Modern landing page for GIS-driven map lookup and survey map workflows."
>
<Stack spacing={2}>
<Typography variant="body1">
This page will host the GIS map search experience and integration points that replace the legacy map
discovery workflow.
</Typography>
<Alert severity="info" icon={<PublicRoundedIcon fontSize="inherit" />}>
GIS Map Search UI is scaffolded and ready for implementation.
</Alert>
</Stack>
</SectionCard>
</Stack>
);
}