import SearchRoundedIcon from "@mui/icons-material/SearchRounded";
import { Box, Button, Stack, Typography } from "@mui/material";
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
          Use the quick search entry point to connect this frontend to the Spring Boot application APIs.
        </Typography>
        <Box>
          <Button
            component={RouterLink}
            to="/search/quick"
            variant="contained"
            size="large"
            startIcon={<SearchRoundedIcon />}
          >
            Open Quick Search
          </Button>
        </Box>
      </Stack>
    </SectionCard>
  );
}
