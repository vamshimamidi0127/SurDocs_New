import ConstructionRoundedIcon from "@mui/icons-material/ConstructionRounded";
import { Alert, Stack, Typography } from "@mui/material";
import { SectionCard } from "../components/SectionCard";

export function AdvancedSearchPage() {
return (
<Stack spacing={3}>
<SectionCard
title="Advance Search"
subtitle="Modern replacement entry point for the legacy advanced search flow."
>
<Stack spacing={2}>
<Typography variant="body1">
This page is reserved for the advanced search experience, including book, index card, and structured
drilldown flows from the legacy application.
</Typography>
<Alert severity="info" icon={<ConstructionRoundedIcon fontSize="inherit" />}>
Advanced Search UI is scaffolded and ready for the next implementation step.
</Alert>
</Stack>
</SectionCard>
</Stack>
);
}